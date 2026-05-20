package model;

import java.io.Serializable;

public abstract class Item implements Serializable, Avaliavel, Validavel, Compartilhavel {
    private static final long serialVersionUID = 1L;

    protected String titulo;
    protected int anoLancamento;
    protected String genero;
    protected String urlImagem;
    protected String linkAcesso;
    protected int nota;
    protected String resenha;

    public Item(String titulo, int anoLancamento, String genero, String urlImagem, String linkAcesso) {
        this.titulo = titulo;
        this.anoLancamento = anoLancamento;
        this.genero = genero;
        this.urlImagem = urlImagem;
        this.linkAcesso = linkAcesso;
        this.nota = 0;
        this.resenha = "Nenhuma avaliação ainda.";
    }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public int getAnoLancamento() { return anoLancamento; }
    public void setAnoLancamento(int anoLancamento) { this.anoLancamento = anoLancamento; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getUrlImagem() { return urlImagem; }
    public void setUrlImagem(String urlImagem) { this.urlImagem = urlImagem; }

    public String getLinkAcesso() { return linkAcesso; }
    public void setLinkAcesso(String linkAcesso) { this.linkAcesso = linkAcesso; }

    public int getNota() { return nota; }
    public void setNota(int nota) { this.nota = nota; }

    public String getResenha() { return resenha; }
    public void setResenha(String resenha) { this.resenha = resenha; }

    public abstract String getTipoMidia();
    public abstract String getDetalhesSpecificos();

    @Override
    public void avaliar(int nota, String resenha) {
        this.nota = nota;
        this.resenha = resenha;
    }
}