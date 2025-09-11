package bc.bfi.youtuber_about;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;

import org.junit.Test;

public class MainConsoleTest {

    @Test
    public void runProcessesUrlsFromFile() throws Exception {
        // Initialization.
        final File file = File.createTempFile("urls", ".txt");
        file.deleteOnExit();
        PrintWriter writer = new PrintWriter(file);
        writer.println("@chan1");
        writer.println("@chan2");
        writer.close();
        final String host = "host";

        // Mocks.
        ScrapeService service = mock(ScrapeService.class);

        // Execution.
        MainConsole console = new MainConsole(service);
        console.run(new String[] {"-selenium-grid-ip", host, "-urls", file.getAbsolutePath()});

        // Assertion.
        verify(service).scrape("@chan1", host);
        verify(service).scrape("@chan2", host);
    }

    @Test
    public void runWithoutRequiredArgsPrintsHelp() {
        // Initialization.
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final PrintStream originalOut = System.out;

        // Mocks.
        ScrapeService service = mock(ScrapeService.class);

        // Execution.
        System.setOut(new PrintStream(output));
        MainConsole console = new MainConsole(service);
        console.run(new String[] {});
        System.setOut(originalOut);

        // Assertion.
        String text = output.toString();
        assertThat(text, containsString("selenium-grid-ip"));
        assertThat(text, containsString("urls"));
        verify(service, never()).scrape(anyString(), anyString());
    }

    @Test
    public void runPrintsCompletionMessage() throws Exception {
        // Initialization.
        final File file = File.createTempFile("urls", ".txt");
        file.deleteOnExit();
        final PrintWriter writer = new PrintWriter(file);
        writer.println("@chan");
        writer.close();
        final String host = "host";
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final PrintStream originalOut = System.out;

        // Mocks.
        ScrapeService service = mock(ScrapeService.class);

        // Execution.
        System.setOut(new PrintStream(output));
        MainConsole console = new MainConsole(service);
        console.run(new String[] {"-selenium-grid-ip", host, "-urls", file.getAbsolutePath()});
        System.setOut(originalOut);

        // Assertion.
        final String text = output.toString();
        assertThat(text, containsString("SCRAPING PROCESS COMPLETED"));
    }
}

