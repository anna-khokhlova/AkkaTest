## AkkaTest
========

## Akka test applications

### Description
````
1. Simple AKKA cluster example consisting of a sender application and a receiver application.
2. This project is a multi-module maven project.
3. A sender is sending HELLO message to each receiver periodically with 20 seconds timeout.
4. A receiver just replies with OK message to the received HELLO message.
5. There are also two unit tests and one integration test.
6. The application logs are written to the AkkaReceiver/log and AkkaSender/log directories.
````
### Installation and Configuring
````
The application is build with "mvn clean install" command. 
The receiver and sender are started with "mvn exec:java" command in the directories 
AkkaReceiver and AkkaSender.
The seed node (sender) is started with "mvn exec:java -Dexec.args="<port number>"" command.
The receiver and sender ip addresses, seed node address should be configured in the
AkkaReceiver/src/main/resources/application_receiver.conf and 
AkkaSender/src/main/resources/application.conf files.
````
