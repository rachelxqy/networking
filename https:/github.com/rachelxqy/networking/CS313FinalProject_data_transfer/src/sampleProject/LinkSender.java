package sampleProject;

// LinkSender sends a message to LinkReceiver and receives a reply.
// LinkReceiver needs to be started before LinkSender.

public class LinkSender {

	static int senderPort = 3206;   // port number used by sender
	static int receiverPort = 3306; // port number used by receiver

	public static void main (String args[]) throws Exception 
	{	

		int lengthMessageToSend; 
		int lengthMessageReceived = 0;
		String messageToSend;
		String messageReceived;
		byte[] sendingBuffer = new byte[512];
		byte[] receivingBuffer = new byte[512];

		// Set up a link with source and destination ports
		Link myLink = new SimpleLink(senderPort, receiverPort);

		// Prepare a message	
		messageToSend = "Hello World! HaHa!";
		lengthMessageToSend = messageToSend.length(); 
		sendingBuffer = messageToSend.getBytes();

		// Send the message
		myLink.sendFrame(sendingBuffer, lengthMessageToSend);
	
		// Print out the message sent
		System.out.println("Message sent is:   [" + messageToSend + "]");

		// Receive a message
		lengthMessageReceived = myLink.receiveFrame(receivingBuffer);

		// Display the message
		messageReceived = new String(receivingBuffer, 0, lengthMessageReceived);
		System.out.println("Message received is: [" + messageReceived + "]");
	
		// Close the connection	
		myLink.disconnect();
	}
}
