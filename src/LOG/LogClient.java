package LOG;

import BRE.BREClient;
import DB.Job;
import DB.Orchestration;
import DB.Rule;
import Services.InfoService.OrchestrationCapsule;

import java.net.HttpURLConnection;

public class LogClient {
    private static final String logJobDescUrl
            = "http://localhost:4000/jobdesc";
    private static final String logRuleUpdateUrl
            = "http://localhost:4000/ruleupdate";
    private static final String logJobRuleUrl
            = "http://localhost:4000/jobrule";
    private static final String logOrchDescUrl
            = "http://localhost:4000/orchdesc";
    private static final String logOrchUrl
            = "http://localhost:4000/orch";

    private static String jobDescParam =
                    "<job>\n" +
                    "    <id>%d</id>\n" +
                    "    <owner_id>%d</owner_id>\n" +
                    "    <job_description>%s</job_description>\n" +
                    "    <destination>%s</destination>\n" +
                    "    <file_url>%s</file_url>\n" +
                    "    <relatives>%s</relatives>\n" +
                    "    <status>%d</status>\n" +
                    "    <rule_id>%d</rule_id>\n" +
                    "    <insert_time>%s</insert_time>\n" +
                    "    <update_time>%s</update_time>\n" +
                    "    <description>%s</description>\n" +
                    "</job>\n";
    private static String ruleUpdateParam =
                    "<rules>\n" +
                    "<rule>\n" +
                    "<id>%d</id>\n" +
                    "    <owner_id>%d</owner_id>\n" +
                    "    <query>%s</query>\n" +
                    "    <yes_edge>%d</yes_edge>\n" +
                    "    <no_edge>%d</no_edge>\n" +
                    "    <relative_results>%s</relative_results>\n" +
                    "</rule>\n" +
                    "<rule>\n" +
                    "    <id>%d</id>\n" +
                    "    <owner_id>%d</owner_id>\n" +
                    "    <query>%s</query>\n" +
                    "    <yes_edge>%d</yes_edge>\n" +
                    "    <no_edge>%d</no_edge>\n" +
                    "    <relative_results>%s</relative_results>\n" +
                    "</rule>\n" +
                    "</rules>\n";


    private static String jobRuleParam =
                "<jobrule>\n" +
                        "<job>\n" +
                        "    <id>%d</id>\n" +
                        "    <owner_id>%d</owner_id>\n" +
                        "    <job_description>%s</job_description>\n" +
                        "    <destination>%s</destination>\n" +
                        "    <file_url>%s</file_url>\n" +
                        "    <relatives>%s</relatives>\n" +
                        "    <status>%d</status>\n" +
                        "    <rule_id>%d</rule_id>\n" +
                        "    <insert_time>%s</insert_time>\n" +
                        "    <update_time>%s</update_time>\n" +
                        "</job>\n"+
                        "<rule>\n" +
                        "    <id>%d</id>\n" +
                        "    <owner_id>%d</owner_id>\n" +
                        "    <query>%s</query>\n" +
                        "    <yes_edge>%d</yes_edge>\n" +
                        "    <no_edge>%d</no_edge>\n" +
                        "    <relative_results>%s</relative_results>\n" +
                        "</rule>\n"
                        +"</jobrule>\n";

    private static String orchDescParam =
            "<orchestration>\n" +
                    "    <id>%d</id>\n" +
                    "    <owner_id>%d</owner_id>\n" +
                    "    <status>%d</status>\n" +
                    "    <start_job_id>%d</start_job_id>\n" +
                    "    <insert_time>%s</insert_time>\n" +
                    "    <update_time>%s</update_time>\n" +
                    "    <description>%s</description>\n" +
                    "</orchestration>\n";

    private static String orchParam =
            "<orchestration>\n" +
                    "    <id>%d</id>\n" +
                    "    <owner_id>%d</owner_id>\n" +
                    "    <status>%d</status>\n" +
                    "    <start_job_id>%d</start_job_id>\n" +
                    "    <insert_time>%s</insert_time>\n" +
                    "    <update_time>%s</update_time>\n" +
                    "    <job_list>%s</job_list>\n" +
                    "    <rule_list>%s</rule_list>\n" +
                    "</orchestration>\n";

    private static HttpURLConnection conn;



    public static void LogRuleUpdate(Rule oldRule,Rule newRule) {
      /*  String response = BREClient.request(logRuleUpdateUrl,  String.format(ruleUpdateParam , oldRule.getId(),oldRule.getOwnerID(),oldRule.getQuery(),
                oldRule.getYesEdge(),oldRule.getNoEdge(),oldRule.getRelativeResults(),newRule.getId(),newRule.getOwnerID(),newRule.getQuery(),
                newRule.getYesEdge(),newRule.getNoEdge(),newRule.getRelativeResults()));
        response = response.toLowerCase();
        System.out.println(response);*/
    }


    public static void LogJobRule(Job job, Rule rule) {
     /*   String response;
        if(rule == null){
            response = BREClient.request(logJobDescUrl,  String.format(jobDescParam,job.getId(),job.getOwner(),job.getDescription(),
                    job.getDestination(),job.getFileUrl(),job.getRelatives(),job.getStatus(),job.getRuleId(),
                    job.getInsertDateTime_Date().toString(),job.getUpdateDateTime_Date().toString(),"Job with no rule"));
        }else{
            response = BREClient.request(logJobRuleUrl,  String.format(jobRuleParam,job.getId(),job.getOwner(),job.getDescription(),
                    job.getDestination(),job.getFileUrl(),job.getRelatives(),job.getStatus(),job.getRuleId(),
                    job.getInsertDateTime_Date().toString(),job.getUpdateDateTime_Date().toString() ,
                    rule.getId(),rule.getOwnerID(),rule.getQuery(),
                    rule.getYesEdge(),rule.getNoEdge(),rule.getRelativeResults()));
        }

        response = response.toLowerCase();
        System.out.println(response);*/
    }


    public static void LogJobDesc(Job job, String description) {
       /* String response = BREClient.request(logJobDescUrl,
                String.format(jobDescParam ,
                        job.getId(), job.getOwner(), job.getDescription(), job.getDestination(),
                        job.getFileUrl(), job.getRelatives(), job.getStatus(), job.getRuleId(),
                        job.getInsertDateTime(), job.getUpdateDateTime(), description));
        response = response.toLowerCase();

        System.out.println(response);*/
    }

    public static void LogOrchDesc(Orchestration orchestration, String description) {
/*        String response = BREClient.request(logOrchDescUrl,
                String.format(orchDescParam ,
                        orchestration.getId(), orchestration.getOwnerID(), orchestration.getStatus(),
                        orchestration.getStartJobID(), orchestration.getInsertDateTime(),
                        orchestration.getUpdateDateTime(), description));
        response = response.toLowerCase();

        System.out.println(response);*/
    }

    public static void LogOrch(OrchestrationCapsule orchestration) {
       /* String response = BREClient.request(logOrchUrl,
                String.format(orchParam ,
                        orchestration.getId(), orchestration.getOwnerID(), orchestration.getStatus(),
                        orchestration.getStartJobID(), orchestration.getInsertDateTime(),
                        orchestration.getUpdateDateTime(),
                        orchestration.getJobs().toString(), orchestration.getRules().toString()));
        response = response.toLowerCase();

        System.out.println(response);*/
    }
}
