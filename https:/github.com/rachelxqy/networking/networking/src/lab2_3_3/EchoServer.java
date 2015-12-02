package lab2_3_3;

//package lab1;

//Simple echo server.  Modified from some example on the Internet.

import java.io.*;
import java.util.*;

import java.net.*;
import java.lang.*;

public class EchoServer {

static DatagramSocket serverSocket;


//private static final int PORT = 52222;
private static int PORT;


public static void main(String args[]) throws Exception
{
   try {
	   Scanner console=new Scanner(System.in);
	   //ask user to enter PORT number
	   System.out.print("Please enter PORT number: ");
	   PORT=console.nextInt();
	   
	   
	   
   	// Open a UDP datagram socket with a specified port number
      int portNumber = PORT;
      serverSocket = new DatagramSocket(portNumber);
   
      System.out.println("Echo server starts ...");
   
   	// Create a buffer for receiving
      byte[] receivedData = new byte[4096];
      byte[] Sdata = new byte[4096];
   	// Run forever
      while (true) {
      	// Create a datagram
         DatagramPacket receivedDatagram =
            new DatagramPacket(receivedData, receivedData.length);
      
      	// Receive a message			
         serverSocket.receive(receivedDatagram);
      
      	// Prepare for sending an echo message
         InetAddress destination = receivedDatagram.getAddress();			
         int clientPortNumber = receivedDatagram.getPort();
         int lengthOfMessage = receivedDatagram.getLength();			
         String message = new String(receivedData, 0, receivedDatagram.getLength());
      
      	// Display received message and client address		 
         System.out.println("The received message is: " + message);
         System.out.println("The client address is: " + destination);
         System.out.println("The client port number is: " + clientPortNumber);
     
         
      	// Create a buffer for sending reply message
      	// Message and its length		
  	   //ask user to enter message
  	     System.out.println("Send Message: ");
  	     String sendMessage = console.nextLine();
         int lengthOfSendMessage = sendMessage.length(); 
         //byte[] Sdata = new byte[lengthOfSendMessage];
         Sdata = sendMessage.getBytes();
        	// Create a datagram
         DatagramPacket Senddatagram = 
            new DatagramPacket(Sdata, lengthOfSendMessage, destination, clientPortNumber);
         
        // Send a message			
         serverSocket.send(Senddatagram);
      }
   } 
   catch (IOException ioEx) {
      ioEx.printStackTrace();
   } 
   finally {
   	// Close the socket 
      serverSocket.close();
   }
}
}
