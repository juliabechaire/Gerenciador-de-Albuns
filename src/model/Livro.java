package model;

import exception.DadosInvalidosException;

public class Livro extends Item {
    private String autor;

    public Livro(String titulo, int anoLancamento, String genero, String urlImagem, String linkAcesso, String autor) {
        super(titulo, anoLancamento, genero, urlImagem, linkAcesso);
        this.autor = autor;
    }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    @Override
    public String getTipoMidia() { return "Livro"; }

    @Override
    public String getDetalhesSpecificos() {
        return "Autor: " + (autor.isEmpty() ? "Não informado" : autor);
    }

    @Override
    public void validar() throws DadosInvalidosException {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new DadosInvalidosException("O título do livro é obrigatório!");
        }
    }

    @Override
    public String gerarTextoCompartilhamento() {
        return "Leia a obra " + titulo + " digitalmente por aqui: " + linkAcesso;
    }
}