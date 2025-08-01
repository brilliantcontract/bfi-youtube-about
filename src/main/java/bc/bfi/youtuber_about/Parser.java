package bc.bfi.youtuber_about;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Parser {

    public ChannelAbout parse(String channelName, String webPage) {
        ChannelAbout channel = new ChannelAbout(channelName);

        if (webPage.isEmpty()) {
            channel.setError("Web page downloaded empty.");
            System.err.println("Web page downloaded empty.");

            return channel;
        }

        Document doc = Jsoup.parse(webPage);

        if (!doc.select("div#alert-domain-not-found, p#ErrorWhoisResult").isEmpty()) {
            channel.setError("GoDaddy retun error message. Most likely scraper request has been blocked.");
            System.err.println("GoDaddy retun error message. Most likely scraper request has been blocked.");

            return channel;
        }

        //channel.setDomainInformationName(fetchText("#domain-information-reveal #title-domainName + span", doc));
        //channel.setError(fetchText("div#alert-domain-not-found", doc));

        return channel;
    }

    private String fetchText(String cssSelector, Document doc) {
        String results = "";

        try {
            Elements elements = doc.select(cssSelector);
            if (!elements.isEmpty()) {
                results = elements.get(0).text();
            }
        } catch (Exception ex) {
            System.err.println("Cannot parse with Jsoup: " + ex.getMessage());
        }

        return results;
    }

}
