package MainProcess;

import DB.DBHandler;
import DB.Job;
import DB.Orchestration;
import DB.Rule;
import LOG.LogClient;

import LOG.LogLevel;
import Services.AdminService;
import Services.StatusCodes;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;

public class MainProcess {
    private final static int WAIT_TIME_MILLISECONDS = 5 * 60 * 1000;
    private static DBHandler dbHandler = new DBHandler();
    public static ArrayList<Orchestration> orchList = new ArrayList<>();
    public static ArrayList<Job> jobList = new ArrayList<>();

    public static String createMessageFile(String message) throws IOException {
        Date date = new Date();
        long time = date.getTime();
        long ts = System.currentTimeMillis() / 1000L;

        try (PrintWriter out = new PrintWriter("temp/" + ts + ".message")) {
            out.println(message);
        }
        return String.valueOf(ts);

    }


    private static void work(Job job) throws IOException {
        System.out.println(String.format("Job: %d islendi", job.getId())); // job islendi.
        String fileName = null;
        String fileUrl = job.getFileUrl();
        String[] destinations = job.getDestination().split(",");
        String[] messages = job.getDescription().split("~");
        int count = 0;
        fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1, fileUrl.length());

        for (String dest : destinations) {
            dest = dest.replaceAll(" ", "");
            String ftpUrlStart = "ftp://%s:%s@%s/%s";
            String ftpUrl = null;
            String host = dest + ":21";
            String user = "BizTalk";
            String pass = "123";
            // String filePath = Paths.get("IncomingFiles\\"+fileName).toString();
            String filePath = job.getFileUrl();
            System.out.println("local file url:" + filePath);

            ftpUrl = String.format(ftpUrlStart, user, pass, host, fileName);
            System.out.println("Upload URL: " + ftpUrl);

            URL url = new URL(ftpUrl);
            URLConnection conn = url.openConnection();


            OutputStream outputStream = conn.getOutputStream();
            InputStream inputStream = new URL(filePath).openStream();

            //Send main file
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);

            }

            //Send message info
            String messageFile = createMessageFile(messages[count]);
            String messagePath = Paths.get("temp\\" + messageFile + ".message").toString();
            System.out.println("local file url:" + messagePath);

            String ftpUrlMessage = String.format(ftpUrlStart, user, pass, host, fileName + ".message");
            System.out.println("Upload URL: " + ftpUrlMessage);
            url = new URL(ftpUrlMessage);
            conn = url.openConnection();
            OutputStream outputStreamMessage = conn.getOutputStream();
            FileInputStream inputStreamMessage = new FileInputStream(messagePath);
            buffer = new byte[4096];

            while ((bytesRead = inputStreamMessage.read(buffer)) != -1) {

                outputStreamMessage.write(buffer, 0, bytesRead);

            }

            inputStreamMessage.close();
            outputStreamMessage.close();
            inputStream.close();
            outputStream.close();

            File deleteFile = new File(messagePath);
            if (deleteFile.delete()) {
                System.out.println("tmp/file.txt File deleted from Project root directory");
            } else System.out.println("File tmp/file.txt doesn't exists in project root directory");

            System.out.println("File uploaded");
            // tek stringli olan LOG metodu. (The file has been sent to the IP address : dest)
            LogClient.LogDesc("The file has been sent to the asked IP address.", job.getOwner(), LogLevel.INFO);
            ++count;
        }



    }

    private static char checkRule(Rule rule) throws Exception {
        Rule realRule = dbHandler.getRule(rule.getId());
        String relativeResults = realRule.getRelativeResults();
        return relativeResults == null ? 'Q' : relativeResults.toCharArray()[0];
    }

    private static void orchestrationRun(Orchestration orchestration) {
        try {
            // StartJobID orchestration objesinden alinir.
            int currentJobID = orchestration.getStartJobID();

            // Baslangic jobu Db'den cekilir.
            Job currentJob = dbHandler.getJob(currentJobID);

            boolean noRuleState = false;

            if (currentJob.getRuleId() == 0) {
                noRuleState = true;
            }
            while (currentJob.getRuleId() != 0) {
                Rule ruleOfCurrentJob = dbHandler.getRule(currentJob.getRuleId());
                char responseOfBRE;
                Date jobInsertDate = currentJob.getInsertDateTime_Date();
                while ((responseOfBRE = checkRule(ruleOfCurrentJob)) == 'X' && (new Date()).getTime() - jobInsertDate.getTime() < WAIT_TIME_MILLISECONDS) {
                    Thread.sleep(100); // Check every 100 ms
                }

                if (responseOfBRE == 'T') {
                    System.out.println("Response True geldi -> JobId: " + currentJob.getId());
                    work(currentJob);
                    dbHandler.updateJob(currentJobID, "Status", StatusCodes.SUCCESS);
                    currentJobID = ruleOfCurrentJob.getYesEdge();
                    orchestration.setStartJobID(currentJobID);
                    dbHandler.updateOrchestration(orchestration.getId(), "StartingJobId", currentJobID);
                } else if (responseOfBRE == 'X'){ // Not responded or False ( Bu durumda herhangi bi info vermiyoruz sanırım. ) //
                    dbHandler.updateJobsSuccesfully(orchestration, StatusCodes.ERROR);
                    dbHandler.updateOrchestration(orchestration.getId(), "Status", StatusCodes.ERROR);
                    orchList.remove(orchestration);
                    System.out.println("--------------------------ERROR");
                    return;
                } else {
                    dbHandler.updateJob(currentJobID, "Status", StatusCodes.NOT_ACCEPTED);
                    currentJobID = ruleOfCurrentJob.getNoEdge();
                    orchestration.setStartJobID(currentJobID);
                    dbHandler.updateOrchestration(orchestration.getId(), "StartingJobId", currentJobID);
                }

                if (currentJobID == 0) { // Rule END e gidecekse orchestration status u success yapmıyoruz sanırım emin miyiz?
                    break;
                }

                currentJob = dbHandler.getJob(currentJobID);
                orchestration.setStartJobID(currentJobID);
                dbHandler.updateOrchestration(orchestration.getId(), "StartingJobId", currentJobID);

                if (currentJob.getRuleId() == 0) {
                    noRuleState = true;
                    break;
                }
            }
            // Eger en son joba kadar varilirsa, o job da islenir.
            if (noRuleState) {
                work(currentJob);
                dbHandler.updateJob(currentJobID, "Status", StatusCodes.SUCCESS);
                LogClient.LogDesc("The rule has changed its status from WORKING to SUCCESS.", currentJobID, LogLevel.UPDATE);

            }

            dbHandler.updateOrchestration(orchestration.getId(), "Status", StatusCodes.SUCCESS);
            dbHandler.updateJobsSuccesfully(orchestration, StatusCodes.SUCCESS);
            LogClient.LogOrchDesc(orchestration, "The orchestration has been completed successfully.", LogLevel.UPDATE);
            orchList.remove(orchestration);
        } catch (Exception e) {
            e.printStackTrace();
            LogClient.LogDesc("Orchestration couldn't be completed successfully.", -1, LogLevel.ERROR);
        }
    }

    public static Runnable singleJobExecution(Job job) {
        Runnable runnable = () -> {
            try {
                dbHandler.updateJob(job.getId(), "Status", StatusCodes.WORKING);//TODO ?
                LogClient.LogJobDesc(job, "Job's status has changed from INITIAL to WORKING status.", LogLevel.UPDATE);
                boolean canWork = true;
                if (job.getRuleId() != 0) {
                    Rule rule = dbHandler.getRule(job.getRuleId());
                    Date jobInsertDate = job.getInsertDateTime_Date();
                    char response;
                    while ((response = checkRule(rule)) == 'X' && (new Date()).getTime() - jobInsertDate.getTime() < WAIT_TIME_MILLISECONDS) {
                        Thread.sleep(100);
                    }
                    canWork = response == 'T';

                }

                if (canWork) {
                    work(job);
                    dbHandler.updateJob(job.getId(), "Status", StatusCodes.SUCCESS);
                    jobList.remove(job.getId());
                    LogClient.LogJobDesc(job, "Job's status has changed from INITIAL to WORKING status.", LogLevel.UPDATE);
                } else {
                    System.out.println("Not Approved job!");
                    dbHandler.updateJob(job.getId(), "Status", StatusCodes.ERROR);
                    jobList.remove(job.getId());
                    LogClient.LogJobDesc(job, "Job's status has changed from WORKING to ERROR status.", LogLevel.UPDATE);
                }
            } catch (Exception e) {
                try {
                    dbHandler.updateJob(job.getId(), "Status", StatusCodes.ERROR);//TODO ?
                    jobList.remove(job.getId());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
                LogClient.LogJobDesc(job, "Job's status has changed from WORKING to ERROR status.", LogLevel.UPDATE);
            }
        };
        return runnable;
    }

    public static Runnable orchestrationExecution(Orchestration orchestration) throws Exception {
        return () -> {
            try {
                dbHandler.updateOrchestration(orchestration.getId(), "Status", StatusCodes.WORKING);
                LogClient.LogOrchDesc(orchestration, "Orchestration has just been started running.", LogLevel.INFO);
            } catch (Exception e) {
                e.printStackTrace();
                LogClient.LogDesc("Orchestration couldn't be started for some reason.", orchestration.getOwnerID(), LogLevel.ERROR);

                // tek stringli LOG metodu.
            }
            orchestrationRun(orchestration);
        };
    }

    public static void main(String[] args) throws Exception {
        new AdminService();

        try {
            while (true) {
                if (!AdminService.stopped) {
                    ArrayList<Orchestration> orchestrations = dbHandler.getOrchestrations();

                   /* if (orchestrations.size() == 0) {//System.out.println("No orchestrations waiting!");}*/

                    for (Orchestration orch : orchestrations) {
                        Thread orchThread = new Thread(orchestrationExecution(orch));
                        orchList.add(orch);
                        orchThread.start();
                    }
                    ArrayList<Job> jobs = dbHandler.getJobs();

                   /* if (jobs.size() == 0) {//System.out.println("No single jobs waiting!");}*/

                    for (Job job : jobs) {
                        Thread jobThread = new Thread(singleJobExecution(job));
                        jobList.add(job);
                        jobThread.start();
                    }
                    Thread.sleep(100);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            LogClient.LogDesc("Server is, down.", -1, LogLevel.FAIL);
        }
    }
}
