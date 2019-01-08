package Services.Orchestration;

import javax.jws.WebService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import DB.*;
import Services.Orchestration.Requests.*;
import Services.StatusCodes;
import com.sun.jmx.snmp.agent.SnmpUserDataFactory;

@WebService(endpointInterface = "Services.Orchestration.IOrchestrationService",
        serviceName = "OrchestrationService")
public class OrchestrationService implements IOrchestrationService {

    /**
     * For accessing database
     */
    private DBHandler dbHandler = new DBHandler();

    /**
     * Introduce an orchestration.
     * @param value Object that contains orchestration information.
     * @param jobRequests  List that contains jobRequests of orchestration.
     * @param ruleRequests List that contains ruleRequests of orchestration.
     * @return Message.
     */
    @Override
    public String addOrchestration(OrchestrationRequest value, List<JobRequest> jobRequests,
                                   List<RuleRequest> ruleRequests) {
        if (value.id == 0)
            return "*** DB.OrchestrationRequest id could not be 0! ***";

        List<Integer> JobIdList = new ArrayList<>();
        List<Integer> RuleIdList = new ArrayList<>();

        //End nodes
        JobIdList.add(0);
        RuleIdList.add(0);

        //Saving jobRequests to the database
        // and adding their generated id's from db to JobIdList.
        for (JobRequest temp : jobRequests)
            JobIdList.add(addJobSub(temp));

        // Saving ruleRequests to db.
        // creating edges between ruleRequests declared from gui.
        // adding DB.RuleRequest id's to the RuleIdList.
        for (RuleRequest temp : ruleRequests) {
            temp.yesEdge = JobIdList.get(temp.yesEdge);
            temp.noEdge = JobIdList.get(temp.noEdge);
            RuleIdList.add(addRuleSub(temp));
        }

        //Updating all jobRequests with their associated ruleRequests with db ids.
        for(int jobId : JobIdList){
            if (jobId == 0) continue;

            Job workOn;
            try {
                workOn = dbHandler.getJob(jobId);
            } catch (Exception e){
                System.err.println("Error to access given id: " + jobId);
                return String.format("*** Error to access given id (job): %d***", jobId);
            }

            int ruleId = RuleIdList.get(workOn.getRuleId());
            try {
                // Update ruleId of the job.
                dbHandler.updateJob(jobId, "RuleId", ruleId);

                // Update the relative result of the rule.
                dbHandler.updateRule(ruleId, "RelativeResult", makeXRelativeResult(workOn.getRelatives()));
            } catch (Exception e) {
                System.err.println("Error to access given id: " + ruleId);
                return String.format("*** Error to access given id (rule): %d***", ruleId);
            }
        }

        // Set the start job's id.
        Orchestration actualOrch = new Orchestration(value.ownerID, StatusCodes.INITIAL, JobIdList.get(1));

        // Adding orchestration value to db.
        try {
            dbHandler.insertOrchestration(actualOrch);
        } catch (Exception e) {
            System.err.println("DB.OrchestrationRequest could not be introduced: " + e);
            return "*** DB.OrchestrationRequest could not be introduced. ***";
        }
        return "DB.OrchestrationRequest has been introduced successfully!";
    }

    /**
     * Add job and rule. (Rule is optional.)
     *
     * @param job  Job to be added.
     * @param rule Rule to be added.
     * @return Message
     */
    @Override
    public String addJobRule(JobRequest job, RuleRequest rule) {
        if (job.id == 0)
            return "*** An occurred while adding job ***";
        if (job.ruleId == 0) {
            System.out.println("addJobRule" + job.id + " Hata burada");
            return addJobSub(job) != -1 ? "Job has been added successfully!" : "*** An occurred while adding job ***";
        }
        job.id = -1;
        rule.relativeResults = makeXRelativeResult(job.relatives);
        job.ruleId =  addRuleSub(rule);
        return addJobSub(job) != -1 ? "Job has been added with rule successfully!" : "*** An occurred while adding job with rule ***";
    }

    /**
     * Add given job to database.
     * @param value DB.JobRequest to be added to database.
     * @return If added, returns job id which is got from db, otherwise -1 to indicate an error.
     */
    private int addJobSub(JobRequest value) {
        int dbJobId;

        Job actualJob = new Job(value.owner, value.description, value.destination, value.fileUrl, value.relatives, 0, value.ruleId);
        if (value.id == -1){
            actualJob.setStatus(StatusCodes.SINGLE_INITIAL_JOB);
        }
        try {
            dbJobId = dbHandler.insertJob(actualJob);
        } catch (Exception e) {
            System.err.println("DB.JobRequest Db insert error: " + e);
            return -1;
        }
        actualJob.setId(dbJobId);
        System.out.printf("addJobSub JobID: %d - RuleID: %d\n", value.id, value.ruleId);
        return dbJobId;
    }

    /**
     * Add given rule to database.
     *
     * @param value DB.RuleRequest to be added to database.
     * @return If added, returns rule id which is got from db, otherwise -1 to indicate an error.
     */
    private int addRuleSub(RuleRequest value) {
        int dbRuleId;

        Rule actualRule = new Rule(value.ownerID, value.query, value.yesEdge, value.noEdge, value.relativeResults);
        try {
            dbRuleId = dbHandler.insertRule(actualRule);
        } catch (Exception e) {
            System.out.println("DB.RuleRequest Db insert error: " + e);
            return -1;
        }
        actualRule.setId(dbRuleId);
        System.out.println("addRuleSub: " + value.id);
        return dbRuleId;
    }

    /**
     * Make X relative result.
     *
     * @param relatives Relatives.
     * @return X
     */
    private String makeXRelativeResult(String relatives) {
        List<String> resList = Arrays.asList(relatives.split("\\s*,\\s*"));
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < resList.size();i++) {
            if(i == resList.size() - 1) {
                strBuilder.append("X");
            } else {
                strBuilder.append("X,");
            }
        }
        return strBuilder.toString();
    }
}
