package Agents;

import jade.core.AID;
import jade.core.Agent;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import java.util.Arrays;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class PartAgent extends Agent
{
		
	int processNumber;
	int partNumber;
	int location = 1;//1) RFIDbegin, 2) waiting for robot 3) CNCbegin, 4) CNCend, 5) waiting for robot 6)RFIDend, 7) exit
	boolean lock = false;//waiting for message from CNC or robot
	boolean CNC3free = false;
	boolean CNC4free = false;

	protected void setup()
	{
		System.out.println(getAID().getName()+" is ready.");
		//doSuspend();//Suspend Agent upon creation. Resume Agent via GUI to start it up.
		Object[] args = getArguments();
		if (args != null && args.length > 0) {
			processNumber = (Integer) args[0];
			partNumber = (Integer) args[1];
			System.out.println("Process#: " + processNumber + "\nPart#: " + partNumber);
		}
		
		//Register agent in DF
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd  = new ServiceDescription();
		sd.setType("Product");
		sd.setName(getLocalName());
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {e.printStackTrace();}
		
		// Add the logic behavior
		addBehaviour(new partLogic(this,1000));

		// Add the behaviour serving busy messages from the robot
		addBehaviour(new BusyHandler());

		// Add the behaviour serving busy messages from the robot
		addBehaviour(new FreeHandler());
		
		//Add behaviour service to handle messages from RFID 3 that empty pallet is available
		addBehaviour(new EmptyPalletHandler());
	}
	
	private class partLogic extends TickerBehaviour
	{
		public partLogic(Agent a, long period) 
		{
			super(a, period);
		}
		protected void onTick()
		{
			if (lock == false)
			{
				switch (location) 
				{
				case 1://RFIDBegin
					System.out.println("Location 1");
					if (processNumber != 3 || partNumber==1)//part doesn't need anything here
					{//tell RFID to send sled
						System.out.println("No services can be done here");
						System.out.println("Sending message to send pallet");
						ACLMessage releasePart= new ACLMessage( ACLMessage.REQUEST );
						releasePart.addReceiver(new AID("rfid3Agent", AID.ISLOCALNAME));
						releasePart.setContent("Release Pallet");
						send(releasePart);
						location = 6;
					}
					else//part needs a process here. For now, send part# 2 to CNC 3, and part# 3 to CNC 4
					{
						if (partNumber==2)
						{//Check if CNC 3 free
							System.out.println("Sending message see if CNC 3 free");
							//Message to CNC
							ACLMessage checkCNC3Free = new ACLMessage( ACLMessage.REQUEST );
							checkCNC3Free.addReceiver(new AID("CNC3Agent", AID.ISLOCALNAME));
							checkCNC3Free.setContent("free?");
							send(checkCNC3Free);
							lock = true;
							//Stop sending requests to robot until robot responds
						}
						else if (partNumber==3)
						{//tell ROBOT to move part to CNC4
							System.out.println("Sending message see if CNC 4 free");
							ACLMessage checkCNC4Free = new ACLMessage( ACLMessage.REQUEST );
							checkCNC4Free.addReceiver(new AID("CNC4Agent", AID.ISLOCALNAME));
							checkCNC4Free.setContent("free?");
							send(checkCNC4Free);
							lock = true;
						}
					}
					break;
				case 2:
					if (partNumber==2)
					{
						ACLMessage tryDropCNC3 = new ACLMessage(ACLMessage.REQUEST );
						tryDropCNC3.addReceiver(new AID("RobotAgent", AID.ISLOCALNAME));
						tryDropCNC3.setContent("dropCNC3");
						send(tryDropCNC3);
						lock = true;
					}
					else if(partNumber == 3)
					{
						ACLMessage tryDropCNC4 = new ACLMessage(ACLMessage.REQUEST );
						tryDropCNC4.addReceiver(new AID("RobotAgent", AID.ISLOCALNAME));
						tryDropCNC4.setContent("dropCNC4");
						send(tryDropCNC4);
						lock = true;
					}
					break;
					
				case 3://CNCbegin
					System.out.println("Location 3");
					//TO DO
					//Need to add in conditions to check when CNC is done
					//Repeatedly send messages to CNC to check if it is done
					
					System.out.println("CNC service has completed");
					location++;
					break;
				case 4://CNCend
					System.out.println("Location 4");
					System.out.println("Waiting for empty pallet message from RFID agent");
					lock = true;
					break;
				case 5://Waiting for robot
					//TO DO
					
					
				case 6://RFIDend
					System.out.println("Location 5");
					System.out.println("Sending message to RFID Agent to let pallet pass");
					ACLMessage releasePart = new ACLMessage( ACLMessage.REQUEST );
					releasePart.addReceiver(new AID("rfid3Agent", AID.ISLOCALNAME));
					releasePart.setContent("Release Pallet");
					send(releasePart);
					location++;
					break;
				case 7://Exit
					System.out.println("Location 6");
					myAgent.doDelete();
					break;
				default:
					System.out.println("Location Error");
					break;
				}
			}

		}
	}
	protected void takeDown() 
	{
		// Printout a dismissal message
		System.out.println(getAID().getName()+" terminating.");
	}

	/**
	   Inner class BusyHandler().
	   This is the behaviour used by the PA to handle when a busy message is received
	 */
	private class BusyHandler extends CyclicBehaviour 
	{
		public void action() 
		{
			MessageTemplate mt = MessageTemplate.MatchContent("busy");
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) 
			{
				if (location == 1)
				{
					System.out.println("The CNC is busy, exiting Cell 2");
					location = 5;//RFIDend
					lock = false;
				}
				else if (location == 2)
				{
					System.out.println("Robot busy. Will send resend message to robot to see if free");
					lock = false;
					//Location is still 2 so case 2 will run again to resend message to robot
				}
				else if (location == 5)
				{
					System.out.println("Robot not free to move part to conveyor. Resending request to robot");
					lock = false;
				}
			}
			else 
			{
				block();
			}
		}
	}  // End of inner class BusyHandler


	/**
	   Inner class FreeHandler().
	   This is the behaviour used by the PA to handle when a free message is received
	 */
	private class FreeHandler extends CyclicBehaviour 
	{
		public void action() 
		{
			MessageTemplate mt = MessageTemplate.MatchContent("free");
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) 
			{
				if (location == 1)
				{
					System.out.println("CNC is free");
					location = 2;//Wait for robot
					lock = false;
				}
				else if (location == 2)
				{
					System.out.println("Robot moving part to CNC");
					location = 3;
					lock = false;
				}
				else if (location == 5)
				{
					System.out.println("The ROBOT is moving part to Conveyor");
					location = 4;//RFIDEnd
					lock = false;
				}
			}
			else 
			{
				block();
			}
		}
	}  // End of inner class BusyHandler
	private class EmptyPalletHandler extends CyclicBehaviour
	{
		public void action(){
			MessageTemplate mt = MessageTemplate.MatchSender(new AID("rfid3Agent",AID.ISLOCALNAME));
			ACLMessage msg = myAgent.receive(mt);
			if(msg != null)
			{
				if(!msg.getContent().equals("Empty Pallet Available")){
					System.out.println("Unexpected message recieved by part agent from rfid 3 agent");
				}
				else
				{
					ACLMessage reply = msg.createReply();
					if(location==4){
						reply.setContent("Hold Pallet");
						location++;//Waiting for robot
					}
					else{
						reply.setContent("Release Pallet");
					}
					send(reply);
					
				}
			}
			else{
				block();
			}
		}
	}

}
//1 RFIDbegin, 2 CNCbegin, 3 CNCend, 4 RFIDend, 5 exit