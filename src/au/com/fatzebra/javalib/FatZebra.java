package au.com.fatzebra.javalib;

/**
 * Represents the static/singleton Fat Zebra configuration
 */
public class FatZebra {
    /**
     * The Library version
     */
    public static final String VERSION = "1.6";

    /**
     * The authentication username for the gateway
     */
    public static String username   = "TEST";

    /**
     * The authentication token for the gateway
     */
    public static String token      = "TEST";

    /**
     * Indicates whether to use the Sandbox or not
     */
    public static boolean sandbox   = true;

    /**
     * The sandbox URL for the gateway
     */
    public static final String SANDBOX_URL  = "https://sandbox-gateway.cloudpayments.com.au/v1.0/";

    /**
     * The live URL for the gateway
     */
    public static final String LIVE_URL     = "https://gateway.fatzebra.com.au/v1.0/";

    public static int timeout = 60;

    /**
     * Builds the gateway URL using the context provided
     * @param suffix the suffix to be appended
     * @param ctx the context for the transaction
     * @return the build gateway URL
     */
    public static String getGatewayUrl(String suffix, GatewayContext ctx) {
        if (ctx.sandbox) {
            return SANDBOX_URL + suffix;
        } else {
            return LIVE_URL + suffix;
        }
    }

    /**
     * Builds the gateway URL using the default context
     * @param suffix the suffix to be appended
     * @return the build gateway URL
     */
    public static String getGatewayUrl(String suffix) {
        return getGatewayUrl(suffix, FatZebra.getContext());
    }

    /**
     * Provides a gateway context from the static/singleton FatZebra object
     * @return build context
     */
    public static GatewayContext getContext() {
        GatewayContext ctx = new GatewayContext();
        ctx.username = FatZebra.username;
        ctx.token    = FatZebra.token;
        ctx.sandbox  = FatZebra.sandbox;

        return ctx;
    }
}