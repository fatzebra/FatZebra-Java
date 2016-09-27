package au.com.fatzebra.javalib.models;

import com.google.gson.*;

import java.util.List;
import java.util.Map;

/**
 * The Fat Zebra response wrapper
 * @param <T> the type (Purchase, Refund etc) of the response
 */
public class FatZebraResponse<T> {
    /**
     * The response Code (HTTP status code)
     */
    public int responseCode;
    /**
     * The response body
     */
    public String responseBody;
    /**
     * The response headers
     */
    public Map<String, List<String>> headers;
    /**
     * indicates the response being successful
     */
    public boolean successful;
    /**
     * Any errors for the request
     */
    public List<String> errors;
    /**
     * The result T of the transaction
     */
    public T result;
    /**
     * Indicates if the transaction was test or not
     */
    public boolean test;

    /**
     * Initialises a new response
     * @param rCode the HTTP response code
     * @param rBody the response body/content
     * @param hdrs the response headers
     */
    public FatZebraResponse(int rCode, String rBody, Map<String, List<String>> hdrs) {
        this.responseCode = rCode;
        this.responseBody = rBody;
        this.headers = hdrs;
    }

    /**
     * Parses the response into the class T provided in clazz
     * @param clazz the class for the receiving object
     */
    public void parseResult(Class<T> clazz) {
        JsonParser parser = new JsonParser();
        JsonObject responsePayload = (JsonObject) parser.parse(this.responseBody);
        JsonElement wrapper = responsePayload.get("response");
        this.result = new GsonBuilder().setDateFormat("yyyy-MM-dd").create().fromJson(wrapper, clazz);

        FatZebraResponse r = new Gson().fromJson(this.responseBody, this.getClass());
        this.successful = r.successful;
        this.errors = r.errors;
    }
}
