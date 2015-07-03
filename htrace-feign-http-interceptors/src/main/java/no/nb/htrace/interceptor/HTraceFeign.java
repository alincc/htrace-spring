package no.nb.htrace.interceptor;

import no.nb.htrace.core.HTraceHttpHeaders;

import org.apache.htrace.Span;

import feign.RequestTemplate;

public abstract class HTraceFeign {
    protected RequestTemplate template;
    protected Span span = null;
    
    public HTraceFeign(RequestTemplate template) {
        this.template = template;
    }
    
    public void setHeaders() {
        extractSpan();
        template.header(HTraceHttpHeaders.TraceId.getName(), ""+span.getTraceId());
        template.header(HTraceHttpHeaders.SpanId.getName(), ""+span.getSpanId());
        template.header(HTraceHttpHeaders.ParentSpanId.getName(), ""+span.getParentId());
        template.header(HTraceHttpHeaders.Sampled.getName(), "1");
        template.header(HTraceHttpHeaders.Flags.getName(), "1");
    }
    
    abstract void extractSpan();
    
}
