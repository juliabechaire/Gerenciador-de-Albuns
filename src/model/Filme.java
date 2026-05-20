package model;

import exception.DadosInvalidosException;

public class Filme extends Item {
    private String diretor;

    public Filme(String titulo, int anoLancamento, String genero, String urlImagem, String linkAcesso, String diretor) {
        super(titulo, anoLancamento, genero, urlImagem, linkAcesso);
        this.diretor = diretor;
    }

    public String getDiretor() { return diretor; }
    public void setDiretor(String diretor) { this.diretor = diretor; }

    @Override
    public String getTipoMidia() { return "Filme"; }

    @Override
    public String getDetalhesSpecificos() {
        return "Direção: " + (diretor == null || diretor.isEmpty() ? "Não informada" : diretor);
    }

    @Override
    public void validar() throws DadosInvalidosException {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new DadosInvalidosException("O título do filme é obrigatório!");
        }
    }

    @Override
    public String gerarTextoCompartilhamento() {
        return "Assista ao filme " + titulo + " acessando: " + linkAcesso;
    }
}