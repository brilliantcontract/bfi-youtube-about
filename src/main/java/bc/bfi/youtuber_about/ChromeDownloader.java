package bc.bfi.youtuber_about;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ChromeDownloader {

    private static final String WHOIS_SERVICE_URL = "https://ph.godaddy.com/whois/results.aspx?itc=dlp_domain_whois&domain=";
    private static final String WHOIS_SERVICE_URL_WITH_WEB_PROXY = "https://54.215.43.55:4002/proxy/load?key=my-little-secret&only-proxy-provider=zyte.com&url=https://ph.godaddy.com/whois/results.aspx?itc=dlp_domain_whois&domain=";
    private static final Logger LOGGER = Logger.getLogger(ChromeDownloader.class.getName());
    private final PageContentExtractor extractor = new PageContentExtractor();

    public String download(String domain) {
        final String queryUrl = WHOIS_SERVICE_URL + domain;

        WebDriver driver = createDriver();
        driver.navigate().to(queryUrl);

        waitPageFullLoading(driver);

        String webPage = "";
        try {
            webPage = extractor.gainDynamic(driver, Boolean.FALSE);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        driver.quit();

        return webPage;
    }

    private void waitPageFullLoading(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, 10);

        try {
            WebElement element = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector("div#domain-information-reveal span#title-domainName, div#alert-domain-not-found, p#ErrorWhoisResult")
                    )
            );
        } catch (TimeoutException ex) {
            System.err.println("Loading web page timeout exception. " + ex.getMessage());
        }

        try {
            Thread.sleep(1_500);
        } catch (InterruptedException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    protected WebDriver createDriver() {
        ChromeOptions options = new ChromeOptions();
        //options.addArguments("--remote-allow-origins=*");
        //options.addArguments("--proxy-server=socks5://3.85.180.106:1080");

        // By pass bot detection.
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-blink-features");

        return connectRemote(options);
    }

    private WebDriver connectRemote(ChromeOptions options) {
        try {
            URL url = new URL("http://localhost:4444/wd/hub");
            return new RemoteWebDriver(url, options);
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
