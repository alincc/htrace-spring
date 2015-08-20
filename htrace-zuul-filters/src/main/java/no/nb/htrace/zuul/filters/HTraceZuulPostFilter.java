package no.nb.htrace.zuul.filters;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.htrace.TraceScope;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import no.nb.htrace.core.HTraceHttpHeaders;

public class HTraceZuulPostFilter extends ZuulFilter  {

    @Override
    public Object run() {
        RequestContext.getCurrentContext().set("javaPostFilter-ran", true);

        TraceScope traceScope = getTraceFromRequest();
        
        annotateTraceSpanWithRequest(traceScope);

        annotateTraceSpanWithResponse(traceScope);
        
        addTraceHeadersToResponse(traceScope);
        
        traceScope.close();
        
        return null;
    }

    private void addTraceHeadersToResponse(TraceScope traceScope) {
        HttpServletResponse response = getResponseFromContext();
        response.addHeader("traceId", "" + traceScope.getSpan().getTraceId());
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        String sampled = getSampled(ctx.getRequest());
        return "1".equals(sampled) ? true : false;
    }

    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    private TraceScope getTraceFromRequest() {
        HttpServletRequest request = getRequestFromContext();
        return (TraceScope)request.getAttribute("SPAN");
    }

    private void annotateTraceSpanWithRequest(TraceScope traceScope) {
        HttpServletRequest request = getRequestFromContext();
        traceScope.getSpan().addKVAnnotation("request".getBytes(), (request.getMethod() + " " + request.getRequestURI()).getBytes());
    }

    private void annotateTraceSpanWithResponse(TraceScope traceScope) {
        HttpServletResponse response = getResponseFromContext();
        traceScope.getSpan().addKVAnnotation("http.responsecode".getBytes(), (""+response.getStatus()).getBytes());
        traceScope.getSpan().addKVAnnotation("traceId".getBytes(), (""+traceScope.getSpan().getTraceId()).getBytes());
    }

    private String getSampled(HttpServletRequest request) {
        String sampled = request.getHeader(HTraceHttpHeaders.SAMPLED.toString());
        return sampled != null ? sampled : "0";
    }
    
    protected HttpServletRequest getRequestFromContext() {
        return RequestContext.getCurrentContext().getRequest();
    }

    private HttpServletResponse getResponseFromContext() {
        return RequestContext.getCurrentContext().getResponse();  
    }


}
