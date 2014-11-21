package ru.mera.emnify.akka_cluster.receiverapi;

import java.io.Serializable;

import akka.actor.ActorRef;

public class HeartBeatMessage implements Serializable {	
	
	private static final long serialVersionUID = 1L;
	private final String message;
	private final ActorRef sender;
	
	public HeartBeatMessage(String aMessage, ActorRef aSender) {
		message = aMessage;
		sender = aSender;
	}
	
	public String getMessage() {
		return message;
	}
	
	public ActorRef getSender() {
		return sender;
	}

}
