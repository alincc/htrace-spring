package no.nb.htrace.zuul.filters;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.htrace.TraceScope;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import no.nb.htrace.core.HTraceHttpHeaders;

public class HTraceZuulPostFilter extends ZuulFilter  {

    @Override
    public boolean shouldFilter() {
        String sampled = getSampled();
        return "1".equals(sampled) ? true : false;
    }

    @Override
    public Object run() {
        TraceScope traceScope = getTraceFromRequestContext();
        
        annotateTraceSpanWithRequest(traceScope);

        annotateTraceSpanWithResponse(traceScope);
        
        addTraceHeadersToResponse(traceScope);
        
        traceScope.close();
        
        return null;
    }

    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 3;
    }
    
    @Override
    public boolean isStaticFilter() {
        return false;
    }

    private String getSampled() {
        RequestContext ctx = RequestContext.getCurrentContext();
        Map<String, String> requestHeaders = ctx.getZuulRequestHeaders();
        String sampled = requestHeaders.get(HTraceHttpHeaders.SAMPLED.toString().toLowerCase());
        return sampled != null ? sampled : "0";
    }
    
    private TraceScope getTraceFromRequestContext() {
        RequestContext ctx = RequestContext.getCurrentContext();
        return (TraceScope)ctx.getRequest().getAttribute("SPAN");
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

    private void addTraceHeadersToResponse(TraceScope traceScope) {
        HttpServletResponse response = getResponseFromContext();
        response.addHeader("traceId", "" + traceScope.getSpan().getTraceId());
    }
    
    protected HttpServletRequest getRequestFromContext() {
        return RequestContext.getCurrentContext().getRequest();
    }

    private HttpServletResponse getResponseFromContext() {
        return RequestContext.getCurrentContext().getResponse();  
    }

}
