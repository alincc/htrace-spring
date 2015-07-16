package no.nb.htrace.zuul.filters;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.htrace.TraceScope;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

public class HTraceZuulPostFilter extends ZuulFilter  {

    @Override
    public Object run() {
        RequestContext.getCurrentContext().set("javaPostFilter-ran", true);

        TraceScope traceScope = getTraceFromRequest();
        
        annotateTraceSpanWithRequest(traceScope);

        annotateTraceSpanWithResponse(traceScope);
        
        traceScope.close();
        
        return null;
    }

    private TraceScope getTraceFromRequest() {
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        return (TraceScope)request.getAttribute("SPAN");
    }

    private void annotateTraceSpanWithRequest(TraceScope traceScope) {
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        traceScope.getSpan().addKVAnnotation("request".getBytes(), (request.getMethod() + " " + request.getRequestURI()).getBytes());
    }

    private void annotateTraceSpanWithResponse(TraceScope traceScope) {
        HttpServletResponse response = RequestContext.getCurrentContext().getResponse();
        traceScope.getSpan().addKVAnnotation("http.responsecode".getBytes(), (""+response.getStatus()).getBytes());
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

}
