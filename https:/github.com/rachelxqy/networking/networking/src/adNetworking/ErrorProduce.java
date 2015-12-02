package adNetworking;



import java.net.*;
import java.nio.*;

import utils.Helper;
public class ErrorProduce
{
   private byte[] fragment=new byte[Helper.bytelength];
   private double p,q;
   
   public ErrorProduce(byte[] fragment1, double p, double q)
   {
      this.fragment=fragment1;
      this.p=p;
      this.q=q;
   }      
   
   
   //Error occure or not, use q value
   public boolean errorOccur()
   {
      boolean ErrorOccur=false;
      int randomError=(int)(Math.random()*100);//math.random include 0, 100=0-99
      System.out.println("random Error number is (<100*p means error occur): "+randomError);
      
      if(randomError<100*p)
         ErrorOccur=true;
       
      return ErrorOccur;   
   }  
     
    //packet lost or just corrupt, use q value
   public boolean packetLost()
   {
      boolean PacketLost=false;
      int randomError=(int)(Math.random()*100);//math.random include 0, 100=0-99
      
      System.out.println("(<100*(1-q) means packet lost): "+randomError);
   
      if(randomError<100*(1-q))
         PacketLost=true;
       
      return PacketLost;   
   }
   
   
   //====================================
   public byte[] getFragNoError()
   {
      return fragment;
   }
   
   public byte[] getFragAfterCorrupt()
   {
      int index=(int)(Math.random()*65524);
      fragment[index]=(byte)(fragment[index]^0x01);
      System.out.println("Wrong fragment number is :" +index);
               
      return fragment;
   }
          
}//the end of class
