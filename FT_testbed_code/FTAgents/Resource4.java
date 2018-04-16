

package FTAgents;

	/**
	   Import libraries
	 */
import java.util.Arrays;
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
import jade.lang.acl.MessageTemplate;

public class Resource4 extends Agent {
/**
	   Data
*/
	private String ServiceList = "";
	private RAGUI myGui;
	private AID currPA;
/**
	   0 is move command
*/ 
	protected void setup() {
		addBehaviour(new RequestPerformer(this, 100));
	}

	protected void takeDown() {
		// Printout a dismissal message
		System.out.println("Resource-agent "+getAID().getName()+" terminating.");
	}

	private class RequestPerformer extends TickerBehaviour {
		public RequestPerformer(Agent a, long period) 
		{
			super(a, period);
		}

		protected void onTick() 
		{
			switch (step) 
			{
			case 1:
				/**
	   			Get Proposal Message
	   			-if 0, send to end and send cancel
	   			-if not 0, send to end and print message
				*/ 

				//System.out.println("RA4 STEP 1");
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);//RA4 tells part its at end: CANCEL
				ACLMessage msg = myAgent.receive(mt);
				if (msg != null){
						runPython("python conv4.py");
						ACLMessage reply = msg.createReply();
						reply.setPerformative(ACLMessage.CANCEL);
						myAgent.send(reply);
				}
				++step;
				break;
			case 2:
				/**
	   			Get DISCONFIRM message, print summary
				*/ 
				//System.out.println("RA4 STEP 2");
				mt = MessageTemplate.MatchPerformative(ACLMessage.DISCONFIRM);//RA4 tells part its at end: CANCEL
				msg = myAgent.receive(mt);
				if (msg != null)
				{
					currPA = msg.getSender();
					ServiceList = msg.getContent();
					System.out.println(ServiceList);
					if (ServiceList == "Part is done!")//Part is done, tell human to bring part out of the system
					{
						myGui = new RAGUI((Resource4)myAgent);
						myGui.showGui();
					}
					else//Part still has services, tell human to bring part to start of the system
					{
						myGui = new RAGUI((Resource4)myAgent);
						myGui.showGui();
					}
				}
				step = 1;
				break;
			}
		}
		private int step = 1;

	}

	protected String getServiceList()
	{
		return ServiceList;
	}

	protected void humanAccept()
	{
		ACLMessage informPA = new ACLMessage(ACLMessage.INFORM);
		informPA.addReceiver(currPA);
		informPA.setContent("Human has moved you");
		this.send(informPA);
		myGui.dispose();
	}

	protected void humanDecline()
	{
		ACLMessage informPA = new ACLMessage(ACLMessage.REFUSE);
		informPA.addReceiver(currPA);
		informPA.setContent("Human cannot move you to the start of the testbed.");
		this.send(informPA);
		myGui.dispose();
		ServiceList = "Remove the part from the system";
		myGui = new RAGUI(this);
		myGui.showGui();
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



/**
Part Agent Priority List
Data:
List of Services [1,2,3]
Current RA

1: Check if at end
-If end, tell RA4 to print summary, message remaining services

2: Check for Priority Messages
-If received, add 0s to start of list of services-send part to end

3: Ask current RA if can do first service
-If can, do service
-if not, add 0 to start of list of services
*/