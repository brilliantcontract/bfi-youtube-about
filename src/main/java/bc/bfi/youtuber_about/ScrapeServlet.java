package bc.bfi.youtuber_about;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ScrapeServlet extends HttpServlet {

    private final ScrapeService service;

    public ScrapeServlet() {
        this(new ScrapeService());
    }

    ScrapeServlet(ScrapeService service) {
        this.service = service;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String channelId = req.getParameter("queries");
        if (channelId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "queries parameter is required");
            return;
        }

        String gridHost = req.getParameter("gridHost");
        String result = service.scrape(channelId, gridHost);

        resp.setContentType("text/plain");
        PrintWriter writer = resp.getWriter();
        writer.write(result);
        writer.flush();
    }
}
