package ru.mera.emnify.akka_cluster.test;
/**
 * Created by vboronin on 25.11.14.
 */

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.testkit.TestActorRef;
import ru.mera.emnify.akka_cluster.sender.SimpleReporter;
import com.typesafe.config.ConfigFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import akka.testkit.JavaTestKit;
import org.junit.Test;

/**
 * Created by vboronin on 24.11.2014.
 */

public class AkkaClusterSampleReporterTest {
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
                log.info("Creating FakeListener as receiver");
                final TestActorRef<FakeSimpleListener> ref1 = TestActorRef.create(system, Props.create(FakeSimpleListener.class), "receiver");
                final FakeSimpleListener fr = ref1.underlyingActor();

                log.info("Creating SimpleReporter as sender");
                final TestActorRef<SimpleReporter> ref2 = TestActorRef.create(system, Props.create(SimpleReporter.class), "sender");

                log.info("Waiting for HeartBeatMessage from sender to receiver");
                new AwaitCond(
                        duration("2 second"),  // maximum wait time
                        duration("500 millis") // interval at which to check the condition
                ) {
                    protected boolean cond() {
                        if (fr.getNbOfMsg() == 1) {
                            log.info("HeartBeatMessage received: TEST PASSED");
                            return true;
                        } else {
                            return false;
                        }
                    }
                };
            }
        };
    }

    @AfterClass
    public static void shutdownSystem() {
        JavaTestKit.shutdownActorSystem(system);
    }

}
