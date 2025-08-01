package bc.bfi.youtuber_about;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class CsvStorage {

    private static final String STORAGE_FILE = "youtube-channels.csv";
    private static final Logger LOGGER = Logger.getLogger(CsvStorage.class.getName());

    private static final String[] HEADERS = {
        "NAME",
        "HANDLER",
        "JOIN_DATE",
        "VERIFIED",
        "SUBSCRIBERS",
        "VIDEOS",
        "VIEWS",
        "LINK_TO_FACEBOOK",
        "LINK_TO_INSTAGRAM",
        "LINK_TO_TIKTOK",
        "LINK_TO_TWITTER",
        "OTHER_LINKS",
        "DESCRIPTION",
        "ERROR"
    };

    public CsvStorage() {
        if (!Files.exists(Paths.get("STORAGE_FILE"))) {
            try {
                createCsvFile();
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Cannot write to .csv file. " + ex.getMessage(), ex);
                System.exit(1);
            }
        }
    }

    public final void createCsvFile() throws Exception {
        if (Files.exists(Paths.get(STORAGE_FILE))) {
            return;
        }

        Writer writer = new FileWriter(STORAGE_FILE);

        CSVFormat format = CSVFormat.DEFAULT
                .withIgnoreHeaderCase();

        try (CSVPrinter printer = new CSVPrinter(writer, format)) {
            printer.printRecord(HEADERS);
        }
    }

    public void append(ChannelAbout channel) {
        List<String> record = new ArrayList<>();
        record.add(channel.getName());
        record.add(channel.getUrl());
        record.add(channel.getJoinDate());
        record.add(channel.getVerified());
        record.add(channel.getSubscribers());
        record.add(channel.getVideos());
        record.add(channel.getViews());
        record.add(channel.getLinkToFacebook());
        record.add(channel.getLinkToInstagram());
        record.add(channel.getLinkToTiktok());
        record.add(channel.getLinkToTwitter());
        record.add(channel.getOtherLinks());
        record.add(channel.getDescription());
        record.add(channel.getError());

        try {
            Writer writer = new FileWriter(STORAGE_FILE, true);
            CSVFormat format = CSVFormat.DEFAULT;
            try (CSVPrinter printer = new CSVPrinter(writer, format)) {
                printer.printRecord(record);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Cannot write to .csv file. " + ex.getMessage(), ex);
            System.exit(1);
        }
    }
}
