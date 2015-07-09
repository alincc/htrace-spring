package no.nb.htrace.zuul.filters;

import javax.servlet.http.HttpServletRequest;

import org.apache.htrace.Sampler;
import org.apache.htrace.Trace;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import no.nb.htrace.aop.aspectj.TraceableRequest;
import no.nb.htrace.core.HTraceHttpHeaders;

public class HTraceZuulPreFilter extends ZuulFilter  {

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        Trace.setProcessId("zuul");
        RequestContext.getCurrentContext().set("javaPreFilter-ran", true);

        TraceableRequest traceableRequest = new TraceableRequest("zuul");
        traceableRequest.startTrace();

        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.addZuulRequestHeader(HTraceHttpHeaders.TRACE_ID, ""+Trace.currentSpan().getTraceId());
        ctx.addZuulRequestHeader(HTraceHttpHeaders.SPAN_ID, ""+Trace.currentSpan().getSpanId());
        
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
