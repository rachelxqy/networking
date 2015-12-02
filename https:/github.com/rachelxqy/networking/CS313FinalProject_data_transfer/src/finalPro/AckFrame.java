package finalPro;

import java.io.Serializable;

public class AckFrame implements Serializable {
	/**
	 * class: AckFrame()
	 *       the ack frame sent by the receiver. if positive, sending 1, negative 0. 
	 */
	private static final long serialVersionUID = 1L;
	byte ACK;
	int positive = 1;
	int negtive = 0;
	
	public AckFrame(){
		
	}
	
	public AckFrame(byte ack){
		ACK = ack;
	}
	
	public void setAck(byte ack){
		ACK = ack;
	}
	
	public byte getAck(){
		return ACK;
	}
	
	public void display(){
		if (ACK ==1)
			System.out.print("OK");
		else 
			System.out.print("error");
	}

}
