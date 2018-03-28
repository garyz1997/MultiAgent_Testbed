import jade.core.Agent;
import java.util.*;
import jade.core.behaviours.*;
import java.io.*;
import jade.core.AID;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;


public class SimplePartCreator extends Agent
{
    
    protected void setup()
    {



		AgentContainer c = getContainerController();
		AgentController a = c.createNewAgent("PA","PartAgent",null);
		
		addBehaviour( new SimpleBehaviour(this)
		{
		    int state = 0;
		
		    public void action()
		    {
			    Object[] out = new Object[4];
			    AgentContainer c = getContainerController();
			    try
			    {
					AgentController a = c.createNewAgent("PA","PartAgent",null);
					++state;
			    }
			    catch (Exception e)
			    {
					System.out.println("IO ERROR");
			    }
			    System.out.println("Reading Done");
			    System.out.println("Agent PA needs these services: ");
		
		    }
		    public boolean done()
		    {
			return state >= 1;
		    }
		    }
		    );
    }
}
