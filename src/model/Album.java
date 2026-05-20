package model;

import exception.DadosInvalidosException;

public class Album extends Item {
    private String banda;
    private String faixaDestaque;

    public Album(String titulo, int anoLancamento, String genero, String urlImagem, String linkAcesso, String banda, String faixaDestaque) {
        super(titulo, anoLancamento, genero, urlImagem, linkAcesso);
        this.banda = banda;
        this.faixaDestaque = faixaDestaque;
    }

    public String getBanda() { return banda; }
    public void setBanda(String banda) { this.banda = banda; }

    public String getFaixaDestaque() { return faixaDestaque; }
    public void setFaixaDestaque(String faixaDestaque) { this.faixaDestaque = faixaDestaque; }

    @Override
    public String getTipoMidia() { return "Álbum Musical"; }

    @Override
    public String getDetalhesSpecificos() {
        return "Artista: " + (banda.isEmpty() ? "Desconhecido" : banda) + " | Faixa: " + (faixaDestaque.isEmpty() ? "Não informada" : faixaDestaque);
    }

    @Override
    public void validar() throws DadosInvalidosException {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new DadosInvalidosException("O título do álbum é obrigatório!");
        }
    }

    @Override
    public String gerarTextoCompartilhamento() {
        return "Confira o álbum " + titulo + " no link: " + linkAcesso;
    }
}