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

    private static final Logger LOGGER = Logger.getLogger(ChromeDownloader.class.getName());
    private final PageContentExtractor extractor = new PageContentExtractor();

    public String download(String url, String gridHost) {
        WebDriver driver = createDriver(gridHost);
        
        // Load page.
        System.out.println("Downloading page " + url);
        driver.navigate().to(url);
        waitPageFullLoading(driver);
        
        // Load about dialog.
        System.out.println("Wait for \"more...\" button");
        driver.findElement(By.className("truncated-text-wiz__absolute-button")).click();
        waitAboutDialogFullLoading(driver);

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
                            By.className("truncated-text-wiz__absolute-button")
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

    private void waitAboutDialogFullLoading(WebDriver driver) {
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

    protected WebDriver createDriver(String gridHost) {
        ChromeOptions options = new ChromeOptions();
        //options.addArguments("--remote-allow-origins=*");
        //options.addArguments("--proxy-server=socks5://3.85.180.106:1080");

        // By pass bot detection.
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-blink-features");

        return connectRemote(gridHost, options);
    }

    protected WebDriver connectRemote(String gridHost, ChromeOptions options) {
        try {
            URL url = new URL("http://" + gridHost + ":4444/wd/hub");
            return new RemoteWebDriver(url, options);
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
