package bc.bfi.youtuber_about;

import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Parser {
    
    private Document doc;

    public ChannelAbout parse(String url, String webPage) {
        ChannelAbout channel = new ChannelAbout(url.replaceAll(".+/", ""));

        this.doc = Jsoup.parse(webPage);
        
        channel.setJoinDate(fetchText("tr.description-item td:has(yt-icon[icon=info_outline]) + td"));
        channel.setVideos(fetchText("tr.description-item td:has(yt-icon[icon=my_videos]) + td"));
        channel.setViews(fetchText("tr.description-item td:has(yt-icon[icon=trending_up]) + td"));
        
        channel.setDescription(fetchText("yt-attributed-string#description-container"));
        
        List<String> links = doc.select("div#links-section a.yt-core-attributed-string__link").eachAttr("abs:href");
        channel.setOtherLinks(String.join("◙", links));
        
        channel.setLinkToFacebook(SocialLinkExtractor.facebook(links));
        channel.setLinkToInstagram(SocialLinkExtractor.instagram(links));
        channel.setLinkToTiktok(SocialLinkExtractor.tiktok(links));
        channel.setLinkToTwitter(SocialLinkExtractor.twitter(links));

        return channel;
    }

    private String fetchText(String cssSelector) {
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
