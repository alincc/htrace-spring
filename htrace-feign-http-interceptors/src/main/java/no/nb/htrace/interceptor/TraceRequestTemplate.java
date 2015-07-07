package no.nb.htrace.interceptor;

import org.apache.htrace.Span;

import feign.RequestTemplate;
import no.nb.htrace.core.HTraceHttpHeaders;

public class TraceRequestTemplate {
    private final RequestTemplate requestTemplate;
    
    public TraceRequestTemplate(RequestTemplate requestTemplate) {
        this.requestTemplate = requestTemplate;
    }

    public void addHeadersToTemplate(Span span) {
        if (span != null) {
            requestTemplate.header(HTraceHttpHeaders.TraceId.getName(), ""+span.getTraceId());
            requestTemplate.header(HTraceHttpHeaders.SpanId.getName(), ""+span.getSpanId());
            requestTemplate.header(HTraceHttpHeaders.ParentSpanId.getName(), ""+span.getParentId());
            requestTemplate.header(HTraceHttpHeaders.Sampled.getName(), "1");
            requestTemplate.header(HTraceHttpHeaders.Flags.getName(), "1");
        }
    }

}
