package model;

import java.io.Serializable;


public class Artista implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nome;
    private String urlFoto;
    private String genero;
    private String descricao;   
    private String pais;

    public Artista(String nome) {
        this.nome = nome;
    }

    public String getNome()              { return nome; }
    public void   setNome(String v)      { this.nome = v; }

    public String getUrlFoto()           { return urlFoto; }
    public void   setUrlFoto(String v)   { this.urlFoto = v; }

    public String getGenero()            { return genero; }
    public void   setGenero(String v)    { this.genero = v; }

    public String getDescricao()         { return descricao; }
    public void   setDescricao(String v) { this.descricao = v; }

    public String getPais()              { return pais; }
    public void   setPais(String v)      { this.pais = v; }

    @Override
    public String toString() { return nome; }
}