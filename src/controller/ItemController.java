package controller;

import model.*;
import persistence.ArquivoRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ItemController {
    private List<Item> biblioteca;
    private final ArquivoRepository repo;

    public ItemController() {
        this.repo = new ArquivoRepository();
        try {
            this.biblioteca = repo.carregar();
        } catch (Exception e) {
            this.biblioteca = new ArrayList<>();
        }
    }

    public void adicionarItem(Item novoItem) throws IOException {
        this.biblioteca.add(novoItem);
        repo.salvar(this.biblioteca);
    }

    // Ponto 5: Filtrar por Tipo e Critério Superior
    public List<Item> obterItensFiltrados(String tipoFiltro, String abaStatus, boolean ordemAlfabetica) {
        List<Item> filtrados = new ArrayList<>();

        for (Item item : this.biblioteca) {
            boolean mapeiaTipo = false;
            if (tipoFiltro.equals("Todos")) mapeiaTipo = true;
            else if (tipoFiltro.equals("Álbuns") && item instanceof Album) mapeiaTipo = true;
            else if (tipoFiltro.equals("Filmes") && item instanceof Filme) mapeiaTipo = true;
            else if (tipoFiltro.equals("Livros") && item instanceof Livro) mapeiaTipo = true;

            boolean mapeiaAba = false;
            if (abaStatus.equals("Minha Lista") && item.isNaMinhaLista()) mapeiaAba = true;
            else if (abaStatus.equals("Vistos/Lidos") && item.isVisto()) mapeiaAba = true;

            if (mapeiaTipo && mapeiaAba) {
                filtrados.add(item);
            }
        }

        if (ordemAlfabetica) {
            filtrados.sort(Comparator.comparing(Item::getTitulo, String.CASE_INSENSITIVE_ORDER));
        }
        return filtrados;
    }

    public void atualizarAvaliacao(Item item, int novaNota, String novaResenha) throws IOException {
        item.setNota(novaNota);
        item.setResenha(novaResenha);
        repo.salvar(this.biblioteca);
    }

    public void alternarStatusVisto(Item item) throws IOException {
        item.setVisto(!item.isVisto());
        item.setNaMinhaLista(!item.isVisto()); // Se viu, sai da lista de desejos (ou configure como preferir)
        repo.salvar(this.biblioteca);
    }

    public void removerItem(Item item) throws IOException {
        this.biblioteca.remove(item);
        repo.salvar(this.biblioteca);
    }

    public List<Item> getBiblioteca() { return this.biblioteca; }
}