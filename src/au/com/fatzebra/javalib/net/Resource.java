package au.com.fatzebra.javalib.net;

import au.com.fatzebra.javalib.FZBase;

import au.com.fatzebra.javalib.FatZebra;
import au.com.fatzebra.javalib.GatewayContext;
import au.com.fatzebra.javalib.errors.APIError;
import au.com.fatzebra.javalib.errors.NetworkError;
import au.com.fatzebra.javalib.models.FatZebraResponse;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.codec.binary.Base64;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


/**
 * Provides the methods for accessing Fat Zebra resources via the API
 */
public abstract class Resource extends FZBase {
    public static final String CHARSET = "UTF-8";
    public static final String CONTENT_TYPE = "application/json";
    private static final String DNS_CACHE_TTL_PROPERTY_NAME = "networkaddress.cache.ttl";

    private static String originalDNSCacheTTL = null;
    private static boolean allowedToSetTTL = true;

    public static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setDateFormat("yyyy-MM-dd")
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    public static enum RequestType {
        GET,
        POST,
        DELETE
    }

    protected static String getResponseBody(InputStream responseStream) throws IOException {
        //\A denotes the start of the stream boundary
        Scanner s = new Scanner(responseStream, CHARSET);
        s.useDelimiter("\\A");

        String rBody = s.next(); //

        s.close();
        responseStream.close();
        return rBody;
    }

    /**
     * Encodes the username and token into a base64 value used for the Authorization header
     * The username and token must be set in the FatZebra class.
     *
     * @return String base64 encoded values joined with a colon (:)
     */
    private static String base64EncodedCredentials(GatewayContext ctx) {
        return Base64.encodeBase64String(String.format("%s:%s", ctx.username, ctx.token).getBytes());
    }

    /**
     * Builds the headers for the request including the auth header and the user agent
     *
     * @return Map<String,String> map of headers (key/value pairs)
     */
    private static Map<String, String> getHeaders(GatewayContext ctx) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Accept-Charset", CHARSET);

        headers.put("User-Agent",
                String.format("Fat Zebra v1 - Java %s", FatZebra.VERSION));

        headers.put("Authorization", String.format("Basic %s", base64EncodedCredentials(ctx)));

        // debug headers
        String[] propertyNames = {"os.name", "os.version", "os.arch",
                "java.version", "java.vendor", "java.vm.version",
                "java.vm.vendor"};
        Map<String, String> propertyMap = new HashMap<String, String>();
        for (String propertyName : propertyNames) {
            propertyMap.put(propertyName, System.getProperty(propertyName));
        }
        propertyMap.put("bindings.version", FatZebra.VERSION);
        propertyMap.put("lang", "Java");
        propertyMap.put("publisher", "Fat Zebra");
        headers.put("X-Client-User-Agent", GSON.toJson(propertyMap));
        return headers;
    }

    /**
     * Builds a HTTP URL Connection for the API endpoint and sets up the headers, timeout values etc required
     *
     * @return HttpsURLConnection the connection Object
     * @throws IOException
     */
    private static javax.net.ssl.HttpsURLConnection createApiConnection(String urlSuffix, GatewayContext ctx) throws IOException {
        URL gatewayUrl = new URL(FatZebra.getGatewayUrl(urlSuffix, ctx));
        javax.net.ssl.HttpsURLConnection conn = (javax.net.ssl.HttpsURLConnection) gatewayUrl.openConnection();
        conn.setConnectTimeout(FatZebra.timeout * 1000);
        conn.setReadTimeout(FatZebra.timeout * 1000);
        conn.setUseCaches(false);
        for (Map.Entry<String, String> header : getHeaders(ctx).entrySet()) {
            conn.setRequestProperty(header.getKey(), header.getValue());
        }
        return conn;
    }

    protected static javax.net.ssl.HttpsURLConnection createGetConnection(String url, String query, GatewayContext ctx) throws IOException {
        String getURL = String.format("%s?%s", url, query);
        javax.net.ssl.HttpsURLConnection conn = createApiConnection(getURL, ctx);
        conn.setRequestMethod("GET");
        return conn;
    }

    protected static javax.net.ssl.HttpsURLConnection createPostConnection(String url, Object payloadObject, GatewayContext ctx) throws IOException {
        javax.net.ssl.HttpsURLConnection conn = createApiConnection(url, ctx);
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", CONTENT_TYPE);
        OutputStream output = null;
        try {
            output = conn.getOutputStream();
            output.write(GSON.toJson(payloadObject).getBytes(CHARSET));
        } finally {
            if (output != null) {
                output.close();
            }
        }
        return conn;
    }

    protected static javax.net.ssl.HttpsURLConnection createDeleteConnection(String url, Object payloadObject, GatewayContext ctx) throws IOException {
        javax.net.ssl.HttpsURLConnection conn = createApiConnection(url, ctx);
        conn.setDoOutput(true);
        conn.setRequestMethod("DELETE");
        conn.setRequestProperty("Content-Type", CONTENT_TYPE);
        OutputStream output = null;
        try {
            output = conn.getOutputStream();
            output.write(GSON.toJson(payloadObject).getBytes(CHARSET));
        } finally {
            if (output != null) {
                output.close();
            }
        }
        return conn;
    }

    protected static <T> FatZebraResponse<T> doRequest(String url, Object payload, RequestType type, Class<T> klass, GatewayContext context) throws IOException, NetworkError, APIError {
        try {
            disableDnsCache();
            HttpsURLConnection conn;
            switch (type) {
                case POST:
                    conn = createPostConnection(url, payload, context);
                    break;

                case DELETE:
                    conn = createDeleteConnection(url, payload, context);
                    break;

                case GET:
                default:
                    conn = createGetConnection(url, (String) payload, context);
                    break;
            }

            int rCode = conn.getResponseCode();
            String rBody;
            Map<String, List<String>> headers;
            if (rCode >= 200 && rCode < 300) {
                rBody = getResponseBody(conn.getInputStream());
            } else {
                rBody = getResponseBody(conn.getErrorStream());
            }
            headers = conn.getHeaderFields();

            FatZebraResponse<T> response = new FatZebraResponse<T>(rCode, rBody, headers);
            response.parseResult(klass);

            if (!response.successful) {
                throw new APIError(response.errors);
            }

            return response;
        } catch(java.net.UnknownHostException ex) {
            throw new NetworkError(String.format("Unable to resolve address for %s", ex.getMessage()), true, ex);
        } catch(java.net.ConnectException ex) {
            throw new NetworkError(String.format("Unable to connect to Gateway: %s", ex.getMessage()), true, ex);
        } finally {
            enableDnsCache();
        }
    }

    protected static void disableDnsCache() {
        try {
            originalDNSCacheTTL = java.security.Security
                    .getProperty(DNS_CACHE_TTL_PROPERTY_NAME);
            // disable DNS cache
            java.security.Security
                    .setProperty(DNS_CACHE_TTL_PROPERTY_NAME, "0");
        } catch (SecurityException se) {
            allowedToSetTTL = false;
        }
    }

    protected static void enableDnsCache() {
        if (allowedToSetTTL) {
            if (originalDNSCacheTTL == null) {
                // value unspecified by implementation
                // DNS_CACHE_TTL_PROPERTY_NAME of -1 = cache forever
                java.security.Security.setProperty(
                        DNS_CACHE_TTL_PROPERTY_NAME, "-1");
            } else {
                java.security.Security.setProperty(
                        DNS_CACHE_TTL_PROPERTY_NAME, originalDNSCacheTTL);
            }
        }
    }

}
