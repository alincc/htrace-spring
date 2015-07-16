package no.nb.htrace.aop.aspectj;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import no.nb.htrace.annotation.Traceable;

@RunWith(MockitoJUnitRunner.class)
public class AopTraceableRequestTest {

    private AopTraceableRequest tracableRequest;
    
    @Mock
    private ProceedingJoinPoint joinPoint;
    
    @Mock
    private Traceable traceable;
    
    @Mock
    private MethodSignature signature;

    @Before
    public void setUp() {
        tracableRequest = new AopTraceableRequest(joinPoint, traceable);
    }

    @Test
    public void getTrecableDescription() {
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("coolMethod");
        when(traceable.description()).thenReturn("some description");

        assertEquals("some description", tracableRequest.getDescription());
    }

    @Test
    public void ifTraceableDescriptionIsEmptyThenDescriptionShouldBeMethodName() {
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("coolMethod");
        when(traceable.description()).thenReturn("");

        assertEquals("coolMethod", tracableRequest.getDescription());
    }

}
