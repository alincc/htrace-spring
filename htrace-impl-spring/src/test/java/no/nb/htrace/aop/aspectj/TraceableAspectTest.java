package no.nb.htrace.aop.aspectj;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.htrace.Sampler;
import org.apache.htrace.Span;
import org.apache.htrace.Trace;
import org.apache.htrace.TraceScope;
import org.apache.http.HttpStatus;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import no.nb.htrace.annotation.Traceable;
import no.nb.htrace.aop.aspectj.TraceableAspect;
import no.nb.htrace.core.HTraceHttpHeaders;

@RunWith(MockitoJUnitRunner.class)
public class TraceableAspectTest {

    private TraceableAspect aspect;
    
    @Mock
    private ProceedingJoinPoint pjp;
    
    @Mock
    private Traceable traceable;
    
    @Mock
    private MethodSignature signature;

    @Before
    public void setUp() {
        aspect = new TraceableAspect();
        
    }

    @Test
    public void startNewTrace() throws Throwable {

        
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/test");
        MockHttpServletResponse response = new MockHttpServletResponse();
        response.setStatus(HttpStatus.SC_ACCEPTED);
        ServletRequestAttributes attributes = new ServletRequestAttributes(request, response);
        RequestContextHolder.setRequestAttributes(attributes);
        
        when(pjp.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("coolMethod");
        when(traceable.description()).thenReturn("some description");
        when(pjp.proceed()).thenAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                if (!Trace.isTracing()) {
                    throw new RuntimeException("Tracing not started");
                };
                return Trace.currentSpan();
            }
            
        });
        

        Span result = (Span)aspect.process(pjp, traceable);
        
        assertNotNull(result);
        
        verify(pjp).proceed();
    }

    @Test
    public void continueRemoteTrace() throws Throwable {

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/test");
        request.addHeader(HTraceHttpHeaders.TRACE_ID, "123");
        request.addHeader(HTraceHttpHeaders.SPAN_ID, "456");
        MockHttpServletResponse response = new MockHttpServletResponse();
        response.setStatus(HttpStatus.SC_ACCEPTED);
        ServletRequestAttributes attributes = new ServletRequestAttributes(request, response);
        RequestContextHolder.setRequestAttributes(attributes);

        when(pjp.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("coolMethod");
        when(traceable.description()).thenReturn("some description");
        when(pjp.proceed()).thenAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                if (!Trace.isTracing()) {
                    throw new RuntimeException("Tracing not started");
                };
                return Trace.currentSpan();
            }
            
        });
        

        Span result = (Span)aspect.process(pjp, traceable);
        
        verify(pjp).proceed();
        assertEquals(123, result.getTraceId());
    }

    @Test
    public void continueThreadTrace() throws Throwable {


        
        
        when(pjp.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("coolMethod");
        when(traceable.description()).thenReturn("some description");
        when(pjp.proceed()).thenAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                if (!Trace.isTracing()) {
                    throw new RuntimeException("Tracing not started");
                };
                return Trace.currentSpan();
            }
            
        });

        TraceScope traceScope = Trace.startSpan("test trace", Sampler.ALWAYS);
        Span result = (Span)aspect.process(pjp, traceable);
        traceScope.close();        
        
        verify(pjp).proceed();
        assertNotNull(result);
        assertEquals(traceScope.getSpan().getTraceId(), result.getTraceId());

    }

}
