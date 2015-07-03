package no.nb.htrace.zuul.filters;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.htrace.Trace;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

public class HTraceZuulPostFilter extends ZuulFilter  {

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext.getCurrentContext().set("javaPostFilter-ran", true);

        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        HttpServletResponse response = RequestContext.getCurrentContext().getResponse();
        
        Trace.currentSpan().addKVAnnotation("request".getBytes(), (request.getMethod() + " " + request.getRequestURI()).getBytes());
        Trace.currentSpan().addKVAnnotation("http.responsecode".getBytes(), (""+response.getStatus()).getBytes());
        Trace.currentSpan().stop();
        
        return null;
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
