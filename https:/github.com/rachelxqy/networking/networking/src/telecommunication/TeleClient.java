package telecommunication;

//package lab1;

//Simple echo client.

import java.io.*;
import java.util.*;
import java.net.*;
import java.lang.*;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import adNetworking.Decapsulation;
import adNetworking.Encapsulation;
import javafx.util.Pair;
import utils.Helper;

public class TeleClient {

	static DatagramSocket clientSocket;
	static Scanner console = new Scanner(System.in);
	//private static int byteleng = 65507;
	private static int byteleng = Helper.bytelength;

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
			//172.22.15.187
			//server: 10.145.10.64
			//			byte[] IGIP = new byte[] { (byte) 172, (byte) 22, (byte) 15,
			//					(byte) 187 }; // IG IP address!!!!! change accordinglly!
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
			byte[] receivedConfirm = new byte[byteleng];// 

			// ===============Determine server port number, then send===================
			DatagramPacket confirmDP = new DatagramPacket(receivedConfirm,receivedConfirm.length);
			clientSocket.receive(confirmDP);
			// Display the message
			String confirmMessage = new String(receivedConfirm, 0,
					confirmDP.getLength());
			System.out.println("Message echoed is: [" + confirmMessage + "]");
			System.out.print("Please enter Server PORT number: ");
			int serverPortNumber = console.nextInt();
			byte[] serPortNumber = Helper.intToByteArray(serverPortNumber);
			//ByteBuffer bb = ByteBuffer.allocate(2);
			//bb.putInt(serverPortNumber);

			//byte[] serPortNumber = new byte[] { 0, 0 }; // will be sent to IG
			//serPortNumber = bb.array();
			//serPortNumber = portBArr;
			DatagramPacket datagramPortNumber = new DatagramPacket(serPortNumber, serPortNumber.length, IGAdd,
					IGPortNumber);
			clientSocket.send(datagramPortNumber);
			receivedConfirm = new byte[byteleng];
			// ===============Determine server port number, then send===================
			confirmDP = new DatagramPacket(receivedConfirm,receivedConfirm.length);
			clientSocket.receive(confirmDP);

			// Display the message
			confirmMessage = new String(receivedConfirm, 0,
					confirmDP.getLength());
			System.out.println("Message echoed is: [" + confirmMessage + "]");
			//			int dataOrAck = console.nextInt(); 
			//			int transferType= console.nextInt();
			//			int fileType = console.nextInt(); 
			//			int sequenceNumber = console.nextInt(); //155
			//			int transrate = console.nextInt(); //504
			//			double transtimeout = console.nextDouble(); //2.002938891

			while(console.nextLine().trim().length()==0){

			}
			String fileName=console.nextLine(); 
			System.out.println("Send file name: " +fileName);
			Pair<String,String> fileContentPair = Helper.readFile(fileName);

			//===============send other msg=================================

			//byte[] data = new byte[byteleng];
			byte[] data = new byte[byteleng-82];
			BufferedInputStream bufferedInput = new BufferedInputStream(new FileInputStream(fileName));

			byte[] dataName=fileName.getBytes();
			//the parameters of Encapsulation
			int dataOrAck = 1; 
			int transferType= 1;
			int fileType = 1; 
			int totalDataLength = 12312344;//??? is this number the length of input?
			int sequenceNumber = 155;
			int transrate = 504;
			double transtimeout = 2.002938891;

			while (true) {
				bufferedInput.read(data);
				String sent = "the patient has a heart disease";
				System.out.println("Send Message: ");
				data = sent.getBytes();
				totalDataLength = data.length;
				System.out.println("data length= "+data.length+" string sent leng="+sent.length());
				Encapsulation encap1=new Encapsulation(dataOrAck,transferType,fileType,
						totalDataLength,sequenceNumber,
						transrate,transtimeout,
						dataName,
						data);
				//Boolean checkEncapp = Helper.checkEncap(encap1);
				//if(checkEncapp){
				// Create a datagram
				byte[] fragment = encap1.getFragment();
				System.out.println("the value of last value of fragment in client="+fragment[fragment.length-1]);
				for(int i=0;i<fragment.length;i++){
					System.out.print(fragment[i]+" ");
				}
				System.out.println();
				String str = new String(fragment);
				System.out.println("fragment length="+fragment.length+" fragement=\n"+str+"\n fragement str length="+str.length()+" data length="+data.length);
//				DatagramPacket datagram = new DatagramPacket(fragment,
//						//str.length(), IGAdd, IGPortNumber);
//						fragment.length, IGAdd, IGPortNumber); // !!!!!!!print the value of fragment.length. whether the length is 65507
				//					
									DatagramPacket datagram = new DatagramPacket(fragment,
											fragment.length, IGAdd, IGPortNumber);

				// Send a message
				clientSocket.send(datagram);
				String message = new String(data, "UTF-8"); 
				// Print out the message sent
				System.out.println("Message sent is:   [" + message + "]");
				//					System.out.println("Send Message: ");
				//					String message = console.nextLine();
				//					int lengthOfMessage = message.length();
				//					data = message.getBytes();
				//					// Create a datagram
				//					DatagramPacket datagram = new DatagramPacket(data,
				//							lengthOfMessage, IGAdd, IGPortNumber);
				//					clientSocket.send(datagram);				

				//byte[]filename,
				//byte[]data
				// Prepare for receiving
				// Create a buffer for receiving
				byte[] receivedData = new byte[byteleng];

				// Create a datagram
				DatagramPacket receivedDatagram = new DatagramPacket(
						receivedData, receivedData.length);

				// Receive a message
				clientSocket.receive(receivedDatagram);

				// Display the message
				String echodMessage = new String(receivedData, 0,
						receivedDatagram.getLength());
				System.out.println("Message echoed is: [" + echodMessage + "]");

				Decapsulation decap1 = new Decapsulation(receivedData);
				int dataOrAckR = decap1.getDataOrAck();
				int transferTypeR = decap1.getFileType();
				int fileTypeR = decap1.getFileType();
				int sequenceNumberR = decap1.getFileType();
				int transrateR = decap1.getRate();
				double transtimeoutR = decap1.getTimeout();
				String dataNameR = decap1.getFileName();
				String fileDataR = decap1.getFileData();
				//decap1.
				System.out.println(dataOrAckR+" "+transferTypeR+" "+fileTypeR
						+" "+sequenceNumberR+" "+transrateR+" "+transtimeoutR
						+" "+dataNameR+" "+fileDataR);
			}

			//}// the end of while

		} catch (IOException ioEx) {
			ioEx.printStackTrace();
		} finally {
			// Close the socket
			clientSocket.close();
		}
	}
}
