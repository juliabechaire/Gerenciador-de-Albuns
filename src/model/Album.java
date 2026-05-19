package model;

public class Album extends Item {
    private static final long serialVersionUID = 1L;
    
    private String banda;
    private String faixaDestaque;

    public Album(String titulo, int anoLancamento, String genero, String urlImagem, String linkAcesso, String banda, String faixaDestaque) {
        super(titulo, anoLancamento, genero, urlImagem, linkAcesso);
        this.banda = (banda == null || banda.trim().isEmpty()) ? "Artista Desconhecido" : banda;
        this.faixaDestaque = (faixaDestaque == null || faixaDestaque.trim().isEmpty()) ? "Não Informada" : faixaDestaque;
    }

    @Override
    public String getTipoMidia() { return "Álbum"; }

    @Override
    public String getDetalhesEspecificos() { return "Banda: " + banda + " | Faixa Destaque: " + faixaDestaque; }

    public String getBanda() { return banda; }
    public void setBanda(String banda) { this.banda = banda; }
    public String getFaixaDestaque() { return faixaDestaque; }
    public void setFaixaDestaque(String faixaDestaque) { this.faixaDestaque = faixaDestaque; }
}