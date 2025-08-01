package bc.bfi.youtuber_about;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Collections;
import org.junit.Test;

public class BaseTest {

    @Test
    public void addShouldStoreChannelAbout() throws Exception {
        Connection connection = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);
        when(connection.isClosed()).thenReturn(false);
        when(connection.prepareStatement(anyString())).thenReturn(stmt);

        Base base = new Base(connection);

        ChannelAbout channel = new ChannelAbout("https://example.com");
        channel.setName("name");
        channel.setVerified("yes");
        channel.setDescription("desc");
        channel.setSubscribers("1");
        channel.setVideos("2");
        channel.setViews("3");
        channel.setJoinDate("4");
        channel.setLinkToInstagram("ig");
        channel.setLinkToFacebook("fb");
        channel.setLinkToTwitter("tw");
        channel.setLinkToTiktok("tt");
        channel.setOtherLinks("links");
        channel.setError("error");

        base.add(Collections.singletonList(channel));

        verify(connection).prepareStatement(anyString());
        verify(stmt).setString(1, "name");
        verify(stmt).setString(2, "https://example.com");
        verify(stmt).setString(3, "yes");
        verify(stmt).setString(4, "desc");
        verify(stmt).setString(5, "1");
        verify(stmt).setString(6, "2");
        verify(stmt).setString(7, "3");
        verify(stmt).setString(8, "4");
        verify(stmt).setString(9, "ig");
        verify(stmt).setString(10, "fb");
        verify(stmt).setString(11, "tw");
        verify(stmt).setString(12, "tt");
        verify(stmt).setString(13, "links");
        verify(stmt).setString(14, "error");
        verify(stmt).executeUpdate();
    }
}
