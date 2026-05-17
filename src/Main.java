
import javafx.application.Application;
import javafx.stage.Stage;
import view.TelaPrincipal;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Instancia a nossa tela principal passando o Palco para ela se desenhar
        TelaPrincipal tela = new TelaPrincipal();
        tela.iniciarTela(primaryStage);
    }

    public static void main(String[] args) {
        // Método nativo do JavaFX que dispara o ciclo de vida da interface gráfica
        launch(args);
    }
}