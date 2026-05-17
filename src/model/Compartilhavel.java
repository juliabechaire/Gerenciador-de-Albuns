package model;

// O professor exige que as interfaces tenham papel funcional.
// Esta interface obriga a classe a saber gerar um texto formatado
// para o usuário copiar e mandar para os amigos no WhatsApp ou redes sociais.
public interface Compartilhavel {
    
    // Método abstrato por padrão. Quem implementar terá que dizer COMO gera esse texto.
    String gerarTextoCompartilhamento();
}