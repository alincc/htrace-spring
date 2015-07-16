package no.nb.htrace.interceptor;

import org.apache.htrace.Span;

import feign.RequestTemplate;
import no.nb.htrace.core.HTraceHttpHeaders;

public class TraceRequestTemplate {
    private final RequestTemplate requestTemplate;
    
    public TraceRequestTemplate(RequestTemplate requestTemplate) {
        this.requestTemplate = requestTemplate;
    }

    public void addTraceHeadersToTemplate(Span span) {
        if (span != null) {
            requestTemplate.header(HTraceHttpHeaders.TRACE_ID.toString(), ""+span.getTraceId());
            requestTemplate.header(HTraceHttpHeaders.SPAN_ID.toString(), ""+span.getSpanId());
            requestTemplate.header(HTraceHttpHeaders.PARENT_SPAN_ID.toString(), ""+span.getParentId());
            requestTemplate.header(HTraceHttpHeaders.SAMPLED.toString(), "1");
            requestTemplate.header(HTraceHttpHeaders.FLAGS.toString(), "1");
        }
    }

}
