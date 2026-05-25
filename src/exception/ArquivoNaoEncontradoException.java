package exception;

public class ArquivoNaoEncontradoException extends Exception {
    private static final long serialVersionUID = 1L;
    
    public ArquivoNaoEncontradoException(String termoBusca) {
        super("Nenhum arquivo correspondente a '" + termoBusca + "' foi encontrado na biblioteca.");
    }
}