package no.nb.htrace.interceptor;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Map;

import org.apache.htrace.Sampler;
import org.apache.htrace.Span;
import org.apache.htrace.Trace;
import org.apache.htrace.TraceScope;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import feign.RequestTemplate;
import no.nb.htrace.core.HTraceHttpHeaders;

public class HTraceFeignInterceptorTest {

    private HTraceFeignInterceptor interceptor;
    private RequestTemplate requestTemplate;
    
    @Before
    public void setUp() {
        interceptor = new HTraceFeignInterceptor();
        requestTemplate = new RequestTemplate();
    }
    
    @After
    public void tearDown() {
        interceptor = null;
        requestTemplate = null;
    }
    
    @Test
    public void testAddTraceHeadersToRequestTemplate() {
        TraceScope traceScope = Trace.startSpan("test", Sampler.ALWAYS);
        interceptor.apply(requestTemplate);
        traceScope.close();
        Map<String, Collection<String>> headers = requestTemplate.headers();
        Span span = traceScope.getSpan();
        
        headersShouldContain(HTraceHttpHeaders.TRACE_ID.toString(), ""+span.getTraceId(), headers);
        headersShouldContain(HTraceHttpHeaders.SPAN_ID.toString(), ""+span.getSpanId(), headers);

    }

    private void headersShouldContain(final String name, String value, Map<String, Collection<String>> headers) {
        assertTrue("RequestTemplate should contain header \""+name+"\"", headers.get(name).contains(value));
    }

    
    @Test
    public void testHeadersAsNoTracingStartet() {
        RequestTemplate requestTemplate = new RequestTemplate();

        interceptor.apply(requestTemplate);
        Map<String, Collection<String>> headers = requestTemplate.headers();
        
        assertEquals(0, headers.size());
    }
    
    
}
