package bc.bfi.youtuber_about;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

public class ChannelAboutTest {

    @Test
    public void getViewsAsNumberReturnsZeroWhenNegative() {
        ChannelAbout channel = new ChannelAbout("https://example.com");
        channel.setViews("-42 views");
        assertThat(channel.getViewsAsNumber(), is("42"));
    }
}

