package no.nb.htrace.interceptor;

import org.apache.htrace.Span;
import org.apache.htrace.Trace;
import org.springframework.stereotype.Component;

import feign.RequestInterceptor;
import feign.RequestTemplate;

@Component
public class HTraceFeignInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        
        TraceRequestTemplate traceRequestTemplate = new TraceRequestTemplate(requestTemplate);
        traceRequestTemplate.addTraceHeadersToTemplate(getCurrentSpan());
    }

    private Span getCurrentSpan() {
        return Trace.currentSpan();
    }

}

