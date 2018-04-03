package Agents;

import jade.core.Agent;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import jade.core.behaviours.*;


public class PartAgent extends Agent
{
		
	String processNumber=null;
	String partNumber=null;
	int location = 1;//1 RFIDbegin, 2 CNCbegin, 3 CNCend, 4 RFIDend, 5 exit

	protected void setup()
	{
		System.out.println(getAID().getName()+" is ready.");

		Object[] args = getArguments();
		if (args != null && args.length > 0) {
			processNumber = (String) args[0];
			partNumber = (String) args[1];
		}


		if (processNumber != "4" || partNumber == 1)//part doesnt need anything here
		{//tell RFID to send sled

		}
		else//part needs a process here depending on part #
		{
			if (partNumber == 2)
			{//tell ROBOT to move part to CNC3

				location = 2;
			}
			else if (partNumber == 3)
			{//tell ROBOT to move part to CNC4

				location = 2;
			}
		}

		// Add the behaviour serving queries from buyer agents
		addBehaviour(new OfferRequestsServer());

		// Add the behaviour serving purchase orders from buyer agents
		addBehaviour(new PurchaseOrdersServer());
	}
	
	private class partLogic extends TickerBehaviour
	{
		public partLogic(Agent a, long period) 
		{
			super(a, period);
		}
		protected void onTick()
		{
			switch (location) {
			case 2://CNCbegin
				break;
			case 3://CNCend
				break;
			case 4://RFIDend
				break;
			case 5://Exit
				break;

		}

	}
	protected void takeDown() 
	{
		// Printout a dismissal message
		System.out.println(getAID().getName()+" terminating.");
	}

}
