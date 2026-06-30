package model;

public class Single extends Musica {

    private static final long serialVersionUID = 1L;
    private String eraDoAlbum;

    public Single(String nome, String artista) {
        super(nome, artista);
    }

    @Override
    public String getTipo() { return "Single"; }

    @Override
    public String exibirInformacoes() {
        StringBuilder sb = new StringBuilder();
        sb.append("🎵 Tipo: ").append(getTipo()).append("\n");
        sb.append("🎤 Artista: ").append(artista != null ? artista : "Não informado").append("\n");
        sb.append("📅 Ano: ").append(anoLancamento > 0 ? anoLancamento : "Não informado").append("\n");
        sb.append("🏷️ Gênero: ").append(genero != null ? genero : "Não informado").append("\n");
        if (eraDoAlbum != null && !eraDoAlbum.isEmpty())
            sb.append("💿 Era: ").append(eraDoAlbum).append("\n");
        sb.append("🎼 Faixas: ").append(faixas.size()).append("\n");
        if (!faixas.isEmpty())
            sb.append("⏱️ Duração: ").append(getDuracaoTotalFormatada());
        return sb.toString();
    }

    @Override
    public void importarDados() { this.importado = true; }

    public String getEraDoAlbum()        { return eraDoAlbum; }
    public void   setEraDoAlbum(String v){ this.eraDoAlbum = v; }
}