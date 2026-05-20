package controller;

import model.*;
import persistence.ArquivoRepository;
import exception.DadosInvalidosException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ItemController {
    private List<Item> biblioteca;
    private final ArquivoRepository repo;

    public ItemController() {
        this.repo = new ArquivoRepository();
        try {
            this.biblioteca = repo.carregar();
            if (this.biblioteca == null || this.biblioteca.isEmpty()) {
                this.biblioteca = new ArrayList<>();
                carregarDadosDemonstracao();
            }
        } catch (Exception e) {
            this.biblioteca = new ArrayList<>();
            carregarDadosDemonstracao();
        }
    }

    private void carregarDadosDemonstracao() {
        this.biblioteca.add(new Filme(
            "Sintel (Animação Open Source)", 2010, "Fantasia",
            "https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=500",
            "https://www.youtube.com/watch?v=eRsGyueVLvQ",
            "Colin Levy"
        ));

        this.biblioteca.add(new Album(
            "Lo-Fi Beats Session", 2024, "Chill/Relax",
            "https://images.unsplash.com/photo-1614613535308-eb5fbd3d2c17?w=500",
            "https://www.youtube.com/watch?v=jfKfPfyJRdk",
            "Lofi Girl Project", "Soft Ambient"
        ));

        this.biblioteca.add(new Livro(
            "Documento de Teste PDF", 2022, "Educação",
            "https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=500",
            "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf",
            "W3C Benchmark"
        ));
        try { repo.salvar(this.biblioteca); } catch (Exception ignored) {}
    }

    public void adicionarItem(Item novoItem) throws DadosInvalidosException, IOException {
        if (novoItem instanceof Validavel) {
            ((Validavel) novoItem).validar();
        }
        this.biblioteca.add(novoItem);
        repo.salvar(this.biblioteca);
    }

    public List<Item> buscarPorPalavraChave(String termo) {
        if (termo == null || termo.trim().isEmpty()) {
            return this.biblioteca;
        }
        List<Item> filtrados = new ArrayList<>();
        for (Item i : biblioteca) {
            if (i.getTitulo().toLowerCase().contains(termo.toLowerCase()) || 
                i.getGenero().toLowerCase().contains(termo.toLowerCase())) {
                filtrados.add(i);
            }
        }
        return filtrados;
    }

    public void removerItem(String titulo) throws IOException {
        biblioteca.removeIf(i -> i.getTitulo().equalsIgnoreCase(titulo.trim()));
        atualizarBiblioteca();
    }

    public void atualizarBiblioteca() throws IOException {
        repo.salvar(this.biblioteca);
    }

    public List<Item> getBiblioteca() {
        return this.biblioteca;
    }
}