package model;

public class Filme extends Item {
    private static final long serialVersionUID = 1L;

    private String diretor;
    private int duracaoMinutos;

    public Filme(String titulo, int anoLancamento, String genero, String urlImagem, String linkAcesso, String diretor, int duracaoMinutos) {
        super(titulo, anoLancamento, genero, urlImagem, linkAcesso);
        this.diretor = (diretor == null || diretor.trim().isEmpty()) ? "Diretor Não Informado" : diretor;
        this.duracaoMinutos = (duracaoMinutos <= 0) ? 0 : duracaoMinutos;
    }

    @Override
    public String getTipoMidia() { return "Filme"; }

    @Override
    public String getDetalhesEspecificos() { return "Diretor: " + diretor + " | Duração: " + duracaoMinutos + " min"; }

    public String getDiretor() { return diretor; }
    public void setDiretor(String diretor) { this.diretor = diretor; }
    public int getDuracaoMinutos() { return duracaoMinutos; }
    public void setDuracaoMinutos(int duracaoMinutos) { this.duracaoMinutos = duracaoMinutos; }
}