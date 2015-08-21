package no.nb.htrace.zuul.filters;

import java.util.concurrent.atomic.AtomicInteger;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import no.nb.htrace.core.HTraceHttpHeaders;

class HTraceCreateSamplePreFilter extends ZuulFilter {
    HTraceCreateSamplePreFilterData data = new HTraceCreateSamplePreFilterData();

    public HTraceCreateSamplePreFilter() {
        this(100);
    }
    
    public HTraceCreateSamplePreFilter(int limit) {
        this.data.limit = limit;
        data.counter = new AtomicInteger();
    }
    
    @Override
    public boolean shouldFilter() {
        if (data.counter.incrementAndGet()%data.limit == 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.addZuulRequestHeader(HTraceHttpHeaders.SAMPLED.toString(), "1");
        return null;
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }
    
}