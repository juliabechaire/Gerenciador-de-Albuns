package model;

public class Livro extends Arquivo {
    private String autor;
    private int numeroPaginas;
    private int status=0; //em porcentagem

    public Livro(String nome, int anoLancamento, String genero, String imagem, String link, String autor, int numeroPaginas) {
        super(nome, anoLancamento, genero, imagem, link);
        this.autor = autor;
        this.numeroPaginas = numeroPaginas;
    }

    public String getAutor() {
        return autor;
    }

    public int getNumeroPaginas() {
        return numeroPaginas;
    }

    public int getStatus() {
        return status;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public void setNumeroPaginas(int numeroPaginas) {
        this.numeroPaginas = numeroPaginas;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public void exibirInformacoes() {
        System.out.println("Nome: " + getNome());
        System.out.println("Autor: " + getAutor());
        System.out.println("Ano de Lançamento: " + getAnoLancamento());
        System.out.println("Gênero: " + getGenero());
        System.out.println("Número de Páginas: " + getNumeroPaginas());
        System.out.println("Status: " + getStatus()+ "%");
        System.out.println("Link do Livro: " + getLink());
    }   
    
}
