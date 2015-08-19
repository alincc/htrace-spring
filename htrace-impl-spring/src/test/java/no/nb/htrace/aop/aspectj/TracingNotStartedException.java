package no.nb.htrace.aop.aspectj;

public class TracingNotStartedException extends Exception {

    private static final long serialVersionUID = 1L;

    public TracingNotStartedException(String message) {
        super(message);
    }
}
