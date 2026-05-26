package model;

public class Album extends Arquivo{
    private String banda;
    private String melhorFaixa;
    private String linkFaixa;
    private int duracao;

    public Album(String nome) {
        super(nome);
        this.banda = "Desconhecido";
        this.melhorFaixa = "Não informado";
        this.linkFaixa = "";
        this.duracao = 0;
    }

    public String getBanda() {
        return banda;
    }       
    public String getMelhorFaixa() {
        return melhorFaixa;
    }
    public String getLinkFaixa() {
        return linkFaixa;
    }   
    public int getDuracao() {
        return duracao;  
    }

    public void setBanda(String banda) {
        this.banda = banda;
    }   
    public void setMelhorFaixa(String melhorFaixa) {
        this.melhorFaixa = melhorFaixa;
    }
    public void setLinkFaixa(String linkFaixa) {
        this.linkFaixa = linkFaixa;
    }   
    public void setDuracao(int duracao) {
        this.duracao = duracao;  
     }          

     @Override
    public String exibirInformacoes() {
        return "💿 Nome do Álbum: " + (getNome() == null || getNome().isEmpty() ? "Não informado" : getNome()) + "\n" +
            "🎵 Artista/Banda: " + (getBanda() == null || getBanda().isEmpty() ? "Não informado" : getBanda()) + "\n" +
            "📅 Ano de Lançamento: " + (getAnoLancamento() == 0 ? "Não informado" : getAnoLancamento()) + "\n" +
            "🏷️ Gênero: " + (getGenero() == null || getGenero().isEmpty() ? "Não informado" : getGenero()) + "\n" +
            "⏱️ Duração: " + (getDuracao() == 0 ? "Não informado" : getDuracao() + " minutos") + "\n" +
            "⭐ Melhor Faixa: " + (getMelhorFaixa() == null || getMelhorFaixa().isEmpty() ? "Não informado" : getMelhorFaixa()) + "\n" +
            "🔗 Link da Faixa: " + (getLinkFaixa() == null || getLinkFaixa().isEmpty() ? "Não informado" : getLinkFaixa()) + "\n" +
            "🌍 Link do Álbum: " + (getLink() == null || getLink().isEmpty() ? "Não informado" : getLink());
    } 

}
