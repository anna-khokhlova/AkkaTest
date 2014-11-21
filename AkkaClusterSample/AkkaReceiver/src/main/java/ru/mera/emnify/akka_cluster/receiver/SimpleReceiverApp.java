package ru.mera.emnify.akka_cluster.receiver;

import akka.actor.ActorSystem;
import akka.actor.Props;

public class SimpleReceiverApp {
	public static void main(String[] args) {
	      ActorSystem system = ActorSystem.create("ClusterSystem");
	      system.actorOf(Props.create(SimpleListener.class),
	          "receiver");
	  }

}
