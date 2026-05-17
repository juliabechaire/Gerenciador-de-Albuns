package view;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.List;

import controller.AlbumController;
import model.*;

public class TelaPrincipal {

    // Conexão direta com a camada de controle
    private AlbumController controller;

    // Componentes visuais que precisamos acessar de vários métodos
    private TableView<Album> tabela;
    private TextField txtBusca, txtBanda, txtTitulo, txtAno, txtIntegrantes, txtGenero, txtDestaque, txtEspecifico1, txtEspecifico2;
    private ComboBox<String> comboTipo;

    public void iniciarTela(Stage palco) {
        // Inicializa o cérebro do sistema (que já carrega os arquivos salvos no HD)
        this.controller = new AlbumController();

        palco.setTitle("RockVault - Gerenciador de Álbuns Digitais");

        // Layout Principal: Divide a tela em regiões
        BorderPane layoutPrincipal = new BorderPane();

        // 1. BARRA DE BUSCA (Fica no Norte/Topo da tela)
        HBox barraBusca = criarBarraBusca();
        layoutPrincipal.setTop(barraBusca);

        // 2. TABELA DE EXIBIÇÃO (Fica no Centro da tela)
        this.tabela = criarTabela();
        layoutPrincipal.setCenter(this.tabela);
        atualizarTabela(controller.getBiblioteca()); // Preenche a tabela com os dados do HD

        // 3. FORMULÁRIO DE CADASTRO E AÇÕES (Fica no Oeste/Lateral Esquerda)
        VBox formulario = criarFormulario();
        layoutPrincipal.setLeft(formulario);

        // Cria o cenário (Scene) com tamanho de 1024x600 pixels
        Scene cenario = new Scene(layoutPrincipal, 1024, 600);
        
        // DICA DE ENGENHARIA: Aqui você poderia aplicar um arquivo CSS para deixar em Dark Mode!
        // cenario.getStylesheets().add("estilos.css");

        palco.setScene(cenario);
        palco.show(); // Abre a janela de verdade
    }

    // Cria a região superior de busca por palavra-chave (Filtro)
    private HBox criarBarraBusca() {
        HBox hbox = new HBox(10); // 10 pixels de espaçamento entre os elementos
        hbox.setStyle("-fx-padding: 10; -fx-background-color: #333333;");

        Label lblBusca = new Label("Buscar Álbum/Integrante:");
        lblBusca.setStyle("-fx-text-fill: white;");
        
        txtBusca = new TextField();
        txtBusca.setPromptText("Digite a palavra-chave...");

        Button btnFiltrar = new Button("Filtrar");
        Button btnLimpar = new Button("Mostrar Todos");

        // AÇÃO DO BOTÃO FILTRAR (Busca por palavra-chave)
        btnFiltrar.setOnAction(e -> {
            String termo = txtBusca.getText();
            List<Album> resultados = controller.buscarPorPalavraChave(termo);
            atualizarTabela(resultados);
        });

        // AÇÃO DO BOTÃO LIMPAR FILTRO
        btnLimpar.setOnAction(e -> {
            txtBusca.clear();
            atualizarTabela(controller.getBiblioteca());
        });

        hbox.getChildren().addAll(lblBusca, txtBusca, btnFiltrar, btnLimpar);
        return hbox;
    }

