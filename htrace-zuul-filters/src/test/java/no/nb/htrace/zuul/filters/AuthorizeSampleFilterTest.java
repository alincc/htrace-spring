package no.nb.htrace.zuul.filters;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.netflix.zuul.context.RequestContext;

import no.nb.commons.web.util.UserUtils;
import no.nb.htrace.core.HTraceHttpHeaders;

public class AuthorizeSampleFilterTest {

    private MockHttpServletRequest request = new MockHttpServletRequest();

    @Before
    public void setUp() {
        RequestContext ctx = new RequestContext();
        RequestContext.testSetCurrentContext(ctx);
        ctx.setRequest(this.request);
    }
    
    @After
    public void tearDown() {
        RequestContext.testSetCurrentContext(null);
    }
    
    @Test
    public void ifInhouseThenAllowSampleHeader() {
        HtraceAuthorizeSampleFilter filter = new HtraceAuthorizeSampleFilter();
        String ip = "158.39.123.123";
        request.addHeader(UserUtils.REAL_IP_HEADER, ip);
        request.addHeader(HTraceHttpHeaders.SAMPLED.toString(), "1");
        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attributes);
        
        filter.run();
        
        RequestContext ctx = RequestContext.getCurrentContext();
        assertEquals("1", ctx.getZuulRequestHeaders().get(HTraceHttpHeaders.SAMPLED.toString().toLowerCase()));
    }

    @Test
    public void ifNotInhouseThenDenySampleHeader() {
        HtraceAuthorizeSampleFilter filter = new HtraceAuthorizeSampleFilter();
        String ip = "200.39.123.123";
        request.addHeader(UserUtils.REAL_IP_HEADER, ip);
        request.addHeader(HTraceHttpHeaders.SAMPLED.toString(), "1");
        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attributes);
        
        filter.run();
        
        RequestContext ctx = RequestContext.getCurrentContext();
        assertEquals("0", ctx.getZuulRequestHeaders().get(HTraceHttpHeaders.SAMPLED.toString().toLowerCase()));
    }
    
}
