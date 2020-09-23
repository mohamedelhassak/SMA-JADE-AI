package agents.acheteur;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AcheteurGui extends Application {

    protected AcheteurAgent acheteurAgent;
    protected ObservableList<String> items;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        startContainer();

        BorderPane pane = new BorderPane();
        VBox vBox = new VBox();
        items = FXCollections.observableArrayList();
        ListView<String> listView = new ListView<String>(items);
        vBox.setPadding(new Insets(10));
        vBox.getChildren().add(listView);
        pane.setCenter(vBox);
        Scene scene = new Scene(pane, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("acheteur");
        primaryStage.show();


    }


    private void startContainer() throws Exception {

        Runtime runtime = Runtime.instance();
        ProfileImpl profileImpl = new ProfileImpl();
        profileImpl.setParameter(ProfileImpl.MAIN_HOST, "localhost");
        AgentContainer agentContainer = runtime.createAgentContainer(profileImpl);
        AgentController agentController = agentContainer.createNewAgent
                ("Acheteur", "agents.acheteur.AcheteurAgent", new Object[]{this});

        agentController.start();
    }

    public void logMessage(String str, ACLMessage msg) {
        Platform.runLater(() -> {
            items.add(" from " +
                    msg.getSender().getName() + "===>'" + msg.getContent() + "' " + str);
        });

    }

}
