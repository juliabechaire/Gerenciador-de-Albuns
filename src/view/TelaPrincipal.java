package view;

import controller.ItemController;
import model.*;
import exception.AlbumNaoEncontradoException;
import exception.DadosInvalidosException;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class TelaPrincipal {

    private ItemController controller;
    
    // Componentes Globais da Tela
    private TableView<Item> tabela;
    private ComboBox<String> cbTipoMidia;
    private TextField txtTitulo, txtAno, txtGenero, txtCampoEspecfico1, txtCampoEspecifico2;
    private Label lblCampoEspecifico1, lblCampoEspecifico2;
    private TextField txtBusca;

    public void iniciarTela(Stage palcoPrincipal) {
        controller = new ItemController();
        palcoPrincipal.setTitle("Cofre Cultural - Multimídias");

        // --- LAYOUT PRINCIPAL ---
        BorderPane layoutRaiz = new BorderPane();
        layoutRaiz.setPadding(new Insets(15));

        // --- FORMULÁRIO DE CADASTRO (ESQUERDA) ---
        VBox formCadastro = new VBox(10);
        formCadastro.setPadding(new Insets(10));
        formCadastro.setPrefWidth(320);
        formCadastro.setStyle("-fx-border-color: #ccc; -fx-border-radius: 5;");

        Label lblTituloForm = new Label("Cadastro de Mídias");
        lblTituloForm.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        // Seleção do Tipo de Mídia
        Label lblTipo = new Label("Selecione o Tipo:");
        cbTipoMidia = new ComboBox<>(FXCollections.observableArrayList("Álbum Musical", "Filme", "Livro"));
        cbTipoMidia.setValue("Álbum Musical"); // Padrão

        // Campos Comuns
        txtTitulo = new TextField(); txtTitulo.setPromptText("Ex: Star Wars, Master of Puppets...");
        txtAno = new TextField(); txtAno.setPromptText("Ex: 1986, 2024...");
        txtGenero = new TextField(); txtGenero.setPromptText("Ex: Heavy Metal, Sci-Fi, Drama...");

        // Campos Dinâmicos (Vão mudar de label dependendo do ComboBox)
        lblCampoEspecifico1 = new Label("Banda / Artista:");
        txtCampoEspecfico1 = new TextField();
        
        lblCampoEspecifico2 = new Label("Faixa Destaque:");
        txtCampoEspecifico2 = new TextField();

        // Evento para mudar os textos das labels ao escolher o tipo no ComboBox
        cbTipoMidia.setOnAction(e -> gerenciarCamposDinamicos());

        Button btnSalvar = new Button("Salvar no Cofre");
        btnSalvar.setMaxWidth(Double.MAX_VALUE);
        btnSalvar.setOnAction(e -> acaoSalvar());

        formCadastro.getChildren().addAll(
            lblTituloForm, new Separator(),
            lblTipo, cbTipoMidia,
            new Label("Título:"), txtTitulo,
            new Label("Ano de Lançamento:"), txtAno,
            new Label("Gênero:"), txtGenero,
            lblCampoEspecifico1, txtCampoEspecfico1,
            lblCampoEspecifico2, txtCampoEspecifico2,
            new Separator(), btnSalvar
        );

        // --- TABELA E BUSCA (CENTRO) ---
        VBox centroLayout = new VBox(10);
        centroLayout.setPadding(new Insets(0, 0, 0, 15));

        // Barra de Busca e Remoção
        HBox barraOperacoes = new HBox(10);
        txtBusca = new TextField();
        txtBusca.setPromptText("Buscar por palavra-chave...");
        txtBusca.setPrefWidth(250);

        Button btnBuscar = new Button("Buscar");
        btnBuscar.setOnAction(e -> acaoBuscar());

        Button btnRemover = new Button("Remover por Título");
        btnRemover.setStyle("-fx-background-color: #ff6666; -fx-text-fill: white;");
        btnRemover.setOnAction(e -> acaoRemover());

        barraOperacoes.getChildren().addAll(txtBusca, btnBuscar, btnRemover);

        // Configuração da Tabela Polimórfica
        tabela = new TableView<>();
        
        TableColumn<Item, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoMidia")); // Chama getTipoMidia()
        colTipo.setPrefWidth(120);

        TableColumn<Item, String> colTitulo = new TableColumn<>("Título");
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colTitulo.setPrefWidth(180);

        TableColumn<Item, Integer> colAno = new TableColumn<>("Ano");
        colAno.setCellValueFactory(new PropertyValueFactory<>("anoLancamento"));
        colAno.setPrefWidth(60);

        TableColumn<Item, String> colDetalhes = new TableColumn<>("Detalhes do Item");
        colDetalhes.setCellValueFactory(new PropertyValueFactory<>("detalhesEspecificos")); // Chama getDetalhesEspecificos()
        colDetalhes.setPrefWidth(300);

        tabela.getColumns().addAll(colTipo, colTitulo, colAno, colDetalhes);
        atualizarTabela();

        centroLayout.getChildren().addAll(barraOperacoes, tabela);

        // Montagem Final do Painel
        layoutRaiz.setLeft(formCadastro);
        layoutRaiz.setCenter(centroLayout);

        Scene cena = new Scene(layoutRaiz, 1024, 600);
        palcoPrincipal.setScene(cena);
        palcoPrincipal.show();
    }

    // Gerencia o texto das Labels do formulário conforme a escolha do tipo
    private void gerenciarCamposDinamicos() {
        String selecao = cbTipoMidia.getValue();
        if ("Álbum Musical".equals(selecao)) {
            lblCampoEspecifico1.setText("Banda / Artista:");
            txtCampoEspecfico1.setPromptText("Ex: Iron Maiden");
            lblCampoEspecifico2.setText("Faixa Destaque:");
            txtCampoEspecifico2.setPromptText("Ex: The Trooper");
        } else if ("Filme".equals(selecao)) {
            lblCampoEspecifico1.setText("Diretor:");
            txtCampoEspecfico1.setPromptText("Ex: Christopher Nolan");
            lblCampoEspecifico2.setText("Duração (Minutos):");
            txtCampoEspecifico2.setPromptText("Ex: 148");
        } else if ("Livro".equals(selecao)) {
            lblCampoEspecifico1.setText("Autor:");
            txtCampoEspecfico1.setPromptText("Ex: J.R.R. Tolkien");
            lblCampoEspecifico2.setText("Nº de Páginas:");
            txtCampoEspecifico2.setPromptText("Ex: 1200");
        }
    }

    private void acaoSalvar() {
        try {
            String tipo = cbTipoMidia.getValue();
            String titulo = txtTitulo.getText();
            int ano = Integer.parseInt(txtAno.getText().trim());
            String genero = txtGenero.getText();
            String campo1 = txtCampoEspecfico1.getText();
            String campo2 = txtCampoEspecifico2.getText();

            Item novoItem = null;

            // Fábrica Polimórfica baseada na escolha da Tela
            if ("Álbum Musical".equals(tipo)) {
                novoItem = new Album(titulo, ano, genero, campo1, "Não especificado", campo2);
            } else if ("Filme".equals(tipo)) {
                int duracao = Integer.parseInt(campo2.trim());
                novoItem = new Filme(titulo, ano, genero, campo1, duracao);
            } else if ("Livro".equals(tipo)) {
                int paginas = Integer.parseInt(campo2.trim());
                novoItem = new Livro(titulo, ano, genero, campo1, paginas);
            }

            controller.adicionarItem(novoItem);
            exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", tipo + " guardado com sucesso no arquivo .dat!");
            limparFormulario();
            atualizarTabela();

        } catch (NumberFormatException ex) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro de Entrada", "Os campos de Ano, Duração ou Páginas precisam ser números válidos.");
        } catch (DadosInvalidosException ex) {
            exibirAlerta(Alert.AlertType.WARNING, "Validação Recusada", ex.getMessage());
        } catch (Exception ex) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro Crítico", "Erro ao processar: " + ex.getMessage());
        }
    }

    private void acaoBuscar() {
        String termo = txtBusca.getText();
        tabela.setItems(FXCollections.observableArrayList(controller.buscarPorPalavraChave(termo)));
    }

    private void acaoRemover() {
        String termo = txtBusca.getText();
        if (termo == null || termo.trim().isEmpty()) {
            exibirAlerta(Alert.AlertType.WARNING, "Atenção", "Digite o título exato do item no campo de busca para remover.");
            return;
        }
        try {
            controller.removerItem(termo);
            exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Item removido com sucesso!");
            txtBusca.clear();
            atualizarTabela();
        } catch (AlbumNaoEncontradoException ex) {
            exibirAlerta(Alert.AlertType.ERROR, "Não Encontrado", ex.getMessage());
        } catch (Exception ex) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível remover.");
        }
    }

    private void atualizarTabela() {
        tabela.setItems(FXCollections.observableArrayList(controller.getBiblioteca()));
    }

    private void limparFormulario() {
        txtTitulo.clear();
        txtAno.clear();
        txtGenero.clear();
        txtCampoEspecfico1.clear();
        txtCampoEspecifico2.clear();
    }

    private void exibirAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}