package view;

import controller.ArquivoController;
import controller.TMDBService;
import controller.OpenLibraryService;
import controller.OpenLibraryService.ResultadoBuscaLivro;
import controller.TMDBService.ResultadoBuscaFilme;
import model.*;
import exception.DadosInvalidosException;
import exception.ArquivoNaoEncontradoException;

import javafx.application.Application;
import javafx.application.Platform;
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
    private TMDBService tmdb;
    private OpenLibraryService openLib;
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
        this.tmdb = new TMDBService();
        this.openLib = new OpenLibraryService();
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

        Scene cena = new Scene(layoutRaiz, 1050, 680);
        aplicarCss(cena);
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
        menuFiltro.setStyle("-fx-background-color: " + COR_CARD + ";"
                          + "-fx-border-color: " + COR_ACENTO2 + "; -fx-border-radius: 6;"
                          + "-fx-background-radius: 6; -fx-font-weight: bold; -fx-font-size: 13px;"
                          + "-fx-padding: 6 12 6 12;"
                          + "-fx-mark-color: " + COR_TEXTO + ";");
        menuFiltro.setMnemonicParsing(false);
        // Força a cor do texto do botão — o skin padrão do JavaFX
        // às vezes ignora -fx-text-fill simples em MenuButton
        menuFiltro.setTextFill(javafx.scene.paint.Color.web(COR_TEXTO));

        MenuItem miTodos  = new MenuItem("🌐 Todos");
        MenuItem miFilmes = new MenuItem("🎬 Filmes");
        MenuItem miLivros = new MenuItem("📚 Livros");

        miTodos.setOnAction(e  -> { filtroAtual = "Todos";  menuFiltro.setText("🌐 Todos ▾");  menuFiltro.setTextFill(javafx.scene.paint.Color.web(COR_TEXTO)); txtBusca.clear(); voltarParaBiblioteca(); });
        miFilmes.setOnAction(e -> { filtroAtual = "Filmes"; menuFiltro.setText("🎬 Filmes ▾"); menuFiltro.setTextFill(javafx.scene.paint.Color.web(COR_TEXTO)); voltarParaBiblioteca(); });
        miLivros.setOnAction(e -> { filtroAtual = "Livros"; menuFiltro.setText("📚 Livros ▾"); menuFiltro.setTextFill(javafx.scene.paint.Color.web(COR_TEXTO)); voltarParaBiblioteca(); });

        // Álbuns removidos daqui — gerenciados no Módulo Musical
        menuFiltro.getItems().addAll(miTodos, miFilmes, miLivros);

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

        List<Arquivo> filmes = lista.stream().filter(a -> a instanceof Filme).collect(Collectors.toList());
        List<Arquivo> livros = lista.stream().filter(a -> a instanceof Livro).collect(Collectors.toList());

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
                    String icone = a instanceof Filme ? "🎬" : "📚";
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
        aplicarCssPopup(popup);
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
                    String icone = a instanceof Filme ? "🎬" : "📚";
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
        aplicarCssPopup(popup);
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
        if (arquivo instanceof Filme) {
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

        // Campo de Status (% assistido/lido) — só para Filme e Livro,
        // que implementam a interface Status.
        Slider sliderStatus = new Slider(0, 100, 0);
        Label lblStatusValor = new Label();
        if (arquivo instanceof Status) {
            Status statusObj = (Status) arquivo;
            sliderStatus.setValue(statusObj.getStatus());
            sliderStatus.setShowTickLabels(true);
            sliderStatus.setShowTickMarks(true);
            sliderStatus.setMajorTickUnit(25);
            sliderStatus.setBlockIncrement(5);
            sliderStatus.setPrefWidth(larg);

            String labelTipo = (arquivo instanceof Filme) ? "Status de Reprodução:" : "Status de Leitura:";
            lblStatusValor.setText(statusObj.mostrarStatus());
            lblStatusValor.setStyle("-fx-text-fill: " + COR_TEXTO + "; -fx-font-size: 12px;");

            sliderStatus.valueProperty().addListener((obs, oldV, newV) -> {
                statusObj.setStatus(newV.intValue());
                lblStatusValor.setText(statusObj.mostrarStatus());
            });

            form.getChildren().addAll(labelPopup(labelTipo), sliderStatus, lblStatusValor);
        }

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

                if (arquivo instanceof Filme)      ((Filme) arquivo).setDiretor(txtExtra.getText().trim());
                else if (arquivo instanceof Livro) ((Livro) arquivo).setAutor(txtExtra.getText().trim());

                // O Status já foi atualizado em tempo real pelo listener do slider,
                // mas garantimos o valor final aqui também por segurança.
                if (arquivo instanceof Status) {
                    ((Status) arquivo).setStatus((int) sliderStatus.getValue());
                }

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
        aplicarCssPopup(popup);
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
        aplicarCssPopup(popup);
        popup.showAndWait();
    }

    // =====================================================================
    //  POPUP DE CADASTRO — escolha do tipo, depois Manual ou via API
    // =====================================================================
    private void abrirPopUpCadastro() {
        Stage popupTipo = new Stage();
        popupTipo.initModality(Modality.APPLICATION_MODAL);
        popupTipo.setTitle("Adicionar Nova Mídia");

        VBox raiz = new VBox(14);
        raiz.setPadding(new Insets(20));
        raiz.setStyle("-fx-background-color: " + COR_SUPERFICIE + ";");

        Label titulo = new Label("O que você quer adicionar?");
        titulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + COR_TEXTO + ";"
                       + "-fx-font-family: 'Segoe UI';");

        Button btnFilme = estilizarBotao("🎬 Filme", "#3498db", COR_TEXTO);
        Button btnLivro = estilizarBotao("📚 Livro", "#8e44ad", COR_TEXTO);
        btnFilme.setPrefWidth(300);
        btnLivro.setPrefWidth(300);

        btnFilme.setOnAction(e -> {
            popupTipo.close();
            Platform.runLater(() -> abrirEscolhaMetodo("Filme"));
        });
        btnLivro.setOnAction(e -> {
            popupTipo.close();
            Platform.runLater(() -> abrirEscolhaMetodo("Livro"));
        });

        raiz.getChildren().addAll(titulo, btnFilme, btnLivro);
        popupTipo.setScene(new Scene(raiz, 340, 200));
        aplicarCssPopup(popupTipo);
        popupTipo.showAndWait();
    }

    /** Segunda etapa: escolher entre cadastro manual ou busca via API */
    private void abrirEscolhaMetodo(String tipo) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Adicionar " + tipo);

        VBox raiz = new VBox(14);
        raiz.setPadding(new Insets(20));
        raiz.setStyle("-fx-background-color: " + COR_SUPERFICIE + ";");

        Label titulo = new Label("Como deseja cadastrar este " + tipo.toLowerCase() + "?");
        titulo.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + COR_TEXTO + ";"
                       + "-fx-font-family: 'Segoe UI';");
        titulo.setWrapText(true);

        String apiNome = tipo.equals("Filme") ? "🔍 Buscar no TMDB" : "🔍 Buscar no Open Library";
        Button btnApi    = estilizarBotao(apiNome, "#27ae60", COR_TEXTO);
        Button btnManual = estilizarBotao("✍️ Cadastro Manual", COR_CARD, COR_TEXTO);
        btnApi.setPrefWidth(300);
        btnManual.setPrefWidth(300);

        btnApi.setOnAction(e -> {
            popup.close();
            Platform.runLater(() -> {
                if (tipo.equals("Filme")) abrirBuscaTMDB();
                else abrirBuscaOpenLibrary();
            });
        });
        btnManual.setOnAction(e -> {
            popup.close();
            Platform.runLater(() -> abrirCadastroManual(tipo));
        });

        raiz.getChildren().addAll(titulo, btnApi, btnManual);
        popup.setScene(new Scene(raiz, 360, 220));
        aplicarCssPopup(popup);
        popup.showAndWait();
    }

    // ── CADASTRO VIA TMDB (Filme) ────────────────────────────────────────
    private void abrirBuscaTMDB() {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Buscar Filme no TMDB");

        VBox raiz = new VBox(10);
        raiz.setPadding(new Insets(18));
        raiz.setStyle("-fx-background-color: " + COR_SUPERFICIE + ";");

        TextField txtNome = new TextField();
        txtNome.setPromptText("Nome do filme...");
        txtNome.setPrefWidth(380);
        estilizarCampo(txtNome);

        Button btnBuscar = estilizarBotao("🔍 Buscar", "#27ae60", COR_TEXTO);

        VBox listaResultados = new VBox(6);
        listaResultados.getChildren().add(labelPopup("Aguardando busca..."));

        btnBuscar.setOnAction(e -> {
            String termo = txtNome.getText().trim();
            if (termo.isEmpty()) { new Alert(Alert.AlertType.WARNING, "Digite o nome do filme.").showAndWait(); return; }
            btnBuscar.setDisable(true); btnBuscar.setText("⏳ Buscando...");
            listaResultados.getChildren().clear();
            listaResultados.getChildren().add(labelPopup("Consultando TMDB..."));

            new Thread(() -> {
                try {
                    List<ResultadoBuscaFilme> resultados = tmdb.buscarFilme(termo);
                    Platform.runLater(() -> {
                        listaResultados.getChildren().clear();
                        listaResultados.getChildren().add(labelPopup("Clique para selecionar:"));
                        for (ResultadoBuscaFilme r : resultados) {
                            HBox item = new HBox(10);
                            item.setAlignment(Pos.CENTER_LEFT);
                            item.setPadding(new Insets(6));
                            item.setStyle("-fx-background-color: " + COR_CARD + "; -fx-border-color: " + COR_ACENTO2
                                        + "; -fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;");
                            ImageView thumb = new ImageView();
                            carregarImagem(thumb, r.poster, 40, 55);
                            Label lbl = new Label(r.toString());
                            lbl.setStyle("-fx-text-fill: " + COR_TEXTO + "; -fx-font-size: 12px;"
                                       + "-fx-font-family: 'Segoe UI';");
                            lbl.setWrapText(true);
                            item.getChildren().addAll(thumb, lbl);
                            item.setOnMouseClicked(ev -> {
                                popup.close();
                                Platform.runLater(() -> confirmarImportFilme(r));
                            });
                            listaResultados.getChildren().add(item);
                        }
                        btnBuscar.setDisable(false); btnBuscar.setText("🔍 Buscar");
                    });
                } catch (ArquivoNaoEncontradoException ex) {
                    // Busca válida, mas o TMDB não retornou nenhum resultado
                    Platform.runLater(() -> {
                        listaResultados.getChildren().clear();
                        listaResultados.getChildren().add(labelPopup("❌ " + ex.getMessage()));
                        btnBuscar.setDisable(false); btnBuscar.setText("🔍 Buscar");
                    });
                } catch (IOException ex) {
                    // Falha de rede/conexão com a API
                    Platform.runLater(() -> {
                        listaResultados.getChildren().clear();
                        listaResultados.getChildren().add(labelPopup("❌ Erro de conexão com o TMDB. Verifique sua internet."));
                        btnBuscar.setDisable(false); btnBuscar.setText("🔍 Buscar");
                    });
                }
            }).start();
        });
        txtNome.setOnAction(e -> btnBuscar.fire());

        ScrollPane sp = new ScrollPane(listaResultados);
        sp.setFitToWidth(true); sp.setPrefHeight(280);
        sp.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        raiz.getChildren().addAll(labelPopup("Nome do filme:"), txtNome, btnBuscar, new Separator(), sp);
        popup.setScene(new Scene(raiz, 440, 460));
        aplicarCssPopup(popup);
        popup.showAndWait();
    }

    private void confirmarImportFilme(ResultadoBuscaFilme resultado) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        VBox raiz = new VBox(8);
        raiz.setPadding(new Insets(18));
        raiz.setStyle("-fx-background-color: " + COR_SUPERFICIE + ";");
        Label info = new Label("Importando: " + resultado.titulo);
        info.setStyle("-fx-font-weight: bold; -fx-text-fill: " + COR_TEXTO + ";");
        Label loading = new Label("⏳ Buscando detalhes no TMDB...");
        loading.setStyle("-fx-text-fill: " + COR_TEXTO_SUAVE + ";");
        raiz.getChildren().addAll(info, loading);
        popup.setScene(new Scene(raiz, 360, 120));
        aplicarCssPopup(popup);
        popup.show();

        new Thread(() -> {
            try {
                Filme filme = new Filme(resultado.titulo);
                tmdb.importarFilmeCompleto(filme, resultado.id);
                cerebro.adicionarArquivo(filme);
                Platform.runLater(() -> {
                    popup.close(); voltarParaBiblioteca();
                    new Alert(Alert.AlertType.INFORMATION, resultado.titulo + " adicionado com sucesso!").showAndWait();
                });
            } catch (ArquivoNaoEncontradoException ex) {
                // O filme estava na lista de busca, mas os detalhes não foram encontrados no TMDB
                Platform.runLater(() -> {
                    popup.close();
                    new Alert(Alert.AlertType.ERROR, "Filme não encontrado: " + ex.getMessage()).showAndWait();
                });
            } catch (IOException ex) {
                // Falha de rede ao buscar detalhes, ou falha ao salvar no arquivo local
                Platform.runLater(() -> {
                    popup.close();
                    new Alert(Alert.AlertType.ERROR, "Erro de conexão ou ao salvar o arquivo: " + ex.getMessage()).showAndWait();
                });
            }
        }).start();
    }

    // ── CADASTRO VIA OPEN LIBRARY (Livro) ────────────────────────────────
    private void abrirBuscaOpenLibrary() {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Buscar Livro no Open Library");

        VBox raiz = new VBox(10);
        raiz.setPadding(new Insets(18));
        raiz.setStyle("-fx-background-color: " + COR_SUPERFICIE + ";");

        TextField txtNome = new TextField();
        txtNome.setPromptText("Nome do livro...");
        txtNome.setPrefWidth(380);
        estilizarCampo(txtNome);

        Button btnBuscar = estilizarBotao("🔍 Buscar", "#27ae60", COR_TEXTO);

        VBox listaResultados = new VBox(6);
        listaResultados.getChildren().add(labelPopup("Aguardando busca..."));

        btnBuscar.setOnAction(e -> {
            String termo = txtNome.getText().trim();
            if (termo.isEmpty()) { new Alert(Alert.AlertType.WARNING, "Digite o nome do livro.").showAndWait(); return; }
            btnBuscar.setDisable(true); btnBuscar.setText("⏳ Buscando...");
            listaResultados.getChildren().clear();
            listaResultados.getChildren().add(labelPopup("Consultando Open Library..."));

            new Thread(() -> {
                try {
                    List<ResultadoBuscaLivro> resultados = openLib.buscarLivro(termo);
                    Platform.runLater(() -> {
                        listaResultados.getChildren().clear();
                        listaResultados.getChildren().add(labelPopup("Clique para selecionar:"));
                        for (ResultadoBuscaLivro r : resultados) {
                            HBox item = new HBox(10);
                            item.setAlignment(Pos.CENTER_LEFT);
                            item.setPadding(new Insets(6));
                            item.setStyle("-fx-background-color: " + COR_CARD + "; -fx-border-color: " + COR_ACENTO2
                                        + "; -fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;");
                            ImageView thumb = new ImageView();
                            carregarImagem(thumb, r.capa, 40, 55);
                            Label lbl = new Label(r.toString());
                            lbl.setStyle("-fx-text-fill: " + COR_TEXTO + "; -fx-font-size: 12px;"
                                       + "-fx-font-family: 'Segoe UI';");
                            lbl.setWrapText(true);
                            item.getChildren().addAll(thumb, lbl);
                            item.setOnMouseClicked(ev -> {
                                popup.close();
                                Platform.runLater(() -> confirmarImportLivro(r));
                            });
                            listaResultados.getChildren().add(item);
                        }
                        btnBuscar.setDisable(false); btnBuscar.setText("🔍 Buscar");
                    });
                } catch (ArquivoNaoEncontradoException ex) {
                    // Busca válida, mas o Open Library não retornou nenhum resultado
                    Platform.runLater(() -> {
                        listaResultados.getChildren().clear();
                        listaResultados.getChildren().add(labelPopup("❌ " + ex.getMessage()));
                        btnBuscar.setDisable(false); btnBuscar.setText("🔍 Buscar");
                    });
                } catch (IOException ex) {
                    // Falha de rede/conexão com a API
                    Platform.runLater(() -> {
                        listaResultados.getChildren().clear();
                        listaResultados.getChildren().add(labelPopup("❌ Erro de conexão com o Open Library. Verifique sua internet."));
                        btnBuscar.setDisable(false); btnBuscar.setText("🔍 Buscar");
                    });
                }
            }).start();
        });
        txtNome.setOnAction(e -> btnBuscar.fire());

        ScrollPane sp = new ScrollPane(listaResultados);
        sp.setFitToWidth(true); sp.setPrefHeight(280);
        sp.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        raiz.getChildren().addAll(labelPopup("Nome do livro:"), txtNome, btnBuscar, new Separator(), sp);
        popup.setScene(new Scene(raiz, 440, 460));
        aplicarCssPopup(popup);
        popup.showAndWait();
    }

    private void confirmarImportLivro(ResultadoBuscaLivro resultado) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        VBox raiz = new VBox(8);
        raiz.setPadding(new Insets(18));
        raiz.setStyle("-fx-background-color: " + COR_SUPERFICIE + ";");
        Label info = new Label("Importando: " + resultado.titulo);
        info.setStyle("-fx-font-weight: bold; -fx-text-fill: " + COR_TEXTO + ";");
        info.setWrapText(true);
        Label loading = new Label("⏳ Buscando detalhes no Open Library...");
        loading.setStyle("-fx-text-fill: " + COR_TEXTO_SUAVE + ";");
        raiz.getChildren().addAll(info, loading);
        popup.setScene(new Scene(raiz, 360, 120));
        aplicarCssPopup(popup);
        popup.show();

        new Thread(() -> {
            try {
                Livro livro = new Livro(resultado.titulo);
                openLib.importarLivroCompleto(livro, resultado);
                cerebro.adicionarArquivo(livro);
                Platform.runLater(() -> {
                    popup.close(); voltarParaBiblioteca();
                    new Alert(Alert.AlertType.INFORMATION, resultado.titulo + " adicionado com sucesso!").showAndWait();
                });
            } catch (ArquivoNaoEncontradoException ex) {
                // O livro estava na lista de busca, mas os detalhes não foram encontrados no Open Library
                Platform.runLater(() -> {
                    popup.close();
                    new Alert(Alert.AlertType.ERROR, "Livro não encontrado: " + ex.getMessage()).showAndWait();
                });
            } catch (IOException ex) {
                // Falha de rede ao buscar detalhes, ou falha ao salvar no arquivo local
                Platform.runLater(() -> {
                    popup.close();
                    new Alert(Alert.AlertType.ERROR, "Erro de conexão ou ao salvar o arquivo: " + ex.getMessage()).showAndWait();
                });
            }
        }).start();
    }

    // ── CADASTRO MANUAL (Filme ou Livro) ─────────────────────────────────
    private void abrirCadastroManual(String tipo) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Cadastro Manual — " + tipo);

        VBox form = new VBox(6);
        form.setPadding(new Insets(18));
        form.setStyle("-fx-background-color: " + COR_SUPERFICIE + ";");

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

        String labelExtra = tipo.equals("Filme") ? "Diretor:" : "Autor:";

        form.getChildren().addAll(
            labelPopup("Título *:"),   txtT,
            labelPopup("Ano:"),        txtA,
            labelPopup("Gênero:"),     txtG,
            labelPopup("Link Capa:"),  txtI,
            labelPopup("Link Mídia:"), txtL,
            labelPopup(labelExtra),    txtE
        );

        Button btnSalvar = estilizarBotao("💾 Salvar", "#27ae60", COR_TEXTO);
        btnSalvar.setPrefWidth(larg);
        btnSalvar.setDefaultButton(true);
        btnSalvar.setOnAction(e -> {
            try {
                String nome = txtT.getText().trim();
                if (nome.isEmpty()) throw new DadosInvalidosException("O campo Título é obrigatório!");

                Arquivo novo;
                if (tipo.equals("Filme")) {
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
                else if (tipo.equals("Filme")) novo.setImagem(imgsFilmes[random.nextInt(imgsFilmes.length)]);
                else novo.setImagem(imgsLivros[random.nextInt(imgsLivros.length)]);

                String lnk = txtL.getText().trim();
                if (!lnk.isEmpty()) novo.setLink(lnk);
                else if (tipo.equals("Filme")) novo.setLink(vidsFilmes[random.nextInt(vidsFilmes.length)]);
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
        aplicarCssPopup(popup);
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

    /** Botão com cor de fundo e texto configuráveis. */
    private Button estilizarBotao(String texto, String corFundo, String corTexto) {
        Button b = new Button(texto);
        b.setStyle("-fx-background-color: " + corFundo + "; -fx-text-fill: " + corTexto
                 + "; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 7 14 7 14;"
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

    /** Aplica o estilos.css a uma Scene, com diagnóstico no console.
     *  Procura em /resources/Estilos.css na raiz do classpath compilado
     *  (deve corresponder a src/resources/Estilos.css). */
    private void aplicarCss(Scene cena) {
        java.net.URL recurso = getClass().getResource("/resources/Estilos.css");
        if (recurso == null) {
            System.out.println("[CSS] estilos.css NÃO encontrado em /resources/Estilos.css — "
                + "verifique se o arquivo está em src/resources/Estilos.css");
            return;
        }
        cena.getStylesheets().add(recurso.toExternalForm());
        System.out.println("[CSS] estilos.css carregado com sucesso: " + recurso.toExternalForm());
    }

    /** Aplica o CSS também a popups (Stages secundários) */
    private void aplicarCssPopup(Stage popup) {
        if (popup.getScene() != null) {
            aplicarCss(popup.getScene());
        }
    }
}