package model;

/**
 * Interface funcional que marca classes capazes de ser preenchidas
 * automaticamente via uma fonte externa (ex: API Last.fm).
 * Usada polimorficamente no LastFmService.
 */
public interface Importavel {

    /**
     * Preenche os dados do objeto com as informações vindas da fonte externa.
     * @param dados String JSON ou estrutura de dados bruta retornada pela API
     */
    void importarDados(String dados);

    /**
     * Retorna true se o objeto foi preenchido via importação automática.
     */
    boolean foiImportado();
}