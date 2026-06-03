package controller;
import model.Arquivo;
import persistence.ArquivoRepository;
import exception.ArquivoNaoEncontradoException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ArquivoController {
    private List<Arquivo> biblioteca;
    private ArquivoRepository repo;    

    public ArquivoController()
    {
        this.repo = new ArquivoRepository();

        try {
            this.biblioteca = this.repo.carregar();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Aviso: Não foi possível carregar os dados. Criando biblioteca vazia.");
            this.biblioteca = new ArrayList<>(); 
        }   
    }  
    
    public void adicionarArquivo(Arquivo item) throws IOException {
        this.biblioteca.add(item);
        repo.salvar(this.biblioteca);
    }

    // Lança ArquivoNaoEncontradoException se o termo não corresponder a nenhum item
    // Exceção propagada daqui (controller) para ser tratada na view
    public List<Arquivo> buscar_palavra_chave(String palavra) throws ArquivoNaoEncontradoException
    {
        if(palavra == null || palavra.isEmpty())
        {
            return this.biblioteca;
        }  
        
        List<Arquivo> filtrados = new ArrayList<>();

        for(Arquivo item : this.biblioteca)
        {
            if(item.getNome().toLowerCase().contains(palavra.toLowerCase()))
            {
                filtrados.add(item);
            }
        }

        // Lançada na camada controller quando nenhum resultado é encontrado
        if(filtrados.isEmpty())
        {
            throw new ArquivoNaoEncontradoException(palavra);
        }

        return filtrados;
    }

    // Lança ArquivoNaoEncontradoException se o título não existir na biblioteca
    public void remover_busca(String titulo) throws IOException, ArquivoNaoEncontradoException
    {
        boolean encontrado = false;
        for(int i = this.biblioteca.size() - 1; i >= 0; i--)
        {
            if(this.biblioteca.get(i).getNome().equalsIgnoreCase(titulo.trim()))
            {
                this.biblioteca.remove(i);
                encontrado = true;
                break;
            }
        }

        // Lançada na camada controller quando o item não existe para remoção
        if(!encontrado)
        {
            throw new ArquivoNaoEncontradoException(titulo);
        }

        repo.salvar(this.biblioteca);
    }

    public void editar_busca(String tituloOriginal, Arquivo itemEditado) throws IOException
    {
        for(int i = 0; i < this.biblioteca.size(); i++)
        {
            if(this.biblioteca.get(i).getNome().equalsIgnoreCase(tituloOriginal.trim()))
            {
                this.biblioteca.set(i, itemEditado);
                break;
            }
        }
        repo.salvar(this.biblioteca);
    }

    // Método auxiliar para a view obter a lista completa
    public List<Arquivo> getBiblioteca() {
        return this.biblioteca;
    }   
}