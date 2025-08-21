package bc.bfi.youtuber_about;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

public class ParserTest {

    @Test
    public void unwrapYoutubeRedirectReturnsTargetUrl() {
        String redirect = "https://www.youtube.com/redirect?event=channel_description&redir_token=QUFFLUhqa09vSWdhTE01LXktMzcyaS0yemxaMWhCSU5Wd3xBQ3Jtc0tubFlQQ0thanNmejREQlhEZEdaaDl0RkhKcmZRWDREYU1maGxISFJkeDY4cFVLejVRV3p2NzJrLW93TFI2dS1ybVJHdmZJNWw1SjVCYlV2QUoxZXVyWnQ4eDZPVDdha0U4V3VSbHF2MHhUNWIwYjYwYw&q=https%3A%2F%2Fwww.facebook.com%2Fgroups%2F332746901873780";
        String expected = "https://www.facebook.com/groups/332746901873780";
        assertThat(Parser.unwrapYoutubeRedirect(redirect), is(expected));
    }

    @Test
    public void parseDecodesRedirectLinks() {
        String html = "<div id='links-section'"
                + "><a class='yt-core-attributed-string__link' href='https://www.youtube.com/redirect?event=channel_description&redir_token=token&q=https%3A%2F%2Ftwitter.com%2FStephenJohnPeel'></a>"
                + "<a class='yt-core-attributed-string__link' href='https://www.youtube.com/redirect?event=channel_description&redir_token=token&q=https%3A%2F%2Fexample.com'></a>"
                + "</div>";
        Parser parser = new Parser();
        ChannelAbout channel = parser.parse("https://www.youtube.com/@some/about", html);
        String expectedTwitter = "https://twitter.com/StephenJohnPeel";
        String expectedOther = "https://example.com";
        assertThat(channel.getLinkToTwitter(), is(expectedTwitter));
        assertThat(channel.getOtherLinks(), is(expectedOther));
    }

    @Test
    public void parseRemovesExtractedSocialLinksFromOtherLinks() {
        String html = "<div id='links-section'>"
                + "<a class='yt-core-attributed-string__link' href='https://facebook.com/f'></a>"
                + "<a class='yt-core-attributed-string__link' href='https://instagram.com/i'></a>"
                + "<a class='yt-core-attributed-string__link' href='https://tiktok.com/@t'></a>"
                + "<a class='yt-core-attributed-string__link' href='https://twitter.com/x'></a>"
                + "<a class='yt-core-attributed-string__link' href='https://example.com'></a>"
                + "</div>";
        Parser parser = new Parser();
        ChannelAbout channel = parser.parse("https://www.youtube.com/@some/about", html);
        assertThat(channel.getLinkToFacebook(), is("https://facebook.com/f"));
        assertThat(channel.getLinkToInstagram(), is("https://instagram.com/i"));
        assertThat(channel.getLinkToTiktok(), is("https://tiktok.com/@t"));
        assertThat(channel.getLinkToTwitter(), is("https://twitter.com/x"));
        assertThat(channel.getOtherLinks(), is("https://example.com"));
    }
}
