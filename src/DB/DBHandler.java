package DB;

import Services.InfoService.OrchestrationCapsule;
import Services.InfoService.RulesAndJobs;
import com.mysql.jdbc.JDBC4PreparedStatement;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class DBHandler {

    //private String dbUrl = "jdbc:mysql://localhost:3306/biztalk?useUnicode=true&characterEncoding=utf-8";
    //< private String dbUrl = "jdbc:mysql://51.158.72.164:3306/biztalk?useUnicode=true&characterEncoding=utf-8&useSSL=false";
    String dbUrl = "jdbc:mysql://10.1.90.19:3306/biztalkdb";
    private String userName = "root";
    //private String password = "dd6dfe6b993b05f305b8ac3d6773cebd7bd7af9f";

    private String password = "";
    private String driver = "com.mysql.jdbc.Driver";
    private static Connection dbConnection;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public DBHandler() {
        dbConnection = createConnection();
    }


    public Connection createConnection() {

        Connection con = null;
        try {
            Class.forName(driver);

            con = DriverManager.getConnection(dbUrl, userName, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }

    public Connection getConnection(){
        return dbConnection;
    }

    public void closeConnection(Connection conn) throws SQLException {
//        if (conn != null)
//            conn.close();
    }

    public void closePreparedStatement(PreparedStatement pst) throws SQLException {
        if (pst != null)
            pst.close();
    }

    public void closeResultSet(ResultSet rs) throws Exception {
        if (rs != null)
            rs.close();
    }

    public Job getJob(int jobID) throws Exception {

        Connection conn = getConnection();
        Job job = new Job();
        PreparedStatement preparedStmt = conn.prepareStatement("SELECT * FROM jobs WHERE JobId=?");

        preparedStmt.setInt(1, jobID);
        ResultSet rs = preparedStmt.executeQuery();

        if (rs.next()) {
            job.setId(rs.getInt("JobId"));
            job.setOwner(rs.getInt("JobOwner"));
            job.setDescription(rs.getString("Description"));
            job.setDestination(rs.getString("Destination"));
            job.setFileUrl(rs.getString("FileUrl"));
            job.setRelatives(rs.getString("Relatives"));
            job.setStatus(rs.getInt("Status"));
            job.setRuleId(rs.getInt("RuleId"));
            job.setInsertDateTime(rs.getString("InsertDateTime"));
            job.setUpdateDateTime(rs.getString("UpdateDateTime"));
            job.setOrchFlag(rs.getInt("OrchFlag"));
        }

        closePreparedStatement(preparedStmt);
        closeResultSet(rs);
        closeConnection(conn);

        return job;

    }

    public ArrayList<Job> getAllJobs() throws Exception {

        Connection conn = getConnection();
        ArrayList<Job> jobs = new ArrayList<>();

        PreparedStatement preparedStmt = conn.prepareStatement("SELECT * FROM jobs");
        ResultSet rs = preparedStmt.executeQuery();

        while (rs.next()) {
            Job job = new Job();
            job.setId(rs.getInt("JobId"));
            job.setOwner(rs.getInt("JobOwner"));
            job.setDestination(rs.getString("Destination"));
            job.setFileUrl(rs.getString("FileUrl"));
            job.setRelatives(rs.getString("Relatives"));
            job.setStatus(rs.getInt("Status"));
            job.setRuleId(rs.getInt("RuleId"));
            job.setInsertDateTime(rs.getString("InsertDateTime"));
            job.setUpdateDateTime(rs.getString("UpdateDateTime"));
            job.setDescription(rs.getString("Description"));
            job.setOrchFlag(rs.getInt("OrchFlag"));
            jobs.add(job);
        }

        closePreparedStatement(preparedStmt);
        closeResultSet(rs);
        closeConnection(conn);

        return jobs;
    }

    public ArrayList<Job> getJobSet(int JobOwner) throws Exception {

        Connection conn = getConnection();
        ArrayList<Job> jobs = new ArrayList<>();

        PreparedStatement preparedStmt = conn.prepareStatement("SELECT * FROM jobs WHERE JobOwner=?");

        preparedStmt.setInt(1, JobOwner);
        ResultSet rs = preparedStmt.executeQuery();

        while (rs.next()) {
            Job job = new Job();
            job.setId(rs.getInt("JobId"));
            job.setOwner(rs.getInt("JobOwner"));
            job.setDestination(rs.getString("Destination"));
            job.setFileUrl(rs.getString("FileUrl"));
            job.setRelatives(rs.getString("Relatives"));
            job.setStatus(rs.getInt("Status"));
            job.setRuleId(rs.getInt("RuleId"));
            job.setInsertDateTime(rs.getString("InsertDateTime"));
            job.setUpdateDateTime(rs.getString("UpdateDateTime"));
            job.setDescription(rs.getString("Description"));
            job.setOrchFlag(rs.getInt("OrchFlag"));
            jobs.add(job);
        }

        closePreparedStatement(preparedStmt);
        closeResultSet(rs);
        closeConnection(conn);

        return jobs;
    }

    public ArrayList<Job> getJobs() throws Exception {

        Connection conn = getConnection();
        ArrayList<Job> jobs = new ArrayList<>();

        PreparedStatement preparedStmt = conn.prepareStatement("SELECT * FROM jobs WHERE Status = -1 ORDER BY InsertDateTime");

        ResultSet rs = preparedStmt.executeQuery();

        while (rs.next()) {
            Job job = new Job();
            job.setId(rs.getInt("JobId"));
            job.setOwner(rs.getInt("JobOwner"));
            job.setDestination(rs.getString("Destination"));
            job.setFileUrl(rs.getString("FileUrl"));
            job.setRelatives(rs.getString("Relatives"));
            job.setStatus(rs.getInt("Status"));
            job.setRuleId(rs.getInt("RuleId"));
            job.setInsertDateTime(rs.getString("InsertDateTime"));
            job.setUpdateDateTime(rs.getString("UpdateDateTime"));
            job.setDescription(rs.getString("Description"));
            job.setOrchFlag(rs.getInt("OrchFlag"));

            jobs.add(job);
        }

        closePreparedStatement(preparedStmt);
        closeResultSet(rs);
        closeConnection(conn);

        return jobs;
    }


    public int insertJob(Job job) throws Exception {

        Connection conn = getConnection();
        PreparedStatement preparedStmt = conn.prepareStatement(" INSERT INTO jobs ( JobOwner, Description, " +
                "Destination, FileUrl, Relatives,Status, RuleId, InsertDateTime, UpdateDateTime, OrchFlag)"
                + " values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        preparedStmt.setInt(1, job.getOwner());
        preparedStmt.setString(2, job.getDescription());
        preparedStmt.setString(3, job.getDestination());
        preparedStmt.setString(4, job.getFileUrl());
        preparedStmt.setString(5, job.getRelatives());
        preparedStmt.setInt(6, job.getStatus());
        preparedStmt.setInt(7, job.getRuleId());
        preparedStmt.setString(8, job.getInsertDateTime());
        preparedStmt.setString(9, job.getUpdateDateTime());
        preparedStmt.setInt(10, job.getOrchFlag());

        String query = ((JDBC4PreparedStatement) preparedStmt).asSql();
        preparedStmt.executeUpdate(query, RETURN_GENERATED_KEYS);
        ResultSet rs = preparedStmt.getGeneratedKeys();

        int result = -1;

        if (rs.next())
            result = rs.getInt(1);

        closePreparedStatement(preparedStmt);
        closeConnection(conn);
        return result;
    }

    public boolean updateJob(int JobId, String columnName, int value) throws Exception {

        Connection conn = getConnection();
        String sql = "UPDATE jobs SET " + columnName + " = " + value + " WHERE JobId = " + JobId;
        PreparedStatement preparedStmt = conn.prepareStatement(sql);


        boolean bool = preparedStmt.execute();
        updateJob(JobId, new Date());
        closePreparedStatement(preparedStmt);
        closeConnection(conn);

        return bool;
    }

    public boolean updateJob(int JobId, String columnName, String value) throws Exception {

        Connection conn = getConnection();
        String sql = "UPDATE jobs SET " + columnName + " = " + "'" + value + "'" + " WHERE JobId = " + JobId;
        PreparedStatement preparedStmt = conn.prepareStatement(sql);

        boolean bool = preparedStmt.execute();
        updateJob(JobId, new Date());
        closePreparedStatement(preparedStmt);
        closeConnection(conn);

        return bool;
    }

    public boolean updateJob(int JobId, Date value) throws Exception {

        String date = dateFormat.format(value);

        Connection conn = getConnection();
        PreparedStatement preparedStmt = conn.prepareStatement("UPDATE jobs SET  UpdateDateTime = ? WHERE JobId = ?");

        preparedStmt.setString(1, date);
        preparedStmt.setInt(2, JobId);


        boolean bool = preparedStmt.execute();
        closePreparedStatement(preparedStmt);
        closeConnection(conn);

        return bool;
    }


    public Rule getRule(int ruleID) throws Exception {

        Connection conn = getConnection();
        Rule rule = new Rule();
        PreparedStatement preparedStmt = conn.prepareStatement("SELECT * FROM rules WHERE RuleId=?");

        preparedStmt.setInt(1, ruleID);
        ResultSet rs = preparedStmt.executeQuery();

        if (rs.next()) {
            rule.setId(rs.getInt("RuleId"));
            rule.setOwnerID(rs.getInt("RuleOwner"));
            rule.setQuery(rs.getString("RuleQuery"));
            rule.setYesEdge(rs.getInt("YesEdge"));
            rule.setNoEdge(rs.getInt("NoEdge"));
            rule.setRelativeResults(rs.getString("RelativeResult"));
        }


        closePreparedStatement(preparedStmt);
        closeResultSet(rs);
        closeConnection(conn);

        return rule;

    }

    public boolean updateRule(int ruleID, String columnName, int value) throws Exception {

        Connection conn = getConnection();

        String sql = "UPDATE rules SET " + columnName + " = " + value + " WHERE RuleId = " + ruleID;

        PreparedStatement preparedStmt = conn.prepareStatement(sql);

        boolean result = preparedStmt.execute();
        closePreparedStatement(preparedStmt);
        closeConnection(conn);

        return result;
    }

    public boolean updateRule(int ruleID, String columnName, String value) throws Exception {

        Connection conn = getConnection();

        String sql = "UPDATE rules SET " + columnName + " = " + "'" + value + "'" + " WHERE RuleId = " + ruleID;
        PreparedStatement preparedStmt = conn.prepareStatement(sql);

        boolean result = preparedStmt.execute();
        closePreparedStatement(preparedStmt);
        closeConnection(conn);

        return result;
    }


    public int insertRule(Rule rule) throws Exception {

        Connection conn = getConnection();
        PreparedStatement preparedStmt = conn.prepareStatement(" INSERT INTO rules " +
                "(RuleOwner, RuleQuery, YesEdge, NoEdge,RelativeResult)" +
                " values (?, ?, ?, ?, ?)");


        preparedStmt.setInt(1, rule.getOwnerID());
        preparedStmt.setString(2, rule.getQuery());
        preparedStmt.setInt(3, rule.getYesEdge());
        preparedStmt.setInt(4, rule.getNoEdge());
        preparedStmt.setString(5, rule.getRelativeResults());

        String query = ((JDBC4PreparedStatement) preparedStmt).asSql();
        preparedStmt.executeUpdate(query, RETURN_GENERATED_KEYS);
        ResultSet rs = preparedStmt.getGeneratedKeys();

        int result = -1;

        if (rs.next())
            result = rs.getInt(1);

        closePreparedStatement(preparedStmt);
        closeResultSet(rs);
        closeConnection(conn);

        return result;
    }


    public Orchestration getOrchestration() throws Exception {

        Connection conn = getConnection();
        Orchestration orchestration = new Orchestration();
        PreparedStatement preparedStmt = conn.prepareStatement("SELECT * FROM orchestrations WHERE Status = 0");

        ResultSet rs = preparedStmt.executeQuery();

        if (rs.next()) {
            OrchestrationHelper(orchestration, rs);
        }

        closePreparedStatement(preparedStmt);
        closeResultSet(rs);
        closeConnection(conn);

        return orchestration;
    }

    public Job getJob() throws Exception {

        Connection conn = getConnection();
        Job job = new Job();
        PreparedStatement preparedStmt = conn.prepareStatement("SELECT * FROM jobs WHERE Status = -1 ORDER BY InsertDateTime");

        ResultSet rs = preparedStmt.executeQuery();

        if (rs.next()) {
            job.setId(rs.getInt("JobId"));
            job.setOwner(rs.getInt("JobOwner"));
            job.setDescription(rs.getString("Description"));
            job.setDestination(rs.getString("Destination"));
            job.setFileUrl(rs.getString("FileUrl"));
            job.setRelatives(rs.getString("Relatives"));
            job.setStatus(rs.getInt("Status"));
            job.setRuleId(rs.getInt("RuleId"));
            job.setInsertDateTime(rs.getString("InsertDateTime"));
            job.setUpdateDateTime(rs.getString("UpdateDateTime"));
            job.setOrchFlag(rs.getInt("OrchestrationId"));
        }

        closePreparedStatement(preparedStmt);
        closeResultSet(rs);
        closeConnection(conn);

        return job;
    }

    public ArrayList<Orchestration> getOrchestration(int OrchestrationOwner) throws Exception {

        Connection conn = getConnection();
        ArrayList<Orchestration> orchestrations = new ArrayList<>();

        PreparedStatement preparedStmt = conn.prepareStatement("SELECT * FROM orchestrations WHERE OrchestrationOwner=?");

        preparedStmt.setInt(1, OrchestrationOwner);
        ResultSet rs = preparedStmt.executeQuery();

        while (rs.next()) {
            Orchestration orchestration = new Orchestration();
            OrchestrationHelper(orchestration, rs);
            orchestrations.add(orchestration);
        }

        closePreparedStatement(preparedStmt);
        closeResultSet(rs);
        closeConnection(conn);

        return orchestrations;
    }

    public ArrayList<Orchestration> getOrchestrations() throws Exception {

        Connection conn = getConnection();
        ArrayList<Orchestration> orchestrations = new ArrayList<>();

        PreparedStatement preparedStmt = conn.prepareStatement("SELECT * FROM orchestrations WHERE Status = 0 ORDER BY InsertDateTime");

        ResultSet rs = preparedStmt.executeQuery();

        while (rs.next()) {
            Orchestration orchestration = new Orchestration();
            OrchestrationHelper(orchestration, rs);
            orchestrations.add(orchestration);
        }

        closePreparedStatement(preparedStmt);
        closeResultSet(rs);
        closeConnection(conn);

        return orchestrations;
    }

    public int insertOrchestration(Orchestration orchestration) throws Exception {

        int result = -1;
        Connection conn = getConnection();
        PreparedStatement preparedStmt = conn.prepareStatement(" INSERT INTO orchestrations " +
                "(OrchestrationOwner, Status, StartingJobId, InsertDateTime,UpdateDateTime)" +
                " values (?, ?, ?, ?, ?)");

        preparedStmt.setInt(1, orchestration.getOwnerID());
        preparedStmt.setInt(2, orchestration.getStatus());
        preparedStmt.setInt(3, orchestration.getStartJobID());
        preparedStmt.setString(4, orchestration.getInsertDateTime());
        preparedStmt.setString(5, orchestration.getUpdateDateTime());

        String query = ((JDBC4PreparedStatement) preparedStmt).asSql();
        preparedStmt.executeUpdate(query, RETURN_GENERATED_KEYS);
        ResultSet rs = preparedStmt.getGeneratedKeys();

        if (rs.next())
            result = rs.getInt(1);

        closeResultSet(rs);
        closePreparedStatement(preparedStmt);
        closeConnection(conn);

        return result;
    }


    public boolean updateOrchestration(int orchestrationid, String columnname, int value) throws Exception {

        boolean result;
        Connection conn = getConnection();
        String sql = "UPDATE orchestrations SET " + columnname + " = " + value + " WHERE OrchestrationId = " + orchestrationid;

        PreparedStatement preparedStmt = conn.prepareStatement(sql);

        result = preparedStmt.execute();
        updateOrchestration(orchestrationid, new Date());

        closePreparedStatement(preparedStmt);
        closeConnection(conn);

        return result;
    }


    public boolean updateOrchestration(int orchestrationid, Date value) throws Exception {
        boolean result;

        Connection conn = getConnection();
        String date = dateFormat.format(value);

        PreparedStatement preparedStmt = conn.prepareStatement("UPDATE orchestrations SET UpdateDateTime = ?" +
                "WHERE OrchestrationId = ?");
        preparedStmt.setString(1, date);
        preparedStmt.setInt(2, orchestrationid);

        result = preparedStmt.execute();

        closePreparedStatement(preparedStmt);
        closeConnection(conn);

        return result;
    }

    private void OrchestrationHelper(Orchestration orchestration, ResultSet rs) throws SQLException, ParseException {
        orchestration.setId(rs.getInt("OrchestrationId"));
        orchestration.setOwnerID(rs.getInt("OrchestrationOwner"));
        orchestration.setStatus(rs.getInt("Status"));
        orchestration.setStartJobID(rs.getInt("StartingJobId"));
        orchestration.setInsertDateTime(rs.getString("InsertDateTime"));
        orchestration.setUpdateDateTime(rs.getString("UpdateDateTime"));
    }

    public OrchestrationCapsule getOrchestrationById(Orchestration orchestration) throws Exception {
        Connection conn = getConnection();
        PreparedStatement preparedStmt = conn.prepareStatement("SELECT * FROM jobs WHERE OrchFlag = ?");
        preparedStmt.setInt(1, orchestration.getId());
        ResultSet rs = preparedStmt.executeQuery();
        OrchestrationCapsule orchestrationCapsule = new OrchestrationCapsule();
        ArrayList<Job> jobList = new ArrayList<>();
        ArrayList<Rule> ruleList = new ArrayList<>();

        while (rs.next()) {
            Job job = new Job();

            // Create Job
            job.setId(rs.getInt("JobId"));
            job.setOwner(rs.getInt("JobOwner"));
            job.setDescription(rs.getString("Description"));
            job.setDestination(rs.getString("Destination"));
            job.setFileUrl(rs.getString("FileUrl"));
            job.setRelatives(rs.getString("Relatives"));
            job.setStatus(rs.getInt("Status"));
            job.setRuleId(rs.getInt("RuleId"));
            job.setInsertDateTime(rs.getString("InsertDateTime"));
            job.setUpdateDateTime(rs.getString("UpdateDateTime"));
            job.setOrchFlag(rs.getInt("OrchFlag"));
            jobList.add(job);

            ruleList.add(this.getRule(job.getRuleId()));
        }
        orchestrationCapsule.setId(orchestration.getId());
        orchestrationCapsule.setOwnerID(orchestration.getOwnerID());
        orchestrationCapsule.setStatus(orchestration.getStatus());
        orchestrationCapsule.setStartJobID(orchestration.getStartJobID());
        orchestrationCapsule.setInsertDateTime(orchestration.getInsertDateTime_Date());
        orchestrationCapsule.setUpdateDateTime(orchestration.getUpdateDateTime_Date());
        orchestrationCapsule.setJobs(jobList);
        orchestrationCapsule.setRules(ruleList);

        return orchestrationCapsule;
    }

    public ArrayList<Job> getUnorchestrainedJobs() throws Exception {
        Connection conn = getConnection();
        PreparedStatement preparedStmt = conn.prepareStatement("SELECT * FROM jobs WHERE OrchFlag = 0");
        ResultSet rs = preparedStmt.executeQuery();
        ArrayList<Job> jobList = new ArrayList<>();
        while (rs.next()) {
            Job job = new Job();
            job.setId(rs.getInt("JobId"));
            job.setOwner(rs.getInt("JobOwner"));
            job.setDescription(rs.getString("Description"));
            job.setDestination(rs.getString("Destination"));
            job.setFileUrl(rs.getString("FileUrl"));
            job.setRelatives(rs.getString("Relatives"));
            job.setStatus(rs.getInt("Status"));
            job.setRuleId(rs.getInt("RuleId"));
            job.setInsertDateTime(rs.getString("InsertDateTime"));
            job.setUpdateDateTime(rs.getString("UpdateDateTime"));
            job.setOrchFlag(0);
            jobList.add(job);
        }

        return jobList;
    }

    public void removeRule(int removedRuleId) throws Exception {
        Connection conn = getConnection();
        PreparedStatement preparedStmt = conn.prepareStatement("DELETE FROM rules WHERE RuleId = ?");
        preparedStmt.setInt(1, removedRuleId);
        preparedStmt.execute();

        closePreparedStatement(preparedStmt);
        closeConnection(conn);
    }

    public void updateJobsSuccesfully(int orchId, int statusCode) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement preparedStmt = conn.prepareStatement("UPDATE jobs SET Status=? WHERE OrchFlag = ? AND Status!=100");
        preparedStmt.setInt(1, statusCode);
        preparedStmt.setInt(2, orchId);
        preparedStmt.execute();
        closePreparedStatement(preparedStmt);
        closeConnection(conn);
    }
}