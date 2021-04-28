package uk.jamesdal.perfmock.GoodMessageUploader;

import uk.jamesdal.perfmock.BadMessageUploader.HttpMessageSender;
import uk.jamesdal.perfmock.BadMessageUploader.Message;
import uk.jamesdal.perfmock.BadMessageUploader.MessageResponse;
import uk.jamesdal.perfmock.perf.concurrent.PerfThreadFactory;
import uk.jamesdal.perfmock.perf.concurrent.PerfThreadPoolExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

class GoodMessageUploader {

    private final HttpMessageSender messageSender;
    private final PerfThreadFactory threadFactory;

    public GoodMessageUploader(HttpMessageSender messageSender, PerfThreadFactory threadFactory) {
        this.messageSender = messageSender;
        this.threadFactory = threadFactory;
    }

    public void sendMessage(String content, String destination) {
        Message message = new Message(content);

        ExecutorService executorService = PerfThreadPoolExecutor.newFixedThreadPool(1, threadFactory);
        Future<MessageResponse> response = executorService.submit(sendMessage(message, destination));

        MessageResponse messageResponse = null;

        try {
            messageResponse = response.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        boolean success = messageResponse.isSuccessful();

        if (success) {
            System.out.println("Successful Message Upload");
        } else {
            System.out.println("Error During Message Upload");
        }
    }

    private Callable<MessageResponse> sendMessage(Message message, String destination) {
        return () -> messageSender.send(message, destination);
    }
}
