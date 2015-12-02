package finalPro;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import org.apache.commons.lang.ArrayUtils;

import sampleProject.CRC8;
import sampleProject.Link;
import sampleProject.SimpleLink;

/**
 * Class LinkSender
 * Function: LinkSender will read in a txt file (input of user) and convert to a byte array, load the payload in to a frame,
 *           and set the sequence(not used here), the length of the data, and the calculate the checksum for the 
 *           data. Then send frames one by one to the receiver.  If need to generate error,(that means the probability of randomly generated
 *           is less or equal to the input error rate )then generate error for each frame
 *           And receive a reply from the receiver, then resend (if negative ack) or send new frames (if positive ack) accordingly
 *           and trace the frames and their status of transmission. After all the frames successfully transferred, send a last empty 
 *           frame to signal the termination of transmission, then print the received file in a output file,
 *           and show the total number of packets read, (theoretical)frames transmitted and frames damaged, 
 * @author rachelxqy
 *
 */
// LinkReceiver needs to be started before LinkSender.

public class LinkSender {
	static byte[] inputByteArr;// file read in will be loaded here
	static byte byteCnt = 16;//size for payload byte array
	static CRC8 crc8;
	static int senderPort = 3206;   // port number used by sender
	static int receiverPort = 3306; // port number used by receiver
	static Link myLink;
	static double errorRate; // user input
	static int packetRead;
	static int framesDamaged;
	static int theorFramesTransmitted;   // equals packetRead/(1- error rate)

	//hold the indexes of the frames will be damaged
	//index refers to the nth number of frames, will be randomly generated, not unique
	//static HashMap<Integer,Integer> indexHm = new HashMap<Integer,Integer>();
	static List<Integer> indList = new ArrayList<Integer>();
	static Scanner console = new Scanner(System.in);


