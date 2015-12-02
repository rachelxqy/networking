package lab2_3_3;

//package lab1;

//Simple echo client.

import java.io.*;
import java.util.*;
import java.net.*;
import java.lang.*;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class EchoClient {

	static DatagramSocket clientSocket;
	static Scanner console = new Scanner(System.in);

	public static void main(String args[]) throws Exception {
		try {

			// Open a UDP datagram socket
			clientSocket = new DatagramSocket();

			// Prepare for transmission
			// Determine server IP address
			// Approach 1: from the host name
			// our example is localhost
			/*
			 * String hostname = "localhost"; InetAddress destination =
			 * InetAddress.getByName(hostname);
			 */
			// Approach 2: from the IP address

			// ask user to enter IP address
			//System.out.print("Please enter Server IP address: ");
			//String IPaddress = console.nextLine();
			// Integer IPaddress = console.nextInt();

			// ====================Step1：prepare IG IP address and port number
			// for sending msg to IG======================================
			//			byte[] IGIP = new byte[] { (byte) 192, (byte) 168, (byte) 155,
			//					(byte) 13 }; // IG IP address!!!!! change accordinglly!
			//byte[] IGIP = new byte[] { (byte) 192, (byte) 168, (byte) 158,
				//	(byte) 55 }; // IG IP address!!!!! change accordinglly!
			//192.168.0.16
			byte[] IGIP = new byte[] { (byte) 192, (byte) 168, (byte) 0,
					(byte) 16 }; // IG IP address!!!!! change accordinglly!
			InetAddress addr = null;
			try {
				addr = InetAddress.getByAddress(IGIP);
			} catch (UnknownHostException impossible) {
				System.out.println("Unable to determine the host by address!");
			}
			InetAddress IGAdd = addr;

			int IGPortNumber = 52222;

			// ===========step2: send final server IP & port number to IG as msg (for IG to send msg to server) ===============
			System.out.print("Please enter Server IP address: ");
			String IPaddress = console.nextLine();
			
			byte[] b = new byte[] { 0, 0, 0, 0 }; // server IP byte array
			Integer[] IP = new Integer[4];
			int j = 0;
			int begin = 0;
			int k = IPaddress.length();
			for (int i = 0; i < k; i++) {
				if (IPaddress.substring(i, i + 1).equals(".")) {
					try {
						IP[j] = Integer.parseInt(IPaddress.substring(begin, i));
					} catch (NumberFormatException e) {

					}
					b[j] = (byte) (IP[j] & 0xFF);
					j++;
					begin = i + 1;
				}
				// else;
			}
			IP[j] = Integer.parseInt(IPaddress.substring(begin, k));
			b[j] = (byte) (IP[j] & 0xFF); // IP byte array, will be send in a
			// datagram

			//			InetAddress serverAddr = null;
			//			try {
			//				addr = InetAddress.getByAddress(b);
			//			} catch (UnknownHostException impossible) {
			//				System.out.println("Unable to determine the host by address!");
			//			}
			//			serverAddr = addr; // get server IP address, will be send later

			// ===============send server IP address to IG==================================

			DatagramPacket datagramIP = new DatagramPacket(b, b.length, IGAdd,
					IGPortNumber);
			clientSocket.send(datagramIP);
			byte[] receivedConfirm = new byte[4096];
			
			// ===============Determine server port number, then send===================
			DatagramPacket confirmDP = new DatagramPacket(receivedConfirm,receivedConfirm.length);
			clientSocket.receive(confirmDP);
			// Display the message
			String confirmMessage = new String(receivedConfirm, 0,
					confirmDP.getLength());
			System.out.println("Message echoed is: [" + confirmMessage + "]");
			System.out.print("Please enter Server PORT number: ");
			int serverPortNumber = console.nextInt();
			byte[] serPortNumber = intToByteArray(serverPortNumber);
			//ByteBuffer bb = ByteBuffer.allocate(2);
			//bb.putInt(serverPortNumber);

			//byte[] serPortNumber = new byte[] { 0, 0 }; // will be sent to IG
			//serPortNumber = bb.array();
			//serPortNumber = portBArr;
			DatagramPacket datagramPortNumber = new DatagramPacket(serPortNumber, serPortNumber.length, IGAdd,
					IGPortNumber);
			clientSocket.send(datagramPortNumber);
			receivedConfirm = new byte[4096];
			// ===============Determine server port number, then send===================
			confirmDP = new DatagramPacket(receivedConfirm,receivedConfirm.length);
			clientSocket.receive(confirmDP);
			
			// Display the message
			confirmMessage = new String(receivedConfirm, 0,
					confirmDP.getLength());
			System.out.println("Message echoed is: [" + confirmMessage + "]");
			
			console.nextLine();
			//===============send other msg=================================

			byte[] data = new byte[4096];
			while (true) {
				System.out.println("Send Message: ");
				String message = console.nextLine();
				int lengthOfMessage = message.length();
				data = message.getBytes();

				// Create a datagram
				DatagramPacket datagram = new DatagramPacket(data,
						lengthOfMessage, IGAdd, IGPortNumber);

				// Send a message
				clientSocket.send(datagram);

				// Print out the message sent
				System.out.println("Message sent is:   [" + message + "]");

				// Prepare for receiving
				// Create a buffer for receiving
				byte[] receivedData = new byte[4096];

				// Create a datagram
				DatagramPacket receivedDatagram = new DatagramPacket(
						receivedData, receivedData.length);

				// Receive a message
				clientSocket.receive(receivedDatagram);

				// Display the message
				String echodMessage = new String(receivedData, 0,
						receivedDatagram.getLength());
				System.out.println("Message echoed is: [" + echodMessage + "]");
			}// the end of while

		} catch (IOException ioEx) {
			ioEx.printStackTrace();
		} finally {
			// Close the socket
			clientSocket.close();
		}
	}

	static public byte[] intToByteArray(int value) {
	    return new byte[] {
	            (byte)(value >>> 24),
	            (byte)(value >>> 16),
	            (byte)(value >>> 8),
	            (byte)value};
	}
}
