package controller;

import exception.ArquivoNaoEncontradoException;
import exception.DadosInvalidosException;
import model.Musica;
import persistence.MusicaRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MusicaController {

    private List<Musica> biblioteca;
    private final MusicaRepository repository;

    public MusicaController() {
        repository = new MusicaRepository();
        try {
            biblioteca = repository.carregar();
        } catch (Exception e) {
            biblioteca = new ArrayList<>();
        }
    }

    public void adicionar(Musica musica)
            throws DadosInvalidosException, IOException {
        if (musica.getNome() == null ||
                musica.getNome().trim().isEmpty()) {

            throw new DadosInvalidosException(
                    "O nome da obra é obrigatório.");
        }
        biblioteca.add(musica);
        repository.salvar(biblioteca);
    }

    public List<Musica> buscar(String termo)
            throws ArquivoNaoEncontradoException {
        if (termo == null || termo.trim().isEmpty()) {
            return biblioteca;
        }

        List<Musica> resultado = biblioteca.stream()
                .filter(m ->
                        m.getNome().toLowerCase().contains(termo.toLowerCase())
                                || (m.getArtista() != null &&
                                m.getArtista().toLowerCase().contains(termo.toLowerCase())))
                .collect(Collectors.toList());
        if (resultado.isEmpty()) {
            throw new ArquivoNaoEncontradoException(
                    "Nenhum resultado para: " + termo);
        }
        return resultado;
    }

    public void editar(String nomeOriginal, Musica editado)
            throws IOException, ArquivoNaoEncontradoException {
        for (int i = 0; i < biblioteca.size(); i++) {
            if (biblioteca.get(i).getNome()
                    .equalsIgnoreCase(nomeOriginal)) {
                biblioteca.set(i, editado);
                repository.salvar(biblioteca);
                return;
            }
        }
        throw new ArquivoNaoEncontradoException(
                "Item não encontrado: " + nomeOriginal);
    }

    public void remover(String nome)
            throws IOException, ArquivoNaoEncontradoException {
        boolean removido = biblioteca.removeIf(
                m -> m.getNome().equalsIgnoreCase(nome.trim()));
        if (!removido) {
            throw new ArquivoNaoEncontradoException(
                    "Item não encontrado: " + nome);
        }
        repository.salvar(biblioteca);
    }

    public void registrarEscuta(Musica musica)
            throws IOException {
        musica.registrarEscuta();
        repository.salvar(biblioteca);
    }

    public List<Musica> getHistoricoRecente(int quantidade) {
        return biblioteca.stream()
                .filter(m -> m.getUltimaEscuta() != null)
                .sorted(
                        Comparator.comparing(Musica::getUltimaEscuta)
                                .reversed()
                )
                .limit(quantidade)
                .collect(Collectors.toList());
    }

    public void limparHistorico()
            throws IOException {
        for (Musica musica : biblioteca) {
            musica.limparHistoricoEscuta();
        }
        repository.salvar(biblioteca);
    }

    public List<Musica> gerarPlaylist(
            String genero,
            int duracaoMaxMin)
            throws ArquivoNaoEncontradoException {
        List<Musica> candidatos = biblioteca.stream()
                .filter(m -> {
                    if (genero == null ||
                            genero.trim().isEmpty()) {
                        return true;
                    }
                    return m.getGenero() != null
                            && m.getGenero()
                            .toLowerCase()
                            .contains(genero.toLowerCase());
                })
                .collect(Collectors.toList());
        if (candidatos.isEmpty()) {
            throw new ArquivoNaoEncontradoException(
                    "Nenhuma obra encontrada"
                            + (genero != null && !genero.isEmpty()
                            ? " com gênero: " + genero
                            : "")
                            + ".");
        }

        candidatos.sort(
                Comparator.comparingInt(Musica::getNota)
                        .reversed()
                        .thenComparingInt(Musica::getTotalEscutas)
                        .reversed()
        );

        if (duracaoMaxMin > 0) {
            int limiteSegundos = duracaoMaxMin * 60;
            List<Musica> playlist = new ArrayList<>();
            int acumulado = 0;
            for (Musica musica : candidatos) {
                int duracao =
                        musica.getDuracaoTotalSegundos();
                if (duracao == 0 ||
                        acumulado + duracao <= limiteSegundos) {
                    playlist.add(musica);
                    acumulado += duracao;
                }
            }

            if (playlist.isEmpty()) {
                throw new ArquivoNaoEncontradoException(
                        "Nenhuma obra cabe em "
                                + duracaoMaxMin
                                + " minutos com os critérios escolhidos.");
            }
            return playlist;
        }
        return candidatos;
    }

    public int getTotalSegundos() {
        return biblioteca.stream()
                .mapToInt(Musica::getDuracaoTotalSegundos)
                .sum();
    }

    public String getDuracaoTotalFormatada() {
        int total = getTotalSegundos();
        int horas = total / 3600;
        int minutos = (total % 3600) / 60;
        return horas > 0
                ? horas + "h " + minutos + "m"
                : minutos + " minutos";
    }

    public String getArtistaTop() {
        return biblioteca.stream()
                .collect(
                        Collectors.groupingBy(
                                m -> m.getArtista() != null
                                        ? m.getArtista()
                                        : "Desconhecido",
                                Collectors.counting()
                        )
                )
                .entrySet()
                .stream()
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
        return biblioteca.stream()
                .mapToLong(m -> m.getFaixas().size())
                .sum();
    }

    public List<Musica> getBiblioteca() {
        return biblioteca;
    }
}