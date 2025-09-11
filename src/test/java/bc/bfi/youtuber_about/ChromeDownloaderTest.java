package bc.bfi.youtuber_about;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import org.junit.Ignore;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;

public class ChromeDownloaderTest {

    @Ignore
    @Test
    public void createDriverShouldUseRemote() {
        final WebDriver remote = mock(WebDriver.class);
        ChromeDownloader downloader = new ChromeDownloader() {
            protected WebDriver connectRemote(String gridHost, ChromeOptions options) {
                return remote;
            }
        };

        WebDriver driver = downloader.createDriver("localhost");

        assertThat(driver, sameInstance(remote));
    }

    @Test
    public void openAboutDialogShouldRetryWhenElementMissing() {
        ChromeDownloader downloader = spy(new ChromeDownloader());
        assertThat(downloader, notNullValue());

        WebDriver driver = mock(WebDriver.class);
        WebDriver.Navigation navigation = mock(WebDriver.Navigation.class);
        WebElement button = mock(WebElement.class);
        By selector = By.className("yt-truncated-text__absolute-button");

        when(driver.findElement(selector))
                .thenThrow(new NoSuchElementException("missing"))
                .thenReturn(button);
        when(driver.navigate()).thenReturn(navigation);
        doNothing().when(downloader).waitPageFullLoading(driver);

        downloader.openAboutDialog(driver);

        verify(driver, times(2)).findElement(selector);
        verify(navigation).refresh();
        verify(button).click();
        verify(downloader).waitPageFullLoading(driver);
    }

    @Test
    public void createDriverShouldAddProxyArgumentWhenEnabled() {
        // Initialization.
        final String expectedProxy = "--proxy-server=http://" + Config.PROXY_USERNAME + ":" + Config.PROXY_PASSWORD + "@" + Config.PROXY_HOST + ":" + Config.PROXY_PORT;

        // Mocks.
        CapturingChromeDownloader downloader = new CapturingChromeDownloader();

        // Execution.
        downloader.createDriver("localhost");

        // Assertion.
        final java.util.Map<java.lang.String, java.lang.Object> options = downloader.captured.asMap();
        final java.util.List<java.lang.String> args = (java.util.List<java.lang.String>) options.get("args");
        assertThat((java.lang.Iterable<java.lang.String>) args, hasItem(expectedProxy));
    }

    private static final class CapturingChromeDownloader extends ChromeDownloader {
        ChromeOptions captured;

        protected WebDriver connectRemote(final String gridHost, final ChromeOptions options) {
            captured = options;
            return mock(WebDriver.class);
        }
    }
}

