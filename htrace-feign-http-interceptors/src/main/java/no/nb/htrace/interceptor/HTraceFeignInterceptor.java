package no.nb.htrace.interceptor;

import java.util.Collection;

import no.nb.htrace.core.HTraceHttpHeaders;

import org.springframework.stereotype.Component;

import feign.RequestInterceptor;
import feign.RequestTemplate;

@Component
public class HTraceFeignInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        HTraceFeign p = null;
        if (spanIsInParams(template)) {
            p = new HTraceFeignParams(template);
        } else {
            p = new HTraceFeignRequest(template);
        }
        
        p.setHeaders();
    }
    
    private boolean spanIsInParams(RequestTemplate template) {
        Collection<String> params = template.queries().get(HTraceHttpHeaders.SpanId.getName());
        if (params != null && !params.isEmpty()) {
            return true;    
        }  else {
            return false;
        }
         
    }

}

