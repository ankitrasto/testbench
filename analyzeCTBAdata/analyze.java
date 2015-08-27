/* Statistical Analysis for cTBA Data
 * Aug 16, 2015
 * */

import org.apache.commons.math3.stat.correlation.*;
import java.io.*;
import java.util.*;
import org.apache.commons.math3.analysis.*;
import java.text.*;

public class analyze{
	public static String iterateDir = "";
	public static ArrayList<String> corrList = new ArrayList<String>();
	
	
	public static double fisher(double r){
		return 0.5*Math.log((1.0 + r)/(1.0 - r));
	}

	public static void readCorrelationFile(){
		String dataHold = "";
		//check
		
		try{
			BufferedReader bR = new BufferedReader(new FileReader("config.txt"));
			//first line should be MEANTS directory
			dataHold = bR.readLine();
			iterateDir = dataHold.split("=")[1].trim();	
			dataHold = bR.readLine(); //next line
			//next line iterate through to add correlations
			while(dataHold != null){
				corrList.add(dataHold);
				dataHold = bR.readLine(); 
			}
			bR.close();		
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Error Reading File. Check format");
			return;
		}
		
		System.out.println("\nIteration Directory = " + iterateDir + "\n");
		
		System.out.println("-------Correlation List-------");
		System.out.println(iterateDir);
		for(int i = 0; i < corrList.size(); i++){
			String temp = (String)corrList.get(i).split(",")[0].trim();
			String temp2 = (String)corrList.get(i).split(",")[1].trim();
			System.out.println(temp + " <------> " + temp2);
		}
		System.out.println("------------------------------");

	}
	
	
	public static double[] fileContents(File x){
		String dataHold = "";
		ArrayList<Double> contents = new ArrayList<Double>();
		try{
			BufferedReader bR = new BufferedReader(new FileReader(x.getAbsoluteFile()));
			dataHold = bR.readLine();
			while(dataHold != null){
				contents.add(Double.parseDouble(dataHold));
				dataHold = bR.readLine();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		double contentsPrimitive[] = new double[contents.size()];
		for(int i = 0; i < contents.size(); i++){
			contentsPrimitive[i] = (double)contents.get(i);
		}
				
		return contentsPrimitive;
	}
	
	public static int searchIndex(File[] auxHold, String key){
		for(int i = 0; i < auxHold.length; i++){
			if((auxHold[i].getAbsolutePath().toLowerCase()).indexOf(key.toLowerCase()) >= 0){
				return i;
			}
		}
		return -1;
	}
	
	public static void calculate(){
		ArrayList<String> finalOutput = new ArrayList<String>();
		String dataHold = ""; //will hold each correlation line
		File iterateDirFile[] = (new File(iterateDir)).listFiles(); //nested level of files x 2
		PearsonsCorrelation auxStat = new PearsonsCorrelation();
		
		dataHold = "Subject,Control,Test,PEARSON,FISHER";
		
		finalOutput.add(dataHold);
		
		for(int i = 0; i < iterateDirFile.length; i++){
			if(iterateDirFile[i].isDirectory()){
				
				File fileList[] = iterateDirFile[i].listFiles(); //list of text files
				
				//analyze correlations here:
				
				for(int j = 0; j < corrList.size(); j++){
						dataHold = iterateDirFile[i].getName() + ",";
						dataHold += (String)corrList.get(j) + ",";
						//System.out.println(fileList[searchIndex(fileList, (String)corrList.get(j).split(",")[1].trim())].getAbsolutePath());
						double[] controlData = fileContents(fileList[searchIndex(fileList, (String)corrList.get(j).split(",")[0].trim())]); 
						double[] testData = fileContents(fileList[searchIndex(fileList, (String)corrList.get(j).split(",")[1].trim())]);
						dataHold += auxStat.correlation(controlData, testData) + ",";
						dataHold += fisher(auxStat.correlation(controlData, testData));
																		
						finalOutput.add(dataHold);
				}
			}
		}
		
		//write output to file
		Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd_hhmmss");
        String fileOutputName = "Results" + ft.format(dNow) + ".csv";
        
        try{
			PrintWriter pW = new PrintWriter(new FileWriter(fileOutputName, false));
			for(int x = 0; x < finalOutput.size(); x++){
				pW.println((String)finalOutput.get(x));
			}	
			pW.close();	
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}
	
	
	public static void main(String []args){
		
		readCorrelationFile();
		
		calculate();
		
		
		
		/*String s1= "asdoaihfoiqebvlwekawbnlvkabwlkaf/assdasdasdaslda/Starts_With_x";
		String s2 = "starts_WITH";
		System.out.println((s1.toLowerCase()).indexOf(s2.toLowerCase()));*/

		
		
		/*File testRead = new File("/home/ankit/Documents/cTBS_DATA_ANALYSIS/MEANTS/EB_Sham_Post_meants/R_Crus2_meants");
		double[] lines = fileContents(testRead);
		
		for(int i = 0; i < lines.length; i++){
			System.out.println(lines[i]);
		}*/
		
		/*PearsonsCorrelation x = new PearsonsCorrelation();
		double[] col1 = new double[12];
		double[] col2 = new double[12];
		Random rg = new Random();
		
		for(int i = 0; i < col1.length; i++){
			col1[i] = i;
			col2[i] = rg.nextInt(50)+1;
			System.out.println(col1[i] + "\t" + col2[i]);
		}
		
		System.out.println("correlation = " + x.correlation(col1, col2));
		System.out.println("FISHER = " + fisher(x.correlation(col1, col2)));*/
	
	}

}
