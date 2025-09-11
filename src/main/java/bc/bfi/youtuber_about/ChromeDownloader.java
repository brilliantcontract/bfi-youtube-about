package bc.bfi.youtuber_about;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Objects;
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

    private static final Logger LOGGER = Logger.getLogger(ChromeDownloader.class.getName());
    private final PageContentExtractor extractor = new PageContentExtractor();

    public String download(final String url, final String gridHost) {
        Objects.requireNonNull(url, "url must not be NULL");
        Objects.requireNonNull(gridHost, "gridHost must not be NULL");

        WebDriver driver = createDriver(gridHost);
        try {
            // Load page.
            System.out.println("Loading page " + url);
            driver.navigate().to(url);
            waitPageFullLoading(driver);

            // Load about dialog.
            System.out.println("Wait for \"more...\" button");
            openAboutDialog(driver);
            waitAboutDialogFullLoading(driver);

            String webPage = "";
            try {
                webPage = extractor.gainDynamic(driver, Boolean.FALSE);
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }

            return webPage;
        } finally {
            driver.quit();
        }
    }

    void openAboutDialog(final WebDriver driver) {
        By selector = By.className("yt-truncated-text__absolute-button");

        for (int attempt = 0; attempt < 3; attempt++) {
            if (attempt > 0) {
                driver.navigate().refresh();
                waitPageFullLoading(driver);
            }

            try {
                driver.findElement(selector).click();
                return;
            } catch (org.openqa.selenium.NoSuchElementException ex) {
                if (attempt == 2) {
                    throw ex;
                }
            }
        }
    }

    protected void waitPageFullLoading(final WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, 10);

        try {
            WebElement element = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.className("yt-truncated-text__absolute-button")
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

    protected void waitAboutDialogFullLoading(final WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, 10);

        try {
            WebElement element = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector("yt-attributed-string#description-container")
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

    protected WebDriver createDriver(final String gridHost) {
        ChromeOptions options = new ChromeOptions();
        if (Config.USE_PROXY.booleanValue()) {
            final String address = Config.PROXY_HOST + ":" + Config.PROXY_PORT;
            final String auth = Config.PROXY_USERNAME + ":" + Config.PROXY_PASSWORD;
            options.addArguments("--proxy-server=http://" + auth + "@" + address);
        }
        //options.addArguments("--remote-allow-origins=*");

        // By pass bot detection.
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-blink-features");

        // Expose arguments for tests.
        options.setCapability("args", options.getArguments());

        return connectRemote(gridHost, options);
    }

    protected WebDriver connectRemote(final String gridHost, final ChromeOptions options) {
        try {
            URL url = new URL("http://" + gridHost + ":4444/wd/hub");
            return new RemoteWebDriver(url, options);
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
