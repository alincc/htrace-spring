package no.nb.htrace.interceptor;

import org.apache.htrace.Trace;

import feign.RequestTemplate;

public class HTraceFeignRequest extends HTraceFeign {

    public HTraceFeignRequest(RequestTemplate template) {
        super(template);
    }

    @Override
    void extractSpan() {
        this.span = Trace.currentSpan();
    }

}
