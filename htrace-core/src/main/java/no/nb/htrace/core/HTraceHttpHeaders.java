package no.nb.htrace.core;

public enum HTraceHttpHeaders {
    
    /**
     * Trace id http header field name.
     */
    TraceId("X-B3-TraceId"),
    
    /**
     * Span id http header field name.
     */
    SpanId("X-B3-SpanId"),
    
    /**
     * Parent span id http header field name.
     */
    ParentSpanId("X-B3-ParentSpanId"),
    
    /**
     * Sampled http header field name. Indicates if this trace should be sampled or not.
     */
    Sampled("X-B3-Sampled"),
    
    /**
     * 
     * Span name as submitted by client.
     * 
     * By default we will use the URL as span name but when we want to do grouping and aggregation of service requests using
     * URL is not always a good idea as it might contain variable parameters that make it difficult to match same service
     * requests. Providing a separate name for it can help.
     */
    SpanName("X-B3-SpanName"),
    
    Flags("X-B3-Flags");

    private final String name;

    HTraceHttpHeaders(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
