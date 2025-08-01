import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import bc.bfi.youtuber_about.Base;
import bc.bfi.youtuber_about.ChannelAbout;
import bc.bfi.youtuber_about.ChromeDownloader;
import bc.bfi.youtuber_about.Parser;
import java.util.Collections;
import org.junit.Test;

public class ScrapeServletTest {

    @Test
    public void shouldDownloadParseAndSaveData() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        when(req.getParameter("queries")).thenReturn("test-domain");

        StringWriter out = new StringWriter();
        when(resp.getWriter()).thenReturn(new PrintWriter(out));

        ChromeDownloader downloader = mock(ChromeDownloader.class);
        Parser parser = mock(Parser.class);
        Base base = mock(Base.class);

        when(downloader.download("test-domain")).thenReturn("page");
        ChannelAbout about = new ChannelAbout("test-domain");
        when(parser.parse("test-domain", "page")).thenReturn(about);

        ScrapeServlet servlet = new ScrapeServlet(downloader, parser, base);
        servlet.doPost(req, resp);

        verify(downloader).download("test-domain");
        verify(parser).parse("test-domain", "page");
        verify(base).add(Collections.singletonList(about));
        assertThat(out.toString(), equalTo("Scraped: test-domain"));
    }
}
