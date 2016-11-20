package org.havalinasw.MGUI;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Scanner;

public final class Utils {

	public static void saveTextFile(String contents, File file) throws IOException {
		PrintWriter out = new PrintWriter(new FileWriter(file));
		out.print(contents);
		out.close();
	}

	public static void saveTextFile(byte[] contents, File file) throws IOException {
		PrintWriter out = new PrintWriter(new FileWriter(file));
		out.print(contents);
		out.close();
	}
	
	// helper function to read a file into a byte array
	public static final byte[] ReadFile(File strFile) throws IOException
	{
		int nSize = 32768;
		// open the input file stream
		BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(strFile),nSize);
		byte[] pBuffer = new byte[nSize];
		int nPos = 0;
		// read bytes into a buffer
		nPos += inStream.read(pBuffer,nPos,nSize-nPos);
		// while the buffer is filled, double the buffer size and read more
		while(nPos==nSize)
		{
			byte[] pTemp = pBuffer;
			nSize *= 2;
			pBuffer = new byte[nSize];
			System.arraycopy(pTemp,0,pBuffer,0,nPos);
			nPos += inStream.read(pBuffer,nPos,nSize-nPos);
		}
		// close the input stream
		inStream.close();
		if(nPos==0)
		{
			return "".getBytes();
		}
		// return data read into the buffer as a byte array
		byte[] pData = new byte[nPos];
		System.arraycopy(pBuffer,0,pData,0,nPos);
		return pData;
	}

	
	public static void copyToDir(File file,File dir){
		try {
			FileChannel srcChannel = new FileInputStream(file).getChannel();
			FileChannel dstChannel = new FileOutputStream(dir).getChannel();
			dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
			srcChannel.close();
			dstChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}  

	public static void replaceInFile(File inputFile, File outputFile, String searchString, String replaceString) {
		try {
			// make 32k buffer for output
			StringBuffer strOutput = new StringBuffer(32768);
			// read input file into a byte array
			byte[] pInput = ReadFile(inputFile);
			// make a backup copy
//			saveTextFile(pInput, inputFile.getName() +".backup.copy");
			String strInput = new String(pInput);
			System.out.println("Replacing \""+searchString+"\" with \""+replaceString+"\" in file: "+inputFile);
			// find all instances of args[1] and replace it with args[2]
			int nPos = 0;
			while(true)
			{
				int nIndex = strInput.indexOf(searchString,nPos);
				// if args[1] can no longer be found, then copy the rest of the input
				if(nIndex<0)
				{
					strOutput.append(strInput.substring(nPos));
					break;
				}
				// otherwise, replace it with args[2] and continue
				else
				{
					strOutput.append(strInput.substring(nPos,nIndex));
					strOutput.append(replaceString);
					nPos = nIndex + searchString.length();
				}
			}
			strInput = strOutput.toString();
			// write the output string to file
			saveTextFile(strInput.getBytes(), inputFile);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	public static File replaceTextInFile(String inputFile, String outputFile, String searchString, String replaceString) throws java.io.IOException {
		
		// read the file into a new StringBuffer
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		char[] buff = new char[1024];
		int numRead = 0;
		while ((numRead=reader.read(buff)) != -1) {
			fileData.append(buff, 0, numRead);
		}
		reader.close();
		//System.out.println(fileData.toString());
		
		// find and replace each instance of replaceString
		int currentIndex = 0;
		int searchStringLength = searchString.length();
		while ((currentIndex = fileData.indexOf(searchString, currentIndex)) != -1) {
			fileData.delete(currentIndex, currentIndex + searchStringLength);
			fileData.insert(currentIndex, replaceString);
		}
		
		// save to the output file
		File result = new File(outputFile);
		Utils.saveTextFile(fileData.toString(), result);
		return result;
		
	}
	
}
