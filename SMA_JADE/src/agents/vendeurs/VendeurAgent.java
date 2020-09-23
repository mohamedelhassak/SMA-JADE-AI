package agents.vendeurs;

import java.util.Random;

import agents.acheteur.AcheteurGui;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import javafx.application.Application;
import javafx.stage.Stage;

public class VendeurAgent extends Agent {
	
	protected VendeurGui gui;
	protected double prix;

	@Override
	protected void setup() {
		if(getArguments().length == 2) {
			gui = (VendeurGui) getArguments()[0];
			prix = (double)getArguments()[1];
			gui.vendeurAgent = this;
		}
		
		ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
		addBehaviour(parallelBehaviour);

		parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
			
			@Override
			public void action() {
				ACLMessage msg = receive();
				if(msg!= null) {

					switch (msg.getPerformative()) {
					case ACLMessage.CFP:
						gui.logMessage(" votre demnde recu  ",msg);
						ACLMessage msgReplay = msg.createReply();
						msgReplay.setPerformative(ACLMessage.PROPOSE);
//						msgReplay.setContent(String.valueOf(500 + new Random().nextInt(1000)));
						msgReplay.setContent(String.valueOf(prix));
						send(msgReplay);
						
						break;
					case ACLMessage.ACCEPT_PROPOSAL:
						gui.logMessage("",msg);
						ACLMessage acceptReplay = msg.createReply();
						acceptReplay.setPerformative(ACLMessage.AGREE);
						send(acceptReplay);

						break;
					
					default:
						break;
					}
					
				}
				else {block();}
				
			}
		});
		
		parallelBehaviour.addSubBehaviour(new OneShotBehaviour() {
			@Override
			public void action() {

				try {
					DFAgentDescription agentDescription = new DFAgentDescription();
					agentDescription.setName(getAID());
					ServiceDescription serviceDescription = new ServiceDescription();
					serviceDescription.setType("transaction");
					serviceDescription.setName("vente-livres");
					
					agentDescription.addServices(serviceDescription);
					DFService.register(myAgent, agentDescription);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

//	@Override
//	protected void onGuiEvent(GuiEvent arg0) {}
	@Override
	protected void takeDown() {
		try {
			DFService.deregister(this);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}
	
	

}
