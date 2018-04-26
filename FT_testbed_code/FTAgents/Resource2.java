

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

public class Resource2 extends Agent {
/**
	   Data
*/
	private int[] Services = {0,1,1,1,0,0,0,1};
	private boolean	maintenanceNeeded = false;
/**
	   0 is move command
*/ 
	protected void setup() {
		addBehaviour(new RequestPerformer(this, 1000));

		// Add the behaviour serving proposal accepts
		addBehaviour(new ProposalServer());

		// Add the behaviour serving maintenance answers
		addBehaviour(new MaintenanceDone());
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
			/**
				Check if Resource 2 needs maintenance
			*/ 
			if (maintenanceNeeded == false)
			{
				if (maintenanceCheck() == true)
				{
					maintenanceNeeded = true;
					ACLMessage reqMaintenance = new ACLMessage(ACLMessage.FAILURE);
					reqMaintenance.addReceiver(new AID("RA4", AID.ISLOCALNAME));
					reqMaintenance.setContent("RA2 needs maintenance!");
					myAgent.send(reqMaintenance);
					//MaintenanceGui = new MaintenanceGUI2((Resource2)myAgent);
					//MaintenanceGui.showGui();	
				}
			}

		}

	}

		/**
	   Inner class ProposalServer().
	   This is the behaviour used by the RA to handle when a PA requests a service.
	   
	 */
	private class ProposalServer extends CyclicBehaviour 
	{
		public void action() 
		{
		/**
			Get Proposal Message
			-if 0, send to next RA and reject
			-if service that RA can do, do service and accept
		*/ 
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null)
			{
				int serviceNum = Integer.parseInt(msg.getContent());
				System.out.println("Proposal received: "+Integer.parseInt(msg.getContent()));
				if (Services[serviceNum] == 1 && maintenanceNeeded == false)
				{
					runPython("python RA2S" + msg.getContent() + ".py");
					switch(serviceNum)
					{
						case 1:
							try{ Thread.sleep(1000); } catch (Exception e){}
							break;
						case 2:
							try{ Thread.sleep(5000); } catch (Exception e){}
							break;
						case 3:
							try{ Thread.sleep(10000); } catch (Exception e){}
							break;
						default:
							System.out.println("Invalid service");
							try{ Thread.sleep(1000); } catch (Exception e){}
							break;
					}
					ACLMessage reply = msg.createReply();
					reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
					reply.setContent("Did service" + msg.getContent());
					try{ Thread.sleep(1000); } catch (Exception e){}
					myAgent.send(reply);
				}
				else
				{
					runPython("python conv2.py");
					try{ Thread.sleep(2000); } catch (Exception e){}
					ACLMessage reply = msg.createReply();
					reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
					reply.setContent("Sent Part to RA3");
					myAgent.send(reply);
				}
			}
	   			block(3000);
		}
	}  // End of inner class ProposalServer


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
	   Inner class MaintenanceReq().
	   This is the behaviour used by the RA to handle when another resource requests maintenance.
	   The RA opens up a GUI asking the human if he can perform maintenance.
	 */
	private class MaintenanceDone extends CyclicBehaviour 
	{
		public void action() 
		{
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null)
			{
				maintenanceNeeded = false;
			}
			else 
			{
				block();
			}
		}
	}  // End of inner class MaintenanceReq

	protected boolean maintenanceCheck()
	{
		try
		{
			Process p = Runtime.getRuntime().exec("python switchtest.py");
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String output = in.readLine();
			char maint = '1';
			while (output != null)
			{
				System.out.println(output.charAt(0));
				if (output.charAt(0) == maint)
				{
					return true;
				}
				else
				{
					return false;
				}
			}
		}
		catch (IOException ioe)
		{
			System.out.println("ERROR RUNNING PYTHON FILE!");
		}
		return true;
	}
}
