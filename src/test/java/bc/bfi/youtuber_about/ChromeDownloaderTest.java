package bc.bfi.youtuber_about;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class ChromeDownloaderTest {

    @Test
    public void createDriverShouldUseRemote() {
        final WebDriver remote = mock(WebDriver.class);
        ChromeDownloader downloader = new ChromeDownloader() {
            @Override
            protected WebDriver connectRemote(ChromeOptions options) {
                return remote;
            }
        };

        WebDriver driver = downloader.createDriver();

        assertThat(driver, sameInstance(remote));
    }
}
