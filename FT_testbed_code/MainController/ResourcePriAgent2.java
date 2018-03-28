	/**
	   Import libraries
	 */
import java.util.Arrays;
import java.util.List;
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

public class ResourcePriAgent2 extends Agent {
/**
	   Data
*/
	private int[] Services = {0,1,1,1,0,0,0,1};
/**
	   0 is move command
*/ 
	//int currRA = 1;
	protected void setup() {
		addBehaviour(new RequestPerformer());
	}

	protected void takeDown() {
		// Printout a dismissal message
		System.out.println("Resource-agent "+getAID().getName()+" terminating.");
	}

	private class RequestPerformer extends Behaviour {
		//int[] Services = {1,2,3,7};
		public void action() {
			/**
   			Get Proposal Message
   			-if can do service, do service and send accept_proposal
   			-if not, move to next RA and send reject_proposal
			*/ 
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null){
				System.out.println("Proposal received: "+Integer.parseInt(msg.getContent()));
				if (Services[Integer.parseInt(msg.getContent())] == 1){
					runPython("python RA2S" + msg.getContent() + ".py");
					//try{ Thread.sleep(10000); } catch (Exception e){}
					ACLMessage reply = msg.createReply();
					reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
					reply.setContent("Did service" + msg.getContent());
					try{ Thread.sleep(1000); } catch (Exception e){}
					myAgent.send(reply);
				}
				else{
					runPython("python conv2.py");
					ACLMessage reply = msg.createReply();
					reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
					reply.setContent("Sent Part to RA3");
					try{ Thread.sleep(1000); } catch (Exception e){}
					
					myAgent.send(reply);
				}
			}
			block(3000);
    	} 
		public boolean done() {
      		return false;
      	}
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