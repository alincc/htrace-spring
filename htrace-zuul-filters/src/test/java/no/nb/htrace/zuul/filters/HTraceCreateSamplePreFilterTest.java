package no.nb.htrace.zuul.filters;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.ZuulRunner;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.monitoring.TracerFactory;
import com.netflix.zuul.plugins.Tracer;

import no.nb.htrace.core.HTraceHttpHeaders;

@RunWith(MockitoJUnitRunner.class)
public class HTraceCreateSamplePreFilterTest {
    @Mock
    HttpServletRequest servletRequest;

    @Mock
    HttpServletResponse servletResponse;
    
    @Mock
    FilterChain filterChain;
    
    @Mock
    ZuulRunner zuulRunner;
    
    @Test
    public void whenCounterReachLimitThenRun() {
        ZuulFilter filter = spy(new HTraceCreateSamplePreFilter(2));

        TracerFactory.initialize(new Tracer());
        for(int i = 0; i < 5; i++) {
            filter.runFilter();
        }
        
        verify(filter, times(2)).run();
    }

    @Test
    public void whenRunThenSetSamplerHeader() {
        ZuulFilter filter = new HTraceCreateSamplePreFilter();

        filter.run();
        RequestContext ctx = RequestContext.getCurrentContext();
        assertTrue(ctx.getZuulRequestHeaders().containsKey(HTraceHttpHeaders.SAMPLED.toString().toLowerCase()));
    }
}
