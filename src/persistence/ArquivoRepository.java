package persistence;

import model.Item;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ArquivoRepository {
    private static final String DIRECTORY_PATH = "dados";
    private static final String FILE_PATH = DIRECTORY_PATH + "/biblioteca.dat";

    public void salvar(List<Item> biblioteca) throws IOException {
        File diretorio = new File(DIRECTORY_PATH);
        if (!diretorio.exists()) {
            diretorio.mkdirs();
        }

        try (FileOutputStream fos = new FileOutputStream(FILE_PATH);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(biblioteca);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Item> carregar() throws IOException, ClassNotFoundException {
        File arquivo = new File(FILE_PATH);
        if (!arquivo.exists()) {
            return new ArrayList<>(); // Retorna uma lista vazia pronta para uso
        }

        try (FileInputStream fis = new FileInputStream(arquivo);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (List<Item>) ois.readObject();
        }
    }
}