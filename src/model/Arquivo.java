package model;
import java.io.Serializable;

public abstract class Arquivo implements Avaliavel, Serializable {
    private String nome;
    private int anoLancamento;
    private String genero;
    private String imagem;
    private String link;
    private Integer nota = null;
    private String comentario = "";

    public Arquivo(String nome) {
        this.nome = nome;
        this.anoLancamento = 0;
        this.genero = "";
        this.imagem = "";
        this.link = "";
    }

    public abstract String exibirInformacoes();

    public String getNome() {
        return nome;
    }

    public int getAnoLancamento() {
        return anoLancamento;
    }

    public String getGenero() {
        return genero;
    }

    public String getImagem() {
        return imagem;
    }

    public String getLink() {
        return link;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setAnoLancamento(int anoLancamento) {
        this.anoLancamento = anoLancamento;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public void setLink(String link) {
        this.link = link;
    }

    // ── Implementação da interface Avaliavel ─────────────────────────────
    @Override
    public void avaliar(int nota, String comentario) {
        this.nota = nota;
        this.comentario = comentario;
    }

    @Override
    public int getNota() {
        return nota == null ? 0 : nota;
    }

    @Override
    public String getComentario() {
        return comentario;
    }
}