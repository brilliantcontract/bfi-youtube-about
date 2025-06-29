package bc.bfi.youtuber_about;

public class Channel {

    private String name = "";
    private final String handler;
    private String verified = "";
    private String description = "";
    private final String address;
    private String subscribers = "";
    private String videos = "";
    private String views = "";
    private String joinDate = "";
    private String linkToInstagram = "";
    private String linkToFacebook = "";
    private String linkToTwitter = "";
    private String linkToTiktok = "";
    private String otherLinks = "";
    private String error = "";

    public Channel(String handler) {
        this.handler = handler;
        this.address = "https://www.youtube.com/" + handler;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getHandler() {
        return handler;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(String subscribers) {
        this.subscribers = subscribers;
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

}
