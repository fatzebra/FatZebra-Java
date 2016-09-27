package au.com.fatzebra.javalib.models;

import au.com.fatzebra.javalib.errors.APIError;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

/**
 * The purchase request which is sent to the gateway
 */
public class PurchaseRequest {
    private final String[] PERMITTED_EXTRA_KEYS = {
            "xid",
            "cavv",
            "sli",
            "ecm",
            "ver",
            "par"
    };

    /**
     * The amount for the purchase
     */
    @Expose public int amount;
    /**
     * The reference for the transaction
     */
    @Expose public String reference;
    /**
     * The card holders name
     */
    @Expose public String card_holder;
    /**
     * The card number
     */
    @Expose public String card_number;
    /**
     * The card security code/cvv
     */
    @Expose public String cvv;
    /**
     * The card expiry date (mm/yyyy format)
     */
    @Expose public String card_expiry;
    /**
     * Indicates whether the transaction should be captured, or only authorised
     */
    @Expose public boolean capture = true;
    /**
     * The transaction currency
     */
    @Expose public String currency = "AUD";
    /**
     * The customers IP address
     */
    @Expose public String customer_ip;

    /**
     * Extra parameters (sli, ecm, cavv and xid)
     */
    @Expose public HashMap<String, String> extra;

    public PurchaseRequest() {
    }

    public PurchaseRequest(HashMap<String, Object> card_data) {
        this.setCard(card_data);
    }

    /**
     * Sets the transaction amount
     *
     * @param amount the amount
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * Sets the transaction reference
     *
     * @param reference the reference
     */
    public void setReference(String reference) {
        this.reference = reference;
    }

    /**
     * Sets the capture flag
     *
     * @param capture flag value
     */
    public void setCapture(boolean capture) {
        this.capture = capture;
    }

    /**
     * Sets the transaction currency
     *
     * @param currency transaction currency code
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * Sets the customers IP address
     *
     * @param ip the IP address
     */
    public void setCustomerIp(String ip) {
        this.customer_ip = ip;
    }

    /**
     * Sets the extra parameters for the transaction
     * Note: these parameters will only be used if the merchant is configured to support them
     *
     * @param val
     */
    public void setExtra(HashMap<String, String> val) throws APIError {
        // Validate first

        Iterator<String> it = val.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            if(!Arrays.asList(PERMITTED_EXTRA_KEYS).contains(key)) {
                String[] messages = {String.format("Extra parameter key %s is not supported", key)};
                throw new APIError(Arrays.asList(messages));
            }
        }
        this.extra = val;
    }

    /**
     * Sets the card details
     *
     * @param card_data HashMap<String,Object> of card details
     */
    public void setCard(HashMap<String, Object> card_data) {
        this.card_holder = (String) card_data.get("card_holder");
        this.card_number = (String) card_data.get("card_number");
        this.cvv = (String) card_data.get("cvv");
        this.card_expiry = (String) card_data.get("card_expiry");
    }

    /**
     * Renders the request as a JSON string
     *
     * @return JSON string
     */
    public String toJsonString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
