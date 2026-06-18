package model;

/**
 * Extended Play: lançamento intermediário entre single e álbum.
 * Convenção: 4 a 6 faixas.
 */
public class EP extends Musica {

    private static final long serialVersionUID = 1L;

    public EP(String nome, String artista) {
        super(nome, artista);
    }

    @Override
    public String getTipo() { return "EP"; }

    @Override
    public String exibirInformacoes() {
        StringBuilder sb = new StringBuilder();
        sb.append("🎵 Tipo: ").append(getTipo()).append("\n");
        sb.append("🎤 Artista: ").append(artista != null ? artista : "Não informado").append("\n");
        sb.append("📅 Ano: ").append(anoLancamento > 0 ? anoLancamento : "Não informado").append("\n");
        sb.append("🏷️ Gênero: ").append(genero != null ? genero : "Não informado").append("\n");
        sb.append("🎼 Faixas: ").append(faixas.size()).append("\n");
        if (!faixas.isEmpty())
            sb.append("⏱️ Duração total: ").append(getDuracaoTotalFormatada());
        return sb.toString();
    }

    @Override
    public void importarDados(String dados) { this.importado = true; }
}