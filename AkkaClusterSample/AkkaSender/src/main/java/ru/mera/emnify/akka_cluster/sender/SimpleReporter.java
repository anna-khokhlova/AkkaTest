package ru.mera.emnify.akka_cluster.sender;

import java.util.ArrayList;

import ru.mera.emnify.akka_cluster.receiverapi.HeartBeatMessage;
import akka.actor.Address;
import akka.actor.ActorSystem;
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
import scala.concurrent.duration.FiniteDuration;
import java.util.concurrent.TimeUnit;
import akka.actor.Cancellable;

public class SimpleReporter extends UntypedActor  {
	
	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	protected ArrayList<Address> listeners = new ArrayList<Address>();
	
	private Cluster cluster = Cluster.get(getContext().system());
	private ActorSystem system = getContext().system();
	final FiniteDuration timeout = FiniteDuration.create(20, TimeUnit.SECONDS);
	private Cancellable cancellable = null;

	
	@Override
	 public void preStart() {
	     cluster.subscribe(getSelf(), ClusterEvent.initialStateAsEvents(),
				 MemberEvent.class, UnreachableMember.class,
				 ClusterEvent.ReachableMember.class);
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
			if (removeListener(mUnreachable.member())){
				log.info("Receiver was found and removed");
			}
		} else if (message instanceof MemberRemoved) {
			MemberRemoved mRemoved = (MemberRemoved) message;
			log.info("Member is Removed: {}", mRemoved.member());
			if (removeListener(mRemoved.member())) {
				log.info("Receiver was found and removed");
			}
		} else if (message instanceof ClusterEvent.ReachableMember) {
			ClusterEvent.ReachableMember mReachable = (ClusterEvent.ReachableMember) message;
			log.info("Member detected as reachable: {}", mReachable.member());
			addListener(mReachable.member());
		} else if (message instanceof MemberEvent) {
		    //ignore
		} else if (message instanceof HeartBeatMessage) {
			log.info("Received a message: {}", ((HeartBeatMessage) message).getMessage());
		} else if (message.equals("Do")) {
			log.debug("Got a Do message");
			for (Address a: listeners) {
				getContext().actorSelection(a + "/user/receiver").tell(new HeartBeatMessage("HELLO", getSelf()), getSelf());
			}
		}else {
			unhandled(message);
	    }
	}
	
	private void onMemberUp(Member member) {
		if (member.hasRole("receiver")) {
			addListener(member);
			log.info("Sending a message HELLO");
			getContext().actorSelection(member.address() + "/user/receiver").tell(new HeartBeatMessage("HELLO", getSelf()), getSelf());
		}
	}

	private boolean removeListener(Member member) {
		boolean result = false;
		if (member.hasRole("receiver") ) {
			result = listeners.remove(member.address());
			if (listeners.size() == 0 && cancellable != null &&
					!cancellable.isCancelled()) {
				cancellable.cancel();
			}
		}
		return result;
	}

	private void addListener(Member member) {
	    if (member.hasRole("receiver") ) {
			if (listeners.size() == 0 &&
					(cancellable == null || cancellable.isCancelled())) {
				cancellable = system.scheduler().schedule(timeout, timeout, getSelf(), "Do",
						system.dispatcher(), null);
			}
			listeners.add(member.address());
		}
	}

}
