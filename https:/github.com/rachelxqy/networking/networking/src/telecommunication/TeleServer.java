package telecommunication;

//package lab1;

//Simple echo server.  Modified from some example on the Internet.

import java.io.*;
import java.util.*;
import java.net.*;
import java.lang.*;

import utils.Helper;
import adNetworking.Decapsulation;
import adNetworking.Encapsulation;
import adNetworking.CRC8;
import adNetworking.EncapsulationACK;

public class TeleServer {

	static DatagramSocket serverSocket;
	//private static final int PORT = 52222;
	private static int PORT;
	private static int byteleng = Helper.bytelength;
	//private static int byteleng = 65525;
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
			byte[] receivedData = new byte[byteleng]; // !!!!!!!!should use byteleng ???????
			byte[] Sdata = new byte[byteleng];
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
				System.out.println("The received message is: " + message);
				
				Decapsulation decap1 = new Decapsulation(receivedData);
				int dataOrAck = decap1.getDataOrAck();
				int transferType = decap1.getFileType();
				int fileType = decap1.getFileType();
				int sequenceNumber = decap1.getFileType();
				int transrate = decap1.getRate();
				double transtimeout = decap1.getTimeout();
				
				String dataName = decap1.getFileName();
				byte[] fileName = dataName.getBytes(); // for ACK
				String fileData = decap1.getFileData();
				
				CRC8 crc8 = new CRC8();
				byte checkSum = decap1.getCheckSum();
				byte[] fragment = Arrays.copyOf(receivedData,receivedData.length);
				System.out.println("the value of last value of fragment in server="+fragment[fragment.length-1]);
				for(int i=0;i<fragment.length;i++){
					System.out.print(fragment[i]+" ");
				}
				//fragment[fragment.length-1]=0;
				String str = new String(fragment);
				System.out.println("fragment length="+fragment.length+" fragement=\n"+str+"\n fragement str length="+str.length());
				byte newCheckSum= crc8.checksum(fragment);
				System.out.println("newCheckSum="+newCheckSum+" checkSum="+checkSum);
				EncapsulationACK encapACK;
				if(newCheckSum==checkSum){
					int pOrNACK = 1;
					encapACK = new EncapsulationACK(dataOrAck, pOrNACK, fileType, sequenceNumber, fileName);
					System.out.println(dataOrAck+" "+transferType+" "+fileType
							+" "+sequenceNumber+" "+transrate+" "+transtimeout
							+" "+dataName+" "+fileData);
					
					// Display received message and client address		 
					System.out.println("The received message is: " + message);
					System.out.println("The client address is: " + destination);
					System.out.println("The client port number is: " + clientPortNumber);


					// Create a buffer for sending reply message
					// Message and its length		
					//ask user to enter message
					System.out.println("Send Message: ");
					String sendMessage = console.nextLine();
					
					//byte[] Sdata = new byte[lengthOfSendMessage];
					Sdata = sendMessage.getBytes();
				}else{
					int pOrNACK =0;
				    encapACK = new EncapsulationACK(dataOrAck, pOrNACK, fileType, sequenceNumber, fileName);	
				}
				
				byte[] ACKFragment = encapACK.getFragment();// how to send ack to client
//				Encapsulation encapR=new Encapsulation(dataOrAck,transferType,fileType,
//						receivedData.length,sequenceNumber,
//						transrate,transtimeout,
//						fileName,
//						Sdata);
				//Boolean checkEncapp = Helper.checkEncap(encapR);
				//if(checkEncapp){
					// Create a datagram
					//int lengthOfSendMessage = Sdata.length; 
					DatagramPacket Senddatagram = 
							new DatagramPacket(ACKFragment, ACKFragment.length, destination, clientPortNumber);
					// Send a message			
					serverSocket.send(Senddatagram);
				//}
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
