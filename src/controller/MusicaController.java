package controller;

import model.Musica;
import persistence.MusicaRepository;
import exception.ArquivoNaoEncontradoException;
import exception.DadosInvalidosException;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class MusicaController {

    private List<Musica> biblioteca;
    private MusicaRepository repo;

    public MusicaController() {
        this.repo = new MusicaRepository();
        try {
            this.biblioteca = this.repo.carregar();
        } catch (IOException | ClassNotFoundException e) {
            this.biblioteca = new ArrayList<>();
        }
    }

    public void adicionar(Musica m) throws DadosInvalidosException, IOException {
        if (m.getNome() == null || m.getNome().trim().isEmpty())
            throw new DadosInvalidosException("O nome da obra é obrigatório.");
        biblioteca.add(m);
        repo.salvar(biblioteca);
    }

    public List<Musica> buscar(String termo) throws ArquivoNaoEncontradoException {
        if (termo == null || termo.trim().isEmpty()) return biblioteca;
        List<Musica> resultado = biblioteca.stream()
            .filter(m -> m.getNome().toLowerCase().contains(termo.toLowerCase())
                      || (m.getArtista() != null &&
                          m.getArtista().toLowerCase().contains(termo.toLowerCase())))
            .collect(Collectors.toList());
        if (resultado.isEmpty())
            throw new ArquivoNaoEncontradoException("Nenhum resultado para: " + termo);
        return resultado;
    }

    public void editar(String nomeOriginal, Musica editado) throws IOException, ArquivoNaoEncontradoException {
        for (int i = 0; i < biblioteca.size(); i++) {
            if (biblioteca.get(i).getNome().equalsIgnoreCase(nomeOriginal)) {
                biblioteca.set(i, editado);
                repo.salvar(biblioteca);
                return;
            }
        }
        throw new ArquivoNaoEncontradoException("Item não encontrado: " + nomeOriginal);
    }

    public void remover(String nome) throws IOException, ArquivoNaoEncontradoException {
        boolean removido = biblioteca.removeIf(
            m -> m.getNome().equalsIgnoreCase(nome.trim()));
        if (!removido)
            throw new ArquivoNaoEncontradoException("Item não encontrado: " + nome);
        repo.salvar(biblioteca);
    }

    public void registrarEscuta(Musica m) throws IOException {
        m.registrarEscuta();
        repo.salvar(biblioteca);
    }

    public List<Musica> getHistoricoRecente(int quantidade) {
        return biblioteca.stream()
            .filter(m -> m.getUltimaEscuta() != null)
            .sorted(Comparator.comparing(Musica::getUltimaEscuta).reversed())
            .limit(quantidade)
            .collect(Collectors.toList());
    }

    public void limparHistorico() throws IOException {
        for (Musica m : biblioteca) {
            m.limparHistoricoEscuta();
        }
        repo.salvar(biblioteca);
    }

    public List<Musica> gerarPlaylist(String genero, int duracaoMaxMin)
            throws ArquivoNaoEncontradoException {

        List<Musica> candidatos = biblioteca.stream()
            .filter(m -> {
                if (genero == null || genero.trim().isEmpty()) return true;
                return m.getGenero() != null &&
                       m.getGenero().toLowerCase().contains(genero.toLowerCase());
            })
            .collect(Collectors.toList());

        if (candidatos.isEmpty())
            throw new ArquivoNaoEncontradoException(
                "Nenhuma obra encontrada" + (genero != null && !genero.isEmpty() ? " com gênero: " + genero : "") + ".");

        candidatos.sort(Comparator
            .comparingInt(Musica::getNota).reversed()
            .thenComparingInt(Musica::getTotalEscutas).reversed());

        if (duracaoMaxMin > 0) {
            int limiteSegundos = duracaoMaxMin * 60;
            List<Musica> playlist = new ArrayList<>();
            int acumulado = 0;
            for (Musica m : candidatos) {
                int dur = m.getDuracaoTotalSegundos();
                if (acumulado + dur <= limiteSegundos) {
                    playlist.add(m);
                    acumulado += dur;
                }
            }
            if (playlist.isEmpty())
                throw new ArquivoNaoEncontradoException(
                    "Nenhuma obra cabe em " + duracaoMaxMin + " minutos com os critérios escolhidos.");
            return playlist;
        }

        return candidatos;
    }

    public int getTotalSegundos() {
        return biblioteca.stream().mapToInt(Musica::getDuracaoTotalSegundos).sum();
    }

    public String getDuracaoTotalFormatada() {
        int total = getTotalSegundos();
        int h   = total / 3600;
        int min = (total % 3600) / 60;
        return h > 0 ? h + "h " + min + "m" : min + " minutos";
    }

    public String getArtistaTop() {
        return biblioteca.stream()
            .collect(Collectors.groupingBy(
                m -> m.getArtista() != null ? m.getArtista() : "Desconhecido",
                Collectors.counting()))
            .entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("—");
    }

    public double getMediaNotas() {
        return biblioteca.stream()
            .filter(m -> m.getNota() > 0)
            .mapToInt(Musica::getNota)
            .average()
            .orElse(0.0);
    }

    public long getTotalFaixas() {
        return biblioteca.stream().mapToLong(m -> m.getFaixas().size()).sum();
    }

    public List<Musica> getBiblioteca() { return biblioteca; }
}