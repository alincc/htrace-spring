package no.nb.htrace.config;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.htrace.HTraceConfiguration;
import org.apache.htrace.SpanReceiver;
import org.apache.htrace.Trace;
import org.apache.htrace.impl.ZipkinSpanReceiver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnWebApplication
public class HTraceConfig {

    @Value("${zipkin.collector-hostname}") 
    private String collectorHostname;
    
    @Value("${spring.application.name}") 
    public String serviceName;

    @PostConstruct
    public void init() {
        Trace.setProcessId(this.serviceName);
    }
    
    @Bean
    public ApplicationListener<EmbeddedServletContainerInitializedEvent> EmbeddedServletContainerInitialized$listener1() {
        return new ApplicationListener<EmbeddedServletContainerInitializedEvent>() {

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
                    ex.printStackTrace();
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

        };
    }

}
