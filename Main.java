import javafx.application.Application;
import javafx.stage.Stage;
import view.TelaMusica;


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