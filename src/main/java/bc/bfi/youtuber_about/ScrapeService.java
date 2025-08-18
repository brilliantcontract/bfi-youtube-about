package bc.bfi.youtuber_about;

public class ScrapeService {

    private final ChromeDownloader downloader;
    private final Parser parser;
    private final Base base;

    public ScrapeService() {
        this(new ChromeDownloader(), new Parser(), new Base());
    }

    ScrapeService(ChromeDownloader downloader, Parser parser, Base base) {
        this.downloader = downloader;
        this.parser = parser;
        this.base = base;
    }

    public String scrape(String channelId, String gridHost) {
        if (gridHost == null || gridHost.isEmpty()) {
            gridHost = "localhost";
        }

        if (base.exists(channelId)) {
            System.out.println("!!! Skipped: " + channelId + "(channel already exist in the database)");
            return "Skipped: " + channelId;
        }

        String url = "https://youtube.com/" + channelId;

        System.out.println("-------------------------------------");
        System.out.println("Scrape: " + url);

        String webPage = downloader.download(url, gridHost);
        ChannelAbout channel = parser.parse(url, webPage);
        base.add(channel);

        return "Scraped: " + channelId;
    }
}
