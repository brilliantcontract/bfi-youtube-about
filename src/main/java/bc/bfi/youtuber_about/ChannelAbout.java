package bc.bfi.youtuber_about;

public class ChannelAbout {

    private final String url;
    private String description = "";
    private String videos = "";
    private String views = "";
    private String joinDate = "";
    private String linkToInstagram = "";
    private String linkToFacebook = "";
    private String linkToTwitter = "";
    private String linkToTiktok = "";
    private String otherLinks = "";
    private String verification = "";
    private String thumbnail = "";

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
    
    public String getVerification() {
        return verification;
    }

    public void setVerification(String verification) {
        this.verification = verification.replaceAll("\\s+", " ").trim();
    }
    
    public ChannelAbout(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideos() {
        return videos;
    }

    public void setVideos(String videos) {
        this.videos = videos;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(String joinDate) {
        this.joinDate = joinDate;
    }

    public String getLinkToInstagram() {
        return linkToInstagram;
    }

    public void setLinkToInstagram(String linkToInstagram) {
        this.linkToInstagram = linkToInstagram;
    }

    public String getLinkToFacebook() {
        return linkToFacebook;
    }

    public void setLinkToFacebook(String linkToFacebook) {
        this.linkToFacebook = linkToFacebook;
    }

    public String getLinkToTwitter() {
        return linkToTwitter;
    }

    public void setLinkToTwitter(String linkToTwitter) {
        this.linkToTwitter = linkToTwitter;
    }

    public String getLinkToTiktok() {
        return linkToTiktok;
    }

    public void setLinkToTiktok(String linkToTiktok) {
        this.linkToTiktok = linkToTiktok;
    }

    public String getOtherLinks() {
        return otherLinks;
    }

    public void setOtherLinks(String otherLinks) {
        this.otherLinks = otherLinks;
    }
    
    public String getVideosAsNumber() {
        int number = parseNumbers(this.videos);
        return String.valueOf(number);
    }
    
    public String getViewsAsNumber() {
        int number = parseNumbers(this.views);
        return String.valueOf(number);
    }

    private static int parseNumbers(String input) {
        if (input == null || input.trim().isEmpty()) {
            return 0;
        }

        input = input
                .replace(" subscribers", "")
                .replace(" videos", "")
                .replace(" views", "")
                .trim(); // Remove text part
        input = input.replace(",", ""); // Remove commas if any

        double multiplier = 1;
        if (input.toLowerCase().endsWith("k")) {
            multiplier = 1_000;
            input = input.substring(0, input.length() - 1);
        } else if (input.toLowerCase().endsWith("m")) {
            multiplier = 1_000_000;
            input = input.substring(0, input.length() - 1);
        }

        try {
            double value = Double.parseDouble(input);
            int result = (int) Math.round(value * multiplier);
            return Math.abs(result);
        } catch (NumberFormatException e) {
            return 0; // Fallback for unexpected input
        }
    }

}
