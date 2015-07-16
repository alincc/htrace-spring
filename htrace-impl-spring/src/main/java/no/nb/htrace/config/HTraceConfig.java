package no.nb.htrace.config;

import javax.annotation.PostConstruct;

import org.apache.htrace.Trace;
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
    public ApplicationListener<EmbeddedServletContainerInitializedEvent> embeddedServletContainerListener() {
        return new EmbeddedServletApplicationListener(collectorHostname, serviceName);
    }
}
