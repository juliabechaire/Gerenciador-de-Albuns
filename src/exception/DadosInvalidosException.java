package exception;

public class DadosInvalidosException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DadosInvalidosException(String mensagem) {
        super(mensagem);
    }
}

//O compilador não te obriga a colocar try/catch ou throws em lugar nenhum! 
//O programa compila normalmente. 
// Ela é usada para erros que acontecem por falha de digitação do usuário ou lógica do programa em tempo de execução.