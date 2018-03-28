import jade.core.Agent;
import java.util.*;
import jade.core.behaviours.*;
import java.io.*;
import jade.core.AID;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;


public class PartCreator extends Agent
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
		//int agentNum = Integer.parseInt((String)args[0]);
		//int process = Integer.parseInt((String)args[1]);

	
	addBehaviour( new SimpleBehaviour(this)
	{
	    int state = 0;
    	int agentNum = 1;
		Object[] last = new Object[] {"0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0"};
	
	    public void action()
	    {
		switch (state) 
		{
		case 0:
		    System.out.println("HELLO! I am a part creator agent " + getAID().getLocalName());
		    System.out.println("Let's check for Part Agents:");
		    Object[] out = new Object[16];
		    boolean equal = true;
			//args2[0] = Integer.toString(state + 1);
		    AgentContainer c = getContainerController();
		    try
		    {
		    	Process p = Runtime.getRuntime().exec("python ReadOnce.py");
				BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String output = in.readLine();
				output = in.readLine();
				String[] services = output.substring(10,output.length()-1).split("[\\s,]+");
				//String serviceList = "";
				for (int a = 0; a < services.length; ++a)
				{
					out[a] = services[a];
					if (!out[a].toString().equals(last[a].toString()))
					{
						equal = false;
					}
					//serviceList += services[a];
				}

				if (equal == false)
				{
					AgentController a = c.createNewAgent("PA"+Integer.toString(agentNum),"PartPriorityAgent",out);
					a.start();
					++agentNum;
				try{ Thread.sleep(10000); } catch (Exception e){}

				}
				else
				{

				try{ Thread.sleep(4000); } catch (Exception e){}
				}
				last = Arrays.copyOf(out, 16);

/*
				Process p = Runtime.getRuntime().exec("python ReadOnce.py");
				BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String output = in.readLine();
				if (output != null)
				{
					while (output != null)
				    	{
						System.out.println(output);
						output = in.readLine();
						out[0] = "1";
						out[1] = "2";
						out[2] = "3";
						out[3] = "4";
					    }
					AgentController a = c.createNewAgent("PA"+Integer.toString(agentNum),"PartAgent",out);
					a.start();
					++agentNum;
					Thread.sleep(10000);
				}
				*/
				equal = true;
		    }
		    catch (Exception e)
		    {
				System.out.println("IO ERROR");
		    }

				//try{ Thread.sleep(10000); } catch (Exception e){}
		    System.out.println("Reading Done");
		    //System.out.println("Agent PA" + (agentNum-1) + " needs these services: ");
							
		    /*
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
		    */
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
		//++state;
		
	    }
	    public boolean done()
	    {
		return state >= 1;
	    }
	    }
	    );
    }
}
