package view;

import controller.ArquivoController;
import model.*;
import exception.DadosInvalidosException;
import exception.ArquivoNaoEncontradoException;

import javafx.application.Application;
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

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class TelaPrincipal extends Application {

    private ArquivoController cerebro;
    private VBox containerCentral;
    private TextField txtBusca;
    private Stage palco;
    private BorderPane layoutRaiz;   // guardamos a referência para reutilizar a mesma Scene

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

    @Override
    public void start(Stage palcoPrincipal) {
        this.palco = palcoPrincipal;
        this.cerebro = new ArquivoController();
        palcoPrincipal.setTitle("Cofre Cultural v1.0");

        layoutRaiz = new BorderPane();
        layoutRaiz.setPadding(new Insets(12));
        layoutRaiz.setStyle("-fx-background-color: #f0f2f5;");

        layoutRaiz.setTop(criarBarraSuperior());

        containerCentral = new VBox(20);
        containerCentral.setPadding(new Insets(10, 0, 10, 0));

        ScrollPane painelRolagem = new ScrollPane(containerCentral);
        painelRolagem.setFitToWidth(true);
        painelRolagem.setStyle("-fx-background-color: transparent; -fx-background: #f0f2f5;");
        layoutRaiz.setCenter(painelRolagem);

        layoutRaiz.setBottom(criarBarraInferior());

        renderizarBiblioteca();

        // UMA única Scene, reutilizada sempre — troca só o centro (layoutRaiz.setCenter)
        Scene cena = new Scene(layoutRaiz, 900, 600);
        palcoPrincipal.setScene(cena);
        palcoPrincipal.show();
    }

    // =====================================================================
    //  Volta para a vista principal (carrossel)
    // =====================================================================
    private void voltarParaBiblioteca() {
        // Recoloca o ScrollPane com os cards no centro
        containerCentral = new VBox(20);
        containerCentral.setPadding(new Insets(10, 0, 10, 0));

        ScrollPane painelRolagem = new ScrollPane(containerCentral);
        painelRolagem.setFitToWidth(true);
        painelRolagem.setStyle("-fx-background-color: transparent; -fx-background: #f0f2f5;");

        layoutRaiz.setCenter(painelRolagem);
        renderizarBiblioteca();
    }

    private void renderizarBiblioteca() {
        renderizarTodasAsSecoes(cerebro.getBiblioteca());
    }

    // =====================================================================
    //  BARRA SUPERIOR
    // =====================================================================
    private VBox criarBarraSuperior() {
        VBox painelTopo = new VBox(8);
        painelTopo.setPadding(new Insets(0, 0, 10, 0));

        HBox linhaBusca = new HBox(8);
        linhaBusca.setAlignment(Pos.CENTER_LEFT);

        txtBusca = new TextField();
        txtBusca.setPromptText("Buscar pelo título...");
        txtBusca.setPrefWidth(240);
        txtBusca.setOnAction(e -> acaoBuscar());

        Button btnBuscar = new Button("🔍 Buscar");
        btnBuscar.setStyle("-fx-background-color: #3182ce; -fx-text-fill: white; -fx-font-weight: bold;");
        btnBuscar.setOnAction(e -> acaoBuscar());

        linhaBusca.getChildren().addAll(txtBusca, btnBuscar);

        HBox linhaFiltros = new HBox(8);
        linhaFiltros.setPadding(new Insets(3, 0, 0, 0));
        linhaFiltros.setAlignment(Pos.CENTER_LEFT);

        Button btnTodos    = new Button("🌐 Todos");
        Button btnFilmes   = new Button("🎬 Filmes");
        Button btnAlbuns   = new Button("🎵 Álbuns");
        Button btnLivros   = new Button("📚 Livros");

        Button btnAvaliados = new Button("⭐ Avaliados");
        btnAvaliados.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold;");

        Button btnEditar  = new Button("✏️ Editar");
        btnEditar.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");

        Button btnRemover = new Button("🗑️ Remover");
        btnRemover.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");

        btnTodos.setOnAction(e -> { txtBusca.clear(); voltarParaBiblioteca(); });

        btnFilmes.setOnAction(e -> renderizarSecaoUnica("🎬 Filmes",
            cerebro.getBiblioteca().stream().filter(a -> a instanceof Filme).collect(Collectors.toList())));
        btnAlbuns.setOnAction(e -> renderizarSecaoUnica("🎵 Álbuns",
            cerebro.getBiblioteca().stream().filter(a -> a instanceof Album).collect(Collectors.toList())));
        btnLivros.setOnAction(e -> renderizarSecaoUnica("📚 Livros",
            cerebro.getBiblioteca().stream().filter(a -> a instanceof Livro).collect(Collectors.toList())));

        btnAvaliados.setOnAction(e -> {
            List<Arquivo> avaliados = cerebro.getBiblioteca().stream()
                .filter(a -> a.getNota() > 0)
                .collect(Collectors.toList());
            containerCentral.getChildren().clear(); //limpa a parte central
            if (avaliados.isEmpty()) {
                Label msg = new Label("Nenhum item avaliado ainda. Abra um item e clique em ⭐ Avaliar.");
                msg.setStyle("-fx-font-size: 14px; -fx-text-fill: #888; -fx-padding: 20;");
                containerCentral.getChildren().add(msg);
            } else {
                renderizarTodasAsSecoes(avaliados);
            }
        });

        btnEditar.setOnAction(e -> abrirFluxoEditarPorBusca());
        btnRemover.setOnAction(e -> abrirFluxoRemoverPorBusca());

        linhaFiltros.getChildren().addAll(btnTodos, btnFilmes, btnAlbuns, btnLivros,
                                          btnAvaliados, btnEditar, btnRemover);

        painelTopo.getChildren().addAll(linhaBusca, linhaFiltros);
        return painelTopo;
    }

    // =====================================================================
    //  BARRA INFERIOR
    // =====================================================================
    private HBox criarBarraInferior() {
        HBox barra = new HBox();
        barra.setPadding(new Insets(10, 0, 0, 0));

        Button btnAdicionar = new Button("➕ Adicionar Nova Mídia");
        btnAdicionar.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16 8 16;");
        btnAdicionar.setOnAction(e -> abrirPopUpCadastro());
        barra.getChildren().add(btnAdicionar);
        return barra;
    }

    // =====================================================================
    //  RENDERIZAÇÃO
    // =====================================================================
    private void renderizarTodasAsSecoes(List<Arquivo> lista) {
        containerCentral.getChildren().clear(); //limpa a tela atual para renderizar a nova lista

        if (lista.isEmpty()) {
            Label msg = new Label("Nenhum arquivo encontrado.");
            msg.setStyle("-fx-font-size: 14px; -fx-text-fill: #888; -fx-padding: 20;");
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

    private void renderizarSecaoUnica(String titulo, List<Arquivo> lista) {
        containerCentral.getChildren().clear();
        if (lista.isEmpty()) {
            Label msg = new Label("Nenhum item nesta categoria.");
            msg.setStyle("-fx-font-size: 14px; -fx-text-fill: #888; -fx-padding: 20;");
            containerCentral.getChildren().add(msg);
        } else {
            containerCentral.getChildren().add(criarSecaoHorizontal(titulo, lista));
        }
    }

    private VBox criarSecaoHorizontal(String tituloSecao, List<Arquivo> itens) {
        VBox secao = new VBox(5);

        Label lbl = new Label(tituloSecao);
        lbl.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #2d3748;");

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

        Button btnEsq = new Button("◀");
        Button btnDir = new Button("▶");
        btnEsq.setOnAction(e -> scroll.setHvalue(Math.max(0, scroll.getHvalue() - 0.25)));
        btnDir.setOnAction(e -> scroll.setHvalue(Math.min(1, scroll.getHvalue() + 0.25)));

        estrutura.getChildren().addAll(btnEsq, scroll, btnDir);
        secao.getChildren().addAll(lbl, estrutura);
        return secao;
    }

    // =====================================================================
    //  CARD MINIATURA — clique abre o painel de detalhes NO CENTRO
    // =====================================================================
    private VBox criarCardMiniatura(Arquivo arquivo) {
        VBox card = new VBox(6);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(8));
        card.setPrefSize(150, 210);
        card.setMinSize(150, 210);
        card.setMaxSize(150, 210);
        card.setStyle("-fx-border-color: #cbd5e1; -fx-border-radius: 8; "
                    + "-fx-background-color: white; -fx-background-radius: 8; -fx-cursor: hand;");

        // Imagem
        ImageView capa = new ImageView();
        capa.setFitWidth(130);
        capa.setFitHeight(140);
        capa.setPreserveRatio(true);
        carregarImagem(capa, arquivo.getImagem(), 130, 140);

        // Título
        Label lblTitulo = new Label(arquivo.getNome() != null ? arquivo.getNome() : "Sem título");
        lblTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-alignment: center; -fx-text-fill: #2d3748;");
        lblTitulo.setWrapText(true);
        lblTitulo.setMaxWidth(135);
        lblTitulo.setAlignment(Pos.CENTER);

        card.getChildren().addAll(capa, lblTitulo);

        // CLIQUE: substitui apenas o centro do layoutRaiz pelo painel de detalhes
        card.setOnMouseClicked(ev -> mostrarPainelDetalhes(arquivo));

        // Efeito hover para deixar mais claro que é clicável
        card.setOnMouseEntered(ev -> card.setStyle(
            "-fx-border-color: #3182ce; -fx-border-radius: 8; "
            + "-fx-background-color: #ebf8ff; -fx-background-radius: 8; -fx-cursor: hand;"));
        card.setOnMouseExited(ev -> card.setStyle(
            "-fx-border-color: #cbd5e1; -fx-border-radius: 8; "
            + "-fx-background-color: white; -fx-background-radius: 8; -fx-cursor: hand;"));

        return card;
    }

    // =====================================================================
    //  PAINEL DE DETALHES — substitui o centro da tela (sem trocar Scene)
    // =====================================================================
    private void mostrarPainelDetalhes(Arquivo arquivo) {
        BorderPane painelDetalhes = new BorderPane();
        painelDetalhes.setPadding(new Insets(20));
        painelDetalhes.setStyle("-fx-background-color: #f8f9fa;");

        // --- TOPO: botão Voltar ---
        Button btnVoltar = new Button("⬅ Voltar para a Biblioteca");
        btnVoltar.setStyle("-fx-font-weight: bold; -fx-padding: 6 14 6 14; -fx-cursor: hand;");
        btnVoltar.setOnAction(e -> voltarParaBiblioteca());
        painelDetalhes.setTop(btnVoltar);
        BorderPane.setMargin(btnVoltar, new Insets(0, 0, 12, 0));

        // --- CENTRO: conteúdo ---
        VBox conteudo = new VBox(14);
        conteudo.setPadding(new Insets(10, 0, 0, 0));

        // Título do item
        Label lblNome = new Label(safe(arquivo.getNome(), "Sem título"));
        lblNome.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1a202c;");
        lblNome.setWrapText(true);

        // Linha: imagem à esquerda + metadados à direita
        HBox linhaInfos = new HBox(20);
        linhaInfos.setAlignment(Pos.TOP_LEFT);

        ImageView grandeCapa = new ImageView();
        grandeCapa.setFitWidth(160);
        grandeCapa.setFitHeight(200);
        grandeCapa.setPreserveRatio(true);
        carregarImagem(grandeCapa, arquivo.getImagem(), 160, 200);

        VBox metadados = new VBox(8);
        metadados.setStyle("-fx-background-color: #edf2f7; -fx-padding: 15; -fx-background-radius: 8;");

        // Determina a info específica do tipo
        String infoEspecifica;
        if (arquivo instanceof Album) {
            infoEspecifica = "🎤 Banda/Artista: " + safe(((Album) arquivo).getBanda(), "Não informado");
        } else if (arquivo instanceof Filme) {
            infoEspecifica = "🎥 Diretor: " + safe(((Filme) arquivo).getDiretor(), "Não informado");
        } else if (arquivo instanceof Livro) {
            infoEspecifica = "✍️ Autor: " + safe(((Livro) arquivo).getAutor(), "Não informado");
        } else {
            infoEspecifica = "";
        }

        metadados.getChildren().addAll(
            labelInfo("📌 Tipo: ",   arquivo.getClass().getSimpleName()),
            labelInfo("📅 Ano: ",    arquivo.getAnoLancamento() == 0 ? "Não informado" : String.valueOf(arquivo.getAnoLancamento())),
            labelInfo("🏷️ Gênero: ", safe(arquivo.getGenero(), "Não informado")),
            labelInfo("", infoEspecifica)
        );
        HBox.setHgrow(metadados, Priority.ALWAYS);

        linhaInfos.getChildren().addAll(grandeCapa, metadados);

        // Bloco de avaliação (exibe o estado atual)
        VBox boxReview = new VBox(6);
        boxReview.setStyle("-fx-border-color: #e2d9a2; -fx-border-width: 1; -fx-padding: 12; "
                         + "-fx-border-radius: 8; -fx-background-color: #fffaf0; -fx-background-radius: 8;");

        int nota = arquivo.getNota();
        String notaTexto = (nota == 0) ? "Não avaliado" : nota + " / 5 ⭐";
        String comentTexto = safe(arquivo.getComentario(), "Nenhum comentário.");

        Label lblNota   = new Label("⭐ Classificação: " + notaTexto);
        lblNota.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        Label lblComent = new Label("📝 Crítica: " + comentTexto);
        lblComent.setWrapText(true);
        lblComent.setStyle("-fx-font-style: italic; -fx-font-size: 12px; -fx-text-fill: #555;");

        boxReview.getChildren().addAll(lblNota, lblComent);

        // Link de acesso
        Hyperlink link = new Hyperlink("🚀 Abrir / Executar mídia");
        link.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
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

        // Botão Avaliar / Revisar — texto muda conforme o estado real
        boolean jaAvaliado = (nota > 0);
        Button btnAvaliar = new Button(jaAvaliado ? "🔄 Revisar Avaliação" : "⭐ Avaliar");
        btnAvaliar.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; "
                          + "-fx-font-weight: bold; -fx-padding: 8 18 8 18; -fx-cursor: hand;");
        btnAvaliar.setOnAction(e -> {
            abrirPopUpAvaliar(arquivo);
            // Após fechar o popup, recarrega o painel de detalhes para refletir a nova nota
            mostrarPainelDetalhes(arquivo);
        });

        HBox acoes = new HBox(10);
        acoes.setPadding(new Insets(6, 0, 0, 0));
        acoes.getChildren().add(btnAvaliar);

        conteudo.getChildren().addAll(lblNome, linhaInfos, boxReview, link, acoes);

        ScrollPane scroll = new ScrollPane(conteudo);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        painelDetalhes.setCenter(scroll);

        // Substitui apenas o centro — barra superior e inferior ficam intactas
        layoutRaiz.setCenter(painelDetalhes);
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

        Label instrucao = new Label("Digite parte do título para buscar:");
        instrucao.setStyle("-fx-font-weight: bold;");

        TextField campoTitulo = new TextField();
        campoTitulo.setPromptText("Ex: Inception, The Beatles...");

        Button btnBuscar = new Button("🔍 Buscar");
        btnBuscar.setStyle("-fx-background-color: #3182ce; -fx-text-fill: white; -fx-font-weight: bold;");

        VBox listaResultados = new VBox(6);
        Label lblStatus = new Label("Aguardando busca...");
        lblStatus.setStyle("-fx-text-fill: #888;");
        listaResultados.getChildren().add(lblStatus);

        btnBuscar.setOnAction(e -> {
            String termo = campoTitulo.getText().trim();
            if (termo.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Digite um título para buscar.").showAndWait();
                return;
            }
            // ArquivoNaoEncontradoException propagada do controller e tratada aqui na view
            try {
                List<Arquivo> encontrados = cerebro.buscar_palavra_chave(termo);
                listaResultados.getChildren().clear();
                listaResultados.getChildren().add(new Label("Clique no item que deseja remover:"));
                for (Arquivo a : encontrados) {
                    String icone = a instanceof Album ? "🎵" : a instanceof Filme ? "🎬" : "📚";
                    Button btnItem = new Button(icone + "  " + a.getNome());
                    btnItem.setMaxWidth(Double.MAX_VALUE);
                    btnItem.setStyle("-fx-background-color: #f7f7f7; -fx-border-color: #ddd; "
                                   + "-fx-cursor: hand; -fx-padding: 8; -fx-alignment: CENTER_LEFT;");
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
                listaResultados.getChildren().add(new Label("❌ " + exNF.getMessage()));
            }
        });

        campoTitulo.setOnAction(e -> btnBuscar.fire());

        ScrollPane scrollLista = new ScrollPane(listaResultados);
        scrollLista.setFitToWidth(true);
        scrollLista.setPrefHeight(200);

        raiz.getChildren().addAll(instrucao, campoTitulo, btnBuscar, new Separator(), scrollLista);
        popup.setScene(new Scene(raiz, 380, 360));
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

        Label instrucao = new Label("Digite parte do título para buscar:");
        instrucao.setStyle("-fx-font-weight: bold;");

        TextField campoTitulo = new TextField();
        campoTitulo.setPromptText("Ex: Inception, The Beatles...");

        Button btnBuscar = new Button("🔍 Buscar");
        btnBuscar.setStyle("-fx-background-color: #3182ce; -fx-text-fill: white; -fx-font-weight: bold;");

        VBox listaResultados = new VBox(6);
        listaResultados.getChildren().add(new Label("Aguardando busca..."));

        btnBuscar.setOnAction(e -> {
            String termo = campoTitulo.getText().trim();
            if (termo.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Digite um título para buscar.").showAndWait();
                return;
            }
            // ArquivoNaoEncontradoException propagada do controller e tratada aqui na view
            try {
                List<Arquivo> encontrados = cerebro.buscar_palavra_chave(termo);
                listaResultados.getChildren().clear();
                listaResultados.getChildren().add(new Label("Clique no item que deseja editar:"));
                for (Arquivo a : encontrados) {
                    String icone = a instanceof Album ? "🎵" : a instanceof Filme ? "🎬" : "📚";
                    Button btnItem = new Button(icone + "  " + a.getNome());
                    btnItem.setMaxWidth(Double.MAX_VALUE);
                    btnItem.setStyle("-fx-background-color: #f7f7f7; -fx-border-color: #ddd; "
                                   + "-fx-cursor: hand; -fx-padding: 8; -fx-alignment: CENTER_LEFT;");
                    btnItem.setOnAction(ev -> {
                        popup.close();
                        abrirFormularioEdicao(a);
                    });
                    listaResultados.getChildren().add(btnItem);
                }
            } catch (ArquivoNaoEncontradoException exNF) {
                listaResultados.getChildren().clear();
                listaResultados.getChildren().add(new Label("❌ " + exNF.getMessage()));
            }
        });

        campoTitulo.setOnAction(e -> btnBuscar.fire());

        ScrollPane scrollLista = new ScrollPane(listaResultados);
        scrollLista.setFitToWidth(true);
        scrollLista.setPrefHeight(200);

        raiz.getChildren().addAll(instrucao, campoTitulo, btnBuscar, new Separator(), scrollLista);
        popup.setScene(new Scene(raiz, 380, 360));
        popup.showAndWait();
    }

    // Formulário de edição com campos pré-preenchidos e editáveis
    private void abrirFormularioEdicao(Arquivo arquivo) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Editando: " + arquivo.getNome());

        // Usa VBox simples — mais confiável que GridPane para exibir campos preenchidos
        VBox form = new VBox(6);
        form.setPadding(new Insets(18));
        form.setStyle("-fx-background-color: #fff;");

        // Helper: cria um bloco label + campo
        // Os campos são criados com texto já preenchido
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

        // Tamanho explícito para garantir que os campos apareçam
        double larguraCampo = 340;
        for (TextField tf : new TextField[]{txtTitulo, txtAno, txtGenero, txtImagem, txtLink, txtExtra}) {
            tf.setPrefWidth(larguraCampo);
            tf.setMinWidth(larguraCampo);
        }

        // Monta o formulário: Label em cima, campo embaixo (layout mais simples e confiável)
        form.getChildren().addAll(
            new Label("Título *:"),   txtTitulo,
            new Label("Ano:"),        txtAno,
            new Label("Gênero:"),     txtGenero,
            new Label("URL Capa:"),   txtImagem,
            new Label("URL Mídia:"),  txtLink,
            new Label(labelExtra),    txtExtra
        );

        Button btnSalvar = new Button("💾 Salvar Alterações");
        btnSalvar.setPrefWidth(larguraCampo);
        btnSalvar.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; "
                         + "-fx-font-weight: bold; -fx-padding: 10 0 10 0;");
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

                if (arquivo instanceof Album)       ((Album) arquivo).setBanda(txtExtra.getText().trim());
                else if (arquivo instanceof Filme)  ((Filme) arquivo).setDiretor(txtExtra.getText().trim());
                else if (arquivo instanceof Livro)  ((Livro) arquivo).setAutor(txtExtra.getText().trim());

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
        scroll.setStyle("-fx-background-color: #fff; -fx-background: #fff;");

        popup.setScene(new Scene(scroll, 420, 430));
        popup.show(); // show() em vez de showAndWait() — garante que o layout renderiza antes de travar
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

        boolean jaAvaliado = (arquivo.getNota() > 0);
        Label info = new Label((jaAvaliado ? "Revisar avaliação: " : "Avaliar: ") + arquivo.getNome());
        info.setStyle("-fx-font-weight: bold;");

        ComboBox<Integer> cbNota = new ComboBox<>(FXCollections.observableArrayList(1, 2, 3, 4, 5));
        cbNota.setValue(jaAvaliado ? arquivo.getNota() : 5);

        TextArea txtComent = new TextArea();
        txtComent.setPromptText("Escreva sua crítica/comentário...");
        txtComent.setPrefRowCount(4);
        if (jaAvaliado && arquivo.getComentario() != null) {
            txtComent.setText(arquivo.getComentario());
        }

        Button btnSalvar = new Button("💾 Salvar Avaliação");
        btnSalvar.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold;");
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
            new Label("Nota (1 a 5):"), cbNota,
            new Label("Comentário (opcional):"), txtComent,
            btnSalvar
        );

        popup.setScene(new Scene(box, 340, 320));
        popup.showAndWait();
        // NÃO chamamos mostrarPainelDetalhes aqui — quem chamou esse método já faz isso depois
    }

    // =====================================================================
    //  POPUP DE CADASTRO
    // =====================================================================
    private void abrirPopUpCadastro() {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Adicionar Nova Mídia");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(18));
        grid.setHgap(12);
        grid.setVgap(10);

        ColumnConstraints col0 = new ColumnConstraints(110);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col0, col1);

        ComboBox<String> cbTipo = new ComboBox<>(FXCollections.observableArrayList("Álbum", "Filme", "Livro"));
        cbTipo.setValue("Álbum");

        TextField txtT = new TextField();
        TextField txtA = new TextField();
        TextField txtG = new TextField();
        TextField txtI = new TextField(); txtI.setPromptText("Opcional (sorteio automático)");
        TextField txtL = new TextField(); txtL.setPromptText("Opcional (sorteio automático)");
        TextField txtE = new TextField();

        for (TextField tf : new TextField[]{txtT, txtA, txtG, txtI, txtL, txtE})
            tf.setMaxWidth(Double.MAX_VALUE);

        int row = 0;
        grid.add(new Label("Categoria:"),  0, row); grid.add(cbTipo, 1, row++);
        grid.add(new Label("Título *:"),   0, row); grid.add(txtT,   1, row++);
        grid.add(new Label("Ano:"),        0, row); grid.add(txtA,   1, row++);
        grid.add(new Label("Gênero:"),     0, row); grid.add(txtG,   1, row++);
        grid.add(new Label("Link Capa:"),  0, row); grid.add(txtI,   1, row++);
        grid.add(new Label("Link Mídia:"), 0, row); grid.add(txtL,   1, row++);
        grid.add(new Label("Info Extra:"), 0, row); grid.add(txtE,   1, row++);

        Button btnSalvar = new Button("💾 Salvar");
        btnSalvar.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 18 8 18;");
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
                if (!img.isEmpty()) {
                    novo.setImagem(img);
                } else {
                    if ("Álbum".equals(tipo)) novo.setImagem(imgsAlbuns[random.nextInt(imgsAlbuns.length)]);
                    else if ("Filme".equals(tipo)) novo.setImagem(imgsFilmes[random.nextInt(imgsFilmes.length)]);
                    else novo.setImagem(imgsLivros[random.nextInt(imgsLivros.length)]);
                }

                String lnk = txtL.getText().trim();
                if (!lnk.isEmpty()) {
                    novo.setLink(lnk);
                } else {
                    if ("Álbum".equals(tipo)) novo.setLink(vidsAlbuns[random.nextInt(vidsAlbuns.length)]);
                    else if ("Filme".equals(tipo)) novo.setLink(vidsFilmes[random.nextInt(vidsFilmes.length)]);
                    else novo.setLink(docsLivros[random.nextInt(docsLivros.length)]);
                }

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

        VBox raiz = new VBox(14, grid, btnSalvar);
        raiz.setPadding(new Insets(15));
        popup.setScene(new Scene(raiz, 420, 400));
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
        // Garante que o containerCentral é o que está visível
        voltarParaBiblioteca();
        // ArquivoNaoEncontradoException propagada do controller e tratada aqui na view
        try {
            renderizarTodasAsSecoes(cerebro.buscar_palavra_chave(termo));
        } catch (ArquivoNaoEncontradoException ex) {
            containerCentral.getChildren().clear();
            Label msg = new Label("❌ " + ex.getMessage());
            msg.setStyle("-fx-font-size: 13px; -fx-text-fill: #c0392b; -fx-padding: 20;");
            containerCentral.getChildren().add(msg);
        }
    }

    // =====================================================================
    //  UTILITÁRIOS
    // =====================================================================
    private String safe(String valor, String fallback) {
        return (valor == null || valor.trim().isEmpty()) ? fallback : valor.trim();
    }

    private Label labelInfo(String chave, String valor) {
        Label l = new Label(chave + valor);
        l.setStyle("-fx-font-size: 13px; -fx-text-fill: #4a5568;");
        l.setWrapText(true);
        return l;
    }

    private void carregarImagem(ImageView iv, String url, double w, double h) {
        iv.setFitWidth(w);
        iv.setFitHeight(h);
        iv.setPreserveRatio(true);
        if (url != null && !url.trim().isEmpty()) {
            try {
                iv.setImage(new Image(url.trim(), w, h, true, true, true));
            } catch (Exception ex) {
                iv.setImage(new Image(IMG_PLACEHOLDER, w, h, true, true, true));
            }
        } else {
            iv.setImage(new Image(IMG_PLACEHOLDER, w, h, true, true, true));
        }
    }
}