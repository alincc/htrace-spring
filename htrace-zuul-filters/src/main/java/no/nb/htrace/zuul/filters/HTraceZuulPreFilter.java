package no.nb.htrace.zuul.filters;

import org.apache.htrace.Sampler;
import org.apache.htrace.Trace;
import org.apache.htrace.TraceScope;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import no.nb.htrace.core.HTraceHttpHeaders;

public class HTraceZuulPreFilter extends ZuulFilter  {

    @Override
    public Object run() {
        RequestContext.getCurrentContext().set("javaPreFilter-ran", true);

        TraceScope span = Trace.startSpan("zuul", Sampler.ALWAYS);
        
        RequestContext ctx = RequestContext.getCurrentContext();

        ctx.getRequest().setAttribute("SPAN", span);
        ctx.addZuulRequestHeader(HTraceHttpHeaders.TRACE_ID.toString(), ""+span.getSpan().getTraceId());
        ctx.addZuulRequestHeader(HTraceHttpHeaders.SPAN_ID.toString(), ""+span.getSpan().getSpanId());
        
        return null;
    }

    @Override
    public boolean shouldFilter() {
        return true;
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
