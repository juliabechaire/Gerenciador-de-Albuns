package model;

import exception.DadosInvalidosException;

// Esta interface garante a segurança dos dados do sistema.
// Qualquer classe que implemente ela será obrigada a checar se suas 
// próprias informações fazem sentido antes de serem salvas no arquivo.
public interface Validavel {
    
    // Se os dados estiverem errados, o método lança a nossa exceção própria.
    // Isso cumpre a regra de "Declarar métodos com throws" do edital.
    void validar() throws DadosInvalidosException;
}