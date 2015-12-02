package finalPro;

import java.io.Serializable;
import java.util.Random;

/**
 * Class: DataFrame
 * Function: to create a data frame(byte array) to load the payload and header items, like sequence number,
 *           payload size, checksum; and transmit this data frame. Error function is included. 
 * @author rachelxqy
 *
 */
public class DataFrame implements Serializable {
	
	private static final long serialVersionUID = 1L;
	int seqSize =1, lenSize=1, payloadSize = 16, checksumSize =1;//generated from CRC-8
	byte lenData; //up to 16
	byte seqNumber; 
	byte[] payload = new byte[payloadSize]; 
	byte checkSum;
	
	/**
	 * Method: default constructor
	 */
	public DataFrame(){
	}
	
	/**
	 * Method: constructor
	 * Function: to construct a data frame
	 * @param seqNumber  byte type the sequence number
	 * @param lenData    byte type data length
	 * @param payload    byte type payload
	 * @param checkSum   byte type checksum
	 */
	public DataFrame(byte seqNumber, byte lenData, byte[] payload, byte checkSum){
		this.seqNumber = seqNumber;
		this.lenData = lenData;
		this.payload =payload;
		this.checkSum = checkSum;	
	}
	
	/**
	 * Method: setSeq()
	 *         to set the sequence number of the frame (not real)
	 * @param seqNumber   a byte type sequence number
	 */
	public void setSeq(byte seqNumber){
		this.seqNumber = seqNumber;
	}
	
	/**
	 * Method: getSeqNumber()
	 *         to get the sequence number
	 * @return  byte  a sequence number
	 */
	public byte getSeqNumber(){
		return this.seqNumber;
	}
	
	/**
	 * method: setLen()
	 *         to set the length of the payload
	 * @param lenData  the length of payload
	 */
	public void setLen(byte lenData){
		this.lenData = lenData;
	}
	
	/**
	 * Method: getLen()
	 *         to get the length of payload
	 * @return  byte  the payload length
	 */
	public byte getLen(){
		return this.lenData;
	}
	
	/**
	 * Method: setPayload()
	 *         to set the payload of the frame
	 * @param payload  a byte array payload
	 */
	public void setPayLoad(byte[] payload){
		this.payload = payload;
	}
	
	/**
	 * Method: getPayload()
	 *         to get the payload of a frame
	 * @return  byte[]  the payload of a frame
	 */
	public byte[]  getPayLoad(){
		return this.payload;
	}
	
	/**
	 * method: setCheckSum()
	 *         to set the checksum of the frame
	 * @param checkSum  the byte value of a checksum
	 */
	public void setCheckSum(byte checkSum){
		this.checkSum = checkSum;
	}
	
	/**
	 * Method: getCheckSum()
	 *         to get the checksum of the frame
	 * @return byte  the checksum byte value
	 */
	public byte  getCheckSum(){
		return this.checkSum;
	}
	
	/**
	 * Method: errorFunction()
	 *         to generate a random error in the payload
	 * @param payload  the byte array payload
	 */
	public void errorFunction(byte[] payload){
		Random rand = new Random();
		int randIndex = rand.nextInt(16);//generate random index of payload, to decide which byte to generate error
		byte mask =1;        //initialize a byte value as 1
		int randBit = rand.nextInt(8); // a random bit of a byte, 
		mask = (byte)(mask << randBit); //shift the byte value 1 left random bit position 
		payload[randIndex] ^= mask;  //the randomly chosen byte of the 16-byte payload conjunct with the mask, to 
		                            //change one bit of the chosen byte, creating the error
	}
}