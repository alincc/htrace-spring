package no.nb.htrace.interceptor;

import java.util.Collection;

import no.nb.htrace.core.HTraceHttpHeaders;

import org.apache.htrace.impl.MilliSpan;

import feign.RequestTemplate;

public class HTraceFeignParams extends HTraceFeign {

    public HTraceFeignParams(RequestTemplate template) {
        super(template);
    }

    @Override
    void extractSpan() {
        long traceId = getParam(template, HTraceHttpHeaders.TraceId.getName());
        long spanId = getParam(template, HTraceHttpHeaders.SpanId.getName());
        long parentSpanId = getParam(template, HTraceHttpHeaders.ParentSpanId.getName());

        this.span = new MilliSpan(null, traceId, parentSpanId, spanId, null);
    }

    private Long getParam(RequestTemplate template, String paramName) {
        Collection<String> params = template.queries().get(paramName);
        if (params != null && !params.isEmpty()) {
            return Long.parseLong(params.iterator().next());    
        }
        return null;
    }
}
