package model;

// Herda de Album e implementa apenas a validação (não faz sentido compartilhar um arquivo local do seu HD)
public class AlbumDownload extends Album implements Validavel {
    
    // ATRIBUTOS EXCLUSIVOS: Informações de arquivos locais no computador.
    private String caminhoArquivo; // Ex: C:/Musicas/Rock/album.mp3
    private double tamanhoMB;     // Espaço que ocupa no disco

    // CONSTRUTOR
    public AlbumDownload(String nomeBanda, String titulo, int anoLancamento, String integrantes, 
                         String genero, String contextoHistorico, String faixaDestaque, 
                         String caminhoArquivo, double tamanhoMB) {
        super(nomeBanda, titulo, anoLancamento, integrantes, genero, contextoHistorico, faixaDestaque);
        this.caminhoArquivo = caminhoArquivo;
        this.tamanhoMB = tamanhoMB;
    }

    // POLIMORFISMO DA MÃE
    @Override
    public String getDestinoAcesso() {
        return this.caminhoArquivo; // Para arquivos baixados, o destino é a pasta do HD
    }

    @Override
    public String getTipoMidia() {
        return "Download Local (" + this.tamanhoMB + " MB)";
    }

    // POLIMORFISMO DA INTERFACE VALIDAVEL
    @Override
    public void validar() throws exception.DadosInvalidosException {
        // O tamanho do arquivo não pode ser zero ou negativo
        if (this.tamanhoMB <= 0) {
            throw new exception.DadosInvalidosException("O tamanho do arquivo baixado precisa ser maior que 0 MB.");
        }
    }

    // GETTERS E SETTERS EXCLUSIVOS
    public String getCaminhoArquivo() { return caminhoArquivo; }
    public void setCaminhoArquivo(String caminhoArquivo) { this.caminhoArquivo = caminhoArquivo; }
    public double getTamanhoMB() { return tamanhoMB; }
    public void setTamanhoMB(double tamanhoMB) { this.tamanhoMB = tamanhoMB; }
}