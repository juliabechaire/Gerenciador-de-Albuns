import javafx.application.Application;
import view.TelaPrincipal;

public class Main {
    public static void main(String[] args) {
        // Para rodar o JavaFX a partir de uma classe externa comum,
        // chamamos o Application.launch passando a classe da tela.
        Application.launch(TelaPrincipal.class, args);
    }
}