package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe abstrata base para todas as obras musicais do sistema.
 * Define atributos comuns e obriga subclasses a implementar
 * exibirInformacoes() e getTipo().
 */
public abstract class Musica implements Serializable, Importavel {

    private static final long serialVersionUID = 1L;

    // Atributos comuns a qualquer obra musical
    protected String nome;
    protected String artista;
    protected String genero;
    protected int    anoLancamento;
    protected String urlCapa;
    protected String urlLink;
    protected int    nota;        // 0 = não avaliado
    protected String comentario;
    protected boolean importado;  // veio da API?

    protected List<Faixa> faixas;

    public Musica(String nome, String artista) {
        this.nome     = nome;
        this.artista  = artista;
        this.faixas   = new ArrayList<>();
        this.nota     = 0;
        this.importado = false;
    }

    // ── Método abstrato: cada subclasse descreve seu tipo ──────────────
    public abstract String getTipo();

    // ── Método abstrato: exibe informações específicas de cada subtipo ──
    public abstract String exibirInformacoes();

    // ── Método concreto: calcula duração total somando todas as faixas ─
    public int getDuracaoTotalSegundos() {
        return faixas.stream().mapToInt(Faixa::getDuracaoSegundos).sum();
    }

    public String getDuracaoTotalFormatada() {
        int total = getDuracaoTotalSegundos();
        int h   = total / 3600;
        int min = (total % 3600) / 60;
        int seg = total % 60;
        if (h > 0) return String.format("%dh %02dm %02ds", h, min, seg);
        return String.format("%dm %02ds", min, seg);
    }

    // ── Avaliação ───────────────────────────────────────────────────────
    public void avaliar(int nota, String comentario) {
        this.nota       = nota;
        this.comentario = comentario;
    }

    // ── Importavel ──────────────────────────────────────────────────────
    @Override
    public boolean foiImportado() { return importado; }

    // ── Getters e Setters ───────────────────────────────────────────────
    public String getNome()           { return nome; }
    public void   setNome(String v)   { this.nome = v; }

    public String getArtista()        { return artista; }
    public void   setArtista(String v){ this.artista = v; }

    public String getGenero()         { return genero; }
    public void   setGenero(String v) { this.genero = v; }

    public int    getAnoLancamento()        { return anoLancamento; }
    public void   setAnoLancamento(int v)   { this.anoLancamento = v; }

    public String getUrlCapa()        { return urlCapa; }
    public void   setUrlCapa(String v){ this.urlCapa = v; }

    public String getUrlLink()        { return urlLink; }
    public void   setUrlLink(String v){ this.urlLink = v; }

    public int    getNota()           { return nota; }
    public String getComentario()     { return comentario; }

    public List<Faixa> getFaixas()    { return faixas; }
    public void addFaixa(Faixa f)     { faixas.add(f); }
    public void setFaixas(List<Faixa> f) { this.faixas = f; }

    public void setImportado(boolean v) { this.importado = v; }
}