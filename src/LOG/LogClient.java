package LOG;

import BRE.BREClient;
import DB.Job;
import DB.Orchestration;
import DB.Rule;
import Services.InfoService.OrchestrationCapsule;
import Services.Orchestration.Requests.JobRequest;

import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;

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
    private static final String logDescUrl
            = "http://localhost:4000/onlydesc";

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


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
                    "    <log_level>%s</log_level>\n" +
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
                    "</rule>\n"+
                    "<level>\n" +
                    "    <log_level>%s</log_level>\n"+
                    "</level>\n"
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
                    "    <log_level>%s</log_level>\n" +
                    "</orchestration>\n";


    private static String orchParam =
            "<start>\n"+
                    "<orchestration>\n" +
                    "    <id>%d</id>\n" +
                    "    <owner_id>%d</owner_id>\n" +
                    "    <status>%d</status>\n" +
                    "    <start_job_id>%d</start_job_id>\n" +
                    "    <insert_time>%s</insert_time>\n" +
                    "    <update_time>%s</update_time>\n" +
                    "</orchestration>\n"+
                    "<jobs>\n%s</jobs>\n"+
                    "<rules>\n%s</rules>\n"+
                    "<level>\n"+
                    "<log_level>%s</log_level>\n" +
                    "</level>\n"+
                    "</start>";


    private static String descParam =
            "<description>\n" +
                    "    <id>%d</id>\n" +
                    "    <desc>%s</desc>\n" +
                    "    <log_level>%s</log_level>\n" +
                    "</description>\n";

    private static String jobParam =
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
                    "    <log_level>%s</log_level>\n" +
                    "</job>\n";

    private static String ruleParam =
            "<rule>\n" +
                    "    <id>%d</id>\n" +
                    "    <owner_id>%d</owner_id>\n" +
                    "    <query>%s</query>\n" +
                    "    <yes_edge>%d</yes_edge>\n" +
                    "    <no_edge>%d</no_edge>\n" +
                    "    <relative_results>%s</relative_results>\n" +
                    "    <log_level>%s</log_level>\n" +
                    "</rule>\n";

    private static HttpURLConnection conn;

    public static void LogRuleUpdate(Rule oldRule,Rule newRule) {
        String response = BREClient.request(logRuleUpdateUrl,  String.format(ruleUpdateParam , oldRule.getId(),oldRule.getOwnerID(),oldRule.getQuery(),
                oldRule.getYesEdge(),oldRule.getNoEdge(),oldRule.getRelativeResults(),newRule.getId(),newRule.getOwnerID(),newRule.getQuery(),
                newRule.getYesEdge(),newRule.getNoEdge(),newRule.getRelativeResults()));
        response = response.toLowerCase();
        System.out.println(response);
    }

    public static void LogJobRule(Job job, Rule rule,LogLevel level) {
        String response;
        if(rule == null){
            response = BREClient.request(logJobDescUrl,  String.format(jobDescParam,job.getId(),job.getOwner(),job.getDescription(),
                    job.getDestination(),job.getFileUrl(),job.getRelatives(),job.getStatus(),job.getRuleId(),
                    job.getInsertDateTime_Date().toString(),job.getUpdateDateTime_Date().toString(),"Job with no rule",level.name()));
        }else{
            response = BREClient.request(logJobRuleUrl,  String.format(jobRuleParam,job.getId(),job.getOwner(),job.getDescription(),
                    job.getDestination(),job.getFileUrl(),job.getRelatives(),job.getStatus(),job.getRuleId(),
                    job.getInsertDateTime_Date().toString(),job.getUpdateDateTime_Date().toString() ,
                    rule.getId(),rule.getOwnerID(),rule.getQuery(),
                    rule.getYesEdge(),rule.getNoEdge(),rule.getRelativeResults(),level.name()));
        }

        response = response.toLowerCase();
        System.out.println(response);
    }

    public static void LogJobDesc(Job job, String description,LogLevel level) {
        String response = BREClient.request(logJobDescUrl,
                String.format(jobDescParam ,
                        job.getId(), job.getOwner(), job.getDescription(), job.getDestination(),
                        job.getFileUrl(), job.getRelatives(), job.getStatus(), job.getRuleId(),
                        job.getInsertDateTime(), job.getUpdateDateTime(), description,level.name()));
        response = response.toLowerCase();

        System.out.println(response);
    }

    public static void LogOrchDesc(Orchestration orchestration, String description,LogLevel level) {
        String response = BREClient.request(logOrchDescUrl,
                String.format(orchDescParam ,
                        orchestration.getId(), orchestration.getOwnerID(), orchestration.getStatus(),
                        orchestration.getStartJobID(), orchestration.getInsertDateTime(),
                        orchestration.getUpdateDateTime(), description,level.name()));
        response = response.toLowerCase();

        System.out.println(response);
    }

    public static void LogOrch(OrchestrationCapsule orchestration,LogLevel level) {
        String jobFormat = "";
        for(Job job : orchestration.getJobs()){
            jobFormat += String.format(jobParam,job.getId(), job.getOwner(), job.getDescription(), job.getDestination(),
                    job.getFileUrl(), job.getRelatives(), job.getStatus(), job.getRuleId(),
                    job.getInsertDateTime(), job.getUpdateDateTime(),level.name());
        }

        String ruleFormat = "";
        for(Rule rule : orchestration.getRules()){
            ruleFormat += String.format(ruleParam,rule.getId(),rule.getOwnerID(),rule.getQuery(),
                    rule.getYesEdge(),rule.getNoEdge(),rule.getRelativeResults(),level.name());
        }
        String format = String.format(orchParam,orchestration.getId(), orchestration.getOwnerID(), orchestration.getStatus(),
                orchestration.getStartJobID(), dateFormat.format(orchestration.getInsertDateTime()),
                dateFormat.format(orchestration.getUpdateDateTime()),jobFormat,ruleFormat,level.name());
        String response = BREClient.request(logOrchUrl,format);
        System.out.println(format);
        response = response.toLowerCase();

        System.out.println(response);
    }

    public static void LogDesc(String description,int user_id,LogLevel level) {
        String response = BREClient.request(logDescUrl,
                String.format(descParam,
                        user_id,description,level));
        response = response.toLowerCase();

        System.out.println(response);
    }
}
