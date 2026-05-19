package model;

import exception.DadosInvalidosException;

public interface Validavel {
    void validar() throws DadosInvalidosException;
}