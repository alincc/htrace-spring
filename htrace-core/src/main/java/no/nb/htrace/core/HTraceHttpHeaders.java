package no.nb.htrace.core;

public interface HTraceHttpHeaders {
    
    public static String TRACE_ID = "X-B3-TraceId";
    public static String SPAN_ID = "X-B3-SpanId";
    public static String PARENT_SPAN_ID = "X-B3-ParentSpanId";
    public static String SAMPLED = "X-B3-Sampled";
    public static String FLAGS = "X-B3-Flags";

}
