package bc.bfi.youtuber_about;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
}
