package agents.acheteur;

import java.util.ArrayList;
import java.util.Iterator;

import com.sun.org.apache.bcel.internal.generic.SALOAD;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AcheteurAgent extends Agent {

	protected AcheteurGui gui;
	protected AID[] sellerAgents;

	@Override
	protected void setup() {
		if(getArguments().length == 1) {
			gui = (AcheteurGui) getArguments()[0];
			gui.acheteurAgent = this;
		}

		ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
		addBehaviour(parallelBehaviour);

		parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
			int counter = 0;
			ArrayList<ACLMessage> relayList = new ArrayList<ACLMessage>();
			@Override
			public void action() {

				//Filter les messages
				MessageTemplate messageTemplate=
						MessageTemplate.or(
						MessageTemplate.or(
						MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
						MessageTemplate.or(
						MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
						MessageTemplate.MatchPerformative(ACLMessage.REQUEST))),
						MessageTemplate.MatchPerformative(ACLMessage.AGREE)
						);
				ACLMessage msg = receive(messageTemplate);

				if(msg!= null) {

					switch (msg.getPerformative()) {
					case ACLMessage.REQUEST:

						String livre = msg.getContent();
						ACLMessage msgToVendors = new ACLMessage(ACLMessage.CFP);
						msgToVendors.setContent(livre);

						for(AID aid:sellerAgents) {
							msgToVendors.addReceiver(aid);
						}
						send(msgToVendors);

						break;

					case ACLMessage.PROPOSE:
						System.out.println("propose");
						++counter;
						relayList.add(msg);
						System.out.println(counter+""+sellerAgents.length);
						if(counter == sellerAgents.length) {
							ACLMessage meilleurOffre = relayList.get(0);
							double min = Double.parseDouble(meilleurOffre.getContent());
							for (ACLMessage offre : relayList) {
								double price = Double.parseDouble(offre.getContent());
								if (price < min) {
									meilleurOffre = offre;
									min = price;
								}
							}
							System.out.println(meilleurOffre.getContent());
							counter = 0;

							//send response to consumer
							ACLMessage response = new ACLMessage(ACLMessage.CONFIRM);
							response.addReceiver(new AID("Consumer",AID.ISLOCALNAME));
							response.setContent(meilleurOffre.getContent());
							send(response);
							System.out.println(response);

							//send AP to vendors
							ACLMessage offreAcept= meilleurOffre.createReply();
							offreAcept.setContent("J'accept Ce l'offre  "+meilleurOffre.getContent());
							offreAcept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
							send(offreAcept);

						}
						break;
					default:
						break;
					}

					gui.logMessage(" ",msg);

					ACLMessage replay = msg.createReply();
					replay.setPerformative(ACLMessage.INFORM);
					replay.setContent(msg.getContent());
					send(replay);

					//send to vendor
					ACLMessage aclMsg = new ACLMessage(ACLMessage.CFP);
					aclMsg.setContent(msg.getContent());
					aclMsg.addReceiver(new AID("Vendeur", AID.ISLOCALNAME));
					send(aclMsg);
				}
				else {block();}

			}
		});

		parallelBehaviour.addSubBehaviour(new TickerBehaviour(this,1000) {


			@Override
			protected void onTick() {

				try {
					// Update the list of seller agents
					DFAgentDescription dfAgentDescription =  new DFAgentDescription();
					ServiceDescription serviceDescription =  new ServiceDescription();
					serviceDescription.setType("transaction");
					serviceDescription.setName("vente-livres");
					dfAgentDescription.addServices(serviceDescription);

					DFAgentDescription[] result = DFService.search(myAgent, dfAgentDescription);
					sellerAgents = new AID[result.length];
					for (int i = 0; i < result.length; ++i) {
					sellerAgents[i] = result[i].getName();
				}
				}
				catch (Exception e) { e.printStackTrace(); }

			}
		});
	}


}
