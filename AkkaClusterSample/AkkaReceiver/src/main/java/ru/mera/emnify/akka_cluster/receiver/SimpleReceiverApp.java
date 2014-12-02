package ru.mera.emnify.akka_cluster.receiver;

import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/*
 * A receiver application main class.
 * 
 * @author      Anna Khokhlova
 */

public class SimpleReceiverApp {
	public static void main(String[] args) {
        Config config = ConfigFactory.load("application_receiver");
	      ActorSystem system = ActorSystem.create("ClusterSystem", config);
	      system.actorOf(Props.create(SimpleListener.class),
	          "receiver");
	  }

}
