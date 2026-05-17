package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import exception.AlbumNaoEncontradoException;
import exception.DadosInvalidosException;
import model.Album;
import model.Validavel;
import persistence.ArquivoRepository;

public class AlbumController {

    // 1. A COLEÇÃO POLIMÓRFICA EXIGIDA: Uma única lista que aceita qualquer tipo de Álbum digital
    private List<Album> biblioteca;
    
    // Instância da camada de persistência para salvar/carregar arquivos
    private ArquivoRepository repo;

    // CONSTRUTOR: Inicializa a lista e tenta carregar os dados salvos do HD
    public AlbumController() {
        this.repo = new ArquivoRepository();
        
        try {
            // Tenta puxar a lista gravada no arquivo biblioteca.dat
            this.biblioteca = repo.carregar();
            
            // Se o arquivo não existir (primeira vez rodando), inicializa uma lista vazia
            if (this.biblioteca == null) {
                this.biblioteca = new ArrayList<>();
            }
        } catch (IOException | ClassNotFoundException e) {
            // Se o arquivo estiver corrompido, começamos com uma lista vazia
            this.biblioteca = new ArrayList<>();
            System.out.println("Aviso: Não foi possível carregar a biblioteca anterior. Criando nova.");
        }
    }

    // 2. OPERAÇÃO: ADICIONAR
    // Repare que o método recebe "Album", que é a mãe genérica (Polimorfismo!)
    public void adicionarAlbum(Album novoAlbum) throws DadosInvalidosException, IOException {
        
        // Se o objeto souber se validar (interface Validavel), nós testamos as regras dele
        if (novoAlbum instanceof Validavel) {
            ((Validavel) novoAlbum).validar(); // Faz o cast e executa o método validar()
        }

        // Adiciona na coleção polimorfa
        this.biblioteca.add(novoAlbum);
        
        // Salva a alteração no arquivo do HD imediatamente
        repo.salvar(this.biblioteca);
    }

    // 3. OPERAÇÃO: BUSCAR (Por palavra-chave)
    // Varre a lista procurando se o termo bate com o Título, Banda ou Integrantes
    public List<Album> buscarPorPalavraChave(String termo) {
        List<Album> resultados = new ArrayList<>();
        String termoMinusculo = termo.toLowerCase();

        for (Album alb : this.biblioteca) {
            if (alb.getTitulo().toLowerCase().contains(termoMinusculo) ||
                alb.getNomeBanda().toLowerCase().contains(termoMinusculo) ||
                alb.getIntegrantes().toLowerCase().contains(termoMinusculo)) {
                
                resultados.add(alb); // Adiciona o que encontrou na lista de resultados
            }
        }
        return resultados;
    }

    // 4. OPERAÇÃO: REMOVER (Através de busca por palavra-chave)
    // O edital exige usar throws e propagar exceções entre as camadas
    public void removerAlbum(String termoBusca) throws AlbumNaoEncontradoException, IOException {
        List<Album> achados = buscarPorPalavraChave(termoBusca);

        // Se a busca não retornou nada, lança a nossa exceção própria de "Não Encontrado"
        if (achados.isEmpty()) {
            throw new AlbumNaoEncontradoException(termoBusca);
        }

        // Para simplificar, vamos remover o primeiro álbum que bater com a busca
        Album alvo = achados.get(0);
        this.biblioteca.remove(alvo);

        // Atualiza o arquivo no HD após a remoção
        repo.salvar(this.biblioteca);
    }

    // 5. OPERAÇÃO: EDITAR (Substitui os dados de um álbum existente)
    public void editarAlbum(String termoBusca, Album albumEditado) throws AlbumNaoEncontradoException, DadosInvalidosException, IOException {
        List<Album> achados = buscarPorPalavraChave(termoBusca);

        if (achados.isEmpty()) {
            throw new AlbumNaoEncontradoException(termoBusca);
        }

        // Valida as novas informações do álbum antes de salvar
        if (albumEditado instanceof Validavel) {
            ((Validavel) albumEditado).validar();
        }

        // Encontra a posição do álbum antigo na lista e substitui pelo novo editado
        Album antigo = achados.get(0);
        int indice = this.biblioteca.indexOf(antigo);
        this.biblioteca.set(indice, albumEditado);

        // Atualiza o arquivo no HD
        repo.salvar(this.biblioteca);
    }

    // Método GET para a interface gráfica puxar a lista completa e exibir na tabela
    public List<Album> getBiblioteca() {
        return this.biblioteca;
    }
}