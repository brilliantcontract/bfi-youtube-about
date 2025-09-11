package bc.bfi.youtuber_about;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;

public class MainTest {

    @Test
    public void startScrapingCallsServiceForEachQuery() {
        ScrapeService service = mock(ScrapeService.class);
        when(service.scrape("chan1", "host")).thenReturn("Scraped: chan1");
        when(service.scrape("chan2", "host")).thenReturn("Scraped: chan2");

        Main ui = new Main(service);
        ui.gridHostField.setText("host");
        ui.queriesArea.setText("chan1\nchan2\n");

        ui.startScraping();

        verify(service).scrape("chan1", "host");
        verify(service).scrape("chan2", "host");
        assertThat(ui.progressBar.getValue(), is(2));
    }

    @Test
    public void startScrapingPrintsCompletionMessage() {
        // Initialization.
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final PrintStream originalOut = System.out;

        // Mocks.
        ScrapeService service = mock(ScrapeService.class);
        when(service.scrape("chan", "host")).thenReturn("Scraped: chan");

        // Execution.
        System.setOut(new PrintStream(output));
        Main ui = new Main(service);
        ui.gridHostField.setText("host");
        ui.queriesArea.setText("chan\n");
        ui.startScraping();
        System.setOut(originalOut);

        // Assertion.
        final String text = output.toString();
        assertThat(text, containsString("SCRAPING PROCESS COMPLETED"));
    }
}
