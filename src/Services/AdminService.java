package Services;

import DB.DBHandler;
import DB.Job;
import DB.Orchestration;
import LOG.LogClient;
import MainProcess.MainProcess;
import Services.Approve.ApproveService;
import Services.InfoService.InfoService;
import Services.Orchestration.OrchestrationService;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

@WebService(serviceName = "AdminService")
public class AdminService {
    private String orchestrationServiceAddress;
    private String infoServiceAddress;
    private String approveServiceAddress;
    private Endpoint orchestrationEndpoint;
    private Endpoint infoEndpoint;
    private Endpoint approveEndpoint;
    public static boolean stopped;

    private DBHandler dbHandler = new DBHandler();

    public AdminService() {
        String hostIp = getHostIp();
        orchestrationServiceAddress = "http://" + hostIp + ":9001/OrchestrationService";
        orchestrationEndpoint = Endpoint.publish(orchestrationServiceAddress, new OrchestrationService());

        infoServiceAddress = "http://" + hostIp + ":9001/InfoService";
        infoEndpoint = Endpoint.publish(infoServiceAddress, new InfoService());

        approveServiceAddress = "http://" + hostIp + ":9001/ApproveService";
        approveEndpoint = Endpoint.publish(approveServiceAddress, new ApproveService());

        String adminServiceAddress = "http://" + hostIp + ":9001/AdminService";
        Endpoint.publish(adminServiceAddress, this);

        stopped = false;


        // tek stringli LOG
    }

    @WebMethod
    public String startServer() {
        if (!orchestrationEndpoint.isPublished()) {
            stopped = false;
            orchestrationEndpoint = Endpoint.publish(orchestrationServiceAddress, new OrchestrationService());
            infoEndpoint = Endpoint.publish(infoServiceAddress, new InfoService());
            approveEndpoint = Endpoint.publish(approveServiceAddress, new ApproveService());
            // tek stringli LOG //System.out.println("----> Server has been started!");
            return "*** Server has just been started! ***";
        }
        return "*** Server is running now! ***";
    }

    @WebMethod
    public String stopServer() {
        if (orchestrationEndpoint.isPublished()) {
            stopped = true;
            updateOrchestrations();
            updateJobs();
            orchestrationEndpoint.stop();
            infoEndpoint.stop();
            approveEndpoint.stop();
            // tek stringli LOG //System.out.println("----> Server has been stopped!");
            return "*** Server has just been stopped! ***";
        }
        else {
            // tek stringli LOG //System.out.println("----> Server has been stopped!");
            return "*** Server was stopped! ***";
        }
    }

    @WebMethod
    public boolean getServerState() {
        return stopped;
    }

    private void updateJobs() {
        for (Job job : MainProcess.jobList) {
            System.out.println("*----->job " + job.getId() + " ");
            job.setStatus(StatusCodes.INITIAL);
            try {
                dbHandler.updateOrchestration(job.getId(), "Status", StatusCodes.INITIAL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateOrchestrations() {
        for (Orchestration orch : MainProcess.orchList) {
            System.out.println("*----->orch " + orch.getId() + " ");
            orch.setStatus(StatusCodes.INITIAL);
            try {
                dbHandler.updateOrchestration(orch.getId(), "Status", StatusCodes.INITIAL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String getHostIp(){
        String ip="";
        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            ip = socket.getLocalAddress().getHostAddress();
        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
        }
        return ip;
    }
}
