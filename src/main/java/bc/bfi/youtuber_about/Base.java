package bc.bfi.youtuber_about;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Base {

    static final String DB_HOST = "3.17.216.88";
    static final int DB_PORT = 3306;
    static final String DB_NAME = "youtube";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "RootSecret1!";
    static final String DB_TABLE = "channels_abouts";

    private static final String DB_URL =
            "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME;

    private static final Logger LOGGER = Logger.getLogger(Base.class.getName());

    private Connection connection;

    public Base() {
    }

    /**
     * Constructor for tests which allows to inject connection instance.
     */
    Base(Connection connection) {
        this.connection = connection;
    }

    void connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        }
    }

    void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ex) {
                LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
    }

    void add(List<ChannelAbout> channels) {
        if (channels == null || channels.isEmpty()) {
            return;
        }

        try {
            connect();

            String sql = "INSERT INTO " + DB_TABLE
                    + "(name, url, verified, description, subscribers, videos, views, join_date, link_to_instagram, link_to_facebook, link_to_twitter, link_to_tiktok, other_links, error)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            for (ChannelAbout channel : channels) {
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setString(1, channel.getName());
                    stmt.setString(2, channel.getUrl());
                    stmt.setString(3, channel.getVerified());
                    stmt.setString(4, channel.getDescription());
                    stmt.setString(5, channel.getSubscribers());
                    stmt.setString(6, channel.getVideos());
                    stmt.setString(7, channel.getViews());
                    stmt.setString(8, channel.getJoinDate());
                    stmt.setString(9, channel.getLinkToInstagram());
                    stmt.setString(10, channel.getLinkToFacebook());
                    stmt.setString(11, channel.getLinkToTwitter());
                    stmt.setString(12, channel.getLinkToTiktok());
                    stmt.setString(13, channel.getOtherLinks());
                    stmt.setString(14, channel.getError());

                    stmt.executeUpdate();
                } catch (SQLIntegrityConstraintViolationException ex) {
                    LOGGER.log(Level.WARNING, "Duplicate url: " + channel.getUrl(), ex);
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

}
