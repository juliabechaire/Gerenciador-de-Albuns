package model;

public class Album extends Arquivo{
    private String banda;
    private String melhorFaixa;
    private String linkFaixa;
    private int duracao;

    public Album(String nome, int anoLancamento, String genero, String imagem, String link, String banda, String melhorFaixa, String linkFaixa, int duracao) {
        super(nome, anoLancamento, genero, imagem, link);
        this.banda = banda;
        this.melhorFaixa = melhorFaixa;
        this.linkFaixa = linkFaixa;
        this.duracao = duracao;
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

    public String setBanda(String banda) {
        return this.banda = banda;
    }   
    public String setMelhorFaixa(String melhorFaixa) {
        return this.melhorFaixa = melhorFaixa;
    }
    public String setLinkFaixa(String linkFaixa) {
        return this.linkFaixa = linkFaixa;
    }   
    public int setDuracao(int duracao) {
        return this.duracao = duracao;  
     }          

     @Override
     public void exibirInformacoes() {
         System.out.println("Nome: " + getNome());
         System.out.println("Banda: " + getBanda());
         System.out.println("Ano de Lançamento: " + getAnoLancamento());
         System.out.println("Gênero: " + getGenero());
         System.out.println("Duração: " + getDuracao() + " minutos");
         System.out.println("Melhor Faixa: " + getMelhorFaixa());
         System.out.println("Link da Melhor Faixa: " + getLinkFaixa());
         System.out.println("Link do Álbum: " + getLink());
     }  

}
