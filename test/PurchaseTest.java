package au.com.fatzebra.javalib.models;

import au.com.fatzebra.javalib.FatZebra;
import au.com.fatzebra.javalib.GatewayContext;
import au.com.fatzebra.javalib.errors.APIError;
import au.com.fatzebra.javalib.errors.NetworkError;
import au.com.fatzebra.javalib.models.Purchase;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by broc on 3/09/2014.
 */
public class PurchaseTest {

    private String payload = "{\"successful\": true,    \"response\": {        \"authorization\": 0,        \"id\": \"369-P-89IY10K7\",        \"card_number\": \"5523509999995094\",        \"card_holder\": \"NA\",        \"card_expiry\": \"2017-01-31\",        \"card_token\": \"lqvubzsr\",        \"amount\": 1,        \"decimal_amount\": 0.01,        \"successful\": false,        \"message\": \"Timeout\",        \"reference\": \"1234-140902092649405\",        \"currency\": \"AUD\",        \"transaction_id\": \"369-P-89IY10K7\",        \"settlement_date\": null,        \"transaction_date\": \"2014-09-02T11:25:53+10:00\",        \"response_code\": \"01\",        \"captured\": true,        \"captured_amount\": 1,        \"rrn\": null,        \"cvv_match\": \"U\"    },    \"errors\": [],    \"test\": false}";
    @Test
    public void testParseInvalid() {

//        JsonParser parser = new JsonParser();
//        JsonObject responsePayload = (JsonObject) parser.parse(payload);
//        JsonElement wrapper = responsePayload.get("response");
//
////        System.out.println(wrapper.toString());
//
//        Purchase x = new GsonBuilder().setDateFormat("yyyy-MM-dd").create().fromJson(wrapper, Purchase.class);

        FatZebraResponse<Purchase> r = new FatZebraResponse<Purchase>(200, payload, null);
        r.parseResult(Purchase.class);
        Purchase x = r.result;
        System.out.println(x.toString());
//        FatZebraResponse<Purchase> p = new FatZebraResponse<Purchase>(200, payload, null);
//
//        Assert.assertEquals(p.result.message, "Test");

    }

}
