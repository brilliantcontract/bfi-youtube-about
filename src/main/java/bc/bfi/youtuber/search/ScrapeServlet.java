package bc.bfi.youtuber.search;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bc.bfi.youtuber_about.Base;
import bc.bfi.youtuber_about.ChannelAbout;
import bc.bfi.youtuber_about.ChromeDownloader;
import bc.bfi.youtuber_about.Parser;

public class ScrapeServlet extends HttpServlet {

    private final ChromeDownloader downloader;
    private final Parser parser;
    private final Base base;

    public ScrapeServlet() {
        this(new ChromeDownloader(), new Parser(), new Base());
    }

    ScrapeServlet(ChromeDownloader downloader, Parser parser, Base base) {
        this.downloader = downloader;
        this.parser = parser;
        this.base = base;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String query = req.getParameter("queries");
        if (query == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "queries parameter is required");
            return;
        }

        System.out.println("Scrape: " + query);

        String webPage = downloader.download(query);
        ChannelAbout channel = parser.parse(query, webPage);
        base.add(Collections.singletonList(channel));

        resp.setContentType("text/plain");
        PrintWriter writer = resp.getWriter();
        writer.write("Scraped: " + query);
        writer.flush();
    }
}
