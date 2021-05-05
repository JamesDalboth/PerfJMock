package uk.jamesdal.perfmock.perf.exceptions;

public class ShutdownException extends RuntimeException {
    @Override
    public String getMessage() {
        return "Service is currently shutdown";
    }
}
