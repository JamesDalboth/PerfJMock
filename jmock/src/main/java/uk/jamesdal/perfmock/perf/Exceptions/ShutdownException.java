package uk.jamesdal.perfmock.perf.Exceptions;

public class ShutdownException extends RuntimeException {
    @Override
    public String getMessage() {
        return "Service is currently shutdown";
    }
}
