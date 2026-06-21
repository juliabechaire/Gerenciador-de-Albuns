package model;

import java.io.Serializable;

public class Faixa implements Serializable {

    private static final long serialVersionUID = 1L;

    private int numero;
    private String titulo;
    private int duracaoSegundos; 

    public Faixa(int numero, String titulo, int duracaoSegundos) {
        this.numero       = numero;
        this.titulo       = titulo;
        this.duracaoSegundos = duracaoSegundos;
    }

    public int getNumero()          { return numero; }
    public String getTitulo()       { return titulo; }
    public int getDuracaoSegundos() { return duracaoSegundos; }

    public String getDuracaoFormatada() {
        int min = duracaoSegundos / 60;
        int seg = duracaoSegundos % 60;
        return String.format("%d:%02d", min, seg);
    }

    @Override
    public String toString() {
        return numero + ". " + titulo + " (" + getDuracaoFormatada() + ")";
    }
}