package model;

import java.io.Serializable;

public abstract class Item implements Serializable, Validavel, Compartilhavel, Avaliavel {
    private static final long serialVersionUID = 1L;
    
    private String titulo;
    private int anoLancamento;
    private String genero;
    private String urlImagem;
    private String linkAcesso; // Atributo opcional de acesso direto
    
    // Status do ponto 7
    private boolean visto;
    private boolean naMinhaLista;
    
    // Atributos para a interface Avaliavel (Ponto 1)
    private int nota;
    private String resenha;

    public Item(String titulo, int anoLancamento, String genero, String urlImagem, String linkAcesso) {
        this.titulo = titulo;
        this.anoLancamento = (anoLancamento <= 0) ? 0 : anoLancamento;
        this.genero = (genero == null || genero.trim().isEmpty()) ? "Não Informado" : genero;
        this.urlImagem = (urlImagem == null || urlImagem.trim().isEmpty()) ? "https://i.imgur.com/bCEq7U9.png" : urlImagem;
        this.linkAcesso = (linkAcesso == null || linkAcesso.trim().isEmpty()) ? "Sem link disponível" : linkAcesso;
        
        this.visto = false;
        this.naMinhaLista = true;
        this.nota = 0;
        this.resenha = "Nenhuma resenha feita ainda.";
    }

    // Validação centralizada do Ponto 2: Apenas o título é obrigatório
    @Override
    public void validar() throws exception.DadosInvalidosException {
        if (this.titulo == null || this.titulo.trim().isEmpty()) {
            throw new exception.DadosInvalidosException("O título é obrigatório e não pode ser deixado em branco.");
        }
    }

    // Implementação padrão de Avaliavel para todas as filhas
    @Override
    public void avaliar(int nota, String resenha) {
        if (nota < 1 || nota > 5) {
            throw new exception.DadosInvalidosException("A nota deve ser de 1 a 5 estrelas.");
        }
        this.nota = nota;
        this.resenha = (resenha == null || resenha.trim().isEmpty()) ? "Sem comentários." : resenha;
    }

    @Override public int getNota() { return this.nota; }
    @Override public String getResenha() { return this.resenha; }

    // Implementação padrão de Compartilhavel para todas as filhas
    @Override
    public String gerarTextoCompartilhamento() {
        return "Confira esse item no meu Cofre Cultural: " + this.titulo + " (" + this.anoLancamento + ") - Gênero: " + this.genero;
    }

    public abstract String getTipoMidia();
    public abstract String getDetalhesEspecificos();

    // Getters e Setters
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
    public boolean isVisto() { return visto; }
    public void setVisto(boolean visto) { this.visto = visto; }
    public boolean isNaMinhaLista() { return naMinhaLista; }
    public void setNaMinhaLista(boolean naMinhaLista) { this.naMinhaLista = naMinhaLista; }
}