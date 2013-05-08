package spikes.concurrency;

import com.google.code.tempusfugit.temporal.Duration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.code.tempusfugit.concurrency.ThreadUtils.sleep;

public class Producer {
  private AtomicInteger messageCount;
  private Duration interMessagePause;
  private BlockingQueue<String> publishQueue;

  private static final Executor exec = Executors.newFixedThreadPool(4);
  private String messageBody;

  public static Producer prepare(int messageCount) {
    Producer producer = new Producer();
    producer.messageCount = new AtomicInteger(messageCount);
    return producer;
  }

  public Producer messages() {
    return this;
  }

  public Producer pausing(Duration duration) {
    this.interMessagePause = duration;
    return this;
  }

  public Producer publishingTo(BlockingQueue<String> queue) {
    this.publishQueue = queue;
    return this;
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

  public Producer withBody(String messageBody) {
    this.messageBody = messageBody;
    return this;
  }
}
