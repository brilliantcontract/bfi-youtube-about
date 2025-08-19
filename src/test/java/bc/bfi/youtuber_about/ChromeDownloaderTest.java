package bc.bfi.youtuber_about;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
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
        ChromeDownloader downloader = new ChromeDownloader() {
            @Override
            protected void waitPageFullLoading(WebDriver driver) {
            }
        };
        assertThat(downloader, notNullValue());

        WebDriver driver = mock(WebDriver.class);
        WebDriver.Navigation navigation = mock(WebDriver.Navigation.class);
        WebElement button = mock(WebElement.class);
        By selector = By.className("truncated-text-wiz__absolute-button");

        when(driver.findElement(selector))
                .thenThrow(new NoSuchElementException("missing"))
                .thenReturn(button);
        when(driver.navigate()).thenReturn(navigation);

        downloader.openAboutDialog(driver);

        verify(driver, times(2)).findElement(selector);
        verify(navigation).refresh();
        verify(button).click();
    }
}
