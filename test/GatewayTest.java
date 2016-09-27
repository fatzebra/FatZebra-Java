import au.com.fatzebra.javalib.FatZebra;
import au.com.fatzebra.javalib.GatewayContext;
import au.com.fatzebra.javalib.errors.APIError;
import au.com.fatzebra.javalib.errors.NetworkError;
import au.com.fatzebra.javalib.models.Purchase;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;


public class GatewayTest {
    @Test
    public void testInitializeNewInstance() {
        GatewayContext ctx = new GatewayContext();
        ctx.username = "TEST";
        ctx.token = "TEST";
        ctx.sandbox = true;

        int cardExpiryMonth = 9;
        int cardExpiryYear = 2017;

        HashMap<String,Object> card_data = new HashMap<String, Object>();
        card_data.put("card_number", "5123456789012346");
        card_data.put("card_holder", "James Smith");
        card_data.put("card_expiry", String.format("%d/%d", cardExpiryMonth, cardExpiryYear));
        card_data.put("cvv", "591");

        HashMap<String,String> extraParams = new HashMap<String, String>();
        extraParams.put("ecm", "22"); // 2 = MOTO & 2 = Recurring

        try {
            Purchase p = Purchase.create(
                    1, // The amount, as an integer. i.e. $150.75 would be 15075
                    card_data, // A HashMap of the card data we defined above
                    "FZJAVATEST004", // Your order reference, such as an invoice number
                    "1.2.3.4", // The customers IP address
                    "AUD",
                    true,
                    extraParams,
                    ctx
            );

            System.out.println("Result: " + p.toString());
        } catch(IOException ex) {
            System.err.println("IOException in purchase request.");
            ex.printStackTrace();
        } catch(APIError ex) {
            System.err.println("API Error in purchase request.");
            ex.printStackTrace();
        } catch(NetworkError ex) {
            System.err.println("Network Error in purchase request.");
            ex.printStackTrace();
        }
    }


}
