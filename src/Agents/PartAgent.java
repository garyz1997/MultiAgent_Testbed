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
		
	String processNumber=null;
	String partNumber=null;
	int location = 1;//1 RFIDbegin, 2 CNCbegin, 3 CNCend, 4 RFIDend, 5 exit
	boolean lock = false;//waiting for message

	protected void setup()
	{
		System.out.println(getAID().getName()+" is ready.");
		//doSuspend();//Suspend Agent upon creation. Resume Agent via GUI to start it up.
		Object[] args = getArguments();
		if (args != null && args.length > 0) {
			processNumber = (String) args[0];
			partNumber = (String) args[1];
			System.out.println("Process#: " + processNumber + "\nPart#: " + partNumber);
		}

		// Add the logic behavior
		addBehaviour(new partLogic(this,1000));

		// Add the behaviour serving busy messages from the robot
		addBehaviour(new BusyHandler());

		// Add the behaviour serving busy messages from the robot
		addBehaviour(new FreeHandler());
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
					if (!processNumber.equals("3") || partNumber.equals("1"))//part doesn't need anything here
					{//tell RFID to send sled
						System.out.println("No services can be done here");
						System.out.println("Sending message to send pallet");
						ACLMessage releasePart= new ACLMessage( ACLMessage.REQUEST );
						releasePart.addReceiver(new AID("rfid3Agent", AID.ISLOCALNAME));
						releasePart.setContent("Release Pallet");
						send(releasePart);
						location = 4;
					}
					else//part needs a process here. For now, send part# 2 to CNC 3, and part# 3 to CNC 4
					{
						if (partNumber.equals("2"))
						{//tell ROBOT to move part to CNC3, check robot is free, cnc is free 
							System.out.println("Sending message to ROBOT to check if it and CNC3 are free");
							ACLMessage tryDropCNC3 = new ACLMessage( ACLMessage.REQUEST );
							tryDropCNC3.addReceiver(new AID("RobotAgent", AID.ISLOCALNAME));
							tryDropCNC3.setContent("dropCNC3");
							send(tryDropCNC3);
							lock = true;
							//Stop sending requests to robot until robot responds
						}
						else if (partNumber.equals("3"))
						{//tell ROBOT to move part to CNC4
							System.out.println("Sending message to ROBOT to check if it and CNC4 are free");
							ACLMessage tryDropCNC4 = new ACLMessage( ACLMessage.REQUEST );
							tryDropCNC4.addReceiver(new AID("RobotAgent", AID.ISLOCALNAME));
							tryDropCNC4.setContent("dropCNC4");
							send(tryDropCNC4);
							lock = true;
						}
					}
					break;
				case 2://CNCbegin
					System.out.println("Location 2");
					System.out.println("CNC service has completed");
					location = 3;
					break;
				case 3://CNCend
					System.out.println("Location 3");
					System.out.println("Sending message to ROBOT to check if it and PALLET are free");
					ACLMessage checkRobotCNCFree = new ACLMessage( ACLMessage.REQUEST );
					checkRobotCNCFree.addReceiver(new AID("RobotAgent", AID.ISLOCALNAME));
					checkRobotCNCFree.setContent("Check PALLET Free");
					send(checkRobotCNCFree);
					lock = true;
					break;
				case 4://RFIDend
					System.out.println("Location 4");
					System.out.println("Sending message to RFID Agent to let pallet pass");
					ACLMessage releasePart = new ACLMessage( ACLMessage.REQUEST );
					releasePart.addReceiver(new AID("rfid3Agent", AID.ISLOCALNAME));
					releasePart.setContent("Release Pallet");
					send(releasePart);
					location = 5;
					break;
				case 5://Exit
					System.out.println("Location 5");
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
					System.out.println("The ROBOT or CNC are busy, exiting Cell 2");
					location = 4;//RFIDend
					lock = false;
				}
				else if (location == 3)
				{
					System.out.println("The ROBOT or PALLET are currently busy");
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
					System.out.println("The ROBOT and CNC are free, moving to CNC");
					location = 2;//CNCBegin
					lock = false;
				}
				else if (location == 3)
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

}
//1 RFIDbegin, 2 CNCbegin, 3 CNCend, 4 RFIDend, 5 exit