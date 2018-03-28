import java.util.*;
import java.util.ArrayList;
import java.io.*;

public class readonce
{
	public static void main(String[] args)
	{
		System.out.println("Hello from Java!");
		try
		{
			Process p = Runtime.getRuntime().exec("python ReadOnce.py");
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String output = in.readLine();
			output = in.readLine();
			/*
			while (output != null)
			{
				//System.out.println(output);
				output = in.readLine();
			}
			*/
			String[] services = output.substring(10,output.length()-1).split("[, ]");
			String serviceList = "";
			for (int a = 0; a < services.length; ++a)
			{
				serviceList += services[a];
			}
			System.out.println(serviceList);
		}
		catch (IOException ioe)
		{
			System.out.println("IO ERROR");
		}
		System.out.println("Goodbye from Java!");
	}
}
