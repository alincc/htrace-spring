package no.nb.htrace.zuul.filters;

import javax.servlet.http.HttpServletRequest;

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
        ctx.addZuulRequestHeader(HTraceHttpHeaders.SAMPLED.toString(), "1");
        
        return null;
    }

    private String getSampled(HttpServletRequest request) {
        String sampled = request.getHeader(HTraceHttpHeaders.SAMPLED.toString());
        return sampled != null ? sampled : "0";
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        String sampled = getSampled(ctx.getRequest());
        return "1".equals(sampled) ? true : false;
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
