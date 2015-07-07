package no.nb.htrace.core;

import org.apache.htrace.Span;

public interface Traceable {

    public Span getSpan();

}
