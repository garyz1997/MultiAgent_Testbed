import java.util.*;
import java.util.ArrayList;
import java.io.*;

public class activateConveyor
{
	public static void main(String[] args)
	{
		//System.out.println("Hello from Java!");
		runPython("python conv4.py");
		//System.out.println("Goodbye from Java!");
	}


	public void runPython(String fileName)
	{
		try
		{
			Process p = Runtime.getRuntime().exec(fileName);
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String output = in.readLine();
			while (output != null)
			{
				System.out.println(output);
				output = in.readLine();
			}
		}
		catch (IOException ioe)
		{
			System.out.println("ERROR RUNNING PYTHON FILE!");
		}		
	}
}
