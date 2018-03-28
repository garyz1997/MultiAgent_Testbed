import jade.core.Agent;
import java.util.*;
import jade.core.behaviours.*;
import java.io.*;
import jade.core.AID;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;


public class HelloWorldAgent extends Agent
{
    
    protected void setup()
    {
	//System.out.println("Hello, I am an Agent!!\n"+"My local-name is " + getAID().getLocalName());
	//System.out.println("My GUID is " + getAID().getName());
	//System.out.println("My addresses are: ");
	//Iterator it = getAID().getAllAddresses();
	//while (it.hasNext())
	//{
	//    System.out.println("- " + it.next());
	//}
    	/*
	Object[] args = getArguments();
	String s;
	int agentNum = Integer.parseInt((String)args[0]);
	int process = Integer.parseInt((String)args[1]);
*/
	
	addBehaviour(new SimpleBehaviour(this)
	{
	
	    public void action()
	    {
	    try
		{
			Process p = Runtime.getRuntime().exec("python conv4.py");
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
	    public boolean done() {  return true;  }
	
    });
	}	
}