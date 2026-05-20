package view;

import controller.ItemController;
import model.*;
import exception.DadosInvalidosException;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URI;
import java.util.List;
import java.util.Random;

public class TelaPrincipal {

    private ItemController controller;
    private TilePane gradeBlocos;
    private TextField txtBusca;
    private final Random random = new Random();

    private final String[] imgsFilmes = {"https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=500", "https://images.unsplash.com/photo-1517604931442-7e0c8ed2963c?w=500"};
    private final String[] vidsFilmes = {"https://www.youtube.com/watch?v=aqz-KE-bpKQ", "https://www.youtube.com/watch?v=eRsGyueVLvQ"};
    private final String[] imgsAlbuns = {"https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=500", "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=500"};
    private final String[] vidsAlbuns = {"https://www.youtube.com/watch?v=9X8SGu-sOas", "https://www.youtube.com/watch?v=jfKfPfyJRdk"};
    private final String[] imgsLivros = {"https://images.unsplash.com/photo-1512820790803-83ca734da794?w=500", "https://images.unsplash.com/photo-1495640388908-05fa85288e61?w=500"};
    private final String[] docsLivros = {"https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf", "https://www.orimi.com/pdf-test.pdf"};

    public void iniciarTela(Stage palcoPrincipal) {
        controller = new ItemController();
        palcoPrincipal.setTitle("Cofre Cultural - Streaming e Avaliações");

        BorderPane layoutRaiz = new BorderPane();
        layoutRaiz.setPadding(new Insets(20));

        HBox barraSuperior = new HBox(10);
        barraSuperior.setPadding(new Insets(0, 0, 15, 0));
        barraSuperior.setAlignment(Pos.CENTER_LEFT);

        txtBusca = new TextField();
        txtBusca.setPromptText("Buscar por título/gênero...");
        txtBusca.setPrefWidth(220);

        Button btnBuscar = new Button("Buscar");
        btnBuscar.setOnAction(e -> acaoBuscar());

        Button btnRemover = new Button("Remover por Título");
        btnRemover.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        btnRemover.setOnAction(e -> acaoRemover());

        Button btnAdicionar = new Button("+ Nova Mídia");
        btnAdicionar.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        btnAdicionar.setOnAction(e -> abrirPopUpCadastro());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        barraSuperior.getChildren().addAll(txtBusca, btnBuscar, btnRemover, spacer, btnAdicionar);

        gradeBlocos = new TilePane();
        gradeBlocos.setPadding(new Insets(10));
        gradeBlocos.setHgap(20);
        gradeBlocos.setVgap(20);
        gradeBlocos.setStyle("-fx-background-color: #f8f9fa;");

        ScrollPane painelRolagem = new ScrollPane(gradeBlocos);
        painelRolagem.setFitToWidth(true);
        painelRolagem.setStyle("-fx-background-color: transparent;");

        layoutRaiz.setTop(barraSuperior);
        layoutRaiz.setCenter(painelRolagem);

        renderizarGrade(controller.getBiblioteca());

        Scene cena = new Scene(layoutRaiz, 1080, 700);
        palcoPrincipal.setScene(cena);
        palcoPrincipal.show();
    }

    private void renderizarGrade(List<Item> itens) {
        gradeBlocos.getChildren().clear();
        for (Item item : itens) {
            VBox card = new VBox(8);
            card.setAlignment(Pos.CENTER);
            card.setPadding(new Insets(12));
            card.setPrefSize(180, 250);
            card.setStyle("-fx-border-color: #e2e8f0; -fx-border-radius: 8; -fx-background-color: white; -fx-background-radius: 8; -fx-cursor: hand;");

            ImageView capa = new ImageView();
            try {
                Image img = new Image(item.getUrlImagem(), 140, 150, true, true, true);
                capa.setImage(img);
            } catch (Exception e) {
                capa.setImage(new Image("https://i.imgur.com/bCEq7U9.png", 140, 150, true, true));
            }

            Label lblTitulo = new Label(item.getTitulo());
            lblTitulo.setStyle("-fx-font-weight: bold; -fx-text-alignment: center;");
            lblTitulo.setWrapText(true);

            Label lblTipo = new Label(item.getTipoMidia() + (item.getNota() > 0 ? " ⭐ " + item.getNota() : ""));
            lblTipo.setStyle("-fx-font-size: 11px; -fx-text-fill: #718096;");

            card.getChildren().addAll(capa, lblTitulo, lblTipo);
            card.setOnMouseClicked(e -> abrirPopUpVisualizacao(item));

            gradeBlocos.getChildren().add(card);
        }
    }

