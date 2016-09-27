package au.com.fatzebra.javalib.models;

import au.com.fatzebra.javalib.FatZebra;
import au.com.fatzebra.javalib.GatewayContext;
import au.com.fatzebra.javalib.errors.APIError;
import au.com.fatzebra.javalib.errors.NetworkError;
import au.com.fatzebra.javalib.net.Resource;
import com.google.gson.annotations.Expose;

import java.io.IOException;

/**
 * Represents a capture request to the gateway
 */
public class CaptureRequest extends Resource {
    /**
     * The amount of the capture
     */
    @Expose public int amount;

    /**
     * The ID for the authorisation
     */
    public String id;

    /**
     * Indicates a successful capture
     */
    public boolean successful;

    /**
     * Performs a capture request
     * @param amount the amount of the capture
     * @param transactionId the authorisation transaction ID
     * @return CaptureRequest with result etc
     * @throws IOException
     * @throws NetworkError
     * @throws APIError
     */
    public static CaptureRequest create(int amount, String transactionId) throws IOException, NetworkError, APIError {
        return create(amount, transactionId, FatZebra.getContext());
    }

    /**
     * Performs a capture request
     * @param amount the amount of the capture
     * @param transactionId the authorisation transaction ID
     * @param ctx the gateway context for authentication
     * @return CaptureRequest with result etc
     * @throws IOException
     * @throws NetworkError
     * @throws APIError
     */
    public static CaptureRequest create(int amount, String transactionId, GatewayContext ctx) throws IOException, NetworkError, APIError {
        CaptureRequest request = new CaptureRequest();
        request.setAmount(amount);
        request.setTransactionId(transactionId);

        FatZebraResponse<CaptureRequest> response = doRequest(String.format("purchases/%s/capture", transactionId), request, RequestType.POST, CaptureRequest.class, ctx);
        request.successful = response.result.successful;
        return response.result;
    }

    /**
     * Sets the amount of the capture
     * @param value the amount of the capture
     */
    public void setAmount(int value) {
        this.amount = value;
    }

    /**
     * Sets the transaction ID of the authorisation
     * @param value the transaction ID
     */
    public void setTransactionId(String value) {
        this.id = value;
    }
}
