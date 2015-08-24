package no.nb.htrace.zuul.filters;

import java.util.concurrent.atomic.AtomicInteger;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import no.nb.htrace.core.HTraceHttpHeaders;

public class HTraceCreateSamplePreFilter extends ZuulFilter {
    private static final int DEFAULT_STEP_SIZE = 100;
    final AtomicInteger counter = new AtomicInteger();
    final int stepSize;
    
    public HTraceCreateSamplePreFilter() {
        this(DEFAULT_STEP_SIZE);
    }
    
    public HTraceCreateSamplePreFilter(int stepSize) {
        this.stepSize = stepSize;
    }
    
    @Override
    public boolean shouldFilter() {
        return isCounterAtStepSize();
    }

    @Override
    public Object run() {
        addZuulRequestHeader(HTraceHttpHeaders.SAMPLED.toString(), "1");
        return null;
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }
    
    private boolean isCounterAtStepSize() {
        return counter.incrementAndGet()%stepSize == 0;
    }

    private void addZuulRequestHeader(String header, String value) {
        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.addZuulRequestHeader(header, value);
    }
}