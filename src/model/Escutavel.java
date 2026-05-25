package model;

public interface Escutavel {
    void reproduzirMelhorFaixa(); // Abre o link da faixa favorita
    void embaralharFaixas();       // Simula um "Shuffle" na lista de músicas
    void pausar();
    double calcularDuracaoMediaPorFaixa(int totalFaixas); 
    boolean ehAlbumLongo(); // Retorna true se a duração passar de, por exemplo, 60 minutos
}

//public interface ImportavelSpotify {
    //void sincronizarPlaylist(String urlSpotify);
    //String obterCodigoIncorporacao(); // Retorna o código HTML do player do Spotify
//}
