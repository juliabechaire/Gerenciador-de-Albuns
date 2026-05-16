import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Criando um texto simples
        Label label = new Label("Olá, Júlia! O JavaFX está funcionando!");
        
        // Criando o painel que vai segurar o texto
        StackPane root = new StackPane();
        root.getChildren().add(label);
        
        // Criando a cena (janela) com tamanho 400x200
        Scene scene = new Scene(root, 400, 200);
        
        // Configurando o palco (Stage)
        primaryStage.setTitle("Teste do RockVault / POO");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}