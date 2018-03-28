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

public class PartPriorityAgent extends Agent {
/**
	   Data
*/
	//private int[] ServiceList = {1,2,4,5,3,7};
	private int[] ServiceList = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	private Object[] args;
	private boolean waitForReply = false;
	//List of services needed
	//args = getArguments();
/**
	   0 is move command
*/ 
	int currRA = 1;
	protected void setup() {
		doSuspend();
		args = getArguments();
		for (int a = 0; a < args.length; ++a) {
			ServiceList[a] = Integer.valueOf((String) args[a]);
		}
		addBehaviour(new RequestPerformer());
	}

	protected void takeDown() {
		/**
	   All tasks done: Send message to RA4 for summary
*/ 
		// Printout a dismissal message
		System.out.println("Part-agent "+getAID().getName()+" terminating.");
	}

	private class RequestPerformer extends Behaviour {
		private int step = 1;

		public void action() {
			switch (step) {
			case 1:
				/**
	   			Check if at end
				-If end, tell RA4 to print summary, message remaining services 
				*/ 
				System.out.println("STEP 1");
					MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CANCEL);//RA4 tells part its at end: CANCEL
					ACLMessage msg = myAgent.receive(mt);
					if (msg!= null) {try{ Thread.sleep(3000); } catch (Exception e){}}
					if (msg != null && ServiceList.length == 0){//Part is done
						ACLMessage printSummary = msg.createReply();
						printSummary.setPerformative(ACLMessage.DISCONFIRM);
						printSummary.setContent("Part is done!");//list of services
						myAgent.send(printSummary);
						block(2000);
						myAgent.doDelete();
						waitForReply = false;
					}
					else if (msg != null && ServiceList.length != 0){//Part still has services left
						ACLMessage printSummary = msg.createReply();
						printSummary.setPerformative(ACLMessage.DISCONFIRM);
						printSummary.setContent("Bring part to start of system. Remaining services needed:\n" + Arrays.toString(ServiceList));//list of services
						myAgent.send(printSummary);
						try{ Thread.sleep(4000); } catch (Exception e){}
						currRA = 1;//reset RA
						waitForReply = false;
					}
				
				++step;
				break;
			case 2:
				/**
	   			2: Send Priority Messages
				-If Higher Priority than Part before, send them a PROPAGATE msg
	   			Check for Priority Messages
				-If received, add 0s to start of list of services-send part to end
				*/ 
				System.out.println("STEP 2");

				if (waitForReply == false)
				{
					mt = MessageTemplate.MatchPerformative(ACLMessage.PROPAGATE);
					msg = myAgent.receive(mt);
					if (msg != null){
						while (currRA != 4)
						{
							ACLMessage reqService = new ACLMessage(ACLMessage.PROPOSE);
							reqService.addReceiver(new AID("RA" + Integer.toString(currRA), AID.ISLOCALNAME));
							reqService.setContent("0");
							myAgent.send(reqService);
							mt = MessageTemplate.MatchPerformative(ACLMessage.REJECT_PROPOSAL);
							ACLMessage reply = myAgent.receive(mt);
							if (reply != null) {
								System.out.println("PROPOSAL REJECTED");
							//int[] newArr = new int[ServiceList.length+1];
						    			//System.arraycopy(ServiceList, 0, newArr, 1, ServiceList.length);
						    			//ServiceList = newArr;
								++currRA;
							}
							step = 1;
						}
					}
				}
				++step;
				break;
			case 3:
				System.out.println("STEP 3");

				/**
				3: Ask current RA if can do first service
				-If can, do service
				-if not, move to next RA
				*/ 
				if (waitForReply == false)
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
					try{ Thread.sleep(4000); } catch (Exception e){}
					waitForReply = true;
				}
				else
				{
					mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
					ACLMessage reply = myAgent.receive(mt);
					if (reply != null) {//remove beginning
						System.out.println("PROPOSAL ACCEPTED");
	        			int[] newArr = new int[ServiceList.length-1];
	        			System.arraycopy(ServiceList, 1, newArr, 0, ServiceList.length-1);
	        			ServiceList = newArr;
	        			waitForReply = false;
						try{ Thread.sleep(10000); } catch (Exception e){}
					}
					mt = MessageTemplate.MatchPerformative(ACLMessage.REJECT_PROPOSAL);
					ACLMessage reply2 = myAgent.receive(mt);
					if (reply2 != null) {
						System.out.println("PROPOSAL REJECTED");
						try{ Thread.sleep(5000); } catch (Exception e){}
						//int[] newArr = new int[ServiceList.length+1];
	        			//System.arraycopy(ServiceList, 0, newArr, 1, ServiceList.length);
	        			//ServiceList = newArr;
	        			++currRA;
	        			waitForReply = false;
	       			}
				}
				
				step = 1;
				break;
			}
			
			try{ Thread.sleep(1000); } catch (Exception e){}}
			/*
			if (currRA == 4) {
				try{ Thread.sleep(10000); } catch (Exception e){}}
		}
		*/

		public boolean done() {
      		return false;
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