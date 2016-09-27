package au.com.fatzebra.javalib;

/**
 * Provides context (authentication credentials etc) for connection to the API
 */
public class GatewayContext {
    public String username = "TEST";
    public String token = "TEST";
    public boolean sandbox = true;
    public String live_url = "gateway.fatzebra.com.au";
    public String sandbox_url = "gateway.sandbox.fatzebra.com.au";

    public GatewayContext(String username, String token, boolean sandbox) {
        this.username = username;
        this.token = token;
        this.sandbox = sandbox;
    }

    public GatewayContext() {
    }
}
