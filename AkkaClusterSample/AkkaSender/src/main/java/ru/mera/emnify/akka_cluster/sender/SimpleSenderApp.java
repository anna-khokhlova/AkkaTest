package ru.mera.emnify.akka_cluster.sender;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.actor.Props;

public class SimpleSenderApp {
	public static void main(String[] args) {
		if (args.length == 0) {
		      startup("0");
		} else {
		      startup(args[0]);
		}
    }

	/**
	 * This method creates an actor system, starts a sender and join it
	 * to the cluster.
	 *
	 * @param  port  a port number which be used by a sender for cluster
	 *               communication
	 */

    public static void startup(String port) {	    
	    // Override the configuration of the port
	    Config config = ConfigFactory.parseString(
	        "akka.remote.netty.tcp.port=" + port).withFallback(
	        ConfigFactory.load());
	      
	    ActorSystem system = ActorSystem.create("ClusterSystem", config);	      
	    system.actorOf(Props.create(SimpleReporter.class), "sender");	    
	}
}
