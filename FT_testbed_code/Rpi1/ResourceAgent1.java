import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.io.*;
import jade.core.AID;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import java.util.*;

public class ResourceAgent1 extends Agent {
	// The catalogue of services available (maps the service to its time)
	
	private Hashtable catalogue;
	// The GUI by means of which the user can add services in the catalogue
	//----private TaskerGui myGui;

	// Put agent initializations here
	protected void setup() {
		// Create the catalogue
		catalogue = new Hashtable();

		// Create and show the GUI 
		//----myGui = new TaskerGui(this);
		//----myGui.showGui();

		// Register the service resource service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("task-serving");//keep this
		sd.setName("JADE-task-running");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}

		// Add the behaviour serving queries from buyer agents
		addBehaviour(new OfferRequestsServer());

		// Add the behaviour serving purchase orders from buyer agents
		addBehaviour(new PurchaseOrdersServer());
		this.updateCatalogue("1", 3, 0);
		//this.updateCatalogue("2", 3, 0);
		//this.updateCatalogue("3", 3, 0);
		System.out.println("Resource-agent "+getAID().getName()+" created.");
	}

	// Put agent clean-up operations here
	protected void takeDown() {
		// Deregister from the yellow pages
		try {
			DFService.deregister(this);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		// Close the GUI
		//----myGui.dispose();
		// Printout a dismissal message
		System.out.println("Resource-agent "+getAID().getName()+" terminating.");
	}

	/**
     This is used to add Services to the Catalogue
	 */
	public void updateCatalogue(final String serviceName, final int time, final int busy) {
		addBehaviour(new OneShotBehaviour() {
			public void action() {
				if (busy == 1)
					System.out.println("This machine is busy");
				else if (busy == 0) {
					catalogue.put(serviceName, new Integer(time));
					System.out.println("Task "+serviceName+" inserted into catalogue. Time required: "+time+" seconds.");
				}
			}
		} );
	}
	/**
     This is invoked by the when the user wants to list services
	 */

	public void printServices() {
		addBehaviour(new OneShotBehaviour() {
			public void action() {
				System.out.println("{Service=Time}:");
				System.out.println(catalogue);
			}
		} );
	}

	/**
     This runs a Python File
	 */
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
	/**
	   Inner class OfferRequestsServer.
	   This is the behaviour used by resource agents to serve incoming requests 
	   for offer from part agents.
	   If the requested service is in the local catalogue the resource agent replies 
	   with a PROPOSE message specifying the time. Otherwise a REFUSE message is
	   sent back.
	 */
	private class OfferRequestsServer extends CyclicBehaviour {
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				// CFP Message received. Process it
				String service = msg.getContent();
				ACLMessage reply = msg.createReply();

				Integer time = (Integer) catalogue.get(service);
				if (time != null) {
					// The requested book is available for sale. Reply with the price
					reply.setPerformative(ACLMessage.PROPOSE);
					reply.setContent(String.valueOf(time.intValue()));
				}
				else {
					// The requested book is NOT available for sale.
					reply.setPerformative(ACLMessage.REFUSE);
					reply.setContent("not-available");
				}
				myAgent.send(reply);
			}
			else {
				block();
			}
		}
	}  // End of inner class OfferRequestsServer

	/**
	   Inner class PurchaseOrdersServer.
	   This is the behaviour used by resource agents to serve incoming 
	   offer acceptances (i.e. service orders) from part agents.
	   The resource agent removes the service from its catalogue 
	   and replies with an INFORM message to notify the part that the
	   service has been sucesfully completed.
	 */
	private class PurchaseOrdersServer extends CyclicBehaviour {
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				// ACCEPT_PROPOSAL Message received. Process it
				String service = msg.getContent();
				ACLMessage reply = msg.createReply();
				if (service.equals("1"))
				{
					System.out.println("Running service "+service);
						runPython("python conv1.py");
				}
				//---Integer time = (Integer) catalogue.remove(service);
				Integer time = (Integer) catalogue.get(service);
				if (time != null) {
					reply.setPerformative(ACLMessage.INFORM);
					System.out.println("["+msg.getSender().getLocalName()+"] Service "+service+" offered to agent "+msg.getSender().getLocalName());
				}
				else {
					// The requested book has been sold to another buyer in the meanwhile .
					reply.setPerformative(ACLMessage.FAILURE);
					reply.setContent("not-available");
				}
				myAgent.send(reply);
			}
			else {
				block();
			}
		}
	}  // End of inner class OfferRequestsServer
}