    private void abrirPopUpVisualizacao(Item item) {
        Stage popUp = new Stage();
        popUp.initModality(Modality.APPLICATION_MODAL);
        popUp.setTitle("Informações do Item");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_LEFT);

        Label lblItemTit = new Label(item.getTitulo());
        lblItemTit.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        VBox infoBox = new VBox(6);
        infoBox.getChildren().addAll(
            new Label("📅 Ano de Lançamento: " + item.getAnoLancamento()),
            new Label("🏷️ Gênero: " + item.getGenero()),
            new Label("📌 Tipo: " + item.getTipoMidia()),
            new Label("📝 Detalhes: " + item.getDetalhesSpecificos())
        );
        infoBox.setStyle("-fx-background-color: #f1f5f9; -fx-padding: 10; -fx-background-radius: 5;");

        VBox avaliacaoBox = new VBox(5);
        avaliacaoBox.setStyle("-fx-border-color: #cbd5e1; -fx-padding: 8; -fx-border-radius: 5;");
        Label lblNota = new Label("Sua Nota: " + (item.getNota() == 0 ? "Não avaliado" : item.getNota() + "/5 ⭐"));
        lblNota.setStyle("-fx-font-weight: bold;");
        Label lblResenha = new Label("Resenha: " + item.getResenha());
        lblResenha.setWrapText(true);
        avaliacaoBox.getChildren().addAll(lblNota, lblResenha);

        Hyperlink linkAcesso = new Hyperlink("👉 Para abrir ou reproduzir o conteúdo, clique aqui!");
        linkAcesso.setStyle("-fx-font-weight: bold; -fx-text-fill: #3498db;");
        linkAcesso.setOnAction(e -> {
            try {
                java.awt.Desktop.getDesktop().browse(new URI(item.getLinkAcesso()));
            } catch (Exception ex) {
                Alert a = new Alert(Alert.AlertType.ERROR, "Link inválido ou indisponível.");
                a.showAndWait();
            }
        });

        HBox botoesAcao = new HBox(10);
        Button btnEditar = new Button("✏️ Editar Cadastro");
        btnEditar.setOnAction(e -> {
            popUp.close();
            abrirPopUpEdicao(item);
        });

        Button btnAvaliar = new Button("➕ Adicionar Avaliação");
        btnAvaliar.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
        btnAvaliar.setOnAction(e -> {
            popUp.close();
            abrirPopUpAvaliar(item);
        });

        botoesAcao.getChildren().addAll(btnEditar, btnAvaliar);
        root.getChildren().addAll(lblItemTit, infoBox, avaliacaoBox, linkAcesso, botoesAcao);

