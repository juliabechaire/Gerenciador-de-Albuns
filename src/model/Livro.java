package model;

public class Livro extends Arquivo implements Status {
    private String autor;
    private int numeroPaginas;
    private int status=0; 

    public Livro(String nome) {
        super(nome);
        this.autor = "Desconhecido";
        this.numeroPaginas = 0;
    }

    public String getAutor() {
        return autor;
    }

    public int getNumeroPaginas() {
        return numeroPaginas;
    }

    @Override
    public int getStatus() {
        return status;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public void setNumeroPaginas(int numeroPaginas) {
        this.numeroPaginas = numeroPaginas;
    }

    @Override
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
                "📊 Status de Leitura: " + mostrarStatus() + "\n" +
                "🔗 Link do Livro: " + (getLink() == null || getLink().isEmpty() ? "Não informado" : getLink());
        }
    
    @Override
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