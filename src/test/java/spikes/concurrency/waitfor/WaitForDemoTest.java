package spikes.concurrency.waitfor;

import com.google.code.tempusfugit.temporal.Condition;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import spikes.concurrency.Consumer;
import spikes.concurrency.Producer;

import java.util.concurrent.*;

import static com.google.code.tempusfugit.temporal.Duration.seconds;
import static com.google.code.tempusfugit.temporal.Timeout.timeout;
import static com.google.code.tempusfugit.temporal.WaitFor.waitOrTimeout;

public class WaitForDemoTest {
  @Rule
  public Timeout globalTimeout = new Timeout((int) seconds(20).inMillis());

  @Test
  public void shouldWaitForExpectationsToBeMet() throws TimeoutException, InterruptedException {

    // Given a simple producer/consumer system
    final BlockingQueue<String> queue = new ArrayBlockingQueue<String>(5);
    final Producer producer1 = Producer.prepare(10).messages().pausing(seconds(1)).publishingTo(queue).withBody("Producer1");
    final Producer producer2 = Producer.prepare(10).messages().pausing(seconds(1)).publishingTo(queue).withBody("Producer2");
    final Consumer consumer = Consumer.consumerOf(queue).interestedInMessagesContaining("2");

    // When the system is started
    producer1.go();
    producer2.go();
    consumer.go();

    // Then the system will have reached the expected state before timing-out
    waitOrTimeout(new Condition() {
      public boolean isSatisfied() {
        return consumer.totalInterestingMessages() == 10;
      }
    }, timeout(seconds(15)));

    consumer.shutdown();
  }

}
