package persistence;

import java.io.*;
import java.util.List;
import model.Album;

public class ArquivoRepository {
    // Altera o caminho para salvar diretamente dentro da sua pasta "dados" que aparece no print
    private static final String CAMINHO_ARQUIVO = "dados/biblioteca.dat";

    public void salvar(List<Album> biblioteca) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(CAMINHO_ARQUIVO);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(biblioteca);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Album> carregar() throws IOException, ClassNotFoundException {
        File arquivo = new File(CAMINHO_ARQUIVO);
        if (!arquivo.exists()) {
            return null;
        }
        try (FileInputStream fis = new FileInputStream(arquivo);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (List<Album>) ois.readObject();
        }
    }
}