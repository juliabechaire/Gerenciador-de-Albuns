package controller;

import model.Artista;
import exception.ArquivoNaoEncontradoException;
import exception.DadosInvalidosException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class ArtistaController {

    private List<Artista> artistas;
    private static final String ARQUIVO = "dados/artistas.dat";

    public ArtistaController() {
        this.artistas = new ArrayList<>();
        carregar();
    }

    public void adicionar(Artista a) throws DadosInvalidosException, IOException {
        if (a.getNome() == null || a.getNome().trim().isEmpty())
            throw new DadosInvalidosException("O nome do artista é obrigatório.");
        artistas.add(a);
        salvar();
    }

    public List<Artista> buscar(String termo) throws ArquivoNaoEncontradoException {
        if (termo == null || termo.trim().isEmpty()) return artistas;
        List<Artista> resultado = artistas.stream()
            .filter(a -> a.getNome().toLowerCase().contains(termo.toLowerCase()))
            .collect(Collectors.toList());
        if (resultado.isEmpty())
            throw new ArquivoNaoEncontradoException("Nenhum artista encontrado para: " + termo);
        return resultado;
    }

    public void editar(String nomeOriginal, Artista editado) throws IOException, ArquivoNaoEncontradoException {
        for (int i = 0; i < artistas.size(); i++) {
            if (artistas.get(i).getNome().equalsIgnoreCase(nomeOriginal)) {
                artistas.set(i, editado);
                salvar();
                return;
            }
        }
        throw new ArquivoNaoEncontradoException("Artista não encontrado: " + nomeOriginal);
    }

    public void remover(String nome) throws IOException, ArquivoNaoEncontradoException {
        boolean removido = artistas.removeIf(
            a -> a.getNome().equalsIgnoreCase(nome.trim()));
        if (!removido)
            throw new ArquivoNaoEncontradoException("Artista não encontrado: " + nome);
        salvar();
    }

    public List<Artista> getLista() { return artistas; }

    @SuppressWarnings("unchecked")
    private void carregar() {
        File f = new File(ARQUIVO);
        if (!f.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            artistas = (List<Artista>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Aviso: não foi possível carregar artistas.dat");
            artistas = new ArrayList<>();
        }
    }

    private void salvar() throws IOException {
        new File("dados").mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARQUIVO))) {
            oos.writeObject(artistas);
        }
    }
}