package model;

import java.io.Serializable;

// "implements Serializable" permite salvar esta classe em arquivos binários
public abstract class Album implements Serializable {
    
    private static final long serialVersionUID = 1L;

    // Atributos privados encapsulados
    private String nomeBanda;
    private String titulo;
    private int anoLancamento;
    private String integrantes; 
    private String genero;
    private String contextoHistorico;
    private String faixaDestaque;

    // Construtor padrão da classe mãe
    public Album(String nomeBanda, String titulo, int anoLancamento, String integrantes, 
                 String genero, String contextoHistorico, String faixaDestaque) {
        this.nomeBanda = nomeBanda;
        this.titulo = titulo;
        this.anoLancamento = anoLancamento;
        this.integrantes = integrantes;
        this.genero = genero;
        this.contextoHistorico = contextoHistorico;
        this.faixaDestaque = faixaDestaque;
    }

    // Métodos abstratos lógicos (Polimorfismo obrigatório nas filhas)
    public abstract String getDestinoAcesso(); 
    public abstract String getTipoMidia();      

    // Getters e Setters concretos herdados por todas as classes filhas
    public String getNomeBanda() { return nomeBanda; }
    public void setNomeBanda(String nomeBanda) { this.nomeBanda = nomeBanda; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public int getAnoLancamento() { return anoLancamento; }
    public void setAnoLancamento(int anoLancamento) { this.anoLancamento = anoLancamento; }

    public String getIntegrantes() { return integrantes; }
    public void setIntegrantes(String integrantes) { this.integrantes = integrantes; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getContextoHistorico() { return contextoHistorico; }
    public void setContextoHistorico(String contextoHistorico) { this.contextoHistorico = contextoHistorico; }

    public String getFaixaDestaque() { return faixaDestaque; }
    public void setFaixaDestaque(String faixaDestaque) { this.faixaDestaque = faixaDestaque; }
}