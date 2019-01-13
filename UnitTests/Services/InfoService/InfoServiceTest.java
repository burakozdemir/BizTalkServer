package Services.InfoService;

import DB.DBHandler;
import DB.Job;
import DB.Rule;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class InfoServiceTest {

    @Test
    public void getJob() throws Exception {
        InfoService inf_ser = new InfoService();
        DBHandler db = new DBHandler();

        Job sample = new Job(3, "job1", "destination", "file url", "111, 222, 333", 1, 1);
        Job sample2 = new Job(4, "job2", "destination", "file url", "444, 555, 666", 1, 2);

        int id1 = db.insertJob(sample);
        int id2 = db.insertJob(sample2);

        sample.setId(id1);
        sample2.setId(id2);

        assertEquals(sample.toString(), inf_ser.getJob(id1).toString());
        assertEquals(sample2.toString(), inf_ser.getJob(id2).toString());
    }

    /*
    After testing, database needs to be emptied so that the jobs that are already written into database in previous test
    can't taken again for every test.
    */
    @Test
    public void getJobsFromOwner() throws Exception {
        InfoService inf_ser = new InfoService();
        DBHandler db = new DBHandler();
        ArrayList<String> sameOwnerJobs = new ArrayList<>();
        ArrayList<JobResponse> retVal = new ArrayList<>();

        Job sample = new Job(1, "job1", "destination", "file url", "111, 222, 333", 1, 1);
        Job sample2 = new Job(2, "job2", "destination", "file url", "444, 555, 666", 1, 2);
        Job sample3 = new Job(1, "job3", "destination", "file url", "777, 888, 999", 1, 3);
        Job sample4 = new Job(1, "job4", "destination", "file url", "1111, 2222, 3333", 1, 4);

        int id1 = db.insertJob(sample);
        int id2 = db.insertJob(sample2);
        int id3 = db.insertJob(sample3);
        int id4 = db.insertJob(sample4);

        sample.setId(id1);
        sample2.setId(id2);
        sample3.setId(id3);
        sample4.setId(id4);

        sameOwnerJobs.add(sample.toString());
        sameOwnerJobs.add(sample3.toString());
        sameOwnerJobs.add(sample4.toString());

        retVal = inf_ser.getJobsFromOwner(1);

        for (int i = 0; i < retVal.size(); ++i) {
            assertEquals(sameOwnerJobs.get(i), retVal.get(i).toString());
        }

    }

    @Test
    public void getOrchestration() {

    }

    @Test
    public void getRule() throws Exception {
        InfoService inf_ser = new InfoService();
        DBHandler db = new DBHandler();

        Rule sample = new Rule(1, "query", 1, 0, "111 222 333");
        Rule sample2 = new Rule(1, "query", 0, 1, "444 555 666");

        RuleResponse test1 = new RuleResponse();
        RuleResponse test2 = new RuleResponse();

        int id1 = db.insertRule(sample);
        int id2 = db.insertRule(sample2);

        sample.setId(id1);
        sample2.setId(id2);

        test1.setId(sample.getId());
        test1.setOwnerID(sample.getOwnerID());
        test1.setQuery(sample.getQuery());
        test1.setYesEdge(sample.getYesEdge());
        test1.setNoEdge(sample.getNoEdge());
        test1.setRelativeResults(sample.getRelativeResults());

        test2.setId(sample2.getId());
        test2.setOwnerID(sample2.getOwnerID());
        test2.setQuery(sample2.getQuery());
        test2.setYesEdge(sample2.getYesEdge());
        test2.setNoEdge(sample2.getNoEdge());
        test2.setRelativeResults(sample2.getRelativeResults());

        assertEquals(test1.toString(), inf_ser.getRule(id1).toString());
        assertEquals(test2.toString(), inf_ser.getRule(id2).toString());
    }


    @Test
    public void getJobsFromRelative() {
    }
}