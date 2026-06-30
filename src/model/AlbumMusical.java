package model;


public class AlbumMusical extends Musica {

    private static final long serialVersionUID = 1L;
    private int numeroDiscos;

    public AlbumMusical(String nome, String artista) {
        super(nome, artista);
        this.numeroDiscos = 1;
    }

    @Override
    public String getTipo() { return "Álbum de Estúdio"; }

    @Override
    public String exibirInformacoes() {
        StringBuilder sb = new StringBuilder();
        sb.append("🎵 Tipo: ").append(getTipo()).append("\n");
        sb.append("🎤 Artista: ").append(artista != null ? artista : "Não informado").append("\n");
        sb.append("📅 Ano: ").append(anoLancamento > 0 ? anoLancamento : "Não informado").append("\n");
        sb.append("🏷️ Gênero: ").append(genero != null ? genero : "Não informado").append("\n");
        sb.append("💿 Discos: ").append(numeroDiscos).append("\n");
        sb.append("🎼 Faixas: ").append(faixas.size()).append("\n");
        if (!faixas.isEmpty())
            sb.append("⏱️ Duração total: ").append(getDuracaoTotalFormatada());
        return sb.toString();
    }

    @Override
    public void importarDados() {
        this.importado = true;
    }

    public int getNumeroDiscos()         { return numeroDiscos; }
    public void setNumeroDiscos(int v)   { this.numeroDiscos = v; }
}