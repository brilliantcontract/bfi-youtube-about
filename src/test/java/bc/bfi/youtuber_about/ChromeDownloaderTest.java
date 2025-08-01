package bc.bfi.youtuber_about;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Ignore;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class ChromeDownloaderTest {

    @Ignore
    @Test
    public void createDriverShouldUseRemote() {
        final WebDriver remote = mock(WebDriver.class);
        ChromeDownloader downloader = new ChromeDownloader() {
            protected WebDriver connectRemote(ChromeOptions options) {
                return remote;
            }
        };

        WebDriver driver = downloader.createDriver();

        assertThat(driver, sameInstance(remote));
    }
}
