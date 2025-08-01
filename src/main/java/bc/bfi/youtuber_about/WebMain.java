package bc.bfi.youtuber_about;

import java.io.File;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.scan.StandardJarScanFilter;
import javax.servlet.ServletRegistration;
import bc.bfi.youtuber_about.ScrapeServlet;

public class WebMain {

    // Port where embedded Tomcat will listen
    private static final Integer PORT_NUMBER = 8080;
    // Location of web resources
    private static final String WEBAPP_DIR = "src/main/webapp";

    static void configureJarScanner(Context ctx) {
        StandardJarScanFilter filter = new StandardJarScanFilter();
        filter.setTldSkip("xalan*.jar,serializer*.jar,xercesImpl.jar,xml-apis.jar");
        ctx.getJarScanner().setJarScanFilter(filter);
    }

    public static void main(String[] args) throws Exception {
        Tomcat tomcat = new Tomcat();
        tomcat.setHostname("localhost");
        tomcat.setPort(PORT_NUMBER);
        tomcat.getConnector();

        Context ctx = tomcat.addWebapp("", new File(WEBAPP_DIR).getAbsolutePath());

        configureJarScanner(ctx);

        // Register servlet with Tomcat BEFORE starting
        Tomcat.addServlet(ctx, "scrape", new ScrapeServlet());
        ctx.addServletMappingDecoded("/scrape", "scrape");

        tomcat.start();
        tomcat.getServer().await();
    }
}
