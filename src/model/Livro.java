package model;

public class Livro extends Arquivo implements Status {
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
    public String exibirInformacoes() {
        return "📚 Nome do Livro: " + (getNome() == null || getNome().isEmpty() ? "Não informado" : getNome()) + "\n" +
                "✍️ Autor: " + (getAutor() == null || getAutor().isEmpty() ? "Não informado" : getAutor()) + "\n" +
                "📅 Ano de Lançamento: " + (getAnoLancamento() == 0 ? "Não informado" : getAnoLancamento()) + "\n" +
                "🏷️ Gênero: " + (getGenero() == null || getGenero().isEmpty() ? "Não informado" : getGenero()) + "\n" +
                "📖 Número de Páginas: " + (getNumeroPaginas() == 0 ? "Não informado" : getNumeroPaginas()) + "\n" +
                "📊 Status de Leitura: " + (getStatus() == 0 ? "Não iniciado" : getStatus() + "% concluído") + "\n" +
                "🔗 Link do Livro: " + (getLink() == null || getLink().isEmpty() ? "Não informado" : getLink());
        }
    
    public String mostrarStatus() {
        if (getStatus() == 0) {
            return "Não iniciado";
        } else if (getStatus() > 0 && getStatus() < 100) {
            return getStatus() + "% concluído";
        } else if (getStatus() == 100) {
            return "Concluído";
        } else {
            return "Status inválido";
        }
    }
}
