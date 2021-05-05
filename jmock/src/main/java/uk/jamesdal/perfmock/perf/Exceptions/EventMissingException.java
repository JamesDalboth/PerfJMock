package uk.jamesdal.perfmock.perf.Exceptions;

public class EventMissingException extends RuntimeException {
    @Override
    public String getMessage() {
        return "Event Missing From Timeline";
    }
}
