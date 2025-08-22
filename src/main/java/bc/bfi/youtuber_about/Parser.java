package bc.bfi.youtuber_about;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.List;
import java.util.stream.Collectors;
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

        // Test on verified channel @StevePeel
        if (!doc.select("h1 span.yt-icon-shape").isEmpty()) {
            channel.setVerification("Verified");
        }

        channel.setDescription(fetchText("yt-attributed-string#description-container"));
        
        channel.setThumbnail(fetchAttribute("yt-avatar-shape img", "src"));

        List<String> links = doc.select("div#links-section a.yt-core-attributed-string__link")
                .eachAttr("abs:href").stream()
                .map(Parser::unwrapYoutubeRedirect)
                .collect(Collectors.toList());

        channel.setLinkToFacebook(SocialLinkExtractor.facebook(links));
        channel.setLinkToInstagram(SocialLinkExtractor.instagram(links));
        channel.setLinkToTiktok(SocialLinkExtractor.tiktok(links));
        channel.setLinkToTwitter(SocialLinkExtractor.twitter(links));
        channel.setOtherLinks(String.join("◙", links));

        return channel;
    }

    private String fetchText(final String cssSelector) {
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

    private String fetchAttribute(final String cssSelector, final String attribute) {
        String results = "";

        try {
            Elements elements = doc.select(cssSelector);
            if (!elements.isEmpty()) {
                results = elements.get(0).attr(attribute);
            }
        } catch (Exception ex) {
            System.err.println("Cannot parse with Jsoup: " + ex.getMessage());
        }

        return results;
    }

    static String unwrapYoutubeRedirect(String url) {
        try {
            URI uri = new URI(url);
            if ("www.youtube.com".equalsIgnoreCase(uri.getHost())
                    && "/redirect".equals(uri.getPath())) {
                String query = uri.getRawQuery();
                if (query != null) {
                    for (String part : query.split("&")) {
                        int eq = part.indexOf('=');
                        String key = eq >= 0 ? part.substring(0, eq) : part;
                        if ("q".equals(key)) {
                            String val = eq >= 0 ? part.substring(eq + 1) : "";
                            return URLDecoder.decode(val, "UTF-8");
                        }
                    }
                }
            }
        } catch (URISyntaxException | UnsupportedEncodingException ex) {
            // ignore and return original URL
        }
        return url;
    }

}
