

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

public class Resource3 extends Agent {
/**
	   Data
*/
	private int[] Services = {0,0,0,0,1,1,1,1};
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
			/**
   			Get Proposal Message
   			-if 0, send to next RA and reject
   			-if service that RA can do, do service and accept
			*/ 
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null){
				int serviceNum = Integer.parseInt(msg.getContent());
				System.out.println("Proposal received: "+Integer.parseInt(msg.getContent()));
				if (Services[serviceNum] == 1){
					runPython("python RA3S" + msg.getContent() + ".py");
					switch(serviceNum)
					{
						case 4:
							try{ Thread.sleep(1000); } catch (Exception e){}
							break;
						case 5:
							try{ Thread.sleep(5000); } catch (Exception e){}
							break;
						case 6:
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
				else{
					runPython("python conv3.py");
					try{ Thread.sleep(3000); } catch (Exception e){}
					ACLMessage reply = msg.createReply();
					reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
					reply.setContent("Sent part to RA4");
					try{ Thread.sleep(1000); } catch (Exception e){}
					myAgent.send(reply);
				}
			}
			block(3000);
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
