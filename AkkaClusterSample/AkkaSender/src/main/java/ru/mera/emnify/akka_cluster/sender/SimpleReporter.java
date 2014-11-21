package ru.mera.emnify.akka_cluster.sender;

import java.util.ArrayList;

import ru.mera.emnify.akka_cluster.receiverapi.HeartBeatMessage;
import akka.actor.Address;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.Member;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.MemberRemoved;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.ClusterEvent.UnreachableMember;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class SimpleReporter extends UntypedActor  {
	
	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	protected ArrayList<Address> listeners = new ArrayList<Address>();
	
	Cluster cluster = Cluster.get(getContext().system());
	
	@Override
	 public void preStart() {
	     cluster.subscribe(getSelf(), ClusterEvent.initialStateAsEvents(), 
	        MemberEvent.class, UnreachableMember.class);
	 }
	  
	 @Override
	 public void postStop() {
	     cluster.unsubscribe(getSelf());
	 }

	
	public SimpleReporter() {		
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof MemberUp) {
		    MemberUp mUp = (MemberUp) message;
		    onMemberUp(mUp.member());
		    log.info("Member is Up: {}", mUp.member());
	    } else if (message instanceof UnreachableMember) {
            UnreachableMember mUnreachable = (UnreachableMember) message;
		    log.info("Member detected as unreachable: {}", mUnreachable.member());
	    } else if (message instanceof MemberRemoved) {
		    MemberRemoved mRemoved = (MemberRemoved) message;
		    log.info("Member is Removed: {}", mRemoved.member());		    		
		    boolean result = listeners.remove(mRemoved.member().address());
		    if (result) {
		    	log.info("Member was found and removed");
		    }		    	
	    } else if (message instanceof MemberEvent) {
	        // ignore
	    } else if (message instanceof HeartBeatMessage) {
	    	log.info("Received a message: {}", ((HeartBeatMessage)message).getMessage());
	    } else {
       	    unhandled(message);
	    }
	}
	
	private void onMemberUp(Member member) {
	    if (member.hasRole("receiver")) {
	    	log.info("New receiver is up " + member.address());
	    	listeners.add(member.address());
	    	getContext().actorSelection(member.address() + "/user/receiver").tell(new HeartBeatMessage("HELLO", getSelf()), getSelf());
	    }
	  }

}
