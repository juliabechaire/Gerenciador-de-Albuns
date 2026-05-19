package model;

public class Livro extends Item {
    private static final long serialVersionUID = 1L;

    private String autor;
    private int numeroPaginas;

    public Livro(String titulo, int anoLancamento, String genero, String urlImagem, String linkAcesso, String autor, int numeroPaginas) {
        super(titulo, anoLancamento, genero, urlImagem, linkAcesso);
        this.autor = (autor == null || autor.trim().isEmpty()) ? "Autor Não Informado" : autor;
        this.numeroPaginas = (numeroPaginas <= 0) ? 0 : numeroPaginas;
    }

    @Override
    public String getTipoMidia() { return "Livro"; }

    @Override
    public String getDetalhesEspecificos() { return "Autor: " + autor + " | Páginas: " + numeroPaginas; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    public int getNumeroPaginas() { return numeroPaginas; }
    public void setNumeroPaginas(int numeroPaginas) { this.numeroPaginas = numeroPaginas; }
}