    // Cria a tabela centralizada usando Polimorfismo (ela exibe a mãe "Album")
    @SuppressWarnings("unchecked")
    private TableView<Album> criarTabela() {
        TableView<Album> table = new TableView<>();

        // Configura as colunas. O "PropertyValueFactory" mapeia direto nos Getters da classe Album!
        TableColumn<Album, String> colBanda = new TableColumn<>("Banda");
        colBanda.setCellValueFactory(new PropertyValueFactory<>("nomeBanda"));

        TableColumn<Album, String> colTitulo = new TableColumn<>("Título do Álbum");
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));

        TableColumn<Album, Integer> colAno = new TableColumn<>("Ano");
        colAno.setCellValueFactory(new PropertyValueFactory<>("anoLancamento"));

        TableColumn<Album, String> colMidia = new TableColumn<>("Tipo de Mídia");
        colMidia.setCellValueFactory(new PropertyValueFactory<>("tipoMidia")); // Método polimórfico!

        TableColumn<Album, String> colAcesso = new TableColumn<>("Acesso / Localização");
        colAcesso.setCellValueFactory(new PropertyValueFactory<>("destinoAcesso")); // Método polimórfico!

        table.getColumns().addAll(colBanda, colTitulo, colAno, colMidia, colAcesso);
        // LINHA NOVA (Substitua por esta):
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        return table;
    }

    // Cria o formulário lateral esquerdo para Adicionar, Editar e Remover
    private VBox criarFormulario() {
        VBox vbox = new VBox(8);
        vbox.setStyle("-fx-padding: 15; -fx-background-color: #f4f4f4; -fx-pref-width: 300;");

        vbox.getChildren().add(new Label("--- CADASTRO DE ÁLBUM ---"));
        
        // Seletor do tipo de álbum
        comboTipo = new ComboBox<>();
        comboTipo.getItems().addAll("Streaming", "Download Local", "Review Crítica");
        comboTipo.setValue("Streaming"); // Padrão
        
        txtBanda = new TextField(); txtBanda.setPromptText("Nome da Banda");
        txtTitulo = new TextField(); txtTitulo.setPromptText("Título do Álbum");
        txtAno = new TextField(); txtAno.setPromptText("Ano de Lançamento");
        txtIntegrantes = new TextField(); txtIntegrantes.setPromptText("Integrantes (separados por vírgula)");
        txtGenero = new TextField(); txtGenero.setPromptText("Subgênero de Rock");
        txtDestaque = new TextField(); txtDestaque.setPromptText("Música Destaque");
        
        // Campos dinâmicos (mudam de significado dependendo do tipo selecionado no Combo)
        txtEspecifico1 = new TextField(); txtEspecifico1.setPromptText("URL do Álbum (HTTP...)");
        txtEspecifico2 = new TextField(); txtEspecifico2.setPromptText("Nome da Plataforma (Spotify...)");

        // Evento para mudar os placeholders das caixas quando o usuário muda o tipo no ComboBox
        comboTipo.setOnAction(e -> ajustarCamposEspecificos());

        // BOTÕES DE AÇÃO DO SISTEMA (CRUD)
        Button btnAdicionar = new Button("Adicionar Álbum");
        btnAdicionar.setMaxWidth(Double.MAX_VALUE);
        
        Button btnRemover = new Button("Remover Selecionado");
        btnRemover.setMaxWidth(Double.MAX_VALUE);
        btnRemover.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white;");

        // --- TRATAMENTO DE EXCEÇÃO EM CAMADAS (O ponto alto do trabalho) ---
        
        // 1. AÇÃO DE ADICIONAR
        btnAdicionar.setOnAction(e -> {
            try {
                // Captura erro de digitação de número (Exceção do Java: NumberFormatException)
                int ano = Integer.parseInt(txtAno.getText());
                
                Album novo = montarObjetoAlbum(ano);
                
                // Dispara o envio para a Controller (pode lançar DadosInvalidosException ou IOException)
                controller.adicionarAlbum(novo);
                
                exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Álbum cadastrado e salvo no arquivo!");
                limparFormulario();
                atualizarTabela(controller.getBiblioteca());

            } catch (NumberFormatException ex) {
                exibirAlerta(Alert.AlertType.ERROR, "Erro de Entrada", "O campo 'Ano' deve ser um número inteiro válido.");
            } catch (exception.DadosInvalidosException ex) {
                // Captura a SUA exceção personalizada lançada lá no Model/Interface
                exibirAlerta(Alert.AlertType.WARNING, "Validação Recusada", ex.getMessage());
            } catch (java.io.IOException ex) {
                // Captura o erro caso o arquivo biblioteca.dat falhe no HD
                exibirAlerta(Alert.AlertType.ERROR, "Erro de Arquivo", "Falha crítica de I/O ao gravar no disco.");
            }
        });

                // --- NOVO BOTÃO DE EDITAR (Adicione esta parte) ---
        Button btnEditar = new Button("Editar por Busca");
        btnEditar.setMaxWidth(Double.MAX_VALUE);
        btnEditar.setStyle("-fx-background-color: #ffb31a; -fx-text-fill: black;");

        btnEditar.setOnAction(e -> {
            String busca = txtBusca.getText();
            if (busca.isEmpty()) {
                exibirAlerta(Alert.AlertType.WARNING, "Atenção", "Digite o termo do álbum que deseja alterar na barra de busca superior antes de clicar em Editar.");
                return;
            }

            try {
                int ano = Integer.parseInt(txtAno.getText());
                Album editado = montarObjetoAlbum(ano);
                
                // Chama a Controller que faz a busca, substitui na lista e atualiza o HD
                controller.editarAlbum(busca, editado);
                
                exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Álbum editado e atualizado no arquivo com sucesso!");
                limparFormulario();
                txtBusca.clear();
                atualizarTabela(controller.getBiblioteca());

            } catch (NumberFormatException ex) {
                exibirAlerta(Alert.AlertType.ERROR, "Erro de Entrada", "O campo 'Ano' deve ser um número inteiro válido.");
            } catch (exception.AlbumNaoEncontradoException ex) {
                exibirAlerta(Alert.AlertType.ERROR, "Não Encontrado", ex.getMessage());
            } catch (exception.DadosInvalidosException ex) {
                exibirAlerta(Alert.AlertType.WARNING, "Validação Recusada", ex.getMessage());
            } catch (java.io.IOException ex) {
                exibirAlerta(Alert.AlertType.ERROR, "Erro de Arquivo", "Falha de I/O ao reescrever o arquivo.");
            }
        });
        // --------------------------------------------------

        // 2. AÇÃO DE REMOVER POR PALAVRA-CHAVE
        btnRemover.setOnAction(e -> {
            String busca = txtBusca.getText();
            if (busca.isEmpty()) {
                exibirAlerta(Alert.AlertType.WARNING, "Atenção", "Digite um termo na barra de busca superior para podermos remover o álbum correspondente.");
                return;
            }

            try {
                // Chama o método da controller (que propaga erros se não achar)
                controller.removerAlbum(busca);
                exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Álbum removido com sucesso!");
                txtBusca.clear();
                atualizarTabela(controller.getBiblioteca());
            } catch (exception.AlbumNaoEncontradoException ex) {
                // Captura a SUA segunda exceção personalizada
                exibirAlerta(Alert.AlertType.ERROR, "Não Encontrado", ex.getMessage());
            } catch (java.io.IOException ex) {
                exibirAlerta(Alert.AlertType.ERROR, "Erro de Arquivo", "Não foi possível atualizar o arquivo após a remoção.");
            }
        });

        vbox.getChildren().addAll(
            comboTipo, txtBanda, txtTitulo, txtAno, 
            txtIntegrantes, txtGenero, txtDestaque, new Label("Campos Específicos:"), 
            txtEspecifico1, txtEspecifico2, new Separator(), 
            btnAdicionar, btnEditar, btnRemover // <-- Adicione o btnEditar aqui no meio
        );

        return vbox;
    }

    // Auxiliar para mudar as caixas de texto dinamicamente na tela
    private void ajustarCamposEspecificos() {
        String tipo = comboTipo.getValue();
        if (tipo.equals("Streaming")) {
            txtEspecifico1.setPromptText("URL do Álbum (HTTP...)"); txtEspecifico1.setVisible(true);
            txtEspecifico2.setPromptText("Nome da Plataforma (Spotify...)"); txtEspecifico2.setVisible(true);
        } else if (tipo.equals("Download Local")) {
            txtEspecifico1.setPromptText("Caminho do Arquivo no HD"); txtEspecifico1.setVisible(true);
            txtEspecifico2.setPromptText("Tamanho em MB (Ex: 125.4)"); txtEspecifico2.setVisible(true);
        } else { // Review
            txtEspecifico1.setPromptText("Nota de Avaliação (1 a 5)"); txtEspecifico1.setVisible(true);
            txtEspecifico2.setPromptText("Sua Resenha Crítica"); txtEspecifico2.setVisible(true);
        }
    }

    // Fábrica de objetos baseada na escolha do formulário (Polimorfismo de Criação)
    private Album montarObjetoAlbum(int ano) {
        String tipo = comboTipo.getValue();
        if (tipo.equals("Streaming")) {
            return new AlbumStreaming(txtBanda.getText(), txtTitulo.getText(), ano, txtIntegrantes.getText(),
                    txtGenero.getText(), "Histórico", txtDestaque.getText(), txtEspecifico1.getText(), txtEspecifico2.getText());
        } else if (tipo.equals("Download Local")) {
            double mb = Double.parseDouble(txtEspecifico2.getText().isEmpty() ? "0" : txtEspecifico2.getText());
            return new AlbumDownload(txtBanda.getText(), txtTitulo.getText(), ano, txtIntegrantes.getText(),
                    txtGenero.getText(), "Histórico", txtDestaque.getText(), txtEspecifico1.getText(), mb);
        } else {
            int nota = Integer.parseInt(txtEspecifico1.getText().isEmpty() ? "0" : txtEspecifico1.getText());
            return new AlbumReview(txtBanda.getText(), txtTitulo.getText(), ano, txtIntegrantes.getText(),
                    txtGenero.getText(), "Histórico", txtDestaque.getText(), nota, txtEspecifico2.getText());
        }
    }

    // Atualiza as linhas da tabela visual
    private void atualizarTabela(List<Album> lista) {
        tabela.getItems().clear();
        tabela.getItems().addAll(lista);
    }

    private void limparFormulario() {
        txtBanda.clear(); txtTitulo.clear(); txtAno.clear(); txtIntegrantes.clear();
        txtGenero.clear(); txtDestaque.clear(); txtEspecifico1.clear(); txtEspecifico2.clear();
    }

    // Janelinha pop-up nativa do JavaFX para alertas de erro ou sucesso
    private void exibirAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}