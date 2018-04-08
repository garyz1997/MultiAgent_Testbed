package Agents;


import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class RobotAgent extends Agent{
	private ClientPython opc=null;
	private Boolean busy = false;
	
	protected void setup(){
		opc=new ClientPython();
		opc.SimulatingTags();
		addBehaviour(new RobotRequest());
		
		
	}
	private class RobotRequest extends CyclicBehaviour{
		public void action(){
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage msg = myAgent.receive(mt);
			if(msg != null){
				ACLMessage reply = msg.createReply();
				if(msg.getContent() == "busy?")
				{
					reply.setPerformative(ACLMessage.INFORM);
					getRobotStatus();
					if(busy){
						reply.setContent("busy");
					}
					else{
						reply.setContent("free");
					}
				}
				//if the request has the word "run" in it
				else if(msg.getContent().indexOf("run") != -1)
				{
					//SEE RUNG 15: NEED TO RESET THE ROBOT PROGRAM TO RUN TAGS WHEN GAT PRESENT GOES LOW
					if(msg.getContent()=="dropCNC3"){
						opc.doWrite("Fanuc_Rbt_C2:O.Data[0].0","1");
					}
					if(msg.getContent()=="dropCNC4"){
						opc.doWrite("Fanuc_Rbt_C2:O.Data[0].1","1");
					}
					
				}
			}
			else{
				block();
			}
			
			
		}
		private void getRobotStatus(){
			String prog1, prog2, prog3, prog4;
			//Pick from conveyor and drop on CNC3 program running
			prog1=opc.getValue("Fanuc_Rbt_C2:I.Data[0].2");
			//Pick from conveyor and drop on CNC4 program running
			prog2=opc.getValue("Fanuc_Rbt_C2:I.Data[0].3");
			//Pick from CNC3 and drop on conveyor running
			prog3=opc.getValue("Fanuc_Rbt_C2:I.Data[0].4");
			//Pick from CNC4 and drop on conveyor running
			prog4=opc.getValue("Fanuc_Rbt_C2:I.Data[0].5");
			if(prog1=="0"&&prog2=="0"&&prog3=="0"&&prog4=="0"){
				busy=false;
			}
			else{
				busy=true;
			}
		}
	}
	protected void takeDown() {
		// Printout a dismissal message
		System.out.println(getAID().getName()+" terminating.");
	}

}