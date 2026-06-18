package controller;

import model.Musica;
import exception.ArquivoNaoEncontradoException;
import exception.DadosInvalidosException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller/Service da camada musical.
 * Gerencia a coleção polimórfica de Musica em memória e no disco.
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
                      || m.getArtista().toLowerCase().contains(termo.toLowerCase()))
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

    public List<Musica> getBiblioteca() { return biblioteca; }

    // ── Estatísticas (usadas no Dashboard) ──────────────────────────────

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
            .max(java.util.Map.Entry.comparingByValue())
            .map(java.util.Map.Entry::getKey)
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

    // ── Persistência ────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private void carregar() {
        File f = new File(ARQUIVO);
        if (!f.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            biblioteca = (List<Musica>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Aviso: não foi possível carregar musicas.dat — biblioteca vazia.");
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