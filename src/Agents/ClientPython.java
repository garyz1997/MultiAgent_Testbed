package Agents;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ClientPython {
	public String getValue(String tagName){
		Runtime rt = Runtime.getRuntime();
		Process pr;
		try {
			pr = rt.exec("python " + System.getProperty("user.dir")+"\\src\\Agents\\PythonOPC.py " + String.format("%s", tagName));
			BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			BufferedReader stdError = new BufferedReader(new 
				     InputStreamReader(pr.getErrorStream()));
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
