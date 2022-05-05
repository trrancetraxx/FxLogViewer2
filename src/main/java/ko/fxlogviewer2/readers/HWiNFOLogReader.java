package ko.fxlogviewer2.readers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import ko.fxlogviewer2.readers.inter.LogReader;

public class HWiNFOLogReader implements LogReader{


String file;


public HWiNFOLogReader(String fileName) {
	this.file=fileName;
}


public ArrayList<String>  getHeaderColumns() throws Exception {
	
	ArrayList<String>columns=new ArrayList<String>();
	
	 BufferedReader reader;
	 reader = new BufferedReader(new FileReader(file));
	 String line = reader.readLine();
	 
	 String[] lineArray=line.split(",");
	 
	 for(int i=0;i<lineArray.length;i++) {
		 if(i<1) continue;
		 else
		 {
			 if(lineArray[i].indexOf("[Yes/No]")!=-1)continue;
			 else columns.add(lineArray[i].trim());
		 }	
	 }
	 columns.set(0,"");
	 reader.close();
	 return columns;
}


public ArrayList<String[]> getData() throws Exception {
	
	ArrayList<String[]>data=new ArrayList<String[]>();
	 BufferedReader reader;
	try {
		reader = new BufferedReader(new FileReader(file));
		String line = reader.readLine(); 
		line = reader.readLine();//ignore first line
		
		 
		
		while (line != null) {
			
			String[] lineArray=line.split(",");
			ArrayList<String> expLine=new ArrayList();
			
			if(lineArray[0].trim().equals("Date"))break;
			
			 for(int i=0;i<lineArray.length;i++) {
				 
				 
				 
				 
				 if(i==0)continue;
				 
				 if(i==1)expLine.add(lineArray[0].trim()+" "+lineArray[1].trim());
				 else
				 {
					 
					 if(lineArray[i].equals("Yes") || lineArray[i].equals("No"))continue;
					 else expLine.add(lineArray[i].trim());
				 }
			 }
			
			
			
			
			
				data.add(expLine.toArray(new String[0]));	
			line = reader.readLine();
		}
		reader.close();
	} catch (IOException e) {
		e.printStackTrace();
	}
	return data;
}




	
}
