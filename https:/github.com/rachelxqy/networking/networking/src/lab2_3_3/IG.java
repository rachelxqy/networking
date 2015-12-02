package lab2_3_3;

// Simple echo server.  Modified from some example on the Internet.

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.lang.*;

public class IG {

	static DatagramSocket serverSocket;
	static DatagramSocket clientSocket;

	private static final int IGPORT = 52222;

	public static void main(String args[]) throws Exception
	{
		try {
			// Open a UDP datagram socket with a specified port number
			int IGportNumber = IGPORT;
			serverSocket = new DatagramSocket(IGportNumber); // to the client, IG works as a server
			// IG port number only be used when it acts as a sever
			clientSocket = new DatagramSocket(); // to the server, IG works as a client, no need port number

			System.out.println("IG starts ...");

			// Create a buffer for receiving
			byte[] DataFromC = new byte[4096]; // data from client
			byte[] DataToS = new byte[4096];       // data to server
			byte[] DataFromS = new byte[4096]; // data from server
			byte[] DataToC = new byte[4096];       // data to client

			int serverPortNumber=0;
			InetAddress serverAddr=null;

			/*byte [] b = new byte[] {(byte)192,(byte)168,(byte)155,(byte)13};  // change to the server IP address!!!!!
			InetAddress addr = null;
			try {
	        		addr = InetAddress.getByAddress(b);
			} catch (UnknownHostException impossible) {
				System.out.println("Unable to determine the host by address!");
			}*/


			//int serverPortNumber = 56789;// the real server port number

			//=====================================================================

			// Run forever
			int counter = 0;
			while (true) {


				// extract server IP, will be used for sending msg to server
				if (counter==0){
					//===========Step 1, IG receive msg from client!!============================
					// Create a datagram from client
					byte[] IPFromC = new byte[4]; 
					DatagramPacket datagramFC =
							new DatagramPacket(IPFromC, IPFromC.length);
					// Receive a message from client	
					serverSocket.receive(datagramFC);
					InetAddress addr = null;
					try {
						addr = InetAddress.getByAddress(IPFromC);
					} catch (UnknownHostException impossible) {
						System.out.println("Unable to determine the host by address!");
					}
					serverAddr = addr; // 	
					String message = "the sever IP address received";
					//int lengthOfMessage = message.length();
					byte[] response = message.getBytes();
					InetAddress clientAddr = datagramFC.getAddress();
					int clientPortNumber = datagramFC.getPort();
					DatagramPacket confirmDP = 
							new DatagramPacket(response,response.length,clientAddr,clientPortNumber);
					serverSocket.send(confirmDP);
					counter++;

				} else if (counter==1){ // extract server port Number 
					byte[] PortFromC = new byte[4]; 
					DatagramPacket datagramFC =
							new DatagramPacket(PortFromC, PortFromC.length);
					// Receive a message from client	
					serverSocket.receive(datagramFC);
//					final ByteBuffer bb = ByteBuffer.wrap(PortFromC);
//					bb.order(ByteOrder.LITTLE_ENDIAN);
//					serverPortNumber = bb.getInt();	
					serverPortNumber = fromBytesArray(PortFromC);
					System.out.println("serverPortNumber======"+serverPortNumber);
					String message = "the sever Port received";
					//int lengthOfMessage = message.length();
					byte[]  response = message.getBytes();
					InetAddress clientAddr = datagramFC.getAddress();
					int clientPortNumber = datagramFC.getPort();
					DatagramPacket confirmDP = 
							new DatagramPacket(response,response.length,clientAddr,clientPortNumber);
					serverSocket.send(confirmDP);
					counter++;
				} else

				{
					//===========Step 1, IG receive msg from client!!============================
					// Create a datagram from client
					DatagramPacket datagramFC =
							new DatagramPacket(DataFromC, DataFromC.length);
					// Receive a message from client	
					serverSocket.receive(datagramFC);
					int lengthOfMessage = datagramFC.getLength();			
					String messageFC = new String(DataFromC, 0, datagramFC.getLength());

					//InetAddress clientAddr = DatagramFC.getAddress();			
					//int clientPortNumber = DatagramFC.getPort();

					// Display received message and client address		 
					System.out.println("The received message from Client is: " + messageFC);
					//System.out.println("The client address is: " + destination);
					//System.out.println("The client port number is: " + clientPortNumber);


					// ===========Step 2:  IG sending msg to server!!===============================
					//byte[] data = new byte[lengthOfMessage];
					// buffer for msg from client
					DataToS = messageFC.getBytes();

					// Create a datagram
					DatagramPacket datagramTS = 
							new DatagramPacket(DataToS, lengthOfMessage, serverAddr, serverPortNumber);

					// IG Send a message to server
					clientSocket.send(datagramTS);

					//============Step 3: IG receive msg from Server!!=================================
					//receive (as a client) a message from server
					DatagramPacket datagramFS =
							new DatagramPacket(DataFromS, DataFromS.length);

					clientSocket.receive(datagramFS);

					//===========Step 4: IG send msg to Client!!=====================================
					//Prepare for sending a message (from IG) to client
					int lengthOfMessageFS = datagramFS.getLength();			
					String messageFS = new String(DataFromS, 0, datagramFS.getLength());
					System.out.println("The received message from Server is: " + messageFS);
					System.out.println("The server address is: " + serverAddr);

					//extract client address and port number	
					InetAddress clientAddr = datagramFC.getAddress();
					int clientPortNumber = datagramFC.getPort();
					//buffer for data to client
					DataToC = messageFS.getBytes();
					DatagramPacket datagramToC = 
							new DatagramPacket (DataToC,lengthOfMessageFS, clientAddr, clientPortNumber);
					serverSocket.send(datagramToC);	
				}
				//counter++;
			}
		} 
		catch (IOException ioEx) {
			ioEx.printStackTrace();
		} 
		finally {
			// Close the socket 
			clientSocket.close();
			serverSocket.close();

		}
	}
	
	public static int fromBytesArray(byte[] bytes) {
	     return ByteBuffer.wrap(bytes).getInt();
	}
	
	public static int fromByteArray(byte[] bytes) {
	     return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
	}
}
