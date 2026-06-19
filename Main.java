import javafx.application.Application;
import javafx.stage.Stage;
import view.TelaMusica;

/**
 * Ponto de entrada do aplicativo.
 * Abre o Módulo Musical como tela principal.
 * A Biblioteca (TelaPrincipal) é acessada via sidebar.
 */
public class Main extends Application {

    @Override
    public void start(Stage palco) {
        palco.setTitle("Cofre Cultural");
        new TelaMusica(palco, null).mostrar();
    }

    public static void main(String[] args) {
        launch(args);
    }
}