

import java.net.*;
import java.nio.*;
public class EncapsulationACK
{
   private int dataOrAck; // data=0, ack=1;
   private int pOrnACK;//negtive=0, positive=1:
   private int fileType; //txt=0, PDF=1; 
   
   private int sequenceNumber;  //after convert to byte, need byte[4] 
   
   private byte checksum = (byte)0x00;//after checksum, replace checksum result to this byte
   
         
   private byte[] filename=new byte[60];//filename is 60 byte long   
   

   
   //constructor
   public EncapsulationACK (int dataOrAck, int pOrnACK, int fileType,
                         int sequenceNumber, 
                         byte[]filename)
                        
   {
      if(dataOrAck==0||dataOrAck==1)   
         this.dataOrAck=dataOrAck;
      
      if(pOrnACK==0||pOrnACK==1)
         this.pOrnACK=pOrnACK;
         
      if(fileType==0||fileType==1)
         this.fileType=fileType;
       
      this.sequenceNumber=sequenceNumber;   
      
     
      for(int j=0;j<filename.length;j++)
         this.filename[j]=filename[j];
     
   }


  //++++++++++++++++++++++++++++++++++++++++++++++++++++++=
   public byte[] getFragment()
   {
      //total fragment long
      byte[] fragment=new byte[65525];
   
      
      //fragment[1]: include data/ACk, transferType,fileType
      byte dOrA =(byte)dataOrAck;
      dOrA=(byte)(dOrA<<6);
      byte pOrn=(byte)pOrnACK;
      pOrn=(byte)(pOrn<<4);
      byte fType=(byte)fileType;
      
      fragment[0]=(byte)((dOrA^pOrn)^fType);
      
    
      
   
      
      //convert sequenceNumber to byte[4]
      byte convertSeqNo[]=getByteArrayFromInteger(sequenceNumber);
      fragment[1]=convertSeqNo[0];
      fragment[2]=convertSeqNo[1];
      fragment[3]=convertSeqNo[2];
      fragment[4]=convertSeqNo[3];
      
   
      
      /*
        *filename is 60 byte long.
        first filename fragment is fragment[5];
        last filename fragment is fragment[64];
      */
      for(int p=0;p<filename.length;p++)
         fragment[p+5]=filename[p];
     
     
      
      /*
        *total fragment is 65525.
        *total head fragment is 82.
        *data fragment is 65525-82= 65443 byte.(This case means all data fragments are used)
        first data fragment is fragment[81];
        last data fragment is fragment[65523];
      */
    
   
   
   
      //fragment[65524] store checksum;
      CRC8 crc8 = new CRC8();
      byte crc= crc8.checksum(fragment);
      fragment[65524]=crc;
   
      //System.out.println("fragment[65524]: "+Integer.toBinaryString(fragment[65524]&0x000000ff));
   
   
      return fragment;
   }
   
   
   
    //==============================================================================================
   private byte[] getByteArrayFromInteger(int intValue) 
   {
      ByteBuffer wrapped = ByteBuffer.allocate(4);
      wrapped.putInt(intValue);
      byte[] byteArray = wrapped.array();
      return byteArray;
   
   }
   
   private byte[] getByteArrayFromDouble(double doubleValue) 
   {
      byte[] byteArray = new byte[8];
      long lng = Double.doubleToLongBits(doubleValue);
      for(int i = 0; i < 8; i++) 
         byteArray[i] = (byte)((lng >> ((7 - i) * 8)) & 0xff);
   
      return byteArray;
   }

   
   
}//the end of class