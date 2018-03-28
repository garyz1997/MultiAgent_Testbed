import jade.core.Agent;
import java.util.*;
import jade.core.behaviours.*;
import java.io.*;
import jade.core.AID;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;


public class startAgent extends Agent
{
    protected void setup()
    {
    	// Registration with the DF 
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();   
		sd.setType("startAgent"); 
		sd.setName(getName());
		sd.setOwnership("Rpi4");
		dfd.setName(getAID());
		dfd.addServices(sd);
		try {
			DFService.register(this,dfd);
			WaitPingAndRunBehaviour PingBehaviour = new  WaitPingAndRunBehaviour(this);
			addBehaviour(PingBehaviour);
		} catch (FIPAException e) {
			System.out.println("Agent "+getLocalName()+" - Cannot register with DF");
			doDelete();
		}
    }

	private class WaitPingAndRunBehaviour extends CyclicBehaviour {

		public WaitPingAndRunBehaviour(Agent a) {
			super(a);
		}

		public void action() {
			System.out.println("HI IM JILL");
				sendping("Jack");
				/*
				try
				{
					Thread.sleep(10000);
				}
				catch (Exception e) {}
				*/
				//block(1000000);
		}
	}


	protected void sendping(String receiver)
	{
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
				//msg.setPerformative(ACLMessage.REQUEST);
		msg.setContent("ping");  
		msg.addReceiver( new AID(receiver, AID.ISLOCALNAME) );
		send(msg);
	}

    protected void runPython(String fileName)
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
