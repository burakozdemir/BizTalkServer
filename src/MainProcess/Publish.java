package MainProcess;

import DB.DBHandler;
import Services.Approve.ApproveService;
import Services.InfoService.InfoService;
import Services.Orchestration.OrchestrationService;

import javax.xml.ws.Endpoint;

public class Publish {

    private static void startOrchestrationService() {
        Object implementor = new OrchestrationService();
        String address = "http://localhost:9001/OrchestrationService";
        Endpoint.publish(address, implementor);
    }

    private static void startInfoService() {
        Object implementor = new InfoService();
        String address = "http://localhost:9001/InfoService";
        Endpoint.publish(address, implementor);
    }

    private static void startApproveService() {
        Object implementor = new ApproveService();
        String address = "http://localhost:9001/ApproveService";
        Endpoint.publish(address, implementor);
    }

    public static void main(String[] argv) {

        startInfoService();
        startOrchestrationService();
        startApproveService();

      /*  DBHandler dbHandler = new DBHandler();
        dbHandler.getOrchestration(0);*/
        // Job job = new Job(151044083, "Açıklama yazısıdır1.", "10.0.0.1,10.0.0.2,10.0.0.3", "http://www.cevkos.gtu.edu.tr/wp-content/uploads/2018/03/GTU_sponsor-1.png", "1510,1511,1512", 0, 123);
        //dbHandler.insertJob(job);
    }

}