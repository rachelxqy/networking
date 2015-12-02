package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

import adNetworking.CRC8;
import adNetworking.CRC8Checksum;
import adNetworking.Encapsulation;
import adNetworking.ErrorProduce;
import javafx.util.Pair;

public class Helper {
	
	public static int bytelength = 150;//60000; //the limit of message sending length
	public static int headlength = 82;

	/**
	 * 
	 * @param fileName
	 * @return
	 */
	public static Pair<String,String> readFile(String fileName){
		StringBuilder content = new StringBuilder();
		try {
			BufferedReader brFile = new BufferedReader(new FileReader(new File(fileName)));
			String line = "";
			while((line=brFile.readLine())!=null){
				content.append(line+"\n");
			}
			brFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Pair<String,String> nameContentHm = new Pair<String,String>(fileName, content.toString());
		return nameContentHm;
	}
	
	/**
	 * 
	 * @param encap1
	 * @return
	 */
	public static boolean checkEncap(Encapsulation encap1){
		byte[] frag1 = encap1.getFragment();
		int a=(int)frag1[0];
		System.out.println("\n"+a);

		byte[] r1=new byte[4];
		r1[0]=frag1[1];
		r1[1]=frag1[2];
		r1[2]=frag1[3];
		r1[3]=frag1[4];
		int x= byteToInt(r1);
		System.out.println("total length: "+x);

		byte[] s1=new byte[4];
		s1[0]=frag1[5];
		s1[1]=frag1[6];
		s1[2]=frag1[7];
		s1[3]=frag1[8];
		int sNo= byteToInt(s1);
		System.out.println("sequence number: "+sNo);

		byte[] rate1=new byte[4];
		rate1[0]=frag1[9];
		rate1[1]=frag1[10];
		rate1[2]=frag1[11];
		rate1[3]=frag1[12];
		int rate= byteToInt(rate1);
		System.out.println("Rate: "+rate);


		byte[] tm1=new byte[8];
		tm1[0]=frag1[13];
		tm1[1]=frag1[14];
		tm1[2]=frag1[15];
		tm1[3]=frag1[16];
		tm1[4]=frag1[17];
		tm1[5]=frag1[18];
		tm1[6]=frag1[19];
		tm1[7]=frag1[20];
		double timeout=byteToDouble(tm1);
		System.out.println("timeout: "+timeout);


		byte[] name=new byte[60];
		for(int m=0;m<60;m++)
			name[m]=frag1[m+21];
		String outName=new String(name);
		System.out.println(outName);    


		byte[] word=new byte[500];
		for(int k=0;k<500;k++)
			word[k]=frag1[k+81];

		String outStr=new String(word);
		System.out.println(outStr);

		CRC8Checksum checksum= new CRC8Checksum(frag1);
		boolean  rightData=checksum.checkRight();
		System.out.println("frag1 checksum is true or false: "+rightData);



		System.out.println("================================");
		ErrorProduce newFrag=new ErrorProduce(frag1,0.5,0.8);
		if(newFrag.errorOccur()==false)
		{
			System.out.println("No error occur.");

			byte[] frag2=newFrag.getFragNoError();
			CRC8Checksum checksum2= new CRC8Checksum(frag2);
			boolean  rightData2=checksum2.checkRight();
			System.out.println("After no error occur checksum is true or false: "+rightData2);
			if(rightData2==false){
				return rightData2;
			}
		}
		else
		{
			if(newFrag.packetLost()==false)
			{
				System.out.println("Error corrupt.");

				byte[] frag3=newFrag.getFragAfterCorrupt();
				CRC8Checksum checksum3= new CRC8Checksum(frag3);
				boolean  rightData3=checksum3.checkRight();
				System.out.println("After error occur checksum is true or false: "+rightData3);
				if(rightData3==false){
					return rightData3;
				}
			}
			else;//notice: after else here, packet lost 
		}   
		return rightData; 
	}
	
	public static int byteToInt(byte[] b)
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



	public static double byteToDouble(byte[] byteArray)
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
	
//	//++++++++++++++++++++++++++++++++++++++++++++++++++++++=
//	   public byte[] getFragment()
//	   {
//	      //total fragment long
//	      byte[] fragment=new byte[65525];
//	   
//	      
//	      //fragment[1]: include data/ACk, transferType,fileType
//	      byte dOrA =(byte)dataOrAck;
//	      dOrA=(byte)(dOrA<<6);
//	      byte tType=(byte)transferType;
//	      tType=(byte)(tType<<4);
//	      byte fType=(byte)fileType;
//	      
//	      fragment[0]=(byte)((dOrA^tType)^fType);
//	      
//	    
//	      
//	      //convert totalDataLength to byte[4]
//	      byte convertLength[]=getByteArrayFromInteger(totalDataLength);
//	      fragment[1]=convertLength[0];
//	      fragment[2]=convertLength[1];
//	      fragment[3]=convertLength[2];
//	      fragment[4]=convertLength[3];
//	    
//	      
//	      //convert sequenceNumber to byte[4]
//	      byte convertSeqNo[]=getByteArrayFromInteger(sequenceNumber);
//	      fragment[5]=convertSeqNo[0];
//	      fragment[6]=convertSeqNo[1];
//	      fragment[7]=convertSeqNo[2];
//	      fragment[8]=convertSeqNo[3];
//	      
//	   
//	      //convert rate to byte[4]
//	      byte convertRate[]=getByteArrayFromInteger(rate);
//	      fragment[9]=convertRate[0];
//	      fragment[10]=convertRate[1];
//	      fragment[11]=convertRate[2];
//	      fragment[12]=convertRate[3];
//	      
//	      
//	      //convert timeout to byte[8]
//	      byte convertTimeout[]=getByteArrayFromDouble(timeout);
//	      fragment[13]=convertTimeout[0];
//	      fragment[14]=convertTimeout[1];
//	      fragment[15]=convertTimeout[2];
//	      fragment[16]=convertTimeout[3];
//	      fragment[17]=convertTimeout[4];
//	      fragment[18]=convertTimeout[5];
//	      fragment[19]=convertTimeout[6];
//	      fragment[20]=convertTimeout[7];
//	         
//	      /*
//	        *filename is 60 byte long.
//	        first filename fragment is fragment[21];
//	        last filename fragment is fragment[80];
//	      */
//	      for(int p=0;p<filename.length;p++)
//	         fragment[p+21]=filename[p];
//	     
//	     
//	      
//	      /*
//	        *total fragment is 65525.
//	        *total head fragment is 82.
//	        *data fragment is 65525-82= 65443 byte.(This case means all data fragments are used)
//	        first data fragment is fragment[81];
//	        last data fragment is fragment[65523];
//	      */
//	      for(int i=0;i<data.length;i++)
//	         fragment[i+81]=data[i];
//	   
//	   
//	   
//	      //fragment[65524] store checksum;
//	      CRC8 crc8 = new CRC8();
//	      byte crc= crc8.checksum(fragment);
//	      fragment[65524]=crc;
//	      //System.out.println("fragment[65524]: "+Integer.toBinaryString(fragment[65524]&0x000000ff));
//	      return fragment;
//	   }
	   
	
	/**
	 * 
	 * @param chars
	 * @return
	 */
	static public byte[] charToByteArray(char[] chars) {
		CharBuffer charBuffer = CharBuffer.wrap(chars);
		ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(charBuffer);
		byte[] bytes = Arrays.copyOfRange(byteBuffer.array(),
				byteBuffer.position(), byteBuffer.limit());
		Arrays.fill(charBuffer.array(), '\u0000'); // clear sensitive data
		Arrays.fill(byteBuffer.array(), (byte) 0); // clear sensitive data
		return bytes;
	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	static public byte[] intToByteArray(int value) {
		return new byte[] {
				(byte)(value >>> 24),
				(byte)(value >>> 16),
				(byte)(value >>> 8),
				(byte)value};
	}

}
