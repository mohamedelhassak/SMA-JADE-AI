package agents.vendeurs;

import agents.acheteur.AcheteurAgent;
import helpers.Util;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VendeurGui extends Application {

	protected VendeurAgent vendeurAgent;
	protected ObservableList<String> items ;
	protected AgentContainer agentContainer;

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		//demarrer le conteneur
		startContainer();
		
		BorderPane pane = new BorderPane();
		Label label = new Label("Nom de l'agent: ");
		Label labelprix = new Label("Prix: ");
		TextField agenntNameField = new TextField();
		TextField prixField = new TextField();
		Button buttonDeploy = new Button("Deploy Agent");

		HBox hBox = new HBox();
		hBox.getChildren().addAll(label,agenntNameField,labelprix,prixField,buttonDeploy);
		hBox.setPadding(new Insets(10));
		hBox.setSpacing(10);
		

		items = FXCollections.observableArrayList();
		ListView<String> listView = new ListView<String>(items );
		VBox vBox = new VBox();
		vBox.setPadding(new Insets(10));
		vBox.getChildren().add(listView);
		pane.setTop(hBox);
		pane.setCenter(vBox);
		Scene scene = new Scene(pane,350,400);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Vendeur");
		primaryStage.show();
		
		
		buttonDeploy.setOnAction((event)->{
			AgentController agentController;
			try {
				if(!agenntNameField.getText().equals("") || !prixField.getText().equals("")) {
					String name = agenntNameField.getText();
					double prix = Double.parseDouble(prixField.getText());
					agentController = agentContainer.createNewAgent
							(name,"agents.vendeurs.VendeurAgent",new Object[] {this,prix} );
					agentController.start();

					Util.showMessage("bien deployee",name);

				}
				else {
					Util.showMessage("veuillez remplir les champs","");
				}
			} catch (StaleProxyException e) {
				
			}
			
		});
		
		
	}
	
	public void logMessage(String str,ACLMessage msg) {
		Platform.runLater(()->{
			items.add( " from " + 
					msg.getSender().getName()+"===>'" + msg.getContent() +"' "+ str);
		});
		
		
		
	}

	private void startContainer() throws Exception {
		
		Runtime runtime = Runtime.instance();
		ProfileImpl profileImpl = new ProfileImpl();
		profileImpl.setParameter(ProfileImpl.MAIN_HOST, "localhost");
		agentContainer = runtime.createAgentContainer(profileImpl);
		
		
		
	}

}