	/**
	 * Method: serialize()
	 * Function: convert an object to a byte array,
	 * @param obj  an Object will be converted
	 * @return  byte[]  an byte array 
	 * @throws IOException   IOException
	 */
	public static byte[] serialize(Object obj) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(out);
		os.writeObject(obj);
		return out.toByteArray();
	}

	//convert byte array to object
	/*public static DataFrame deserialize(byte[] data) throws IOException, ClassNotFoundException {
	    ByteArrayInputStream in = new ByteArrayInputStream(data);
	    ObjectInputStream is = new ObjectInputStream(in);
	    return (DataFrame) is.readObject();
	}*/

	/**
	 * Method: deserialize ()
	 * Function:convert a byte array to AckFrame
	 * @param data  a byte array
	 * @return AckFrame   an AckFrame 
	 * @throws IOException  an IOException
	 * @throws ClassNotFoundException  a ClassNotFoundException
	 */
	public static AckFrame deserialize(byte[] data) throws IOException, ClassNotFoundException {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		ObjectInputStream is = new ObjectInputStream(in);
		return (AckFrame) is.readObject();
	}


	/**
	 * method: main()
	 * Function: to send the data and get reply
	 * @param args 
	 * @throws Exception
	 */
	public static void main (String args[]) throws Exception 
	{	
		byte cycle = 0; // record how many packets have been read, and will be used as the sequence number(not real)
		int cycleCount =0;
		int lengthMessageReceived = 0;

		System.out.println("This app is going to read in text file, convert it to byte array, and transmit locally, "+
				"\nthen output to an output file. \nPlease Enter input file name, such as \"src/mission1.txt\"");
		String inFile = console.nextLine();
		System.out.println("\nPlease enter the error rate, such as 0.5");
		String errRate = console.nextLine();


		byte[] receivingBuffer = new byte[255]; //receiving ACK frame from the receiver
		String messageReceived = null;         //the ack info
		//inputByteArr = ReadFile.readInBytes(args[0]); // read in the file in argument
		//errorRate = Double.parseDouble(args[1]); //  
		inputByteArr = ReadFile.readInBytes(inFile); // read in the file 
		errorRate = Double.parseDouble(errRate);

		crc8 = new CRC8();
		// Set up a link with source and destination ports
		// Any 4-digit number greater than 3000 should be fine. 
		myLink = new SimpleLink(senderPort,receiverPort);
		DataFrame df;
		int countPacket =0;
		int countFTransmitted=0;
		int countFDamaged=0;

		//calculate the amount of of packets read, theoretical frames transmitted
		if (inputByteArr.length>0 && inputByteArr.length % byteCnt ==0)
			packetRead = (int)inputByteArr.length/byteCnt;
		else
			packetRead=(int)(inputByteArr.length/byteCnt)+1;
		theorFramesTransmitted = (int) (packetRead/(1-errorRate));
		//framesDamaged = theorFramesTransmitted - packetRead;
		Integer maxRetransmission = 0; // (actually damaged time). So max transmission  times for a single frame
		                               //need adding 1 correct transmission
		HashMap<Integer,Integer> retransmissionMap = new HashMap<Integer,Integer>();//store the retransmission times
		
		// while there is data untransmitted, keep on transmitting
		while((inputByteArr.length-cycleCount*byteCnt>0)){
			int errorCnt = 0;
			df = new DataFrame();
			byte[] payload = new byte[byteCnt];
			byte[] bufPayload = new byte[byteCnt];
			for(int i=0;i<byteCnt;i++){
				if((byteCnt*cycleCount+i)==inputByteArr.length){//limit 16 bytes length
					break;
				}
				payload[i] = inputByteArr[byteCnt*cycleCount+i];//load the payload     
				bufPayload[i] = inputByteArr[byteCnt*cycleCount+i];// load the buffer payload				
			}
			countPacket++; //increments packets read
			byte checkSum = crc8.checksum(payload);//calculate the checksum of payload

			//set the frame data
			df.setPayLoad(payload);
			df.setSeq(cycle);	
			df.setLen(byteCnt);
			df.setCheckSum(checkSum);

			DataFrame bufferFrame = new DataFrame();// buffer frame to load the frame data
			bufferFrame.setPayLoad(bufPayload);
			bufferFrame.setSeq(cycle);
			bufferFrame.setLen(byteCnt);
			bufferFrame.setCheckSum(checkSum);
			
			//randomly generate error, if less or equal to error rate, generate error
			boolean genError = false;
			double prob = Math.random();
			if(prob<=errorRate){
				genError = true;
			}
			
			if(genError){
				df.errorFunction(payload);
				countFDamaged++; // record the number of damaged frames
				errorCnt++;
				retransmissionMap.put(cycleCount, errorCnt);//put in hashmap, cycle count here refers to the count of transmission
			}

			//convert data frame(object) to byte array, since sendframe() parameters are a byte array and its length
			byte[] dFrame = serialize(df);

			// Send the frame
			myLink.sendFrame(dFrame, dFrame.length);
			countFTransmitted++;

			// Receive a message/ the ack frame
			lengthMessageReceived = myLink.receiveFrame(receivingBuffer);
			//convert the receiving buffer(a byte array) to an Ack frame
			AckFrame af = new AckFrame();
			af = deserialize(receivingBuffer);
			byte ackMsg = af.getAck();//get the ack msg

			if (ackMsg==1){
				messageReceived = "OK!";	
			}
			else if (ackMsg == 0){
				messageReceived = "Error!";
			}
			// trace the frames
			System.out.println("\nFrame "+ cycle+ " transmitted,  ["+messageReceived+"]");

			//while ack msg is error, retransmit frame.whether to damage the same frame depends on the error index hash map
			while(ackMsg==0){
				prob = Math.random();
				if(prob<=errorRate){
					genError = true;
				}else{
					genError = false;
				}
				//System.out.println(prob);
				//if(genError&& errorCnt<framesDamaged){//if to damage it again
				if(genError){	
					errorCnt = retransmissionMap.get(cycleCount)+1; ///////////
					retransmissionMap.put(cycleCount, errorCnt);
					for(int i=0;i<byteCnt;i++){  //reload the payload
						if((byteCnt*cycleCount+i)==inputByteArr.length){
							break;
						}
						payload[i] = inputByteArr[byteCnt*cycleCount+i];
					}
					//reload frame
					df.setPayLoad(payload);
					df.setSeq(cycle);
					df.setLen(byteCnt);
					checkSum = crc8.checksum(payload);//recalculate checksum
					df.setCheckSum(checkSum);
					//generate error
					payload = df.getPayLoad();
					df.errorFunction(payload);
					countFDamaged++;
					dFrame = serialize(df);//convert from data frame to byte array
					myLink.sendFrame(dFrame, dFrame.length);// send again
					errorCnt++;
				}else{  //if no need to re-damage the same frame, send the buffer frame
					dFrame = serialize(bufferFrame);
					myLink.sendFrame(dFrame, dFrame.length);
				}
				countFTransmitted++;

				// Receive a message
				lengthMessageReceived = myLink.receiveFrame(receivingBuffer);
				af = new AckFrame();
				af = deserialize(receivingBuffer);
				ackMsg = af.getAck();
				// Display the message
				if(ackMsg==1){
					messageReceived = "OK";
				}else{
					messageReceived = "Error";
				}
				System.out.println("\nFrame "+ df.getSeqNumber()+ " transmitted,  ["+messageReceived+"]");
			}
			cycle++;
			cycleCount++;
		}

		//if nothing to transmit, send an empty frame as the signal of termination of data transmisssion 
		if (inputByteArr.length-cycleCount*byteCnt<=0){
			DataFrame last = new DataFrame(); 
			last.setLen((byte) (0));
			last.setSeq((byte)-1);
			byte[] lastf = serialize(last);
			myLink.sendFrame(lastf, lastf.length);
			countFTransmitted +=1;
			System.out.println("\nLast Frame "+ last.getSeqNumber()+" (empty) transmitted,  ["+messageReceived+"]");
		}


		for(Map.Entry<Integer, Integer> entry : retransmissionMap.entrySet()){
			cycleCount = entry.getKey();
			int errorCnt = entry.getValue();
			if(maxRetransmission<errorCnt){
				maxRetransmission = errorCnt;
			}
		}

		System.out.println("\n\nTotal number of packets read: "+ countPacket+
				"\nTotal number of frames transmitted: "+ countFTransmitted+" (including the last empty frame)."+
				"\nTheoretical total number of frames transmitted = "+ countPacket/(1-errorRate)+"( from calculation.) "+
				"\nTotal number of frames damaged: "+ (countFTransmitted - countPacket-1)+//minus the last empty one
				"\nMaximum number of retransmission for any single frame: "+ (maxRetransmission)+
				"\nMaximum number of transmission for any single frame: "+ (maxRetransmission+1));//max is the error transmission 
		                                                                              //count, so need to plus the correct transmission 
		
		// Close the connection
		myLink.disconnect();
	}

}
