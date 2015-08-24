package no.nb.htrace.zuul.filters;

import java.util.Map;

import org.apache.htrace.Sampler;
import org.apache.htrace.Trace;
import org.apache.htrace.TraceScope;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import no.nb.htrace.core.HTraceHttpHeaders;

public class HTraceZuulPreFilter extends ZuulFilter  {

    @Override
    public boolean shouldFilter() {
        String sampled = getSampled();
        return "1".equals(sampled) ? true : false;
    }
    
    @Override
    public Object run() {
        TraceScope span = Trace.startSpan("zuul", Sampler.ALWAYS);
        
        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.getRequest().setAttribute("SPAN", span);
        ctx.addZuulRequestHeader(HTraceHttpHeaders.TRACE_ID.toString(), ""+span.getSpan().getTraceId());
        ctx.addZuulRequestHeader(HTraceHttpHeaders.SPAN_ID.toString(), ""+span.getSpan().getSpanId());
        ctx.addZuulRequestHeader(HTraceHttpHeaders.SAMPLED.toString(), "1");
        
        return null;
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 2;
    }

    @Override
    public boolean isStaticFilter() {
        return false;
    }

    private String getSampled() {
        String sampled = getSampledFromZuulRequestHeader();
        return sampled != null ? sampled : "0";
    }

    private String getSampledFromZuulRequestHeader() {
        RequestContext ctx = RequestContext.getCurrentContext();
        Map<String, String> requestHeaders = ctx.getZuulRequestHeaders();
        return requestHeaders.get(HTraceHttpHeaders.SAMPLED.toString().toLowerCase());
    }

}
