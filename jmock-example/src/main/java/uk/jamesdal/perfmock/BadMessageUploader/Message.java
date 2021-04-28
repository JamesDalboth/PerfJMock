package uk.jamesdal.perfmock.BadMessageUploader;

import java.util.Objects;

public class Message {

    private final String message;

    public Message(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message1 = (Message) o;
        return message.equals(message1.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message);
    }
}
