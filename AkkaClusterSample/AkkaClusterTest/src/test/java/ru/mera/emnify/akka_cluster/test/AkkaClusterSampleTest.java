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
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.mera.emnify.akka_cluster.receiver.SimpleReceiverApp;
import ru.mera.emnify.akka_cluster.sender.SimpleSenderApp;

import static org.junit.Assert.assertTrue;

public class AkkaClusterSampleTest {
    protected static ActorSystem system;
    private LoggingAdapter log;

    public Object msg;

    @BeforeClass
    public static void setupSystem() {
        Config config = ConfigFactory.load("application_test");
        system = ActorSystem.create("ClusterSystem",config);
        SimpleSenderApp.main(new String[] {"2552"});
        SimpleReceiverApp.main(new String[]{});
    }

    @Test
    public void testSenderLocally() throws InterruptedException {
        log = Logging.getLogger(system, this);
        new JavaTestKit(system) {
            {
                assertTrue("Error: HELLO message is not sent by SimpleReporter",
                        TestUtils.waitLog("Sending a message HELLO", 3000));

                assertTrue("Error: HELLO message is not received by SimpleListener",
                        TestUtils.waitLog("Received a message: HELLO", 3000));

                assertTrue("Error: OK message is not received by SimpleReporter",
                        TestUtils.waitLog("Received a message: OK", 3000));

            }
        };
    }

    @AfterClass
    public static void shutdownSystem() {
        JavaTestKit.shutdownActorSystem(system);
    }

}
