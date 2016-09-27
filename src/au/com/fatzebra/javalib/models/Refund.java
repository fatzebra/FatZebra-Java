package au.com.fatzebra.javalib.models;

import au.com.fatzebra.javalib.FatZebra;
import au.com.fatzebra.javalib.GatewayContext;
import au.com.fatzebra.javalib.errors.APIError;
import au.com.fatzebra.javalib.errors.NetworkError;
import au.com.fatzebra.javalib.net.Resource;

import java.io.IOException;
import java.util.Date;

/**
 * Represents a refund from the Gateway
 */
public class Refund extends Resource {
    /**
     * The gateway ID
     */
    public String id;
    /**
     * The reference for the transaction
     */
    public String reference;
    /**
     * The refunded amount
     */
    public int amount;
    /**
     * The authorization ID
     */
    public String authorization;
    /**
     * The transaction message
     */
    public String message;
    /**
     * The card holders name
     */
    public String card_holder;
    /**
     * The card number
     */
    public String card_number;
    /**
     * The card expiry date
     */
    public Date card_expiry;
    /**
     * The card type
     */
    public String card_type;
    /**
     * The transaction date
     */
    public Date transaction_date;
    /**
     * Indicates if the transaction was successful
     */
    public boolean successful;
    /**
     * The response code from the acquirer
     */
    public String response_code;

    /**
     * Refunds a transaction based on the original transaction ID
     * @param amount the refund amount
     * @param originalTransactionId the original transaction ID
     * @return Refund object representing result
     * @throws IOException
     * @throws NetworkError
     * @throws APIError
     */
    public static Refund create(int amount, String originalTransactionId) throws IOException, NetworkError, APIError {
        return create(amount, originalTransactionId, FatZebra.getContext());
    }

    /**
     * Refunds a transaction based on the original transaction ID
     * @param amount the refund amount
     * @param originalTransactionId the original transaction ID
     * @param ctx the gateway context for authentication
     * @return Refund object representing result
     * @throws IOException
     * @throws NetworkError
     * @throws APIError
     */
    public static Refund create(int amount, String originalTransactionId, GatewayContext ctx) throws IOException, NetworkError, APIError {
        Purchase p = Purchase.find(originalTransactionId, ctx);
        return create(amount, originalTransactionId, p.reference, ctx);
    }

    /**
     * Refunds a transaction based on the original transaction ID with a reference
     * @param amount the refund amount
     * @param originalTransactionId the original transaction ID
     * @param reference the refund reference
     * @return Refund object representing result
     * @throws IOException
     * @throws NetworkError
     * @throws APIError
     */
    public static Refund create(int amount, String originalTransactionId, String reference) throws IOException, NetworkError, APIError {
        return create(amount, originalTransactionId, reference, FatZebra.getContext());
    }

    /**
     * Refunds a transaction based on the original transaction ID with a reference
     * @param amount the refund amount
     * @param originalTransactionId the original transaction ID
     * @param reference the refund reference
     * @param ctx the gateway context for authentication
     * @return Refund object representing result
     * @throws IOException
     * @throws NetworkError
     * @throws APIError
     */
    public static Refund create(int amount, String originalTransactionId, String reference, GatewayContext ctx) throws IOException, NetworkError, APIError {
        RefundRequest request = new RefundRequest();
        request.setAmount(amount);
        request.setReference(reference);
        request.setOriginalTransactionId(originalTransactionId);

        FatZebraResponse<Refund> response = doRequest("refunds", request, RequestType.POST, Refund.class, ctx);
        return response.result;
    }


}
