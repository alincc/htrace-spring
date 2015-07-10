package no.nb.htrace.config;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.htrace.HTraceConfiguration;
import org.apache.htrace.SpanReceiver;
import org.apache.htrace.Trace;
import org.apache.htrace.impl.ZipkinSpanReceiver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.context.ApplicationListener;

public class EmbeddedServletApplicationListener implements ApplicationListener<EmbeddedServletContainerInitializedEvent> {
    private static final Logger LOG = Logger.getLogger(HTraceConfig.class.getName());

    private String collectorHostname;
    
    @Value("${spring.application.name}") 
    public String serviceName;

    public EmbeddedServletApplicationListener(String collectorHostname,
            String serviceName) {
        this.collectorHostname = collectorHostname;
        this.serviceName = serviceName;
    }

    @Override
    public void onApplicationEvent(
            EmbeddedServletContainerInitializedEvent event) {

        Trace.addReceiver(spanReceiver(getServerHostName(), event
                .getEmbeddedServletContainer().getPort()));

    }

    private String getServerHostName() {
        String hostname = null;
        try {
            hostname = InetAddress.getLocalHost()
                    .getCanonicalHostName();
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "Could not get server host name" , ex);
        }
        return hostname;
    }
    
    private SpanReceiver spanReceiver(String tracedServiceHostname, int tracedServicePort) {
        HTraceConfiguration config = createConfiguration(tracedServiceHostname, tracedServicePort);

        return new ZipkinSpanReceiver(config);
    }

    private HTraceConfiguration createConfiguration(
            String tracedServiceHostname, int tracedServicePort) {
        Map<String, String> conf = new HashMap<>();
        conf.put("zipkin.traced-service-hostname", tracedServiceHostname);
        conf.put("zipkin.traced-service-port", String.valueOf(tracedServicePort));
        conf.put("zipkin.collector-hostname", collectorHostname);
        return HTraceConfiguration.fromMap(conf);
    }

}
