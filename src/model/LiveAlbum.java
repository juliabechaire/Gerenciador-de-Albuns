package model;


public class LiveAlbum extends Musica {

    private static final long serialVersionUID = 1L;
    private String localShow;
    private String cidadeShow;

    public LiveAlbum(String nome, String artista) {
        super(nome, artista);
    }

    @Override
    public String getTipo() { return "Álbum ao Vivo"; }

    @Override
    public String exibirInformacoes() {
        StringBuilder sb = new StringBuilder();
        sb.append("🎵 Tipo: ").append(getTipo()).append("\n");
        sb.append("🎤 Artista: ").append(artista != null ? artista : "Não informado").append("\n");
        sb.append("📅 Ano: ").append(anoLancamento > 0 ? anoLancamento : "Não informado").append("\n");
        sb.append("🏷️ Gênero: ").append(genero != null ? genero : "Não informado").append("\n");
        if (localShow != null && !localShow.isEmpty())
            sb.append("📍 Local: ").append(localShow).append("\n");
        if (cidadeShow != null && !cidadeShow.isEmpty())
            sb.append("🌎 Cidade: ").append(cidadeShow).append("\n");
        sb.append("🎼 Faixas: ").append(faixas.size()).append("\n");
        if (!faixas.isEmpty())
            sb.append("⏱️ Duração total: ").append(getDuracaoTotalFormatada());
        return sb.toString();
    }

    @Override
    public void importarDados() { this.importado = true; }

    public String getLocalShow()         { return localShow; }
    public void   setLocalShow(String v) { this.localShow = v; }

    public String getCidadeShow()         { return cidadeShow; }
    public void   setCidadeShow(String v) { this.cidadeShow = v; }
}