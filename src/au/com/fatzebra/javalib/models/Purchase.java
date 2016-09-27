package au.com.fatzebra.javalib.models;
import au.com.fatzebra.javalib.FatZebra;
import au.com.fatzebra.javalib.GatewayContext;
import au.com.fatzebra.javalib.errors.APIError;
import au.com.fatzebra.javalib.errors.NetworkError;
import au.com.fatzebra.javalib.net.Resource;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

/**
 * The Purchase model
 */
public class Purchase extends Resource {
    /**
     * The Fat Zebra ID
     */
    public String id;
    /**
     * The purchase amount
     */
    public int amount;
    /**
     * The purchase amount as a decimal
     */
    public double decimal_amount;
    /**
     * The total amount which has been captured
     */
    public int captured_total;
    /**
     * Indicates if a purchase has been captured or not
     */
    public boolean captured;
    /**
     * The authorization ID
     */
    public String authorization;
    /**
     * The card number
     */
    public String card_number;
    /**
     * The card holders name
     */
    public String card_holder;
    /**
     * The card expiry date
     */
    public Date card_expiry;
    /**
     * The card token
     */
    public String card_token;
    /**
     * Indicates if the transaction is successful
     */
    public boolean successful;
    /**
     * The message from the gateway for this transaction
     */
    public String message;
    /**
     * The reference for the transaction
     */
    public String reference;
    /**
     * The currency code for the transaction
     */
    public String currency;
    /**
     * The settlement date of the transaction
     */
    public Date settlement_date;
    /**
     * The date of the transaction
     */
    public Date transaction_date;
    /**
     * The Acquirer response code for the transaction
     */
    public String response_code;

    /**
     * The Request Retrieval Number
     */
    public String rrn;

    /**
     * The CVV match result
     */
    public String cvv_match;

    public Purchase() {}

    /**
     * Create a purchase with real-time capture
     * @param amount the amount to be charged (as an integer - i.e. $100.50 will be 10050)
     * @param card_data a HashMap<String,Object> of card data containing the card_expiry, card_number, card_holder, card_security_code
     * @param reference the order reference, usually an invoice or order number
     * @param ip the customers IP address
     * @param currency the currency code for the order (e.g. AUD, USD etc)
     * @return Purchase
     */
    public static Purchase create(int amount, HashMap<String, Object> card_data, String reference, String ip, String currency) throws IOException, NetworkError, APIError {
        return create(amount, card_data, reference, ip, currency, true);
    }

    /**
     * Create a purchase with the option of capture or real-time capture
     * @param amount the amount to be charged (as an integer - i.e. $100.50 will be 10050)
     * @param card_data a HashMap<String,Object> of card data containing the card_expiry, card_number, card_holder, card_security_code
     * @param reference the order reference, usually an invoice or order number
     * @param ip the customers IP address
     * @param currency the currency code for the order (e.g. AUD, USD etc)
     * @param capture indicates whether to capture this transaction immediately or not. If this is false the request will become a pre-auth and a capture will be required to settle the funds.
     * @return Purchase
     */
    public static Purchase create(int amount, HashMap<String, Object> card_data, String reference, String ip, String currency, boolean capture) throws IOException, NetworkError, APIError {
        return create(amount, card_data, reference, ip, currency, capture, null, FatZebra.getContext());
    }

    /**
     * Create a purchase with the option of capture or real-time capture
     * @param amount the amount to be charged (as an integer - i.e. $100.50 will be 10050)
     * @param card_data a HashMap<String,Object> of card data containing the card_expiry, card_number, card_holder, card_security_code
     * @param reference the order reference, usually an invoice or order number
     * @param ip the customers IP address
     * @param currency the currency code for the order (e.g. AUD, USD etc)
     * @param capture indicates whether to capture this transaction immediately or not. If this is false the request will become a pre-auth and a capture will be required to settle the funds.
     * @param ctx the gateway context (authentication etc)
     * @return Purchase
     */
    public static Purchase create(int amount, HashMap<String, Object> card_data, String reference, String ip, String currency, boolean capture, HashMap<String,String> extraParameters, GatewayContext ctx) throws IOException, NetworkError, APIError {
        // Create the purchase message
        PurchaseRequest request = new PurchaseRequest();
        request.setAmount(amount);
        request.setReference(reference);
        request.setCustomerIp(ip);
        request.setCapture(capture);
        request.setCurrency(currency);
        request.setCard(card_data);
        if (extraParameters != null) {
            request.setExtra(extraParameters);
        }

        FatZebraResponse<Purchase> response = doRequest("purchases", request, RequestType.POST, Purchase.class, ctx);
        return response.result;
    }

    /**
     * Finds a Purchase by the Purchase ID or the merchants reference
     * @param idOrReference the Fat Zebra ID or Reference for the record
     * @return Purchase
     */
    public static Purchase find(String idOrReference) throws IOException, NetworkError, APIError {
        return find(idOrReference, FatZebra.getContext());
    }

    /**
     * Finds a Purchase by the Purchase ID or the merchants reference
     * @param idOrReference the Fat Zebra ID or Reference for the record
     * @param ctx the gateway context (authentication etc)
     * @return Purchase
     */
    public static Purchase find(String idOrReference, GatewayContext ctx) throws IOException, NetworkError, APIError {
        FatZebraResponse<Purchase> response = doRequest(String.format("purchases/%s", idOrReference), null, RequestType.GET, Purchase.class, ctx);
        return response.result;
    }

    /**
     * Refunds the transaction for the amount specified
     * @param amount the amount to be refunded
     * @param reference the reference for the refund transaction
     * @return boolean indicating outcome
     */
    public boolean refund(int amount, String reference) throws IOException, NetworkError, APIError{
        return refund(amount, reference, FatZebra.getContext());
    }

    /**
     * Refunds the transaction for the amount specified
     * @param amount the amount to be refunded
     * @param reference the reference for the refund transaction
     * @param ctx the gateway context (authentication etc)
     * @return boolean indicating outcome
     */
    public boolean refund(int amount, String reference, GatewayContext ctx) throws IOException, NetworkError, APIError{
        Refund r = Refund.create(amount, this.id, reference, ctx);
        return r.successful;
    }

    /**
     * Performs a capture for a previously authorised, but not captured, transaction
     * Note a capture can only be performed up to three days after the original authorisation
     * @param amount the amount to capture - must be less then or equal to the original transaction amount
     * @return boolean indicating outcome
     */
    public boolean capture(int amount) throws IOException, NetworkError, APIError {
        return capture(amount, FatZebra.getContext());
    }

    /**
     * Performs a capture for a previously authorised, but not captured, transaction
     * Note a capture can only be performed up to three days after the original authorisation
     * @param amount the amount to capture - must be less then or equal to the original transaction amount
     * @param ctx the gateway context (authentication etc)
     * @return boolean indicating outcome
     */
    public boolean capture(int amount, GatewayContext ctx) throws IOException, NetworkError, APIError {
        CaptureRequest cr = CaptureRequest.create(amount, this.id, ctx);

        if(cr.successful) {
            this.captured_total = amount;
            this.captured = true;
        }

        return cr.successful;
    }
}
