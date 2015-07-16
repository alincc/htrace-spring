package no.nb.htrace.zuul.filters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
        
        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.setRequest(this.request);
    }
    
    @After
    public void tearDown() {
        Trace.currentSpan().stop();
    }
    
    @Test
    public void startNewTrace () {
        filter.run();
        RequestContext ctx = RequestContext.getCurrentContext();
        Map<String, String> requestHeaders = ctx.getZuulRequestHeaders();

        assertNotNull(HTraceHttpHeaders.TRACE_ID.toString() + " should not be null", requestHeaders.get(HTraceHttpHeaders.TRACE_ID.toString().toLowerCase()));
        assertNotNull(HTraceHttpHeaders.SPAN_ID.toString() + " should not be null", requestHeaders.get(HTraceHttpHeaders.SPAN_ID.toString().toLowerCase()));
        assertEquals("Description should be \"zuul\"", "zuul", Trace.currentSpan().getDescription());
    }
    


}
