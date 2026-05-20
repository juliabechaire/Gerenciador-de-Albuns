
import javafx.application.Application;
import javafx.stage.Stage;
import view.TelaPrincipal;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        TelaPrincipal tela = new TelaPrincipal();
        tela.iniciarTela(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}