package adNetworking;

import java.net.*;
import java.nio.*;

import utils.Helper;
public class Decapsulation
{

	private byte[] fragment=new byte[Helper.bytelength];

	private int dataOrAck; // data=0, ack=1;
	private int transferType;//download=0, upLoad=1:
	private int fileType; //txt=0, PDF=1; 

	private int totalDataLength; 
	private int sequenceNumber;  

	//private byte checksum ;

	private int rate;
	private double timeout;

	private byte[] filename=new byte[60];   
	private byte[] data;


	//constructor
	public Decapsulation (byte[] fragment1)
	{
		this.fragment=fragment1;    
	}

	//==============================================================================================

	//get DataOrAck, transferType, fileType
	public int getDataOrAck()
	{
		dataOrAck=fragment[0]>>6;
		return dataOrAck;
	}

	public int getTransferType()
	{
		transferType=(fragment[0]>>4)&0x03;
		return transferType;
	}

	public int getFileType()
	{
		fileType=fragment[0]&0x0F;
		return fileType;
	}

	//get total data length
	public int getTotalDataLength()
	{
		byte[] tl1=new byte[4];
		tl1[0]=fragment[1];
		tl1[1]=fragment[2];
		tl1[2]=fragment[3];
		tl1[3]=fragment[4];
		totalDataLength= byteToInt(tl1);

		return totalDataLength;
	} 

	//get sequence number 
	public int getSeqNumber()
	{
		byte[] s1=new byte[4];
		s1[0]=fragment[5];
		s1[1]=fragment[6];
		s1[2]=fragment[7];
		s1[3]=fragment[8];
		sequenceNumber= byteToInt(s1);

		return sequenceNumber;
	} 

	//get rate 
	public int getRate()
	{
		byte[] r1=new byte[4];
		r1[0]=fragment[9];
		r1[1]=fragment[10];
		r1[2]=fragment[11];
		r1[3]=fragment[12];
		rate= byteToInt(r1);

		return rate;
	}

	//get timeout 
	public double getTimeout()
	{
		byte[] to1=new byte[8];
		to1[0]=fragment[13];
		to1[1]=fragment[14];
		to1[2]=fragment[15];
		to1[3]=fragment[16];
		to1[4]=fragment[17];
		to1[5]=fragment[18];
		to1[6]=fragment[19];
		to1[7]=fragment[20];

		timeout= byteToDouble(to1);

		return timeout;
	}


	//****
	//get file name
	public String getFileName()
	{
		int k=0;
		for(int i=59;i>=0;i--)
		{
			if(fragment[21+i]==(byte)0x00)
				k++;
		}

		byte[] name=new byte[60-k];
		for(int m=0;m<(60-k);m++)
			name[m]=fragment[m+21];
		String fName=new String(name);

		return fName;
	}



	//get file data
	public String getFileData()
	{
		int k=0;
//		for(int i=(Helper.bytelength-Helper.headlength+1);i>=0;i--)
//		{
//			System.out.println("i="+i);
//			if(fragment[Helper.headlength-1+i]==(byte)0x00)
//				k++;
//		}
		
		for(int i=(Helper.bytelength-1);i>=Helper.headlength;i--)
		{
			//System.out.println("i="+i);
			if(fragment[i]==(byte)0x00)
				k++;
		}

		byte[] file=new byte[Helper.bytelength-Helper.headlength-k];
		for(int m=0;m<((Helper.bytelength-Helper.headlength)-k);m++)
			file[m]=fragment[m+Helper.headlength-1];
		String fileData=new String(file);

		return fileData;
	}

	public byte getCheckSum(){
		return fragment[fragment.length-1];
	}

	//==============================================================================================

	//value type change method
	private int byteToInt(byte[] b)
	{  

		int mask=0xff;  
		int temp=0;  
		int n=0;  
		for(int i=0;i<4;i++)
		{  
			n<<=8;  
			temp=b[i]&mask;  
			n|=temp;  
		}  
		return n;  
	}

	private double byteToDouble(byte[] byteArray)
	{
		long bits = 0L;
		int k=7;
		for (int i = 0; i<8; i++) 
		{
			bits |= (byteArray[i] & 0xFFL) << (8 * k);
			k--;
		}
		return Double.longBitsToDouble(bits);
	}   

}//the end of class