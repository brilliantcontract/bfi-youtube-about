package bc.bfi.youtuber_about;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class PageContentExtractor {

    private static final Logger LOGGER = Logger.getLogger(PageContentExtractor.class.getName());
    private JavascriptExecutor javascriptExecutor;

    public String gainDynamic(WebDriver driver, Boolean captureFrames) throws IOException {
        try {
            if (javascriptExecutor == null) {
                javascriptExecutor = (JavascriptExecutor) driver;
            }

            String document = getJsContent();

            if (captureFrames != null && captureFrames == true) {
                int numberOfFrames = driver.findElements(By.xpath("//iframe")).size();
                for (int i = 0; i < numberOfFrames; i++) {
                    try {
                        driver.switchTo().frame(i);
                        String frameContent = getJsContent();
                        driver.switchTo().defaultContent();
                        document = replaceLast(document, "</BODY>", frameContent);
                    } catch (Exception ex) {
                        LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                    }
                }
            }

            return document;
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
        }

        return driver.getPageSource();
    }

    public String gainStatic(WebDriver driver) throws IOException {
        return driver.getPageSource();
    }

    public byte[] gainBinary(WebDriver driver) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        byte[] bytes = null;

        if (driver instanceof HtmlUnitDriver) {
            Map headers = new HashMap();
            headers.put("User-Agent", "5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36 Edge/18.18362");
            headers.put("Accept-Language", "en-US,en;q=0.8");
            headers.put("Referer", "google.com");

            URL url = new URL(driver.getCurrentUrl());
            bytes = new Downloader().download(url, headers);
        } else {
            String script = "var url = arguments[0];"
                    + "var callback = arguments[arguments.length - 1];"
                    + "var xhr = new XMLHttpRequest();"
                    + "xhr.open('GET', url, true);"
                    + "xhr.responseType = \"arraybuffer\";"
                    + "xhr.onload = function() {"
                    + "  var arrayBuffer = xhr.response;"
                    + "  var byteArray = new Uint8Array(arrayBuffer);"
                    + "  callback(byteArray);"
                    + "};"
                    + "xhr.send();";
            Object response = ((JavascriptExecutor) driver).executeAsyncScript(script, driver.getCurrentUrl());

            List<Long> byteList = (ArrayList<Long>) response;
            bytes = new byte[byteList.size()];
            for (int i = 0; i < byteList.size(); i++) {
                bytes[i] = (byte) (long) byteList.get(i);
            }
        }

        return bytes;
    }

    private String getJsContent() {
        String content = "";

        try {
            content = javascriptExecutor.executeScript("return document.documentElement.outerHTML;").toString();
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
        }

        return content;
    }

    private String replaceLast(String text, String what, String replacement) {
        int index = text.toUpperCase().lastIndexOf(what.toUpperCase());
        if (index >= 0) {
            text = new StringBuilder(text).replace(index, index + what.length(), replacement).toString();
        }

        return text;
    }

}

class Downloader {

    byte[] download(final URL url, final Map<String, String> headers)
            throws IOException, NoSuchAlgorithmException, KeyManagementException {
        HttpsURLConnection.setDefaultSSLSocketFactory(initSslContext().getSocketFactory());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setInstanceFollowRedirects(true);
        connection.setRequestMethod("GET");

        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                connection.setRequestProperty(header.getKey(), header.getValue());
            }
        }

        connection = redirect(connection, headers);

        byte[] data;
        if ("gzip".equals(connection.getContentEncoding())) {
            data = readStream(new GZIPInputStream(connection.getInputStream()));
        } else {
            data = readStream(connection.getInputStream());
        }

        return data;
    }

    /**
     * HttpURLConnection by design won't automatically redirect from HTTP to
     * HTTPS (or vice versa).
     *
     * This method fix this situation.
     */
    private HttpURLConnection redirect(HttpURLConnection connection, final Map<String, String> headers)
            throws IOException {
        boolean redirect = false;
        int status = connection.getResponseCode();
        if (status != HttpURLConnection.HTTP_OK) {
            if (status == HttpURLConnection.HTTP_MOVED_TEMP
                    || status == HttpURLConnection.HTTP_MOVED_PERM
                    || status == HttpURLConnection.HTTP_SEE_OTHER) {
                redirect = true;
            }
        }
        System.out.println("Response Code is " + status);

        if (redirect) {
            String newUrl = connection.getHeaderField("Location");
            String cookies = connection.getHeaderField("Set-Cookie");

            connection = (HttpURLConnection) new URL(newUrl).openConnection();
            if (headers != null) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    connection.setRequestProperty(header.getKey(), header.getValue());
                }
            }
            if (cookies != null) {
                connection.setRequestProperty("Cookie", cookies);
            }

            System.out.println("Redirect to URL : " + newUrl);
        }

        return connection;
    }

    private byte[] readStream(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[0xFFFF];
        for (int len = is.read(buffer); len != -1; len = is.read(buffer)) {
            baos.write(buffer, 0, len);
        }
        return baos.toByteArray();
    }

    private SSLContext initSslContext() throws NoSuchAlgorithmException, KeyManagementException {
        // Create a trust manager that does not validate certificate chains.
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
        };

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

        return sslContext;
    }
}
