package view;

import controller.ItemController;
import model.*;
import exception.DadosInvalidosException;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class TelaPrincipal {

    private ItemController controller;
    
    // Componentes de Interface
    private TableView<Item> tabela;
    private ComboBox<String> cbTipoMidia;
    private TextField txtTitulo, txtAno, txtGenero, txtUrlImagem, txtLinkAcesso, txtCampoEspecifico1, txtCampoEspecifico2;
    private Label lblCampoEspecifico1, lblCampoEspecifico2;
    private TextField txtBusca;

    public void iniciarTela(Stage palcoPrincipal) {
        controller = new ItemController();
        palcoPrincipal.setTitle("Cofre Cultural - Multimídias (Modo Streaming)");

        BorderPane layoutRaiz = new BorderPane();
        layoutRaiz.setPadding(new Insets(15));

        // --- FORMULÁRIO DE CADASTRO ---
        VBox formCadastro = new VBox(8);
        formCadastro.setPadding(new Insets(10));
        formCadastro.setPrefWidth(320);
        formCadastro.setStyle("-fx-border-color: #ccc; -fx-border-radius: 5;");

        Label lblTituloForm = new Label("Cadastro de Mídias");
        lblTituloForm.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        cbTipoMidia = new ComboBox<>(FXCollections.observableArrayList("Álbum Musical", "Filme", "Livro"));
        cbTipoMidia.setValue("Álbum Musical");

        txtTitulo = new TextField(); txtTitulo.setPromptText("Apenas o título é obrigatório");
        txtAno = new TextField(); txtAno.setPromptText("Ex: 1986, 2024 (opcional)");
        txtGenero = new TextField(); txtGenero.setPromptText("Ex: Rock, Sci-Fi (opcional)");
        txtUrlImagem = new TextField(); txtUrlImagem.setPromptText("URL da Capa da Internet (opcional)");
        txtLinkAcesso = new TextField(); txtLinkAcesso.setPromptText("URL do Streaming/Link (opcional)");

        // Campos Dinâmicos (Erros de digitação corrigidos aqui)
        lblCampoEspecifico1 = new Label("Banda / Artista:");
        txtCampoEspecifico1 = new TextField();
        lblCampoEspecifico2 = new Label("Faixa Destaque:");
        txtCampoEspecifico2 = new TextField();

        cbTipoMidia.setOnAction(e -> gerenciarCamposDinamicos());

        Button btnSalvar = new Button("Salvar no Cofre");
        btnSalvar.setMaxWidth(Double.MAX_VALUE);
        btnSalvar.setOnAction(e -> acaoSalvar());

        formCadastro.getChildren().addAll(
            lblTituloForm, new Separator(),
            new Label("Tipo:"), cbTipoMidia,
            new Label("Título * :"), txtTitulo,
            new Label("Ano:"), txtAno,
            new Label("Gênero:"), txtGenero,
            new Label("Link da Imagem:"), txtUrlImagem,
            new Label("Link do Streaming/Acesso:"), txtLinkAcesso,
            lblCampoEspecifico1, txtCampoEspecifico1,
            lblCampoEspecifico2, txtCampoEspecifico2,
            new Separator(), btnSalvar
        );

        // --- TABELA E BUSCA ---
        VBox centroLayout = new VBox(10);
        centroLayout.setPadding(new Insets(0, 0, 0, 15));

        HBox barraOperacoes = new HBox(10);
        txtBusca = new TextField();
        txtBusca.setPromptText("Buscar por palavra-chave...");
        txtBusca.setPrefWidth(250);

        Button btnBuscar = new Button("Buscar");
        btnBuscar.setOnAction(e -> acaoBuscar());

        Button btnRemover = new Button("Remover");
        btnRemover.setStyle("-fx-background-color: #ff6666; -fx-text-fill: white;");
        btnRemover.setOnAction(e -> acaoRemover());

        barraOperacoes.getChildren().addAll(txtBusca, btnBuscar, btnRemover);

        tabela = new TableView<>();
        
        TableColumn<Item, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoMidia"));
        colTipo.setPrefWidth(100);

        TableColumn<Item, String> colTitulo = new TableColumn<>("Título");
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colTitulo.setPrefWidth(150);

        TableColumn<Item, Integer> colAno = new TableColumn<>("Ano");
        colAno.setCellValueFactory(new PropertyValueFactory<>("anoLancamento"));
        colAno.setPrefWidth(60);

        TableColumn<Item, String> colDetalhes = new TableColumn<>("Detalhes");
        colDetalhes.setCellValueFactory(new PropertyValueFactory<>("detalhesEspecificos"));
        colDetalhes.setPrefWidth(250);

        tabela.getColumns().addAll(colTipo, colTitulo, colAno, colDetalhes);
        atualizarTabela();

        centroLayout.getChildren().addAll(barraOperacoes, tabela);

        layoutRaiz.setLeft(formCadastro);
        layoutRaiz.setCenter(centroLayout);

        Scene cena = new Scene(layoutRaiz, 1024, 650);
        palcoPrincipal.setScene(cena);
        palcoPrincipal.show();
    }

    private void gerenciarCamposDinamicos() {
        String selecao = cbTipoMidia.getValue();
        if ("Álbum Musical".equals(selecao)) {
            lblCampoEspecifico1.setText("Banda / Artista:");
            lblCampoEspecifico2.setText("Faixa Destaque:");
        } else if ("Filme".equals(selecao)) {
            lblCampoEspecifico1.setText("Diretor:");
            lblCampoEspecifico2.setText("Duração (Minutos):");
        } else if ("Livro".equals(selecao)) {
            lblCampoEspecifico1.setText("Autor:");
            lblCampoEspecifico2.setText("Nº de Páginas:");
        }
    }

    private void acaoSalvar() {
        try {
            String tipo = cbTipoMidia.getValue();
            String titulo = txtTitulo.getText();
            
            int _ano = txtAno.getText().trim().isEmpty() ? 0 : Integer.parseInt(txtAno.getText().trim());
            String genero = txtGenero.getText();
            String urlImg = txtUrlImagem.getText();
            String linkAcesso = txtLinkAcesso.getText();
            
            String campo1 = txtCampoEspecifico1.getText();
            String campo2 = txtCampoEspecifico2.getText().trim();

            Item novoItem = null;

            if ("Álbum Musical".equals(tipo)) {
                novoItem = new Album(titulo, _ano, genero, urlImg, linkAcesso, campo1, campo2);
            } else if ("Filme".equals(tipo)) {
                int duracao = campo2.isEmpty() ? 0 : Integer.parseInt(campo2);
                novoItem = new Filme(titulo, _ano, genero, urlImg, linkAcesso, campo1, duracao);
            } else if ("Livro".equals(tipo)) {
                int paginas = campo2.isEmpty() ? 0 : Integer.parseInt(campo2);
                novoItem = new Livro(titulo, _ano, genero, urlImg, linkAcesso, campo1, paginas);
            }

            controller.adicionarItem(novoItem);
            exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Item cadastrado com sucesso!");
            limparFormulario();
            atualizarTabela();

        } catch (NumberFormatException ex) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro Numérico", "Os campos de Ano, Duração ou Páginas devem conter apenas números.");
        } catch (DadosInvalidosException ex) {
            exibirAlerta(Alert.AlertType.WARNING, "Validação", ex.getMessage());
        } catch (Exception ex) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao salvar: " + ex.getMessage());
        }
    }

    private void acaoBuscar() {
        // Comentado para evitar incompatibilidade com a Controller antiga antes do recomeço
        // String termo = txtBusca.getText();
        // tabela.setItems(FXCollections.observableArrayList(controller.buscarPorPalavraChave(termo)));
        System.out.println("Busca desativada temporariamente para reestruturação.");
    }

    private void acaoRemover() {
        // Comentado para evitar incompatibilidade com a Controller antiga antes do recomeço
        // String termo = txtBusca.getText();
        // if (termo == null || termo.trim().isEmpty()) {
        //     exibirAlerta(Alert.AlertType.WARNING, "Atenção", "Digite uma palavra-chave para achar e remover o item.");
        //     return;
        // }
        // try {
        //     controller.removerItem(termo);
        //     exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Item removido!");
        //     txtBusca.clear();
        //     atualizarTabela();
        // } catch (Exception ex) {
        //     exibirAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível remover.");
        // }
        System.out.println("Remoção desativada temporariamente para reestruturação.");
    }

    private void atualizarTabela() {
        tabela.setItems(FXCollections.observableArrayList(controller.getBiblioteca()));
    }

    private void limparFormulario() {
        txtTitulo.clear(); txtAno.clear(); txtGenero.clear();
        txtUrlImagem.clear(); txtLinkAcesso.clear();
        txtCampoEspecifico1.clear(); txtCampoEspecifico2.clear();
    }

    private void exibirAlerta(Alert.AlertType tipo, String titulo, String msg) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(msg);
        alerta.showAndWait();
    }
}