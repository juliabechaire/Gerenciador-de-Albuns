package exception;

public class AlbumNaoEncontradoException extends Exception {
    private static final long serialVersionUID = 1L;
    public AlbumNaoEncontradoException(String termoBusca) {
        super("Nenhum álbum correspondente a '" + termoBusca + "' foi encontrado na biblioteca.");
    }
}