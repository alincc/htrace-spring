package no.nb.htrace.core;

public enum HTraceHttpHeaders {
    
    TRACE_ID("X-B3-TraceId"),
    SPAN_ID("X-B3-SpanId"),
    PARENT_SPAN_ID("X-B3-ParentSpanId"),
    SAMPLED("X-B3-Sampled"),
    FLAGS("X-B3-Flags");
    
    private final String header;

    HTraceHttpHeaders(String header) {
        this.header = header;
    }

    public String getHeader() {
        return header;
    }

    @Override
    public String toString() {
        return this.getHeader();
    }
}
