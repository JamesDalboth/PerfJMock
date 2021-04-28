package uk.jamesdal.perfmock.BadMessageUploader;

import uk.jamesdal.perfmock.perf.concurrent.PerfThreadFactory;
import uk.jamesdal.perfmock.perf.concurrent.PerfThreadPoolExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

class BadMessageUploader {

    private final HttpMessageSender messageSender;
    private final PerfThreadFactory threadFactory;

    public BadMessageUploader(HttpMessageSender messageSender, PerfThreadFactory threadFactory) {
        this.messageSender = messageSender;
        this.threadFactory = threadFactory;
    }

    public void sendMessage(String content, String destination) {
        Message message = new Message(content);

        ExecutorService executorService = PerfThreadPoolExecutor.newFixedThreadPool(1, threadFactory);
        executorService.submit(sendMessage(message, destination));
    }

    private Callable<MessageResponse> sendMessage(Message message, String destination) {
        return () -> messageSender.send(message, destination);
    }
}
