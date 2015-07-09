package no.nb.htrace.zuul.filters;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Map;

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
    
    @Test
    public void startNewTrace () {

        filter.run();
        RequestContext ctx = RequestContext.getCurrentContext();
        Map<String, String> requestHeaders = ctx.getZuulRequestHeaders();

        assertNotNull(HTraceHttpHeaders.TRACE_ID + " should not be null", requestHeaders.get(HTraceHttpHeaders.TRACE_ID.toLowerCase()));
        assertNotNull(HTraceHttpHeaders.SPAN_ID + " should not be null", requestHeaders.get(HTraceHttpHeaders.SPAN_ID.toLowerCase()));
    }
    


}
