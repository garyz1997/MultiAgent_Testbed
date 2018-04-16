package FTAgents;

	/**
	   Import libraries
	 */
import java.util.Arrays;
import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class Part extends Agent {
/**
	   Data
*/
	private int[] ServiceList = {1,2,4,5,3,6,7};//TODO get this from RFID
/**
	   0 is move command
*/ 
	int currRA = 1;
	boolean busy = false; //0 is free, 1 is busy

	protected void setup() 
	{
		doSuspend();//Suspend Agent upon creation. Resume Agent via GUI to start it up.
		addBehaviour(new RequestPerformer(this,1000));		

		// Add the behaviour serving proposal accepts
		addBehaviour(new ProposalAccepted());

		// Add the behaviour serving proposal denies
		addBehaviour(new ProposalDenied());

		// Add the behaviour serving end of testbed message
		addBehaviour(new EndofTestbed());

		// Add the behaviour serving priority messages
		addBehaviour(new PrioritySendtoEnd());

		// Add the behaviour serving bringing the part back to the start of the testbed
		addBehaviour(new BroughtBacktoStart());

		// Add the behaviour serving bringing the part back to the start of the testbed
		addBehaviour(new StuckatEnd());
	}

	protected void takeDown() 
	{
		// Printout a dismissal message
		System.out.println("Part-agent "+getAID().getName()+" terminating.");
	}

	/**
	   Inner class RequestPerformer().
	   This is the behaviour used by the PA to request RA's to do services when it is not busy.
	   The PA tries to request the first service in its list
	 */
	private class RequestPerformer extends TickerBehaviour 
	{
		public RequestPerformer(Agent a, long period) 
		{
			super(a, period);
		}

		protected void onTick() 
		{
			System.out.println("CurrRA = " + currRA);
			if (!busy)
			{
				ACLMessage reqService = new ACLMessage(ACLMessage.PROPOSE);
				reqService.addReceiver(new AID("RA" + Integer.toString(currRA), AID.ISLOCALNAME));
				if (ServiceList.length != 0){
					reqService.setContent(Integer.toString(ServiceList[0]));
				}
				else
				{
					reqService.setContent("0");
				}
				myAgent.send(reqService);
				busy = true;
			}
		}
	}

	/**
	   Inner class ProposalAccepted().
	   This is the behaviour used by the PA to handle when a RA accepts a service request.
	   The PA removes the first service in its list
	 */
	private class ProposalAccepted extends CyclicBehaviour 
	{
		public void action() 
		{
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) 
			{
				System.out.println("PROPOSAL ACCEPTED");
        		int[] newArr = new int[ServiceList.length-1];
        		System.arraycopy(ServiceList, 1, newArr, 0, ServiceList.length-1);
        		ServiceList = newArr;
				busy = false;
			}
			else 
			{
				block();
			}
		}
	}  // End of inner class ProposalAccepted

	/**
	   Inner class ProposalDenied().
	   This is the behaviour used by the PA to handle when a RA moves it to the next RA, either because it can't do the service or it has finished its services.
	   The PA increments its location (currRA)
	 */
	private class ProposalDenied extends CyclicBehaviour 
	{
		public void action() 
		{
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REJECT_PROPOSAL);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) 
			{
				System.out.println("PROPOSAL REJECTED");
        		++currRA;
				busy = false;
			}
			else 
			{
				block();
			}
		}
	}  // End of inner class ProposalDenied

	/**
	   Inner class EndofTestbed().
	   This is the behaviour used by the PA to handle when it reaches the end of the testbed (RA4).
	   If the PA has completed all of its services, it tells RA4 to tell the human to remove it, and deletes the PA.
	   If the PA still has services left, it tells RA4 to tell the human to bring it to the start of the system and prints the services left, then resets location (currRA=1)
	 */
	private class EndofTestbed extends CyclicBehaviour 
	{
		public void action() 
		{
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CANCEL);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null && ServiceList.length == 0){//Part is done
				ACLMessage printSummary = msg.createReply();
				printSummary.setPerformative(ACLMessage.DISCONFIRM);
				printSummary.setContent("<html><center>Part is done!<br>Remove the part from the system</center></html>");
				myAgent.send(printSummary);
				myAgent.doDelete();
			}
			else if (msg != null && ServiceList.length != 0){//Part still has services left
				ACLMessage printSummary = msg.createReply();
				printSummary.setPerformative(ACLMessage.DISCONFIRM);
				printSummary.setContent("<html><center>Bring part to start of system.<br>Remaining services needed:<br>" + Arrays.toString(ServiceList) + "</center></html>");//list of services
				myAgent.send(printSummary);
			}
			else 
			{
				block();
			}
		}
	}  // End of inner class EndofTestbed


	/**
	   Inner class BroughtBacktoStart().
	   This is the behaviour used by the PA to handle when the human has brought it back to the start of the testbed.
	   The PA resets currRA and goes through the system again
	 */
	private class BroughtBacktoStart extends CyclicBehaviour 
	{
		public void action() 
		{
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);//wait for human to bring part to end
				ACLMessage msg = myAgent.receive(mt);
				if (msg != null)
				{
					currRA = 1;//reset RA
					busy = false;
				}
			else 
			{
				block();
			}
		}
	}  // End of inner class BroughtBacktoStart


	/**
	   Inner class StuckatEnd().
	   This is the behaviour used by the PA to handle when the human cannot bring it back to the start of the testbed.
	   The human removes the part
	 */
	private class StuckatEnd extends CyclicBehaviour 
	{
		public void action() 
		{
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REFUSE);//wait for human to bring part to end
				ACLMessage msg = myAgent.receive(mt);
				if (msg != null)
				{
					System.out.println("Part has been removed from the system. Remaining services needed: " + Arrays.toString(ServiceList));
					myAgent.doDelete();
				}
			else 
			{
				block();
			}
		}
	}  // End of inner class BroughtBacktoStart


	/**
	   Inner class PrioritySendtoEnd().
	   This is the behaviour used by the PA to handle when another PA has higher priority and needs this one to move to the end.
	   The PA tells every RA to send it to the next by sending it 0's, until it reaches RA4
	 */
	private class PrioritySendtoEnd extends CyclicBehaviour 
	{
		public void action() 
		{
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPAGATE);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) 
			{
				while (currRA != 4)
				{
					ACLMessage reqService = new ACLMessage(ACLMessage.PROPOSE);
					reqService.addReceiver(new AID("RA" + Integer.toString(currRA), AID.ISLOCALNAME));
					reqService.setContent("0");
					myAgent.send(reqService);
					mt = MessageTemplate.MatchPerformative(ACLMessage.REJECT_PROPOSAL);
					ACLMessage reply = myAgent.receive(mt);
					if (reply != null) 
					{
						System.out.println("MOVING PA TO NEXT RESOURCE");
						++currRA;
					}
				}
				busy = false;
			}
			else 
			{
				block();
			}
		}
	}  // End of inner class PrioritySendtoEnd

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