        popUp.setScene(new Scene(root, 450, 420));
        popUp.showAndWait();
    }

    private void abrirPopUpEdicao(Item item) {
        Stage popUpEdit = new Stage();
        popUpEdit.initModality(Modality.APPLICATION_MODAL);
        popUpEdit.setTitle("Editar: " + item.getTitulo());

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10); grid.setVgap(10);

        TextField txtT = new TextField(item.getTitulo());
        TextField txtA = new TextField(String.valueOf(item.getAnoLancamento()));
        TextField txtG = new TextField(item.getGenero());
        TextField txtI = new TextField(item.getUrlImagem());
        TextField txtL = new TextField(item.getLinkAcesso());
        TextField txtE = new TextField();

        if (item instanceof Album) txtE.setText(((Album) item).getBanda());
        else if (item instanceof Filme) txtE.setText(((Filme) item).getDiretor());
        else if (item instanceof Livro) txtE.setText(((Livro) item).getAutor());

        grid.add(new Label("Título:"), 0, 0); grid.add(txtT, 1, 0);
        grid.add(new Label("Ano:"), 0, 1); grid.add(txtA, 1, 1);
        grid.add(new Label("Gênero:"), 0, 2); grid.add(txtG, 1, 2);
        grid.add(new Label("URL Capa:"), 0, 3); grid.add(txtI, 1, 3);
        grid.add(new Label("URL Mídia:"), 0, 4); grid.add(txtL, 1, 4);
        grid.add(new Label("Info Específica:"), 0, 5); grid.add(txtE, 1, 5);

        Button btnSalvar = new Button("Confirmar Alterações");
        btnSalvar.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        btnSalvar.setOnAction(e -> {
            try {
                item.setTitulo(txtT.getText());
                item.setAnoLancamento(Integer.parseInt(txtA.getText().trim()));
                item.setGenero(txtG.getText());
                item.setUrlImagem(txtI.getText());
                item.setLinkAcesso(txtL.getText());

                if (item instanceof Album) ((Album) item).setBanda(txtE.getText());
                else if (item instanceof Filme) ((Filme) item).setDiretor(txtE.getText());
                else if (item instanceof Livro) ((Livro) item).setAutor(txtE.getText());

                controller.atualizarBiblioteca();
                renderizarGrade(controller.getBiblioteca());
                popUpEdit.close();
            } catch (Exception ex) {
                System.out.println("Erro ao salvar edição.");
            }
        });

        VBox v = new VBox(15, grid, btnSalvar);
        v.setPadding(new Insets(15));
        popUpEdit.setScene(new Scene(v, 400, 350));
        popUpEdit.showAndWait();
    }

    private void abrirPopUpAvaliar(Item item) {
        Stage popUpAv = new Stage();
        popUpAv.initModality(Modality.APPLICATION_MODAL);
        popUpAv.setTitle("Avaliar Mídia");

        VBox box = new VBox(10);
        box.setPadding(new Insets(20));

        Label info = new Label("Dê sua nota para: " + item.getTitulo());
        info.setStyle("-fx-font-weight: bold;");

        ComboBox<Integer> cbNota = new ComboBox<>(FXCollections.observableArrayList(1, 2, 3, 4, 5));
        cbNota.setValue(5);

        TextArea txtResenha = new TextArea();
        txtResenha.setPromptText("Escreva um comentário sobre o que achou...");
        txtResenha.setPrefRowCount(4);

        Button btnConfirmar = new Button("Salvar Avaliação");
        btnConfirmar.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
        btnConfirmar.setOnAction(e -> {
            try {
                item.avaliar(cbNota.getValue(), txtResenha.getText());
                controller.atualizarBiblioteca();
                renderizarGrade(controller.getBiblioteca());
                popUpAv.close();
            } catch (Exception ex) {
                System.out.println("Erro ao salvar avaliação.");
            }
        });

        box.getChildren().addAll(info, new Label("Nota (1 a 5 estrelas):"), cbNota, new Label("Comentário:"), txtResenha, btnConfirmar);
        popUpAv.setScene(new Scene(box, 350, 320));
        popUpAv.showAndWait();
    }

    private void abrirPopUpCadastro() {
        Stage popUpCad = new Stage();
        popUpCad.initModality(Modality.APPLICATION_MODAL);
        popUpCad.setTitle("Novo Cadastro");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10); grid.setVgap(10);

        ComboBox<String> cbTipo = new ComboBox<>(FXCollections.observableArrayList("Álbum", "Filme", "Livro"));
        cbTipo.setValue("Álbum");

        TextField txtT = new TextField();
        TextField txtA = new TextField();
        TextField txtG = new TextField();
        TextField txtI = new TextField(); txtI.setPromptText("Opcional (Sorteio Automático)");
        TextField txtL = new TextField(); txtL.setPromptText("Opcional (Sorteio Automático)");
        TextField txtE = new TextField();

        grid.add(new Label("Categoria:"), 0, 0); grid.add(cbTipo, 1, 0);
        grid.add(new Label("Título * :"), 0, 1); grid.add(txtT, 1, 1);
        grid.add(new Label("Ano:"), 0, 2); grid.add(txtA, 1, 2);
        grid.add(new Label("Gênero:"), 0, 3); grid.add(txtG, 1, 3);
        grid.add(new Label("Link Imagem:"), 0, 4); grid.add(txtI, 1, 4);
        grid.add(new Label("Link Mídia/PDF:"), 0, 5); grid.add(txtL, 1, 5);
        grid.add(new Label("Info Adicional:"), 0, 6); grid.add(txtE, 1, 6);

        Button btnSalvar = new Button("Salvar");
        btnSalvar.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        btnSalvar.setOnAction(e -> {
            try {
                String tipo = cbTipo.getValue();
                int ano = txtA.getText().trim().isEmpty() ? 0 : Integer.parseInt(txtA.getText().trim());
                String imgFinal = txtI.getText().trim();
                String linkFinal = txtL.getText().trim();

                Item novo = null;
                if ("Álbum".equals(tipo)) {
                    if(imgFinal.isEmpty()) imgFinal = imgsAlbuns[random.nextInt(imgsAlbuns.length)];
                    if(linkFinal.isEmpty()) linkFinal = vidsAlbuns[random.nextInt(vidsAlbuns.length)];
                    novo = new Album(txtT.getText(), ano, txtG.getText(), imgFinal, linkFinal, txtE.getText(), "Faixa Padrão");
                } else if ("Filme".equals(tipo)) {
                    if(imgFinal.isEmpty()) imgFinal = imgsFilmes[random.nextInt(imgsFilmes.length)];
                    if(linkFinal.isEmpty()) linkFinal = vidsFilmes[random.nextInt(vidsFilmes.length)];
                    novo = new Filme(txtT.getText(), ano, txtG.getText(), imgFinal, linkFinal, txtE.getText());
                } else if ("Livro".equals(tipo)) {
                    if(imgFinal.isEmpty()) imgFinal = imgsLivros[random.nextInt(imgsLivros.length)];
                    if(linkFinal.isEmpty()) linkFinal = docsLivros[random.nextInt(docsLivros.length)];
                    novo = new Livro(txtT.getText(), ano, txtG.getText(), imgFinal, linkFinal, txtE.getText());
                }

                controller.adicionarItem(novo);
                renderizarGrade(controller.getBiblioteca());
                popUpCad.close();
            } catch (DadosInvalidosException ex) {
                Alert a = new Alert(Alert.AlertType.WARNING, ex.getMessage());
                a.showAndWait();
            } catch (Exception ex) {
                System.out.println("Erro no cadastro.");
            }
        });

        VBox b = new VBox(15, grid, btnSalvar);
        b.setPadding(new Insets(15));
        popUpCad.setScene(new Scene(b, 380, 380));
        popUpCad.showAndWait();
    }

    private void acaoBuscar() {
        String termo = txtBusca.getText();
        renderizarGrade(controller.buscarPorPalavraChave(termo));
    }

    private void acaoRemover() {
        String termo = txtBusca.getText();
        if (termo == null || termo.trim().isEmpty()) {
            Alert a = new Alert(Alert.AlertType.WARNING, "Digite o título na barra de busca para remover.");
            a.showAndWait();
            return;
        }
        try {
            controller.removerItem(termo);
            txtBusca.clear();
            renderizarGrade(controller.getBiblioteca());
        } catch (Exception e) {
            System.out.println("Erro ao remover.");
        }
    }
}