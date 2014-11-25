package ru.mera.emnify.akka_cluster.receiver;

import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import ru.mera.emnify.akka_cluster.receiverapi.*;

public class SimpleListener extends UntypedActor  {
	
	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	
	public SimpleListener() {		
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof HeartBeatMessage) {
	    	log.info("Received a message: {}", ((HeartBeatMessage)message).getMessage());
	    	getSender().tell(new HeartBeatMessage("OK", getSelf()), getSelf());
	    } else {
       	    unhandled(message);
	    }
	}
}
