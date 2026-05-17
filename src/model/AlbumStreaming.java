package model;

// Herda de Album e assina os contratos de Compartilhavel e Validavel
public class AlbumStreaming extends Album implements Compartilhavel, Validavel {
    
    // 1. ATRIBUTOS EXCLUSIVOS: Coisas que só existem no Streaming de internet.
    private String urlPlataforma; // Link direto do Spotify/Tidal
    private String nomePlataforma; // Nome do serviço (ex: Spotify)

    // 2. CONSTRUTOR: Recebe os dados da mãe (via super) + os dados exclusivos dela.
    public AlbumStreaming(String nomeBanda, String titulo, int anoLancamento, String integrantes, 
                          String genero, String contextoHistorico, String faixaDestaque, 
                          String urlPlataforma, String nomePlataforma) {
        // O "super" repassa as informações para o construtor da classe mãe (Album)
        super(nomeBanda, titulo, anoLancamento, integrantes, genero, contextoHistorico, faixaDestaque);
        this.urlPlataforma = urlPlataforma;
        this.nomePlataforma = nomePlataforma;
    }

    // 3. POLIMORFISMO DA MÃE: Implementando os métodos abstratos que a classe Album exigiu.
    @Override
    public String getDestinoAcesso() {
        return this.urlPlataforma; // Para o streaming, o destino é o link da internet
    }

    @Override
    public String getTipoMidia() {
        return "Streaming (" + this.nomePlataforma + ")";
    }

    // 4. POLIMORFISMO DA INTERFACE COMPARTILHAVEL: Como um link de streaming é compartilhado?
    @Override
    public String gerarTextoCompartilhamento() {
        return "Estou ouvindo o álbum '" + getTitulo() + "' do " + getNomeBanda() + 
               " no " + this.nomePlataforma + "! Acesse aqui: " + this.urlPlataforma;
    }

    // 5. POLIMORFISMO DA INTERFACE VALIDAVEL: Regra de validação do Streaming
    @Override
    public void validar() throws exception.DadosInvalidosException {
        // Se o usuário não digitar um link que comece com http, dá erro
        if (!this.urlPlataforma.startsWith("http")) {
            throw new exception.DadosInvalidosException("URL de streaming inválida! Deve começar com http.");
        }
    }

    // 6. GETTERS E SETTERS EXCLUSIVOS
    public String getUrlPlataforma() { return urlPlataforma; }
    public void setUrlPlataforma(String urlPlataforma) { this.urlPlataforma = urlPlataforma; }
    public String getNomePlataforma() { return nomePlataforma; }
    public void setNomePlataforma(String nomePlataforma) { this.nomePlataforma = nomePlataforma; }
}