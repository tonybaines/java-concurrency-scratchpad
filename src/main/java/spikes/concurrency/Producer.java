package spikes.concurrency;

import com.google.code.tempusfugit.concurrency.annotations.GuardedBy;
import com.google.code.tempusfugit.concurrency.annotations.ThreadSafe;
import com.google.code.tempusfugit.temporal.Duration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.code.tempusfugit.concurrency.ThreadUtils.sleep;

@ThreadSafe
public class Producer {
  private static final Executor exec = Executors.newFixedThreadPool(4);

  @GuardedBy(lock = GuardedBy.Type.ITSELF)
  private final AtomicInteger messageCount;
  private final Duration interMessagePause;
  @GuardedBy(lock = GuardedBy.Type.ITSELF)
  private final BlockingQueue<String> publishQueue;
  private final String messageBody;

  public Producer(BlockingQueue<String> publishQueue, String messageBody, int messageCount, Duration interMessagePause) {
    this.publishQueue = publishQueue;
    this.messageBody = messageBody;
    this.interMessagePause = interMessagePause;
    this.messageCount = new AtomicInteger(messageCount);
  }

  public void go() {
    exec.execute(new Runnable() {
      @Override
      public void run() {
        while (messageCount.getAndDecrement() > 0) {
          sleep(interMessagePause);
          publishQueue.offer(messageBody);
        }
      }
    });
  }

  public static ProducerBuilder publishingTo(BlockingQueue<String> queue) {
    return new ProducerBuilder(queue);
  }

  public static class ProducerBuilder {
    private int messageCount;
    private Duration interMessagePause;
    private final BlockingQueue<String> publishQueue;
    private String messageBody;

    public ProducerBuilder(BlockingQueue<String> publishQueue) {
      this.publishQueue = publishQueue;
    }

    public ProducerBuilder send(int messageCount) {
      this.messageCount = messageCount;
      return this;
    }

    public ProducerBuilder messages() {
      return this;
    }

    public ProducerBuilder pausing(Duration duration) {
      this.interMessagePause = duration;
      return this;
    }

    public ProducerBuilder withBody(String messageBody) {
      this.messageBody = messageBody;
      return this;
    }

    public Producer build() {
      return new Producer(publishQueue, messageBody, messageCount, interMessagePause);
    }
  }
}
