package no.nb.htrace.zuul.filters;

import static org.junit.Assert.*;

import java.util.Map;

import org.apache.htrace.Trace;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;

import com.netflix.zuul.context.RequestContext;

import no.nb.htrace.core.HTraceHttpHeaders;

@RunWith(MockitoJUnitRunner.class)
public class HTraceZuulPreFilterTest {

    private HTraceZuulPreFilter filter;
    
    private MockHttpServletRequest request = new MockHttpServletRequest();

    @Before
    public void setUp() {
        this.filter = new HTraceZuulPreFilter();
        
        RequestContext ctx = new RequestContext();
        RequestContext.testSetCurrentContext(ctx);
        ctx.setRequest(this.request);
    }
    
    @After
    public void tearDown() {
        if (Trace.currentSpan() != null) {
            Trace.currentSpan().stop();
        }
        
        RequestContext.testSetCurrentContext(null);
    }
    
    @Test
    public void testNoTracing () {
        boolean shouldFilter = filter.shouldFilter();
        assertFalse(shouldFilter);
    }

    @Test
    public void startNewTrace () {
        this.request.addHeader(HTraceHttpHeaders.SAMPLED.toString(), "1");
        
        filter.run();
        RequestContext ctx = RequestContext.getCurrentContext();
        Map<String, String> requestHeaders = ctx.getZuulRequestHeaders();

        assertNotNull(HTraceHttpHeaders.TRACE_ID.toString() + " should not be null", requestHeaders.get(HTraceHttpHeaders.TRACE_ID.toString().toLowerCase()));
        assertNotNull(HTraceHttpHeaders.SPAN_ID.toString() + " should not be null", requestHeaders.get(HTraceHttpHeaders.SPAN_ID.toString().toLowerCase()));
        assertEquals(HTraceHttpHeaders.SAMPLED.toString() + " should be 1", "1", requestHeaders.get(HTraceHttpHeaders.SAMPLED.toString().toLowerCase()));
        assertEquals("Description should be \"zuul\"", "zuul", Trace.currentSpan().getDescription());
    }

}
