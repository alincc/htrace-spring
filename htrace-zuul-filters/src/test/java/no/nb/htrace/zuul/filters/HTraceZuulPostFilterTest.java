package no.nb.htrace.zuul.filters;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.apache.htrace.Sampler;
import org.apache.htrace.Trace;
import org.apache.htrace.TraceScope;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.netflix.zuul.context.RequestContext;

@RunWith(MockitoJUnitRunner.class)
public class HTraceZuulPostFilterTest {

    private HTraceZuulPostFilter filter;
    
    private MockHttpServletRequest request = new MockHttpServletRequest();
    private MockHttpServletResponse response = new MockHttpServletResponse();

    @Before
    public void setUp() {
        this.filter = new HTraceZuulPostFilter();
        
        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.setRequest(request);
        ctx.setResponse(response);
    }
    
    @Test
    public void endTrace () {
        TraceScope traceScope = Trace.startSpan("test", Sampler.ALWAYS);
        request.setMethod(HttpMethod.GET.name());
        request.setRequestURI("/test");
        
        filter.run();
        traceScope.close();
        Map<byte[], byte[]> kvAnnotations = traceScope.getSpan().getKVAnnotations();

        kvAnnotations.forEach((k, v) -> {
            String key = new String(k);
            String value = new String(v);
            
            if (key.equals("http.responsecode")) {
                assertEquals("ResponseCode should be 200", "200", value);
            } else if (key.equals("request")) {
                assertEquals("Request should be \"GET /test\"", "GET /test", value);
            }
        });
    }
    


}
