package persistence;

import model.Artista;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ArtistaRepository {
    private static final String DIRECTORY_PATH = "dados";
    private static final String FILE_PATH = DIRECTORY_PATH + "/artistas.dat";

    public void salvar(List<Artista> artistas) throws IOException {
        File directory = new File(DIRECTORY_PATH);
        if (!directory.exists()) {
            directory.mkdir();
        }
        try (FileOutputStream fos = new FileOutputStream(FILE_PATH);
            ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(artistas);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Artista> carregar() throws IOException, ClassNotFoundException {
        File arquivo = new File(FILE_PATH);
        if (!arquivo.exists()) {
            return new ArrayList<>();
        }
        try (FileInputStream fis = new FileInputStream(arquivo);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (List<Artista>) ois.readObject();
        }
    }
}