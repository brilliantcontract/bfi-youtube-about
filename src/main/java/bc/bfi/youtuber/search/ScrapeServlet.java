package bc.bfi.youtuber.search;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ScrapeServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String query = req.getParameter("queries");
        if (query == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "queries parameter is required");
            return;
        }

        String url = "https://youtube.com/" + query;
        System.out.println("Scrape: " + url);

        resp.setContentType("text/plain");
        PrintWriter writer = resp.getWriter();
        writer.write("Scraped: " + url);
        writer.flush();
    }
}
