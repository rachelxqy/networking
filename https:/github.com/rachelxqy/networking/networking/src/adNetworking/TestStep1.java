package adNetworking;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;
import java.nio.*;
import java.util.HashMap;

public class TestStep1
{

	public static void main (String args[]) throws Exception 
	{	
		HashMap<String,String> nameContentHm = readFile(args[0]);
		//String str1="Hello world, every body I love you. 11122236. he is almost good.";
		//String filename="White blue book [23] editor Author chun guo 000";
		String str1 = nameContentHm.get(args[0]);
		String filename = args[0];
		byte[] data=str1.getBytes();
		byte[] dataName=filename.getBytes();
		//the parameters of Encapsulation
		int dataOrAck = Integer.parseInt(args[1]); 
		int transferType= Integer.parseInt(args[2]);
		int fileType = Integer.parseInt(args[3]); 
		int totalDataLength = Integer.parseInt(args[4]); //12312344??? is this number the length of input?
		int sequenceNumber = Integer.parseInt(args[5]); //155
		int transrate = Integer.parseInt(args[6]); //504
		double transtimeout = Double.parseDouble(args[7]); //2.002938891
		//byte[]filename,
		//byte[]data
		Encapsulation encap1=new Encapsulation(dataOrAck,transferType,fileType,
				totalDataLength,sequenceNumber,
				transrate,transtimeout,
				dataName,
				data);

		byte[] frag1=encap1.getFragment();

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
			}
			else;//notice: after else here, packet lost 
		}   


		/*

   System.out.println("================================");
   Decapsulation decap1=new Decapsulation(frag1);
   System.out.println(" DataOrAck: "+decap1.getDataOrAck()+
                     " transfer type: "+decap1.getTransferType()+
                     " file type: "+decap1.getFileType()
                     );
   System.out.println("Total data length: "+decap1.getTotalDataLength());
   System.out.println("Sequence number: "+decap1.getSeqNumber());
   System.out.println("Rate: "+decap1.getRate());
   System.out.println("Time out: "+decap1.getTimeout());

   System.out.println("File name: "+decap1.getFileName());
   System.out.println("File data: "+decap1.getFileData());

		 */


		//s1[3]=(byte)4;
		//System.out.println("test test int to byte: "+s1[3]);

		//for test error happen only one bit
		//fragment[0]=(byte)(fragment[0]^0x01);



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

	/**
	 * 
	 * @param fileName
	 * @return
	 */
	public static HashMap<String,String> readFile(String fileName){
		HashMap<String,String> nameContentHm = new HashMap<String,String>();

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
		nameContentHm.put(fileName, content.toString());
		return nameContentHm;
	}

}//the end of class

