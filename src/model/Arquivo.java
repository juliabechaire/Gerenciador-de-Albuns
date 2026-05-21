package model;

public abstract class Arquivo implements Avaliavel {
    private String nome;
    private int AnoLancamento;
    private String genero; //nao sei e é util mesmo
    private String imagem;
    private String link;
    private String tipo; //talvez de pra remover
    public Integer nota= null;
    public String comentario="";  

    public Arquivo(String nome, int anoLancamento, String genero, String imagem, String link) {
        this.nome = nome;
        this.AnoLancamento = anoLancamento;
        this.genero = genero;
        this.imagem = imagem;
        this.link = link;
    }

    public abstract void exibirInformacoes();

    public String getNome() {
        return nome;
    }

    public int getAnoLancamento() {
        return AnoLancamento;
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

    public String getTipo() {
        return tipo;
    }

    public void setNome(String nome) {
        this.nome = nome;
    } 

    public void setAnoLancamento(int anoLancamento) {
        this.AnoLancamento = anoLancamento;
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

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void getNota(int nota) {
        this.nota = nota;
    }
    public void getComentario(String comentario) {
        this.comentario = comentario;
    }


}
