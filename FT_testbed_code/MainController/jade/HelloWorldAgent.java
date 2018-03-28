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

	Object[] args = getArguments();
	String s;
	int agentNum = Integer.parseInt((String)args[0]);
	int process = Integer.parseInt((String)args[1]);

	
	addBehaviour( new SimpleBehaviour(this)
	{
	    int state = agentNum;
	
	    public void action()
	    {
		switch (state) {
		case 0:
		    System.out.println("HELLO! I am resource agent " + getAID().getLocalName());
		    System.out.println("Let's check for Part Agents:");
		    AgentContainer c = getContainerController();
		    try
		    {
			Object[] args2 = new Object[2];
			args2[0] = Integer.toString(state + 1);
			args2[1] = "4";
			AgentController a = c.createNewAgent("Jill"+Integer.toString(agentNum),"HelloWorldAgent",args2);
			done();
			a.start();
		    }
		    catch (Exception e) {}
		    break;
		case 1:
		    System.out.println("HELLO! I am part agent " + getAID().getLocalName());
		    System.out.println("This is my information:");
		    try
		    {
			Process p = Runtime.getRuntime().exec("python ReadOnce.py");
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
			System.out.println("IO ERROR");
		    }
		    System.out.println("Reading Done");
		    System.out.println("I need these services: " + (String)args[1]);
		}
		++state;
		
	    }
	    public boolean done()
	    {
		return state >= 1;
	    }
	    }
	    );
    }
}
