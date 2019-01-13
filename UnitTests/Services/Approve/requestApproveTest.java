package Services.Approve;

import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;

import static org.junit.Assert.*;

public class requestApproveTest {

    private JAXBContext context;
    private requestApprove unmarshalled;

    // Initializes the context that its marshaller is going to be created.
    // marshal method is used for writing the context into an XML file format.
    // unmarshal method is used for writing XML file back into requestApprove object so that taking requestApprove object
    // as parameter can be simulated.
    @Before
    public void init() throws JAXBException {
        this.context = JAXBContext.newInstance(requestApprove.class);
        Marshaller marshaller = this.context.createMarshaller();
        requestApprove ra = new requestApprove("true", 12, 321);
        marshaller.marshal(ra, new File("reqApproveTest.xml"));
        Unmarshaller unmarshaller = this.context.createUnmarshaller();
        unmarshalled = (requestApprove) unmarshaller.unmarshal(new File("reqApproveTest.xml"));
    }


    @Test
    public void getUserApprove() throws JAXBException {
        init();
        assertEquals("true", this.unmarshalled.getUserApprove());
    }


    @Test
    public void getJobId() throws JAXBException {
        init();
        assertEquals(12, unmarshalled.getJobId());
    }


    @Test
    public void getRelativeId() throws JAXBException {
        init();
        assertEquals(321, unmarshalled.getRelativeId());
    }
}