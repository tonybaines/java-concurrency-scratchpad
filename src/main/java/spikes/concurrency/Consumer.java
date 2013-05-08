package spikes.concurrency;

import com.google.code.tempusfugit.concurrency.annotations.GuardedBy;
import com.google.code.tempusfugit.concurrency.annotations.Immutable;
import com.google.code.tempusfugit.concurrency.annotations.ThreadSafe;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
public class Consumer {
  private static final Executor exec = Executors.newSingleThreadExecutor();

  @GuardedBy(lock = GuardedBy.Type.ITSELF)
  private final BlockingQueue<String> consumeQueue;
  private final String interestingContent;
  @GuardedBy(lock = GuardedBy.Type.ITSELF)
  private final AtomicInteger receivedMessageCount = new AtomicInteger(0);
  @GuardedBy(lock = GuardedBy.Type.ITSELF)
  private final AtomicBoolean running = new AtomicBoolean(true);

  public Consumer(BlockingQueue<String> queue, String interestingContent) {
    this.consumeQueue = queue;
    this.interestingContent = interestingContent;
  }

  public static ConsumerBuilder consumerOf(BlockingQueue<String> queue) {
    ConsumerBuilder builder = new ConsumerBuilder(queue);
    return builder;
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

  @Immutable
  public static class ConsumerBuilder {
    private BlockingQueue<String> queue;
    private String interestingContent;

    public ConsumerBuilder(BlockingQueue<String> queue) {
      this.queue = queue;
    }

    public ConsumerBuilder interestedInMessagesContaining(String interestingContent) {
      this.interestingContent = interestingContent;
      return this;
    }

    public Consumer build() throws InterruptedException {
      return new Consumer(queue, interestingContent);
    }
  }
}
