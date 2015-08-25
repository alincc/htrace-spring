package no.nb.htrace.zuul.filters;

import javax.servlet.http.HttpServletRequest;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import no.nb.commons.web.util.UserUtils;
import no.nb.htrace.core.HTraceHttpHeaders;

public class HtraceAuthorizeSampleFilter extends ZuulFilter{
    public static final String IP_RANGE = "158.39.";
    
    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        HttpServletRequest request = getRequestFromContext();
        String ip = UserUtils.getClientIp(request);
        String sampled = request.getHeader(HTraceHttpHeaders.SAMPLED.toString());
        if (sampled != null) {
            if (ip.startsWith(IP_RANGE)) {
                addZuulHeader(HTraceHttpHeaders.SAMPLED.toString(), sampled);
            } else {
                addZuulHeader(HTraceHttpHeaders.SAMPLED.toString(), "0");
            }
        }
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

    private HttpServletRequest getRequestFromContext() {
        return RequestContext.getCurrentContext().getRequest();
    }

    private void addZuulHeader(String name, String value) {
        RequestContext.getCurrentContext().addZuulRequestHeader(name, value);
    }
    
}
