package MainProcess;

import DB.DBHandler;
import DB.Job;
import DB.Orchestration;
import DB.Rule;
import Services.InfoService.RulesAndJobs;
import Services.StatusCodes;
import org.apache.commons.lang3.time.StopWatch;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class MainProcess {
    private static DBHandler dbHandler = new DBHandler();
    private final static int WAIT_TIME_SECONDS = 300;

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
        String result = "Hello, world, from " + job.getOwner();
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
            //FileInputStream inputStream = new FileInputStream(filePath);
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
            ++count;
        }

    }

    private static char checkRule(Rule rule) throws Exception {
        Rule realRule = dbHandler.getRule(rule.getId());
        String relativeResults = realRule.getRelativeResults();
        if (relativeResults.equals("X"))
            return 'X';
        if (relativeResults.equals("F"))
            return 'F';
        return 'T';
    }

    private static void orchestrationRun(Orchestration orchestration) {
        try {
            // StartJobID orchestration objesinden alinir.
            int currentJobID = orchestration.getStartJobID();

            // Baslangic jobu Db'den cekilir.
            Job currentJob = dbHandler.getJob(currentJobID);
            boolean noRuleState = false;

            if(currentJob.getRuleId()==0) {
                noRuleState = true;
            }
            while (currentJob.getRuleId() != 0) {
                Rule ruleOfCurrentJob = dbHandler.getRule(currentJob.getRuleId());
                char responseOfBRE;

                StopWatch sw = StopWatch.createStarted();
                while ((responseOfBRE = checkRule(ruleOfCurrentJob)) == 'X' && sw.getTime(TimeUnit.SECONDS) < WAIT_TIME_SECONDS){
                    Thread.sleep(100); // Check every 100 ms
                }
                sw.stop();

                if (responseOfBRE == 'T') {
                    work(currentJob);
                    dbHandler.updateJob(currentJobID, "Status", StatusCodes.SUCCESS);
                    currentJobID = ruleOfCurrentJob.getYesEdge();
                }
                else{ // Not responded or False ( Bu durumda herhangi bi info vermiyoruz sanırım. ) // TODO?
                    dbHandler.updateJob(currentJobID, "Status", StatusCodes.ERROR);
                    currentJobID = ruleOfCurrentJob.getNoEdge();
                }

                if (currentJobID == 0) { // Rule END e gidecekse orchestration status u success yapmıyoruz sanırım emin miyiz? TODO?
                    break;
                }

                currentJob = dbHandler.getJob(currentJobID);

                if(currentJob.getRuleId()==0) {
                    noRuleState = true;
                    break;
                }

            }
            // Eger en son joba kadar varilirsa, o job da islenir.
            if (noRuleState) {
                work(currentJob);
                dbHandler.updateJob(currentJobID, "Status", StatusCodes.SUCCESS);
            }
            dbHandler.updateOrchestration(orchestration.getId(), "Status", StatusCodes.SUCCESS);  //TODO ?
        } catch (Exception e) {
            // TODO: log basacak.
            System.out.println(String.format("*** An error occured while getting orchestration from DB: %s ***", e));
        }
    }

    private static void orchFinishLog(Orchestration orchestration) throws Exception {
        DBHandler dbHandler = new DBHandler();
        ArrayList<RulesAndJobs> rulesAndJobs = dbHandler.getRulesAndJobs(orchestration.getId());
        System.out.println(rulesAndJobs);
/*        ArrayList<Job> jobList = rulesAndJobs.get(0).getJobs();
        ArrayList<Rule> ruleList = rulesAndJobs.get(0).getRules();*/
        /*for (int i = 0; i < jobList.size(); ++i) {
            Job job = jobList.get(i);
            for (int j = 0; j < ruleList.size(); ++j) {
                Rule rule = ruleList.get(j);
                if (job.getRuleId() == rule.getId()) {
                    BizLog.Log("1", String.valueOf(orchestration.getOwnerID()), LogLevel.INFO,
                            job, rule, orchestration);
                    break;
                }
            }
        }
        dbHandler.updateOrchestration(orchestration.getId(), "Status", StatusCodes.SUCCESS);  //TODO ?
        BizLog.Log("1", String.valueOf(orchestration.getOwnerID()), LogLevel.INFO,orchestration);*/
    }

    public static Runnable singleJobExecution(Job job) {
        //DBHandler dbHandlerSingle = new DBHandler();
        Runnable runnable = () -> {
            try {
                dbHandler.updateJob(job.getId(), "Status", StatusCodes.WORKING);//TODO ?
                boolean canWork = true;
                if (job.getRuleId() != 0) {
                    Rule rule = dbHandler.getRule(job.getRuleId());
                    StopWatch sw = StopWatch.createStarted();
                    char response;
                    while ((response = checkRule(rule)) == 'X' && sw.getTime(TimeUnit.SECONDS) < WAIT_TIME_SECONDS){
                        System.out.println("geldi" + response);
                        Thread.sleep(100);
                    }
                    canWork = response == 'T';

                }

                if (canWork){
                    work(job);
                    dbHandler.updateJob(job.getId(), "Status", StatusCodes.SUCCESS);
                }
                else {
                    System.out.println("Not Approved job!");
                    dbHandler.updateJob(job.getId(), "Status", StatusCodes.ERROR);
                }
            } catch (Exception e) {
                try {
                    dbHandler.updateJob(job.getId(), "Status", StatusCodes.ERROR);//TODO ?
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        };
        return runnable;
    }

    public static Runnable orchestrationExecution(Orchestration orchestration) throws Exception {
        return () -> {
            try {
                dbHandler.updateOrchestration(orchestration.getId(), "Status", StatusCodes.WORKING);
            } catch (Exception e) {
                e.printStackTrace();
            }
            orchestrationRun(orchestration);
        };
    }

    public static void main(String[] args) {
        Publish.main(null);
        try {
            while(true){
                Set<Orchestration> orchestrations = dbHandler.getOrchestrations();
                if (orchestrations.size() == 0){
                    System.out.println("No orchestrations waiting!");
                }
                for (Orchestration orch:
                        orchestrations) {
                    Thread orchThread = new Thread(orchestrationExecution(orch));
                    orchThread.start();
                }
                Set<Job> jobs = dbHandler.getJobs();
                if (jobs.size() == 0){
                    System.out.println("No single jobs waiting!");
                }
                for (Job job:
                        jobs) {
                    Thread jobThread = new Thread(singleJobExecution(job));
                    jobThread.start();
                }
                Thread.sleep(100);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
