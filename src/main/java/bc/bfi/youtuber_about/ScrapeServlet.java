package bc.bfi.youtuber_about;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
        String channelId = req.getParameter("queries");
        if (channelId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "queries parameter is required");
            return;
        }

        if (base.exists(channelId)) {
            resp.setContentType("text/plain");
            PrintWriter writer = resp.getWriter();
            writer.write("Skipped: " + channelId);
            writer.flush();
            return;
        }

        String url = "https://youtube.com/" + channelId;

        System.out.println("-------------------------------------");
        System.out.println("Scrape: " + url);

        String webPage = downloader.download(url);
        ChannelAbout channel = parser.parse(url, webPage);
        base.add(channel);

        resp.setContentType("text/plain");
        PrintWriter writer = resp.getWriter();
        writer.write("Scraped: " + channelId);
        writer.flush();
    }
}
