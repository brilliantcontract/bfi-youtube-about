package bc.bfi.youtuber_about;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

public class ScrapeServletTest {

    @Test
    public void shouldDelegateToService() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        when(req.getParameter("queries")).thenReturn("test-domain");
        when(req.getParameter("gridHost")).thenReturn("remote");

        StringWriter out = new StringWriter();
        when(resp.getWriter()).thenReturn(new PrintWriter(out));

        ScrapeService service = mock(ScrapeService.class);
        when(service.scrape("test-domain", "remote")).thenReturn("Scraped: test-domain");

        ScrapeServlet servlet = new ScrapeServlet(service);
        servlet.doPost(req, resp);

        verify(service).scrape("test-domain", "remote");
        assertThat(out.toString(), equalTo("Scraped: test-domain"));
    }

    @Test
    public void shouldReturnBadRequestWhenChannelIdMissing() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);

        ScrapeService service = mock(ScrapeService.class);

        ScrapeServlet servlet = new ScrapeServlet(service);
        servlet.doPost(req, resp);

        verify(resp).sendError(HttpServletResponse.SC_BAD_REQUEST, "queries parameter is required");
        verify(service, never()).scrape(anyString(), anyString());
    }
}
