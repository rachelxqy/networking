package finalPro;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;
/**
 * Class: ReadFile
 *       to read in file and convert to byte array
 * @author rachelxqy
 *
 */
public class ReadFile {

	//static int size = 16;
	//static byte[] bArray= new byte[size];

	int size =16;
	static byte[] bArray;
	/**
	 * method: readInBytes()
	 *         to read in a file, convert to  byte array
	 * @param filename  the name of a file
	 * @return  byte[]  byte array be returned
	 */
	public static byte[] readInBytes(String filename){

		BufferedReader bfReader = null;
		String file="",line;
		try {
			bfReader = new BufferedReader(new FileReader(filename));
		}catch (FileNotFoundException fnfe){
			System.out.println(fnfe.getMessage()+" not found!");
			System.exit(0);
		} 
		try{
			while((line = bfReader.readLine()) != null){
				//System.out.println(line);
				file+=" "+line;
			}
		}catch (IOException ioe){
			System.out.println(ioe.getMessage()+ " error reading file");
		}
		bArray = file.trim().getBytes();
		return bArray;
	}
	/*
	public static void main(String[] args){
		//FileReader file = new FileReader("src/mission1.txt");
		if (file.exists()){
			System.out.println(file.getName()+" exist!!");
		}else{
			System.out.println("no no no!");
		}

		//FileInputStream fileStream=new FileInputStream(file);
		// byte[] content = fileStream.getBytes();
		//InputStream is = null;
		//Scanner scanner = new Scanner(System.in);
		int count =0;
		byte[] bArray;
		BufferedReader bfReader = null;
		String line;
		try {
			// is = new ByteArrayInputStream(content);
			//bfReader = new BufferedReader(new InputStreamReader(file));
			bfReader = new BufferedReader(new FileReader("src/mission1.txt"));
		}catch (FileNotFoundException fnfe){
			System.out.println(fnfe.getMessage()+" not found!");
			System.exit(0);
		} 
		try{
			//String temp = null;
			while((line = bfReader.readLine()) != null){
				System.out.println(line);
				bArray = line.getBytes();
				System.out.println(bArray.length);
				for(int i=0;i<bArray.length;i++){
					System.out.print(bArray[i]+" ");
				}
				System.out.println();
				System.out.println(bArray);
				String value = new String(bArray, "UTF-8");
				System.out.println(value);
			}
		}catch (IOException ioe){
			System.out.println(ioe.getMessage()+ " error reading file");
		}finally{
			System.exit(0);

		}
	}*/

}
