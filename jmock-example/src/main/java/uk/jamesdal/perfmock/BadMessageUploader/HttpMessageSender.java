package uk.jamesdal.perfmock.BadMessageUploader;

public interface HttpMessageSender {
    MessageResponse send(Message message, String destination);
}
