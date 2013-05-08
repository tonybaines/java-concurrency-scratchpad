package spikes.concurrency;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Consumer {
  private BlockingQueue<String> consumeQueue;
  private AtomicInteger receivedMessageCount = new AtomicInteger(0);

  private static final Executor exec = Executors.newSingleThreadExecutor();
  private String interestingContent;
  private final AtomicBoolean running = new AtomicBoolean(true);

  public static Consumer consumerOf(BlockingQueue<String> queue) {
    Consumer consumer = new Consumer();
    consumer.consumeQueue = queue;
    return consumer;
  }

  public Consumer interestedInMessagesContaining(String interestingContent) {
    this.interestingContent = interestingContent;
    return this;
  }

  public int totalInterestingMessages() {
    return receivedMessageCount.get();
  }

  public void go() throws InterruptedException {
    exec.execute(new Runnable() {
      @Override
      public void run() {
        while (running.get()) {
          try {
            String message = consumeQueue.take();
            System.out.println("Got a message");
            if (message.contains(interestingContent)) {
              System.out.println("... it was interesting");
              receivedMessageCount.incrementAndGet();
            }
          } catch (InterruptedException e) {
            break;
          }
        }
      }
    });
  }

  public void shutdown() {
    running.compareAndSet(true, false);
  }
}
