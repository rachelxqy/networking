package finalPro;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream; // out print byte
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.io.PrintWriter;//out put and print string
import java.util.Scanner; //scan in input

import sampleProject.CRC8;
import sampleProject.Link;
import sampleProject.SimpleLink;

/**
 * Author: Qiongying Xiu
 * Date: 04/20/2014
 * Assignment: CS313 Computer Network Final Project   Spring 2014
 * Description: LinkReceiver receives a message from LinkSender and replies.
 *              LinkReceiver needs to be started before LinkSender.
 */ 

public class LinkReceiver {

	static Scanner console = new Scanner(System.in);
	static int senderPort = 3206;   // port number used by sender
	static int receiverPort = 3306; // port number used by receiver
	static Link myLink;
	static CRC8 crc8;
	static int byteCnt = 16;   //availiable byte size for payload
	static String fileReceived="";//Initialize it empty, if "null" will cause printing problem

	/**
	 * method: serialize()
	 *         convert object to byte array
	 * @param obj  an class object
	 * @return  byte[]  a byte array
	 * @throws IOException  IOException
	 */
	public static byte[] serialize(Object obj) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(out);
		os.writeObject(obj);
		return out.toByteArray();
	}

	/**
	 * method: deserialize()
	 *         convert a byte array to a DataFrame
	 * @param data    a byte array
	 * @return DataFrame  a data Frame
	 * @throws IOException  IOException
	 * @throws ClassNotFoundException
	 */
	public static DataFrame deserialize(byte[] data) throws IOException, ClassNotFoundException {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		ObjectInputStream is = new ObjectInputStream(in);
		return (DataFrame) is.readObject();
	}

    /**
     * method: main()
     *         to to receive data from sender, response accordingly and output to a file
     * @param args
     * @throws Exception
     */
	public static void main (String args[]) throws Exception 
	{	
		PrintWriter pwOut = null; //for output and  print string 
		System.out.println("This app is going to read in text file, convert it to byte array, and transmit locally, "+
				"\nthen output to an output file. \nPlease Enter output file name, such as \"src/outfile.txt\"");
		String outfile = console.nextLine();

		//try {
		
			//out = new FileOutputStream(outfile); out.write() print byte
			pwOut = new PrintWriter(new FileWriter(outfile));// print string
			int lengthMessageToSend; 
			int lengthMessageReceived = 0;
			String messageToSend;
			String messageReceived;
			byte receivedCheckSum;
			byte[] receivingBuffer = new byte[255];
			crc8 = new CRC8();
			DataFrame receivedDFrame = new DataFrame(); 
			AckFrame ackframe = new AckFrame();
			
			// Set up a link with source and destination ports
			// Any 4-digit number greater than 3000 should be fine. 
			myLink= new SimpleLink(receiverPort, senderPort);
			int cycle = 0;
			int countFrameReceived =0;
			while(true){
				// Receive a message
				lengthMessageReceived = myLink.receiveFrame(receivingBuffer);
				countFrameReceived++;

				//convert byte array to data frame (received data frame)
				receivedDFrame = deserialize(receivingBuffer);


				byte seqNumreceived = receivedDFrame.getSeqNumber();
				byte lenDataReceived = receivedDFrame.getLen();
				receivedCheckSum = receivedDFrame.getCheckSum();
				byte[] payload = receivedDFrame.getPayLoad();
				messageReceived = new String(payload);//convert byte payload to string message

				//use the received payload to generate a new checksum and compare with the received one, 
				//if not the same, send negative ack, 0; else positive ack, 1.
				byte newCheckSum = crc8.checksum(payload);
				if(newCheckSum==receivedCheckSum){
					ackframe.setAck((byte)1);
					fileReceived += messageReceived;//store the correct msg for later output file
				}else{
					ackframe.setAck((byte)0);
				}

				//convert ack frame to byte array for transmission
				byte[] afBuffer = serialize(ackframe);
				myLink.sendFrame(afBuffer, afBuffer.length);

				System.out.println("\n"+(cycle+1)+ "th transmission, Frame "+seqNumreceived+" received is: [" + messageReceived + "]");
				ackframe.display();
				System.out.println();
				cycle++;
				
				// Close the connection if received an empty frame, and print received file to output file 
				if (lenDataReceived==0){
					String[] fileArr = fileReceived.split(" ");
					for(int i=1;i<=fileArr.length;i++){
						if(i%10==0){
							pwOut.print(fileArr[i-1]+"\n");//print 10 words every line
						}else{
							pwOut.print(fileArr[i-1]+" ");
						}
					}
					pwOut.flush();
					pwOut.close();
					myLink.disconnect();
					return;
				}
			}
			
		}

	}
