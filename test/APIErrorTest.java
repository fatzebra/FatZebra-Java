import au.com.fatzebra.javalib.errors.APIError;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;


public class APIErrorTest {
    @Test
    public void testInitializeNewInstance() {
        String[] messages = {
                "Hello",
                "World"
        };

        APIError err = new APIError(Arrays.asList(messages));

        Assert.assertEquals(err.getMessages().toArray(), messages);
    }


}
