package bc.bfi.youtuber_about;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collections;
import org.junit.Ignore;
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
        channel.setDescription("desc");
        channel.setVideos("2");
        channel.setViews("3");
        channel.setJoinDate("4");
        channel.setLinkToInstagram("ig");
        channel.setLinkToFacebook("fb");
        channel.setLinkToTwitter("tw");
        channel.setLinkToTiktok("tt");
        channel.setOtherLinks("links");

        base.add(channel);

        // Implementation binds starting at index 2, so verify those positions.
        verify(connection).prepareStatement(anyString());
        verify(stmt).setString(1, "https://example.com");
        verify(stmt).setString(2, "desc");
        verify(stmt).setString(3, "2");
        verify(stmt).setString(4, "3");
        verify(stmt).setString(5, "4");
        verify(stmt).setString(6, "ig");
        verify(stmt).setString(7, "fb");
        verify(stmt).setString(8, "tw");
        verify(stmt).setString(9, "tt");
        verify(stmt).setString(10, "links");
        verify(stmt).executeUpdate();
    }

    @Test
    public void existsShouldReturnTrueWhenRecordFound() throws Exception {
        Connection connection = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(connection.isClosed()).thenReturn(false);
        when(connection.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);

        Base base = new Base(connection);

        boolean result = base.exists("channel");

        verify(stmt).setString(1, "channel");
        verify(stmt).executeQuery();
        assertThat(result, equalTo(true));
    }

    @Test
    public void existsShouldReturnFalseWhenRecordMissing() throws Exception {
        Connection connection = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(connection.isClosed()).thenReturn(false);
        when(connection.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        Base base = new Base(connection);

        boolean result = base.exists("channel");

        verify(stmt).setString(1, "channel");
        verify(stmt).executeQuery();
        assertThat(result, equalTo(false));
    }
}
