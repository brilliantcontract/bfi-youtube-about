import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;

public class ScrapeServletTest {

    @Test
    public void shouldWriteYoutubeUrlToResponse() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        when(req.getParameter("queries")).thenReturn("@channel");

        StringWriter out = new StringWriter();
        when(resp.getWriter()).thenReturn(new PrintWriter(out));

        ScrapeServlet servlet = new ScrapeServlet();
        servlet.doPost(req, resp);

        assertThat(out.toString(), equalTo("Scraped: https://youtube.com/@channel"));
    }
}
