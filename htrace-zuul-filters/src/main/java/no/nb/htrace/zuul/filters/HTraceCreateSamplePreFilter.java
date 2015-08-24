package no.nb.htrace.zuul.filters;

import java.util.concurrent.atomic.AtomicInteger;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import no.nb.htrace.core.HTraceHttpHeaders;

public class HTraceCreateSamplePreFilter extends ZuulFilter {
    private static final int DEFAULT_SAMPLE_RATE = 100;
    final AtomicInteger counter = new AtomicInteger();
    final int sampleRate;
    
    public HTraceCreateSamplePreFilter() {
        this(DEFAULT_SAMPLE_RATE);
    }
    
    public HTraceCreateSamplePreFilter(int sampleRate) {
        this.sampleRate = sampleRate;
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
        return counter.incrementAndGet()%sampleRate == 0;
    }

    private void addZuulRequestHeader(String header, String value) {
        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.addZuulRequestHeader(header, value);
    }
}