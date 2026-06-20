package model;

public class Filme extends Arquivo implements Status {
    private String diretor;
    private int duracao;
    private String elencoPrincipal;
    private int status=0; //em porcentagem

    public Filme(String nome) {
        super(nome);
        this.diretor = "Desconhecido";
        this.duracao = 0;
        this.elencoPrincipal = "";
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
    @Override
    public int getStatus() {
        return status;
    }
    public void setDiretor(String diretor) {
        this.diretor = diretor;
    }   
    public void setDuracao(int duracao) {
        this.duracao = duracao;
    }
    public void setElencoPrincipal(String elencoPrincipal) {
        this.elencoPrincipal = elencoPrincipal;
    }   
    @Override
    public void setStatus(int status) {
        this.status = status;
    }   

    @Override
    public String exibirInformacoes() {
        return "🎬 Nome do Filme: " + (getNome() == null || getNome().isEmpty() ? "Não informado" : getNome()) + "\n" +
            "🎥 Diretor: " + (getDiretor() == null || getDiretor().isEmpty() ? "Não informado" : getDiretor()) + "\n" +
            "📅 Ano de Lançamento: " + (getAnoLancamento() == 0 ? "Não informado" : getAnoLancamento()) + "\n" +
            "🏷️ Gênero: " + (getGenero() == null || getGenero().isEmpty() ? "Não informado" : getGenero()) + "\n" +
            "⏱️ Duração: " + (getDuracao() == 0 ? "Não informado" : getDuracao() + " minutos") + "\n" +
            "👥 Elenco Principal: " + (getElencoPrincipal() == null || getElencoPrincipal().isEmpty() ? "Não informado" : getElencoPrincipal()) + "\n" +
            "📊 Status de Reprodução: " + mostrarStatus() + "\n" +
            "🔗 Link do Filme: " + (getLink() == null || getLink().isEmpty() ? "Não informado" : getLink());
    }  

    @Override
    public String mostrarStatus() {
        if (getStatus() == 0) {
            return "Não assistido";
        } else if (getStatus() > 0 && getStatus() < 100) {
            return getStatus() + "% concluído";
        } else if (getStatus() == 100) {
            return "Concluído";
        } else {
            return "Status inválido";
        }
    }
}