package bc.bfi.youtuber_about;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Test;

public class ScrapeServiceTest {

    @Test
    public void shouldDownloadParseAndSaveData() {
        ChromeDownloader downloader = mock(ChromeDownloader.class);
        Parser parser = mock(Parser.class);
        Base base = mock(Base.class);
        when(base.exists("test-domain")).thenReturn(false);

        when(downloader.download("https://youtube.com/test-domain", "localhost")).thenReturn("page");
        ChannelAbout about = new ChannelAbout("https://youtube.com/test-domain");
        when(parser.parse("https://youtube.com/test-domain", "page")).thenReturn(about);

        ScrapeService service = new ScrapeService(downloader, parser, base);
        String result = service.scrape("test-domain", "localhost");

        verify(base).exists("test-domain");
        verify(downloader).download("https://youtube.com/test-domain", "localhost");
        verify(parser).parse("https://youtube.com/test-domain", "page");
        verify(base).add(about);
        assertThat(result, equalTo("Scraped: test-domain"));
    }

    @Test
    public void shouldSkipIfRecordExists() {
        ChromeDownloader downloader = mock(ChromeDownloader.class);
        Parser parser = mock(Parser.class);
        Base base = mock(Base.class);
        when(base.exists("test-domain")).thenReturn(true);

        ScrapeService service = new ScrapeService(downloader, parser, base);
        String result = service.scrape("test-domain", null);

        verify(base).exists("test-domain");
        verify(downloader, never()).download(anyString(), anyString());
        verify(parser, never()).parse(anyString(), anyString());
        verify(base, never()).add(any(ChannelAbout.class));
        assertThat(result, equalTo("Skipped: test-domain"));
    }
}
