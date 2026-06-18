package controller;

import model.Musica;
import exception.ArquivoNaoEncontradoException;
import exception.DadosInvalidosException;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller da camada musical.
 * Gerencia a coleção polimórfica de Musica em memória e no disco.
 * Inclui geração de playlist por critério e histórico de escuta.
 */
public class MusicaController {

    private List<Musica> biblioteca;
    private static final String ARQUIVO = "dados/musicas.dat";

    public MusicaController() {
        this.biblioteca = new ArrayList<>();
        carregar();
    }

    // ── CRUD ────────────────────────────────────────────────────────────

    public void adicionar(Musica m) throws DadosInvalidosException, IOException {
        if (m.getNome() == null || m.getNome().trim().isEmpty())
            throw new DadosInvalidosException("O nome da obra é obrigatório.");
        biblioteca.add(m);
        salvar();
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
                salvar();
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
        salvar();
    }

    // ── Registrar escuta e salvar ────────────────────────────────────────
    public void registrarEscuta(Musica m) throws IOException {
        m.registrarEscuta();
        salvar();
    }

    // ── Histórico: retorna os N mais ouvidos recentemente ───────────────
    public List<Musica> getHistoricoRecente(int quantidade) {
        return biblioteca.stream()
            .filter(m -> m.getUltimaEscuta() != null)
            .sorted(Comparator.comparing(Musica::getUltimaEscuta).reversed())
            .limit(quantidade)
            .collect(Collectors.toList());
    }

    // ── Gerador de Playlist por critério ────────────────────────────────
    /**
     * Gera uma playlist filtrada por gênero e/ou duração máxima total.
     * Ordena por nota (melhor avaliados primeiro), depois por mais ouvidos.
     *
     * @param genero        filtro de gênero (null ou vazio = ignora)
     * @param duracaoMaxMin duração máxima da playlist em minutos (0 = sem limite)
     * @return lista ordenada de músicas que cabem nos critérios
     * @throws ArquivoNaoEncontradoException se nenhum item atender os critérios
     */
    public List<Musica> gerarPlaylist(String genero, int duracaoMaxMin)
            throws ArquivoNaoEncontradoException {

        // 1. Filtra por gênero se informado
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

        // 2. Ordena: melhor nota primeiro, depois mais ouvido
        candidatos.sort(Comparator
            .comparingInt(Musica::getNota).reversed()
            .thenComparingInt(Musica::getTotalEscutas).reversed());

        // 3. Aplica limite de duração se informado
        if (duracaoMaxMin > 0) {
            int limiteSegundos = duracaoMaxMin * 60;
            List<Musica> playlist = new ArrayList<>();
            int acumulado = 0;
            for (Musica m : candidatos) {
                int dur = m.getDuracaoTotalSegundos();
                // inclui mesmo sem duração cadastrada (dur == 0)
                if (dur == 0 || acumulado + dur <= limiteSegundos) {
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

    // ── Estatísticas ────────────────────────────────────────────────────
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

    // ── Persistência ────────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    private void carregar() {
        File f = new File(ARQUIVO);
        if (!f.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            biblioteca = (List<Musica>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Aviso: não foi possível carregar musicas.dat");
            biblioteca = new ArrayList<>();
        }
    }

    private void salvar() throws IOException {
        new File("dados").mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARQUIVO))) {
            oos.writeObject(biblioteca);
        }
    }
}