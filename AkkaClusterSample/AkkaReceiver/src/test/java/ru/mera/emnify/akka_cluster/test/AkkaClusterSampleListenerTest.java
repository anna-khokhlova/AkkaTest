package ru.mera.emnify.akka_cluster.test;
/**
 * Created by vboronin on 25.11.14.
 */

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.testkit.JavaTestKit;
import akka.testkit.TestActorRef;
import com.typesafe.config.ConfigFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.mera.emnify.akka_cluster.receiver.SimpleListener;
import ru.mera.emnify.akka_cluster.receiverapi.HeartBeatMessage;

import static org.junit.Assert.assertTrue;

/**
 * Created by vboronin on 24.11.2014.
 */

public class AkkaClusterSampleListenerTest {
    protected static ActorSystem system;
    private LoggingAdapter log;

    public Object msg;

    @BeforeClass
    public static void setupSystem() {
        system = ActorSystem.create("ClusterSystem",
                ConfigFactory.parseResources("application_test.conf"));
    }

    @Test
    public void testSenderLocally() throws InterruptedException {
        log = Logging.getLogger(system, this);
        new JavaTestKit(system) {
            {
                log.info("Creating SimpleListener as receiver");
                final TestActorRef<SimpleListener> ref2 = TestActorRef.create(system, Props.create(SimpleListener.class), "receiver");
                log.info("Sending HELLO via HeartBeatMessage to receiver");
                ref2.tell(new HeartBeatMessage("HELLO", getRef()), getRef());

                log.info("Waiting for HeartBeatMessage reply from receiver");
                HeartBeatMessage msg = expectMsgClass(duration("1 sec"), HeartBeatMessage.class);
                log.info("HeartBeatMessage reply from receiver received: " + msg.getMessage() + ". TEST PASSED");
            }
        };
    }

    @AfterClass
    public static void shutdownSystem() {
        JavaTestKit.shutdownActorSystem(system);
    }

}
