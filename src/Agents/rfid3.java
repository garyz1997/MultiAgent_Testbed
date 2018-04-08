package Agents;

//import BookSellerAgent.PurchaseOrdersServer;
import jade.core.Agent;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


public class rfid3 extends Agent{
	//Comment out one of the following depending on which OPC language to use for connection and change  usePython above
		
	//Use Utgard OPC client (Java):
	//private Client opc = null;
		
	//Use OpenOPC (Python)
	private ClientPython opc=null;
	int agentNum = 0;
	boolean AgentCreatorLocked=false;
	boolean emptyPalletLocked=false;
		
	protected void setup(){
		System.out.println(getAID().getName()+" is ready.");
	
		String[] allTags=new String[]{"Test.PLC.Message1","Test.PLC.Message2","Test.PLC.Message3"};
		//Comment out following if not using Utgard
		//opc=new Client(allTags);
		
		opc=new ClientPython();
		//Converts actual tag names to simulated tag names for python version of OPC client
		opc.SimulatingTags();
		
		opc.init();
		opc.connect();
		opc.doRead();
			
		addBehaviour(new checkPartPresent(this,100));
		addBehaviour(new releasePallet());
	}
	
	private class checkPartPresent extends TickerBehaviour{
		public checkPartPresent(Agent a, long period) {
			super(a, period);
		}
		protected void onTick(){
			String palletPresent=opc.getValue("Conv_N053:I.Data[3].1");
			String tagPresent="0";
			String processNumber=null;
			String partNumber=null;
			String tagReadyForReading=null;
			//wait 250 milliseconds if there is a part present
			if(palletPresent.equals("1"))
			{
					//Wait 250 ms if there is a pallet present before checking for part presence
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					tagPresent = opc.getValue("RFID_N055:I.Channel[0].TagPresent");
			}
			//Exit onTick() if there is no pallet present
			else{
				emptyPalletLocked=false;
				return;
			}
			//If there is an empty pallet and this stuff hasn't already been done for the empty pallet
			if(tagPresent.equals("0") && emptyPalletLocked==false)
			{
				//Once old part is gone, reset lock on agent creator
				AgentCreatorLocked=false;
				//Send message to all part agents saying there is empty pallet
				//TO DO
				System.out.println("Informing part agents that there is empy pallet present");
				emptyPalletLocked=true;
				return;
			}
			tagReadyForReading=opc.getValue("UpdateStep_RFID3");
			if(tagPresent.equals("1") && AgentCreatorLocked==false && tagReadyForReading.equals("5"))
			{
				processNumber=opc.getValue("R3J_Current_Process_NO");
				partNumber=opc.getValue("R3J_Current_Part_NO");
				 Object[] init = new Object[1];
				 init[0]=processNumber;
				 init[1]=partNumber;
				AgentContainer c = getContainerController();
				try {
					AgentController a = c.createNewAgent("PA"+Integer.toString(agentNum),"PartAgent",init);
					System.out.println("Created new Product Agent");
				} catch (StaleProxyException e) {
					e.printStackTrace();
				}
				agentNum++;
				AgentCreatorLocked=true;
			}
		}
	}
	private class releasePallet extends CyclicBehaviour {
		public void action(){
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) 
			{
				String content = msg.getContent();
				if (content == "Release Pallet")
				{
					opc.doWrite("C2RobotStop.Ext", "0");
					opc.doWrite("C2RobotStop.Ret", "1");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					opc.doWrite("C2RobotStop.Ext", "1");
					opc.doWrite("C2RobotStop.Ret", "0");
				}
					
			}
			else {
				block();
			}
		}
		
	}
	protected void takeDown() {
		// Printout a dismissal message
		opc.closeConnection();
		System.out.println(getAID().getName()+" terminating.");
	}

}
