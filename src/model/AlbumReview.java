package model;

// A terceira classe exigida pelo edital. Focada na sua opinião crítica sobre o álbum.
public class AlbumReview extends Album implements Compartilhavel, Validavel {
    
    // ATRIBUTOS EXCLUSIVOS: Dados de avaliação e resenha histórica.
    private int notaAvaliacao;      // Nota de 1 a 5 estrelas
    private String resenhaCritica;  // Seu texto sobre o álbum

    // CONSTRUTOR
    public AlbumReview(String nomeBanda, String titulo, int anoLancamento, String integrantes, 
                       String genero, String contextoHistorico, String faixaDestaque, 
                       int notaAvaliacao, String resenhaCritica) {
        super(nomeBanda, titulo, anoLancamento, integrantes, genero, contextoHistorico, faixaDestaque);
        this.notaAvaliacao = notaAvaliacao;
        this.resenhaCritica = resenhaCritica;
    }

    // POLIMORFISMO DA MÃE
    @Override
    public String getDestinoAcesso() {
        return "Nota: " + this.notaAvaliacao + "/5 - Ver Resenha";
    }

    @Override
    public String getTipoMidia() {
        return "Review Crítica";
    }

    // POLIMORFISMO DA INTERFACE COMPARTILHAVEL: Compartilha a sua opinião com texto formatado
    @Override
    public String gerarTextoCompartilhamento() {
        return "Minha nota para o álbum '" + getTitulo() + "' do " + getNomeBanda() + 
               " é " + this.notaAvaliacao + "/5 Estrelas! Minha análise: " + this.resenhaCritica;
    }

    // POLIMORFISMO DA INTERFACE VALIDAVEL: Valida se a nota está no limite correto
    @Override
    public void validar() throws exception.DadosInvalidosException {
        if (this.notaAvaliacao < 1 || this.notaAvaliacao > 5) {
            throw new exception.DadosInvalidosException("A nota de avaliação precisa ser estritamente entre 1 e 5.");
        }
    }

    // GETTERS E SETTERS EXCLUSIVOS
    public int getNotaAvaliacao() { return notaAvaliacao; }
    public void setNotaAvaliacao(int notaAvaliacao) { this.notaAvaliacao = notaAvaliacao; }
    public String getResenhaCritica() { return resenhaCritica; }
    public void setResenhaCritica(String resenhaCritica) { this.resenhaCritica = resenhaCritica; }
}