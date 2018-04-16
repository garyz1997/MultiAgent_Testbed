package Agents;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class ClientPython {
	Map<String,String> map = new HashMap<String,String>();
	Boolean useSimulation = false;
	
	public void SimulatingTags(){
		useSimulation = true;
		String fileToParse = "SimulationTagMapping.csv";
		BufferedReader fileReader = null;
		final String DELIMITER = ",";
		try
		{
			String line="";
			fileReader = new BufferedReader(new FileReader(fileToParse));
			while((line=fileReader.readLine()) != null){
				String[] tokens = line.split(DELIMITER);
				//Ignore blank lines
				if(tokens.length > 0){
					//Add to map. The key is the actual tag and the definition is the simulated tag
					map.put(tokens[0], tokens[1]);
				}
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		try {
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getValue(String tagName){
		if(useSimulation){
			tagName = "AdvManLab.PLC." + map.get(tagName);
		}
		else{
			tagName = "[AdvManLab]" + tagName;
		}
		Runtime rt = Runtime.getRuntime();
		Process pr;
		try {
			pr = rt.exec("python " + System.getProperty("user.dir")+"\\src\\Agents\\PythonOPCRead.py " + tagName);
			BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
			String line=null;
			line=input.readLine();
			String s=null;
			while ((s = stdError.readLine()) != null) {
			    System.out.println(s);
			}
			return line;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Error";
	}
	public void doWrite(String tagName, String newValue){
		if(useSimulation){
			tagName = "AdvManLab.PLC." + map.get(tagName);
		}
		Runtime rt = Runtime.getRuntime();
		Process pr;
		try {
			pr = rt.exec("python " + System.getProperty("user.dir")+"\\src\\Agents\\PythonOPCWrite.py " + tagName + " " + newValue);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//These empty methods are just to make this class look like Java client class
	public void closeConnection(){
	}
	public void init(){
	}
	public void doRead(){
	}
	public void connect(){
	}
	

}
