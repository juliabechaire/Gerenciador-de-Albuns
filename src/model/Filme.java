package model;

public class Filme extends Arquivo {
    private String diretor;
    private int duracao;
    private String elencoPrincipal;
    private int status=0; //em porcentagem

    public Filme(String nome, int anoLancamento, String genero, String imagem, String link, String diretor, int duracao, String elencoPrincipal) {
        super(nome, anoLancamento, genero, imagem, link);
        this.diretor = diretor;
        this.duracao = duracao;
        this.elencoPrincipal = elencoPrincipal;
    }   

    public String getDiretor() {
        return diretor;
    }   
    public int getDuracao() {
        return duracao;
    }
    public String getElencoPrincipal() {
        return elencoPrincipal;
    }
    public int getStatus() {
        return status;
    }       
    public String setDiretor(String diretor) {
        return this.diretor = diretor;
    }   
    public int setDuracao(int duracao) {
        return this.duracao = duracao;
    }
    public String setElencoPrincipal(String elencoPrincipal) {
        return this.elencoPrincipal = elencoPrincipal;
    }   
    public int setStatus(int status) {
        return this.status = status;
    }   

    @Override
    public void exibirInformacoes() {
        System.out.println("Nome: " + getNome());
        System.out.println("Diretor: " + getDiretor());
        System.out.println("Ano de Lançamento: " + getAnoLancamento());
        System.out.println("Gênero: " + getGenero());
        System.out.println("Duração: " + getDuracao() + " minutos");
        System.out.println("Elenco Principal: " + getElencoPrincipal());
        System.out.println("Status: " + getStatus()+ "%");
        System.out.println("Link do Filme: " + getLink());
    }   
}
