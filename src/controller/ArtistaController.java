package controller;

import exception.ArquivoNaoEncontradoException;
import exception.DadosInvalidosException;
import model.Artista;
import persistence.ArtistaRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ArtistaController {

    private List<Artista> artistas;
    private final ArtistaRepository repository;

    public ArtistaController() {
        repository = new ArtistaRepository();
        try {
            artistas = repository.carregar();
        } catch (Exception e) {
            artistas = new ArrayList<>();
        }
    }

    public void adicionar(Artista artista)
            throws DadosInvalidosException, IOException {
        if (artista.getNome() == null || artista.getNome().trim().isEmpty()) {
            throw new DadosInvalidosException(
                    "O nome do artista é obrigatório.");
        }
        artistas.add(artista);
        repository.salvar(artistas);
    }

    public List<Artista> buscar(String termo)
            throws ArquivoNaoEncontradoException {
        if (termo == null || termo.trim().isEmpty()) {
            return artistas;
        }
        List<Artista> resultado = artistas.stream()
                .filter(a -> a.getNome()
                        .toLowerCase()
                        .contains(termo.toLowerCase()))
                .collect(Collectors.toList());
        if (resultado.isEmpty()) {
            throw new ArquivoNaoEncontradoException(
                    "Nenhum artista encontrado para: " + termo);
        }
        return resultado;
    }

    public void editar(String nomeOriginal, Artista editado)
            throws IOException, ArquivoNaoEncontradoException {
        for (int i = 0; i < artistas.size(); i++) {
            if (artistas.get(i).getNome()
                    .equalsIgnoreCase(nomeOriginal)) {

                artistas.set(i, editado);
                repository.salvar(artistas);
                return;
            }
        }
        throw new ArquivoNaoEncontradoException(
                "Artista não encontrado: " + nomeOriginal);
    }

    public void remover(String nome)
            throws IOException, ArquivoNaoEncontradoException {
        boolean removido = artistas.removeIf(
                a -> a.getNome().equalsIgnoreCase(nome.trim()));
        if (!removido) {
            throw new ArquivoNaoEncontradoException(
                    "Artista não encontrado: " + nome);
        }
        repository.salvar(artistas);
    }
    public List<Artista> getLista() {
        return artistas;
    }
}