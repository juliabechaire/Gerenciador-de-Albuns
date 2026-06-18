package model;

import java.io.Serializable;

/**
 * Representa uma faixa individual dentro de uma obra musical.
 * Armazena título, número na tracklist e duração em segundos.
 */
public class Faixa implements Serializable {

    private static final long serialVersionUID = 1L;

    private int numero;
    private String titulo;
    private int duracaoSegundos; // duração em segundos

    public Faixa(int numero, String titulo, int duracaoSegundos) {
        this.numero       = numero;
        this.titulo       = titulo;
        this.duracaoSegundos = duracaoSegundos;
    }

    public int getNumero()          { return numero; }
    public String getTitulo()       { return titulo; }
    public int getDuracaoSegundos() { return duracaoSegundos; }

    /** Retorna a duração formatada como mm:ss */
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