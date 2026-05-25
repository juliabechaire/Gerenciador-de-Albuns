package persistence;

import model.Arquivo;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ArquivoRepository {
    private static final String DIRECTORY_PATH = "dados";
    private static final String FILE_PATH = DIRECTORY_PATH + "/biblioteca.dat";

    public void salvar(List<Arquivo> biblioteca) throws IOException {
        
        File directory = new File(DIRECTORY_PATH);
        if (!directory.exists()) {
            directory.mkdir(); 
        }
        
        try (FileOutputStream fos = new FileOutputStream(FILE_PATH);
            ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(biblioteca);
            } 
    }
    
     @SuppressWarnings("unchecked")
    public List<Arquivo> carregar() throws IOException, ClassNotFoundException {
        File arquivo = new File(FILE_PATH);
        if (!arquivo.exists()) {
            return new ArrayList<>(); 
        }

        try (FileInputStream fis = new FileInputStream(arquivo);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (List<Arquivo>) ois.readObject();
        }
    }
}
