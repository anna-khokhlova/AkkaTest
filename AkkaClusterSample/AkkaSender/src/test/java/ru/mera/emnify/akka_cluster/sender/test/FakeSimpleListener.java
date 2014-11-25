package ru.mera.emnify.akka_cluster.sender.test; /**
 * Created by vboronin on 25.11.14.
 */
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import ru.mera.emnify.akka_cluster.receiverapi.HeartBeatMessage;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by vboronin on 24.11.2014.
 */
public class FakeSimpleListener extends UntypedActor {
    volatile List<HeartBeatMessage> msgs = new LinkedList();
    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public void onReceive(Object o) throws Exception {
        if (o instanceof HeartBeatMessage) {
            HeartBeatMessage msg = (HeartBeatMessage) o;
            msgs.add(msg);
            log.info("Received message: " + msg.getMessage());
        }
    }
    public int getNbOfMsg() { return msgs.size(); }
}

