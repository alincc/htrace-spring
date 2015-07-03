package no.nb.htrace.zuul.filters;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.htrace.Span;
import org.apache.htrace.Trace;
import org.apache.htrace.TraceInfo;
import org.apache.htrace.impl.MilliSpan;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

public class HTraceZuulPreFilter extends ZuulFilter  {

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext.getCurrentContext().set("javaPreFilter-ran", true);

        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        
        long traceId = 0;
        long spanId = 0;
        long parentSpanId = 0;
        
        String desccription = "zuul";
        
        long zipkinId = UUID.randomUUID().getLeastSignificantBits();
        if (request.getHeader("X-B3-TraceId") != null) {
            traceId = new Long(request.getHeader("X-B3-TraceId"));
        } else {
            traceId = zipkinId;
        }
        
        if (request.getHeader("X-B3-SpanId") != null) {
            spanId = new Long(request.getHeader("X-B3-SpanId"));
        } else {
            spanId = zipkinId;
        }
        
        if (request.getHeader("X-B3-ParentSpanId") != null) {
            parentSpanId = new Long(request.getHeader("X-B3-ParentSpanId"));
        }
        
        if (request.getHeader("X-B3-TraceId") != null) {
            Span parent = new MilliSpan.Builder().
                description(request.getServletPath()).
                traceId(traceId).
                parents(new long[] {parentSpanId} ).
                spanId(spanId).
                processId("zuul").
                begin(System.currentTimeMillis()).
                build();
            Trace.startSpan(desccription, parent);
        } else {
            TraceInfo info = new TraceInfo(traceId, spanId);
            Trace.startSpan(desccription, info);
        }
        
        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.addZuulRequestHeader("X-B3-TraceId", ""+traceId);
        ctx.addZuulRequestHeader("X-B3-SpanId", ""+spanId);
        ctx.addZuulRequestHeader("X-B3-ParentSpanId", ""+parentSpanId);
        
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
