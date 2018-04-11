package Agents;

import jade.core.AID;
import jade.core.Agent;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;


public class PartAgent extends Agent
{
		
	String processNumber=null;
	String partNumber=null;
	int location = 1;//1 RFIDbegin, 2 CNCbegin, 3 CNCend, 4 RFIDend, 5 exit
	boolean lock = false;//waiting for message

	protected void setup()
	{
		System.out.println(getAID().getName()+" is ready.");

		Object[] args = getArguments();
		if (args != null && args.length > 0) {
			processNumber = (String) args[0];
			partNumber = (String) args[1];
		}

		// Add the logic behavior
		addBehaviour(new partLogic());

		// Add the behaviour serving busy messages from the robot
		addBehaviour(new BusyHandler());
	}
	
	private class partLogic extends TickerBehaviour
	{
		public partLogic(Agent a, long period) 
		{
			super(a, period);
		}
		protected void onTick()
		{
			switch (location) 
			{
			case 1://RFIDBegin
				if (processNumber != "3" || partNumber == "1")//part doesn't need anything here
				{//tell RFID to send sled
					location = 4;
				}
				else//part needs a process here. For now, send part# 2 to CNC 3, and part# 3 to CNC 4
				{
					if (partNumber == "2")
					{//tell ROBOT to move part to CNC3, check robot is free, cnc is free 
						System.out.println("Sending message to ROBOT to check if it and CNC3 are free");
						ACLMessage checkRobotCNCFree = new ACLMessage( ACLMessage.REQUEST );
						checkRobotCNCFree.addReceiver(new AID("RobotAgent", AID.ISLOCALNAME));
						checkRobotCNCFree.setContent("Check CNC3 Free");
						send(checkRobotCNCFree);
					}
					else if (partNumber == "3")
					{//tell ROBOT to move part to CNC4
						System.out.println("Sending message to ROBOT to check if it and CNC4 are free");
						ACLMessage checkRobotCNCFree = new ACLMessage( ACLMessage.REQUEST );
						checkRobotCNCFree.addReceiver(new AID("RobotAgent", AID.ISLOCALNAME));
						checkRobotCNCFree.setContent("Check CNC4 Free");
						send(checkRobotCNCFree);
					}
				}
				break;
			case 2://CNCbegin
				break;
			case 3://CNCend
				System.out.println("Sending message to ROBOT to check if it and PALLET are free");
				ACLMessage checkRobotCNCFree = new ACLMessage( ACLMessage.REQUEST );
				checkRobotCNCFree.addReceiver(new AID("RobotAgent", AID.ISLOCALNAME));
				checkRobotCNCFree.setContent("Check PALLET Free");
				send(checkRobotCNCFree);
				break;
			case 4://RFIDend
				System.out.println("Sending message to RFID Agent to let pallet pass");
				ACLMessage releasePart = new ACLMessage( ACLMessage.REQUEST );
				releasePart.addReceiver(new AID("rfid3Agent", AID.ISLOCALNAME));
				releasePart.setContent("Release Pallet");
				send(releasePart);
				location = 5;
				break;
			case 5://Exit
				myAgent.doDelete();
				break;
			default:
				System.out.println("Location Error");
				break;
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
					System.out.println("The ROBOT or CNC are busy, exiting Cell 2");
					location = 4;//RFIDend
				}
				else if (location == 3)
				{
					System.out.println("The ROBOT or PALLET are currently busy");
				}
			}
			else 
			{
				block();
			}
		}
	}  // End of inner class BusyHandler

}
//1 RFIDbegin, 2 CNCbegin, 3 CNCend, 4 RFIDend, 5 exit