package Services.Approve;

import BRE.BREClient;
import DB.DBHandler;
import DB.Job;
import DB.Rule;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.ws.Endpoint;
import java.util.Arrays;
import java.util.List;

@WebService()
public class ApproveService {
  private String userApp;
  private int jobId, relativeId;

  @WebMethod()
  public String updateUserApprove(@XmlElement(required = true, nillable = false) requestApprove ra){

    //guiden gelen degerler.
    userApp = ra.getUserApprove();
    jobId = ra.getJobId();
    relativeId = ra.getRelativeId();
    System.out.println("RelId:"  + relativeId);
    DBHandler db = new DBHandler();
    //guiden gelen jobid ile db deki job bilgilerine ulasiyoruz.
    Job job = null;
    try {
      job = db.getJob(jobId);
    } catch (Exception e) {
      e.printStackTrace();
    }
    //job tablosundan ruleid ye ulasip oradan rule tablosuna erisiyoruz.
    int ruleId = job.getRuleId();
    String newRelative = BREClient.approve(ruleId, relativeId, userApp);

    //En son olarak rule tablosunu guncelledim.
    try {
      db.updateRule(ruleId,"RelativeResult",newRelative);
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println(ra.getRelativeId() + ra.getUserApprove());
    return "update relative";
  }

}