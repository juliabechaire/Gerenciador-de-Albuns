package view;

import controller.ArquivoController;
import model.*;
import exception.DadosInvalidosException;
import exception.ArquivoNaoEncontradoException;
import view.TelaMusica;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class TelaPrincipal extends Application {

    private ArquivoController cerebro;
    private VBox containerCentral;
    private TextField txtBusca;
    private Stage palco;
    private BorderPane layoutRaiz;

    // Filtro atual selecionado no MenuButton
    private String filtroAtual = "Todos";

    private final Random random = new Random();

    private final String[] imgsFilmes = {
        "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=500",
        "https://images.unsplash.com/photo-1517604931442-7e0c8ed2963c?w=500"
    };
    private final String[] vidsFilmes = {
        "https://www.youtube.com/watch?v=aqz-KE-bpKQ",
        "https://www.youtube.com/watch?v=eRsGyueVLvQ"
    };
    private final String[] imgsAlbuns = {
        "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=500",
        "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=500"
    };
    private final String[] vidsAlbuns = {
        "https://www.youtube.com/watch?v=9X8SGu-sOas",
        "https://www.youtube.com/watch?v=jfKfPfyJRdk"
    };
    private final String[] imgsLivros = {
        "https://images.unsplash.com/photo-1512820790803-83ca734da794?w=500",
        "https://images.unsplash.com/photo-1495640388908-05fa85288e61?w=500"
    };
    private final String[] docsLivros = {
        "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf",
        "https://www.orimi.com/pdf-test.pdf"
    };

    private static final String IMG_PLACEHOLDER = "https://placehold.co/140x160?text=Sem+Capa";

    // Paleta de cores centralizada
    private static final String COR_FUNDO        = "#1a1a2e";
    private static final String COR_SUPERFICIE   = "#16213e";
    private static final String COR_CARD         = "#0f3460";
    private static final String COR_ACENTO       = "#e94560";
    private static final String COR_ACENTO2      = "#533483";
    private static final String COR_TEXTO        = "#eaeaea";
    private static final String COR_TEXTO_SUAVE  = "#a0aec0";
    private static final String COR_DOURADO      = "#f6c90e";

    @Override
    public void start(Stage palcoPrincipal) {
        this.palco = palcoPrincipal;
        this.cerebro = new ArquivoController();
        palcoPrincipal.setTitle("Cofre Cultural v1.0");

        layoutRaiz = new BorderPane();
        layoutRaiz.setPadding(new Insets(14));
        layoutRaiz.setStyle("-fx-background-color: " + COR_FUNDO + ";");

        layoutRaiz.setTop(criarBarraSuperior());

        containerCentral = new VBox(20);
        containerCentral.setPadding(new Insets(10, 0, 10, 0));

        ScrollPane painelRolagem = new ScrollPane(containerCentral);
        painelRolagem.setFitToWidth(true);
        painelRolagem.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        layoutRaiz.setCenter(painelRolagem);

        layoutRaiz.setBottom(criarBarraInferior());

        renderizarBiblioteca();

        Scene cena = new Scene(layoutRaiz, 960, 640);
        palcoPrincipal.setScene(cena);
        palcoPrincipal.show();
    }

    // =====================================================================
    //  VOLTAR PARA BIBLIOTECA
    // =====================================================================
    private void voltarParaBiblioteca() {
        containerCentral = new VBox(20);
        containerCentral.setPadding(new Insets(10, 0, 10, 0));

        ScrollPane painelRolagem = new ScrollPane(containerCentral);
        painelRolagem.setFitToWidth(true);
        painelRolagem.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        layoutRaiz.setCenter(painelRolagem);
        renderizarBiblioteca();
    }

    private void renderizarBiblioteca() {
        List<Arquivo> lista = cerebro.getBiblioteca();
        switch (filtroAtual) {
            case "Filmes":
                renderizarTodasAsSecoes(lista.stream().filter(a -> a instanceof Filme).collect(Collectors.toList()));
                break;
            case "Álbuns":
                renderizarTodasAsSecoes(lista.stream().filter(a -> a instanceof Album).collect(Collectors.toList()));
                break;
            case "Livros":
                renderizarTodasAsSecoes(lista.stream().filter(a -> a instanceof Livro).collect(Collectors.toList()));
                break;
            default:
                renderizarTodasAsSecoes(lista);
        }
    }

    // =====================================================================
    //  BARRA SUPERIOR
    // =====================================================================
    private VBox criarBarraSuperior() {
        VBox painelTopo = new VBox(10);
        painelTopo.setPadding(new Insets(0, 0, 12, 0));

        // Título do app
        Label lblApp = new Label("🎬 Cofre Cultural");
        lblApp.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + COR_ACENTO + ";");

        // Linha de busca
        HBox linhaBusca = new HBox(8);
        linhaBusca.setAlignment(Pos.CENTER_LEFT);

        txtBusca = new TextField();
        txtBusca.setPromptText("Buscar pelo título...");
        txtBusca.setPrefWidth(260);
        txtBusca.setStyle("-fx-background-color: " + COR_SUPERFICIE + "; -fx-text-fill: " + COR_TEXTO
                        + "; -fx-prompt-text-fill: " + COR_TEXTO_SUAVE + "; -fx-border-color: " + COR_ACENTO2
                        + "; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 6 10 6 10;");
        txtBusca.setOnAction(e -> acaoBuscar());

        Button btnBuscar = estilizarBotao("🔍 Buscar", COR_ACENTO2, COR_TEXTO);
        btnBuscar.setOnAction(e -> acaoBuscar());

        // ── MenuButton de filtro por categoria (substituí os 4 botões) ──
        MenuButton menuFiltro = new MenuButton("🌐 Todos ▾");
        menuFiltro.setStyle("-fx-background-color: " + COR_CARD + "; -fx-text-fill: " + COR_TEXTO
                          + "; -fx-border-color: " + COR_ACENTO2 + "; -fx-border-radius: 6;"
                          + "-fx-background-radius: 6; -fx-font-weight: bold; -fx-padding: 6 12 6 12;");

        MenuItem miTodos  = new MenuItem("🌐 Todos");
        MenuItem miFilmes = new MenuItem("🎬 Filmes");
        MenuItem miAlbuns = new MenuItem("🎵 Álbuns");
        MenuItem miLivros = new MenuItem("📚 Livros");

        miTodos.setOnAction(e  -> { filtroAtual = "Todos";  menuFiltro.setText("🌐 Todos ▾");  txtBusca.clear(); voltarParaBiblioteca(); });
        miFilmes.setOnAction(e -> { filtroAtual = "Filmes"; menuFiltro.setText("🎬 Filmes ▾"); voltarParaBiblioteca(); });
        miAlbuns.setOnAction(e -> { filtroAtual = "Álbuns"; menuFiltro.setText("🎵 Álbuns ▾"); voltarParaBiblioteca(); });
        miLivros.setOnAction(e -> { filtroAtual = "Livros"; menuFiltro.setText("📚 Livros ▾"); voltarParaBiblioteca(); });

        menuFiltro.getItems().addAll(miTodos, miFilmes, miAlbuns, miLivros);

        // Botões de ação
        Button btnAvaliados = estilizarBotao("⭐ Avaliados", COR_DOURADO, "#1a1a2e");
        Button btnEditar    = estilizarBotao("✏️ Editar",   "#3498db",   COR_TEXTO);
        Button btnRemover   = estilizarBotao("🗑️ Remover",  COR_ACENTO,  COR_TEXTO);

        btnAvaliados.setOnAction(e -> {
            List<Arquivo> avaliados = cerebro.getBiblioteca().stream()
                .filter(a -> a.getNota() > 0)
                .collect(Collectors.toList());
            containerCentral.getChildren().clear();
            if (avaliados.isEmpty()) {
                Label msg = new Label("Nenhum item avaliado ainda. Abra um item e clique em ⭐ Avaliar.");
                msg.setStyle("-fx-font-size: 14px; -fx-text-fill: " + COR_TEXTO_SUAVE + "; -fx-padding: 20;");
                containerCentral.getChildren().add(msg);
            } else {
                mostrarPainelAvaliados(avaliados);
            }
        });

        btnEditar.setOnAction(e  -> abrirFluxoEditarPorBusca());
        btnRemover.setOnAction(e -> abrirFluxoRemoverPorBusca());

        // Botão que abre o Módulo Musical (TelaMusica) passando a cena atual para poder voltar
        Button btnMusica = estilizarBotao("🎵 Módulo Musical", "#7c3aed", COR_TEXTO);
        btnMusica.setOnAction(e -> {
            Scene cenaAtual = palco.getScene();
            new TelaMusica(palco, cenaAtual).mostrar();
        });

        linhaBusca.getChildren().addAll(menuFiltro, txtBusca, btnBuscar, btnAvaliados, btnEditar, btnRemover, btnMusica);

        painelTopo.getChildren().addAll(lblApp, linhaBusca);
        return painelTopo;
    }

    // =====================================================================
    //  BARRA INFERIOR
    // =====================================================================
    private HBox criarBarraInferior() {
        HBox barra = new HBox();
        barra.setPadding(new Insets(10, 0, 0, 0));

        Button btnAdicionar = estilizarBotao("➕ Adicionar Nova Mídia", "#27ae60", COR_TEXTO);
        btnAdicionar.setOnAction(e -> abrirPopUpCadastro());
        barra.getChildren().add(btnAdicionar);
        return barra;
    }

    // =====================================================================
    //  RENDERIZAÇÃO
    // =====================================================================
    private void renderizarTodasAsSecoes(List<Arquivo> lista) {
        containerCentral.getChildren().clear();

        if (lista.isEmpty()) {
            Label msg = new Label("Nenhum arquivo encontrado.");
            msg.setStyle("-fx-font-size: 14px; -fx-text-fill: " + COR_TEXTO_SUAVE + "; -fx-padding: 20;");
            containerCentral.getChildren().add(msg);
            return;
        }

        List<Arquivo> albuns = lista.stream().filter(a -> a instanceof Album).collect(Collectors.toList());
        List<Arquivo> filmes = lista.stream().filter(a -> a instanceof Filme).collect(Collectors.toList());
        List<Arquivo> livros = lista.stream().filter(a -> a instanceof Livro).collect(Collectors.toList());

        if (!albuns.isEmpty()) containerCentral.getChildren().add(criarSecaoHorizontal("🎵 Álbuns", albuns));
        if (!filmes.isEmpty()) containerCentral.getChildren().add(criarSecaoHorizontal("🎬 Filmes", filmes));
        if (!livros.isEmpty()) containerCentral.getChildren().add(criarSecaoHorizontal("📚 Livros", livros));
    }

    private VBox criarSecaoHorizontal(String tituloSecao, List<Arquivo> itens) {
        VBox secao = new VBox(6);

        Label lbl = new Label(tituloSecao);
        lbl.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + COR_TEXTO + ";");

        HBox estrutura = new HBox(5);
        estrutura.setAlignment(Pos.CENTER);

        HBox containerCards = new HBox(12);
        containerCards.setPadding(new Insets(5));

        for (Arquivo a : itens) {
            containerCards.getChildren().add(criarCardMiniatura(a));
        }

        ScrollPane scroll = new ScrollPane(containerCards);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setFitToHeight(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        HBox.setHgrow(scroll, Priority.ALWAYS);

        Button btnEsq = estilizarBotao("◀", COR_CARD, COR_TEXTO);
        Button btnDir = estilizarBotao("▶", COR_CARD, COR_TEXTO);
        btnEsq.setOnAction(e -> scroll.setHvalue(Math.max(0, scroll.getHvalue() - 0.25)));
        btnDir.setOnAction(e -> scroll.setHvalue(Math.min(1, scroll.getHvalue() + 0.25)));

        estrutura.getChildren().addAll(btnEsq, scroll, btnDir);
        secao.getChildren().addAll(lbl, estrutura);
        return secao;
    }

    // =====================================================================
    //  CARD MINIATURA — só imagem + título, sem espaço em branco
    // =====================================================================
    private VBox criarCardMiniatura(Arquivo arquivo) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(6));

        // Imagem
        ImageView capa = new ImageView();
        capa.setFitWidth(120);
        capa.setFitHeight(155);
        capa.setPreserveRatio(false); // preenche o espaço sem espaço em branco
        carregarImagem(capa, arquivo.getImagem(), 120, 155);

        // Título — wrapping limitado a 2 linhas
        Label lblTitulo = new Label(arquivo.getNome() != null ? arquivo.getNome() : "Sem título");
        lblTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-alignment: center;"
                         + "-fx-text-fill: " + COR_TEXTO + ";");
        lblTitulo.setWrapText(true);
        lblTitulo.setMaxWidth(120);
        lblTitulo.setAlignment(Pos.CENTER);

        card.getChildren().addAll(capa, lblTitulo);

        // Tamanho ajustado ao conteúdo (sem altura fixa que cria espaço vazio)
        card.setPrefWidth(136);
        card.setMaxWidth(136);
        card.setStyle("-fx-border-color: " + COR_ACENTO2 + "; -fx-border-radius: 8;"
                    + "-fx-background-color: " + COR_CARD + "; -fx-background-radius: 8; -fx-cursor: hand;");

        card.setOnMouseClicked(ev -> mostrarPainelDetalhes(arquivo));
        card.setOnMouseEntered(ev -> card.setStyle(
            "-fx-border-color: " + COR_ACENTO + "; -fx-border-radius: 8;"
            + "-fx-background-color: " + COR_ACENTO2 + "; -fx-background-radius: 8; -fx-cursor: hand;"));
        card.setOnMouseExited(ev -> card.setStyle(
            "-fx-border-color: " + COR_ACENTO2 + "; -fx-border-radius: 8;"
            + "-fx-background-color: " + COR_CARD + "; -fx-background-radius: 8; -fx-cursor: hand;"));

        return card;
    }

    // =====================================================================
    //  PAINEL DE DETALHES — usa exibirInformacoes() de cada classe
    // =====================================================================
    private void mostrarPainelDetalhes(Arquivo arquivo) {
        BorderPane painelDetalhes = new BorderPane();
        painelDetalhes.setPadding(new Insets(20));
        painelDetalhes.setStyle("-fx-background-color: " + COR_FUNDO + ";");

        Button btnVoltar = estilizarBotao("⬅ Voltar", COR_CARD, COR_TEXTO);
        btnVoltar.setOnAction(e -> voltarParaBiblioteca());
        painelDetalhes.setTop(btnVoltar);
        BorderPane.setMargin(btnVoltar, new Insets(0, 0, 14, 0));

        VBox conteudo = new VBox(16);
        conteudo.setPadding(new Insets(6, 0, 0, 0));

        // Título
        Label lblNome = new Label(safe(arquivo.getNome(), "Sem título"));
        lblNome.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + COR_TEXTO + ";");
        lblNome.setWrapText(true);

        // Linha: imagem + informações (usando exibirInformacoes() da classe)
        HBox linhaInfos = new HBox(22);
        linhaInfos.setAlignment(Pos.TOP_LEFT);

        ImageView grandeCapa = new ImageView();
        grandeCapa.setFitWidth(160);
        grandeCapa.setFitHeight(210);
        grandeCapa.setPreserveRatio(false);
        carregarImagem(grandeCapa, arquivo.getImagem(), 160, 210);

        // Bloco de informações — lê direto de exibirInformacoes()
        VBox blocoInfos = new VBox(6);
        blocoInfos.setStyle("-fx-background-color: " + COR_SUPERFICIE + "; -fx-padding: 14;"
                          + "-fx-background-radius: 10;");
        blocoInfos.setPrefWidth(480);

        // Quebra o texto de exibirInformacoes() em linhas e cria um Label por linha
        String[] linhas = arquivo.exibirInformacoes().split("\n");
        for (String linha : linhas) {
            Label l = new Label(linha);
            l.setStyle("-fx-font-size: 13px; -fx-text-fill: " + COR_TEXTO + ";");
            l.setWrapText(true);
            blocoInfos.getChildren().add(l);
        }

        HBox.setHgrow(blocoInfos, Priority.ALWAYS);
        linhaInfos.getChildren().addAll(grandeCapa, blocoInfos);

        // Link
        Hyperlink link = new Hyperlink("🚀 Abrir / Executar mídia");
        link.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + COR_ACENTO + ";");
        link.setOnAction(e -> {
            String url = arquivo.getLink();
            if (url == null || url.trim().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Nenhum link cadastrado.").showAndWait();
                return;
            }
            try {
                java.awt.Desktop.getDesktop().browse(new java.net.URI(url.trim()));
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Não foi possível abrir o link.").showAndWait();
            }
        });

        // Botão único de avaliar / revisar
        int nota = arquivo.getNota();
        Button btnAvaliar = estilizarBotao(
            nota > 0 ? "🔄 Revisar Avaliação" : "⭐ Avaliar",
            COR_DOURADO, "#1a1a2e");
        btnAvaliar.setOnAction(e -> {
            abrirPopUpAvaliar(arquivo);
            mostrarPainelDetalhes(arquivo);
        });

        conteudo.getChildren().addAll(lblNome, linhaInfos, link, btnAvaliar);

        ScrollPane scroll = new ScrollPane(conteudo);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        painelDetalhes.setCenter(scroll);
        layoutRaiz.setCenter(painelDetalhes);
    }

    // =====================================================================
    //  PAINEL DE AVALIADOS — lista cards e ao clicar mostra nota + crítica
    // =====================================================================
    private void mostrarPainelAvaliados(List<Arquivo> avaliados) {
        BorderPane painel = new BorderPane();
        painel.setPadding(new Insets(20));
        painel.setStyle("-fx-background-color: " + COR_FUNDO + ";");

        Button btnVoltar = estilizarBotao("⬅ Voltar", COR_CARD, COR_TEXTO);
        btnVoltar.setOnAction(e -> voltarParaBiblioteca());
        painel.setTop(btnVoltar);
        BorderPane.setMargin(btnVoltar, new Insets(0, 0, 14, 0));

        Label titulo = new Label("⭐ Itens Avaliados");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + COR_DOURADO + ";");

        // Grid de cards avaliados (mesma aparência dos cards da biblioteca)
        HBox gridCards = new HBox(14);
        gridCards.setPadding(new Insets(10, 0, 0, 0));

        for (Arquivo a : avaliados) {
            VBox card = criarCardMiniatura(a);
            // Sobrescreve o clique: abre a view de avaliação em vez de detalhes
            card.setOnMouseClicked(ev -> mostrarDetalheAvaliacao(a));
            card.setOnMouseEntered(ev -> card.setStyle(
                "-fx-border-color: " + COR_DOURADO + "; -fx-border-radius: 8;"
                + "-fx-background-color: " + COR_ACENTO2 + "; -fx-background-radius: 8; -fx-cursor: hand;"));
            card.setOnMouseExited(ev -> card.setStyle(
                "-fx-border-color: " + COR_ACENTO2 + "; -fx-border-radius: 8;"
                + "-fx-background-color: " + COR_CARD + "; -fx-background-radius: 8; -fx-cursor: hand;"));
            gridCards.getChildren().add(card);
        }

        ScrollPane scroll = new ScrollPane(gridCards);
        scroll.setFitToHeight(true);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        VBox centro = new VBox(12, titulo, scroll);
        painel.setCenter(centro);
        layoutRaiz.setCenter(painel);
    }

    // Detalhe de avaliação: título, imagem, nota em destaque, crítica
    private void mostrarDetalheAvaliacao(Arquivo arquivo) {
        BorderPane painel = new BorderPane();
        painel.setPadding(new Insets(24));
        painel.setStyle("-fx-background-color: " + COR_FUNDO + ";");

        Button btnVoltar = estilizarBotao("⬅ Voltar para Avaliados", COR_CARD, COR_TEXTO);
        btnVoltar.setOnAction(e -> {
            List<Arquivo> avaliados = cerebro.getBiblioteca().stream()
                .filter(a -> a.getNota() > 0).collect(Collectors.toList());
            mostrarPainelAvaliados(avaliados);
        });
        painel.setTop(btnVoltar);
        BorderPane.setMargin(btnVoltar, new Insets(0, 0, 16, 0));

        // Linha: imagem + lado direito
        HBox linha = new HBox(24);
        linha.setAlignment(Pos.TOP_LEFT);

        ImageView capa = new ImageView();
        capa.setFitWidth(150);
        capa.setFitHeight(195);
        capa.setPreserveRatio(false);
        carregarImagem(capa, arquivo.getImagem(), 150, 195);

        VBox ladoDireito = new VBox(12);
        ladoDireito.setAlignment(Pos.TOP_LEFT);

        Label lblTitulo = new Label(safe(arquivo.getNome(), "Sem título"));
        lblTitulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + COR_TEXTO + ";");
        lblTitulo.setWrapText(true);

        // Nota em destaque: círculo grande com o número
        int nota = arquivo.getNota();
        StackPane circleNota = criarCirculoNota(nota);

        Label lblNotaLabel = new Label("Nota");
        lblNotaLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + COR_TEXTO_SUAVE + ";");

        VBox blocoNota = new VBox(4, circleNota, lblNotaLabel);
        blocoNota.setAlignment(Pos.CENTER);

        // Crítica / Comentário
        String comentario = safe(arquivo.getComentario(), "Nenhum comentário registrado.");
        Label lblCriticaTitulo = new Label("📝 Crítica:");
        lblCriticaTitulo.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + COR_TEXTO_SUAVE + ";");

        Label lblCritica = new Label(comentario);
        lblCritica.setStyle("-fx-font-size: 13px; -fx-font-style: italic; -fx-text-fill: " + COR_TEXTO + ";"
                          + "-fx-background-color: " + COR_SUPERFICIE + "; -fx-padding: 12;"
                          + "-fx-background-radius: 8;");
        lblCritica.setWrapText(true);
        lblCritica.setMaxWidth(420);

        Button btnRevisar = estilizarBotao("🔄 Revisar Avaliação", COR_DOURADO, "#1a1a2e");
        btnRevisar.setOnAction(e -> {
            abrirPopUpAvaliar(arquivo);
            mostrarDetalheAvaliacao(arquivo);
        });

        ladoDireito.getChildren().addAll(lblTitulo, blocoNota, lblCriticaTitulo, lblCritica, btnRevisar);
        linha.getChildren().addAll(capa, ladoDireito);

        ScrollPane scroll = new ScrollPane(linha);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        painel.setCenter(scroll);
        layoutRaiz.setCenter(painel);
    }

    // Círculo com número da nota em destaque
    private StackPane criarCirculoNota(int nota) {
        Circle circulo = new Circle(36);
        circulo.setFill(Color.web(COR_DOURADO));

        Text txtNota = new Text(String.valueOf(nota));
        txtNota.setFont(Font.font("System", FontWeight.BOLD, 28));
        txtNota.setFill(Color.web("#1a1a2e"));

        StackPane sp = new StackPane(circulo, txtNota);
        sp.setPrefSize(72, 72);
        return sp;
    }

    // =====================================================================
    //  FLUXO REMOVER POR BUSCA
    // =====================================================================
    private void abrirFluxoRemoverPorBusca() {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Remover Mídia");

        VBox raiz = new VBox(10);
        raiz.setPadding(new Insets(18));
        raiz.setStyle("-fx-background-color: " + COR_SUPERFICIE + ";");

        Label instrucao = new Label("Digite parte do título para buscar:");
        instrucao.setStyle("-fx-font-weight: bold; -fx-text-fill: " + COR_TEXTO + ";");

        TextField campoTitulo = new TextField();
        campoTitulo.setPromptText("Ex: Inception, The Beatles...");
        estilizarCampo(campoTitulo);

        Button btnBuscar = estilizarBotao("🔍 Buscar", COR_ACENTO2, COR_TEXTO);

        VBox listaResultados = new VBox(6);
        Label lblStatus = new Label("Aguardando busca...");
        lblStatus.setStyle("-fx-text-fill: " + COR_TEXTO_SUAVE + ";");
        listaResultados.getChildren().add(lblStatus);

        btnBuscar.setOnAction(e -> {
            String termo = campoTitulo.getText().trim();
            if (termo.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Digite um título para buscar.").showAndWait();
                return;
            }
            try {
                List<Arquivo> encontrados = cerebro.buscar_palavra_chave(termo);
                listaResultados.getChildren().clear();
                listaResultados.getChildren().add(labelPopup("Clique no item que deseja remover:"));
                for (Arquivo a : encontrados) {
                    String icone = a instanceof Album ? "🎵" : a instanceof Filme ? "🎬" : "📚";
                    Button btnItem = new Button(icone + "  " + a.getNome());
                    btnItem.setMaxWidth(Double.MAX_VALUE);
                    btnItem.setStyle("-fx-background-color: " + COR_CARD + "; -fx-text-fill: " + COR_TEXTO
                                   + "; -fx-border-color: " + COR_ACENTO2 + "; -fx-border-radius: 6;"
                                   + "-fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 8;");
                    btnItem.setOnAction(ev -> {
                        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                        confirm.setTitle("Confirmar Remoção");
                        confirm.setHeaderText("Remover \"" + a.getNome() + "\"?");
                        confirm.setContentText("Esta ação não pode ser desfeita.");
                        confirm.showAndWait().ifPresent(resp -> {
                            if (resp == ButtonType.OK) {
                                try {
                                    cerebro.remover_busca(a.getNome());
                                    voltarParaBiblioteca();
                                    popup.close();
                                } catch (ArquivoNaoEncontradoException exNF) {
                                    new Alert(Alert.AlertType.WARNING, exNF.getMessage()).showAndWait();
                                } catch (IOException ex) {
                                    new Alert(Alert.AlertType.ERROR, "Erro ao remover o arquivo.").showAndWait();
                                }
                            }
                        });
                    });
                    listaResultados.getChildren().add(btnItem);
                }
            } catch (ArquivoNaoEncontradoException exNF) {
                listaResultados.getChildren().clear();
                listaResultados.getChildren().add(labelPopup("❌ " + exNF.getMessage()));
            }
        });

        campoTitulo.setOnAction(e -> btnBuscar.fire());

        ScrollPane scrollLista = new ScrollPane(listaResultados);
        scrollLista.setFitToWidth(true);
        scrollLista.setPrefHeight(200);
        scrollLista.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        raiz.getChildren().addAll(instrucao, campoTitulo, btnBuscar, new Separator(), scrollLista);
        popup.setScene(new Scene(raiz, 400, 380));
        popup.showAndWait();
    }

    // =====================================================================
    //  FLUXO EDITAR POR BUSCA
    // =====================================================================
    private void abrirFluxoEditarPorBusca() {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Editar Mídia — Busca");

        VBox raiz = new VBox(10);
        raiz.setPadding(new Insets(18));
        raiz.setStyle("-fx-background-color: " + COR_SUPERFICIE + ";");

        Label instrucao = new Label("Digite parte do título para buscar:");
        instrucao.setStyle("-fx-font-weight: bold; -fx-text-fill: " + COR_TEXTO + ";");

        TextField campoTitulo = new TextField();
        campoTitulo.setPromptText("Ex: Inception, The Beatles...");
        estilizarCampo(campoTitulo);

        Button btnBuscar = estilizarBotao("🔍 Buscar", COR_ACENTO2, COR_TEXTO);

        VBox listaResultados = new VBox(6);
        listaResultados.getChildren().add(labelPopup("Aguardando busca..."));

        btnBuscar.setOnAction(e -> {
            String termo = campoTitulo.getText().trim();
            if (termo.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Digite um título para buscar.").showAndWait();
                return;
            }
            try {
                List<Arquivo> encontrados = cerebro.buscar_palavra_chave(termo);
                listaResultados.getChildren().clear();
                listaResultados.getChildren().add(labelPopup("Clique no item que deseja editar:"));
                for (Arquivo a : encontrados) {
                    String icone = a instanceof Album ? "🎵" : a instanceof Filme ? "🎬" : "📚";
                    Button btnItem = new Button(icone + "  " + a.getNome());
                    btnItem.setMaxWidth(Double.MAX_VALUE);
                    btnItem.setStyle("-fx-background-color: " + COR_CARD + "; -fx-text-fill: " + COR_TEXTO
                                   + "; -fx-border-color: " + COR_ACENTO2 + "; -fx-border-radius: 6;"
                                   + "-fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 8;");
                    btnItem.setOnAction(ev -> {
                        popup.close();
                        abrirFormularioEdicao(a);
                    });
                    listaResultados.getChildren().add(btnItem);
                }
            } catch (ArquivoNaoEncontradoException exNF) {
                listaResultados.getChildren().clear();
                listaResultados.getChildren().add(labelPopup("❌ " + exNF.getMessage()));
            }
        });

        campoTitulo.setOnAction(e -> btnBuscar.fire());

        ScrollPane scrollLista = new ScrollPane(listaResultados);
        scrollLista.setFitToWidth(true);
        scrollLista.setPrefHeight(200);
        scrollLista.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        raiz.getChildren().addAll(instrucao, campoTitulo, btnBuscar, new Separator(), scrollLista);
        popup.setScene(new Scene(raiz, 400, 380));
        popup.showAndWait();
    }

    // =====================================================================
    //  FORMULÁRIO DE EDIÇÃO
    // =====================================================================
    private void abrirFormularioEdicao(Arquivo arquivo) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Editando: " + arquivo.getNome());

        VBox form = new VBox(6);
        form.setPadding(new Insets(18));
        form.setStyle("-fx-background-color: " + COR_SUPERFICIE + ";");

        TextField txtTitulo = new TextField(safe(arquivo.getNome(), ""));
        TextField txtAno    = new TextField(arquivo.getAnoLancamento() == 0 ? "" : String.valueOf(arquivo.getAnoLancamento()));
        TextField txtGenero = new TextField(safe(arquivo.getGenero(), ""));
        TextField txtImagem = new TextField(safe(arquivo.getImagem(), ""));
        TextField txtLink   = new TextField(safe(arquivo.getLink(), ""));
        TextField txtExtra  = new TextField();

        String labelExtra;
        if (arquivo instanceof Album) {
            labelExtra = "Banda/Artista:";
            txtExtra.setText(safe(((Album) arquivo).getBanda(), ""));
        } else if (arquivo instanceof Filme) {
            labelExtra = "Diretor:";
            txtExtra.setText(safe(((Filme) arquivo).getDiretor(), ""));
        } else {
            labelExtra = "Autor:";
            txtExtra.setText(safe(((Livro) arquivo).getAutor(), ""));
        }

        double larg = 340;
        for (TextField tf : new TextField[]{txtTitulo, txtAno, txtGenero, txtImagem, txtLink, txtExtra}) {
            tf.setPrefWidth(larg);
            tf.setMinWidth(larg);
            estilizarCampo(tf);
        }

        form.getChildren().addAll(
            labelPopup("Título *:"),  txtTitulo,
            labelPopup("Ano:"),       txtAno,
            labelPopup("Gênero:"),    txtGenero,
            labelPopup("URL Capa:"),  txtImagem,
            labelPopup("URL Mídia:"), txtLink,
            labelPopup(labelExtra),   txtExtra
        );

        Button btnSalvar = estilizarBotao("💾 Salvar Alterações", "#27ae60", COR_TEXTO);
        btnSalvar.setPrefWidth(larg);
        btnSalvar.setDefaultButton(true);

        btnSalvar.setOnAction(e -> {
            try {
                if (txtTitulo.getText().trim().isEmpty()) {
                    new Alert(Alert.AlertType.WARNING, "O título não pode ficar vazio.").showAndWait();
                    return;
                }
                String nomeOriginal = arquivo.getNome();

                arquivo.setNome(txtTitulo.getText().trim());
                arquivo.setGenero(txtGenero.getText().trim());
                arquivo.setImagem(txtImagem.getText().trim());
                arquivo.setLink(txtLink.getText().trim());

                String anoStr = txtAno.getText().trim();
                arquivo.setAnoLancamento(anoStr.isEmpty() ? 0 : Integer.parseInt(anoStr));

                if (arquivo instanceof Album)      ((Album) arquivo).setBanda(txtExtra.getText().trim());
                else if (arquivo instanceof Filme) ((Filme) arquivo).setDiretor(txtExtra.getText().trim());
                else if (arquivo instanceof Livro) ((Livro) arquivo).setAutor(txtExtra.getText().trim());

                cerebro.editar_busca(nomeOriginal, arquivo);
                voltarParaBiblioteca();
                popup.close();

            } catch (NumberFormatException ex) {
                new Alert(Alert.AlertType.ERROR, "O campo 'Ano' deve ser um número inteiro.").showAndWait();
            } catch (IOException ex) {
                new Alert(Alert.AlertType.ERROR, "Erro ao salvar no arquivo.").showAndWait();
            }
        });

        form.getChildren().add(btnSalvar);

        ScrollPane scroll = new ScrollPane(form);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: " + COR_SUPERFICIE + "; -fx-background: " + COR_SUPERFICIE + ";");

        popup.setScene(new Scene(scroll, 420, 440));
        popup.show();
    }

    // =====================================================================
    //  POPUP DE AVALIAÇÃO
    // =====================================================================
    private void abrirPopUpAvaliar(Arquivo arquivo) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Avaliar: " + arquivo.getNome());

        VBox box = new VBox(10);
        box.setPadding(new Insets(18));
        box.setStyle("-fx-background-color: " + COR_SUPERFICIE + ";");

        boolean jaAvaliado = (arquivo.getNota() > 0);
        Label info = new Label((jaAvaliado ? "Revisar avaliação: " : "Avaliar: ") + arquivo.getNome());
        info.setStyle("-fx-font-weight: bold; -fx-text-fill: " + COR_TEXTO + ";");

        ComboBox<Integer> cbNota = new ComboBox<>(FXCollections.observableArrayList(1, 2, 3, 4, 5));
        cbNota.setValue(jaAvaliado ? arquivo.getNota() : 5);
        cbNota.setStyle("-fx-background-color: " + COR_CARD + "; -fx-text-fill: " + COR_TEXTO + ";");

        TextArea txtComent = new TextArea();
        txtComent.setPromptText("Escreva sua crítica/comentário...");
        txtComent.setPrefRowCount(4);
        txtComent.setStyle("-fx-background-color: " + COR_CARD + "; -fx-text-fill: " + COR_TEXTO
                         + "; -fx-control-inner-background: " + COR_CARD + ";");
        if (jaAvaliado && arquivo.getComentario() != null) {
            txtComent.setText(arquivo.getComentario());
        }

        Button btnSalvar = estilizarBotao("💾 Salvar Avaliação", COR_DOURADO, "#1a1a2e");
        btnSalvar.setDefaultButton(true);
        btnSalvar.setOnAction(e -> {
            try {
                arquivo.avaliar(cbNota.getValue(), txtComent.getText());
                cerebro.editar_busca(arquivo.getNome(), arquivo);
                popup.close();
            } catch (IOException ex) {
                new Alert(Alert.AlertType.ERROR, "Erro ao salvar avaliação.").showAndWait();
            }
        });

        box.getChildren().addAll(
            info,
            labelPopup("Nota (1 a 5):"), cbNota,
            labelPopup("Comentário (opcional):"), txtComent,
            btnSalvar
        );

        popup.setScene(new Scene(box, 360, 340));
        popup.showAndWait();
    }

    // =====================================================================
    //  POPUP DE CADASTRO
    // =====================================================================
    private void abrirPopUpCadastro() {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Adicionar Nova Mídia");

        VBox form = new VBox(6);
        form.setPadding(new Insets(18));
        form.setStyle("-fx-background-color: " + COR_SUPERFICIE + ";");

        ComboBox<String> cbTipo = new ComboBox<>(FXCollections.observableArrayList("Álbum", "Filme", "Livro"));
        cbTipo.setValue("Álbum");
        cbTipo.setStyle("-fx-background-color: " + COR_CARD + "; -fx-text-fill: " + COR_TEXTO + ";");

        TextField txtT = new TextField(); txtT.setPromptText("Obrigatório");
        TextField txtA = new TextField(); txtA.setPromptText("Opcional");
        TextField txtG = new TextField(); txtG.setPromptText("Opcional");
        TextField txtI = new TextField(); txtI.setPromptText("Opcional (sorteio automático)");
        TextField txtL = new TextField(); txtL.setPromptText("Opcional (sorteio automático)");
        TextField txtE = new TextField(); txtE.setPromptText("Opcional");

        double larg = 340;
        for (TextField tf : new TextField[]{txtT, txtA, txtG, txtI, txtL, txtE}) {
            tf.setPrefWidth(larg); tf.setMinWidth(larg);
            estilizarCampo(tf);
        }

        form.getChildren().addAll(
            labelPopup("Categoria:"), cbTipo,
            labelPopup("Título *:"),   txtT,
            labelPopup("Ano:"),        txtA,
            labelPopup("Gênero:"),     txtG,
            labelPopup("Link Capa:"),  txtI,
            labelPopup("Link Mídia:"), txtL,
            labelPopup("Info Extra:"), txtE
        );

        Button btnSalvar = estilizarBotao("💾 Salvar", "#27ae60", COR_TEXTO);
        btnSalvar.setPrefWidth(larg);
        btnSalvar.setDefaultButton(true);
        btnSalvar.setOnAction(e -> {
            try {
                String nome = txtT.getText().trim();
                if (nome.isEmpty()) throw new DadosInvalidosException("O campo Título é obrigatório!");

                String tipo = cbTipo.getValue();
                Arquivo novo;

                if ("Álbum".equals(tipo)) {
                    novo = new Album(nome);
                    if (!txtE.getText().trim().isEmpty()) ((Album) novo).setBanda(txtE.getText().trim());
                } else if ("Filme".equals(tipo)) {
                    novo = new Filme(nome);
                    if (!txtE.getText().trim().isEmpty()) ((Filme) novo).setDiretor(txtE.getText().trim());
                } else {
                    novo = new Livro(nome);
                    if (!txtE.getText().trim().isEmpty()) ((Livro) novo).setAutor(txtE.getText().trim());
                }

                if (!txtA.getText().trim().isEmpty())
                    novo.setAnoLancamento(Integer.parseInt(txtA.getText().trim()));
                if (!txtG.getText().trim().isEmpty())
                    novo.setGenero(txtG.getText().trim());

                String img = txtI.getText().trim();
                if (!img.isEmpty()) novo.setImagem(img);
                else if ("Álbum".equals(tipo)) novo.setImagem(imgsAlbuns[random.nextInt(imgsAlbuns.length)]);
                else if ("Filme".equals(tipo)) novo.setImagem(imgsFilmes[random.nextInt(imgsFilmes.length)]);
                else novo.setImagem(imgsLivros[random.nextInt(imgsLivros.length)]);

                String lnk = txtL.getText().trim();
                if (!lnk.isEmpty()) novo.setLink(lnk);
                else if ("Álbum".equals(tipo)) novo.setLink(vidsAlbuns[random.nextInt(vidsAlbuns.length)]);
                else if ("Filme".equals(tipo)) novo.setLink(vidsFilmes[random.nextInt(vidsFilmes.length)]);
                else novo.setLink(docsLivros[random.nextInt(docsLivros.length)]);

                cerebro.adicionarArquivo(novo);
                voltarParaBiblioteca();
                popup.close();

            } catch (DadosInvalidosException ex) {
                new Alert(Alert.AlertType.WARNING, ex.getMessage()).showAndWait();
            } catch (NumberFormatException ex) {
                new Alert(Alert.AlertType.ERROR, "O campo 'Ano' deve ser um número inteiro.").showAndWait();
            } catch (IOException ex) {
                new Alert(Alert.AlertType.ERROR, "Erro ao salvar no arquivo.").showAndWait();
            }
        });

        form.getChildren().add(btnSalvar);

        ScrollPane scroll = new ScrollPane(form);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: " + COR_SUPERFICIE + "; -fx-background: " + COR_SUPERFICIE + ";");
        popup.setScene(new Scene(scroll, 420, 480));
        popup.showAndWait();
    }

    // =====================================================================
    //  BUSCA NA BARRA PRINCIPAL
    // =====================================================================
    private void acaoBuscar() {
        String termo = txtBusca.getText().trim();
        if (termo.isEmpty()) {
            voltarParaBiblioteca();
            return;
        }
        voltarParaBiblioteca();
        try {
            renderizarTodasAsSecoes(cerebro.buscar_palavra_chave(termo));
        } catch (ArquivoNaoEncontradoException ex) {
            containerCentral.getChildren().clear();
            Label msg = new Label("❌ " + ex.getMessage());
            msg.setStyle("-fx-font-size: 13px; -fx-text-fill: " + COR_ACENTO + "; -fx-padding: 20;");
            containerCentral.getChildren().add(msg);
        }
    }

    // =====================================================================
    //  UTILITÁRIOS DE UI
    // =====================================================================
    private String safe(String valor, String fallback) {
        return (valor == null || valor.trim().isEmpty()) ? fallback : valor.trim();
    }

    /** Botão com cor de fundo e texto configuráveis */
    private Button estilizarBotao(String texto, String corFundo, String corTexto) {
        Button b = new Button(texto);
        b.setStyle("-fx-background-color: " + corFundo + "; -fx-text-fill: " + corTexto
                 + "; -fx-font-weight: bold; -fx-padding: 7 14 7 14;"
                 + "-fx-background-radius: 6; -fx-cursor: hand;");
        return b;
    }

    /** Label padrão para uso em popups escuros */
    private Label labelPopup(String texto) {
        Label l = new Label(texto);
        l.setStyle("-fx-font-size: 12px; -fx-text-fill: " + COR_TEXTO_SUAVE + ";");
        return l;
    }

    /** Estiliza TextField para o tema escuro */
    private void estilizarCampo(TextField tf) {
        tf.setStyle("-fx-background-color: " + COR_CARD + "; -fx-text-fill: " + COR_TEXTO
                  + "; -fx-prompt-text-fill: " + COR_TEXTO_SUAVE
                  + "; -fx-border-color: " + COR_ACENTO2
                  + "; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 5 8 5 8;");
    }

    private void carregarImagem(ImageView iv, String url, double w, double h) {
        iv.setFitWidth(w);
        iv.setFitHeight(h);
        iv.setPreserveRatio(false);
        try {
            String src = (url != null && !url.trim().isEmpty()) ? url.trim() : IMG_PLACEHOLDER;
            iv.setImage(new Image(src, w, h, false, true, true));
        } catch (Exception ex) {
            iv.setImage(new Image(IMG_PLACEHOLDER, w, h, false, true, true));
        }
    }
}