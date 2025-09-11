package bc.bfi.youtuber_about;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class MainConsole {

    private final ScrapeService service;

    public MainConsole() {
        this(new ScrapeService());
    }

    MainConsole(final ScrapeService service) {
        this.service = service;
    }

    public void run(final String[] args) {
        Objects.requireNonNull(args, "args");

        Options options = new Options();
        Option gridOption = Option.builder().longOpt("selenium-grid-ip")
                .hasArg()
                .desc("Selenium Grid host IP address")
                .required()
                .build();
        Option urlsOption = Option.builder().longOpt("urls")
                .hasArg()
                .desc("Path to text file with a list of search urls")
                .required()
                .build();
        options.addOption(gridOption);
        options.addOption(urlsOption);

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("MainConsole", options);
            return;
        }

        String gridHost = cmd.getOptionValue("selenium-grid-ip");
        String urlsPath = cmd.getOptionValue("urls");

        File file = new File(urlsPath);
        assert file.exists() : "File not found: " + urlsPath;

        List<String> ids = new ArrayList<String>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty()) {
                    ids.add(trimmed);
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            System.out.println("Failed to read urls file: " + e.getMessage());
            return;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    System.out.println("Failed to close reader: " + ex.getMessage());
                }
            }
        }

        for (int i = 0; i < ids.size(); i++) {
            String id = ids.get(i);
            service.scrape(id, gridHost);
        }
    }

    public static void main(String[] args) {
        new MainConsole().run(args);
    }
}

