package view;

import model.*;
import controller.LastFmService;
import controller.LastFmService.ResultadoBusca;
import controller.MusicaController;
import exception.ArquivoNaoEncontradoException;
import exception.DadosInvalidosException;

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
import javafx.scene.text.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class TelaMusica {

    private final Stage palco;
    private final Scene cenaAnterior;
    private MusicaController ctrl;
    private LastFmService lastFm;
    private BorderPane layoutRaiz;
    private VBox containerCentral;
    private TextField txtBusca;

    private static final String BG      = "#0d0d1a";
    private static final String SURFACE = "#12122a";
    private static final String CARD    = "#1a1a3e";
    private static final String ACCENT  = "#7c3aed";
    private static final String ACCENT2 = "#a78bfa";
    private static final String GOLD    = "#f6c90e";
    private static final String TEXT    = "#f1f0ff";
    private static final String MUTED   = "#8b87b5";
    private static final String DANGER  = "#e74c3c";
    private static final String PH      = "https://placehold.co/120x160/1a1a3e/a78bfa?text=Musica";

    public TelaMusica(Stage palco, Scene cenaAnterior) {
        this.palco        = palco;
        this.cenaAnterior = cenaAnterior;
        this.ctrl         = new MusicaController();
        this.lastFm       = new LastFmService();
    }

    public void mostrar() {
        layoutRaiz = new BorderPane();
        layoutRaiz.setStyle("-fx-background-color:" + BG + ";");
        layoutRaiz.setPadding(new Insets(14));
        layoutRaiz.setTop(criarTopo());

        containerCentral = new VBox(18);
        containerCentral.setPadding(new Insets(10, 0, 10, 0));
        layoutRaiz.setCenter(scrollTransparente(containerCentral));
        layoutRaiz.setBottom(criarRodape());

        renderizarBiblioteca();
        palco.setScene(new Scene(layoutRaiz, 980, 660));
    }

    private VBox criarTopo() {
        VBox topo = new VBox(10);
        topo.setPadding(new Insets(0, 0, 12, 0));

        HBox linha1 = new HBox(12);
        linha1.setAlignment(Pos.CENTER_LEFT);
        Button btnVoltar = btn("⬅ Biblioteca", CARD, TEXT);
        btnVoltar.setOnAction(e -> palco.setScene(cenaAnterior));
        Label titulo = new Label("🎵 Módulo Musical");
        titulo.setStyle("-fx-font-size:22px;-fx-font-weight:bold;-fx-text-fill:" + ACCENT2 + ";");
        linha1.getChildren().addAll(btnVoltar, titulo);

        HBox linha2 = new HBox(8);
        linha2.setAlignment(Pos.CENTER_LEFT);

        txtBusca = new TextField();
        txtBusca.setPromptText("Buscar por título ou artista...");
        txtBusca.setPrefWidth(260);
        estilizarCampo(txtBusca);
        txtBusca.setOnAction(e -> acaoBuscar());

        Button btnBuscar = btn("🔍 Buscar", ACCENT, TEXT);
        btnBuscar.setOnAction(e -> acaoBuscar());

        MenuButton menuTipo = new MenuButton("🎵 Todos ▾");
        menuTipo.setStyle("-fx-background-color:" + CARD + ";-fx-text-fill:" + TEXT
                        + ";-fx-border-color:" + ACCENT + ";-fx-border-radius:6;"
                        + "-fx-background-radius:6;-fx-font-weight:bold;-fx-padding:6 12 6 12;");
        String[] tipos = {"Todos", "Álbum de Estúdio", "Single", "EP", "Álbum ao Vivo"};
        for (String tipo : tipos) {
            MenuItem mi = new MenuItem(tipo);
            mi.setOnAction(e -> {
                menuTipo.setText(tipo.equals("Todos") ? "🎵 Todos ▾" : tipo + " ▾");
                filtrarPorTipo(tipo);
            });
            menuTipo.getItems().add(mi);
        }

        Button btnAvaliados = btn("⭐ Avaliados", GOLD, "#0d0d1a");
        btnAvaliados.setOnAction(e -> mostrarAvaliados());

        Button btnDashboard = btn("📊 Dashboard", "#27ae60", TEXT);
        btnDashboard.setOnAction(e -> mostrarDashboard());

        Button btnEditar  = btn("✏️ Editar",   "#3498db", TEXT);
        Button btnRemover = btn("🗑️ Remover",  DANGER,    TEXT);
        btnEditar.setOnAction(e  -> fluxoEditar());
        btnRemover.setOnAction(e -> fluxoRemover());

        linha2.getChildren().addAll(menuTipo, txtBusca, btnBuscar,
                                    btnAvaliados, btnDashboard, btnEditar, btnRemover);
        topo.getChildren().addAll(linha1, linha2);
        return topo;
    }

    private HBox criarRodape() {
        HBox r = new HBox();
        r.setPadding(new Insets(10, 0, 0, 0));
        Button btnAdd = btn("➕ Adicionar via Last.fm", "#27ae60", TEXT);
        btnAdd.setOnAction(e -> fluxoCadastroLastFm());
        r.getChildren().add(btnAdd);
        return r;
    }

    private void renderizarBiblioteca() { renderizar(ctrl.getBiblioteca()); }

    private void renderizar(List<Musica> lista) {
        containerCentral.getChildren().clear();
        if (lista.isEmpty()) {
            Label msg = new Label("Nenhuma obra cadastrada ainda.\nClique em ➕ Adicionar via Last.fm para começar.");
            msg.setStyle("-fx-font-size:14px;-fx-text-fill:" + MUTED + ";-fx-padding:30;-fx-text-alignment:center;");
            msg.setWrapText(true);
            containerCentral.getChildren().add(msg);
            return;
        }
        String[] ordem = {"Álbum de Estúdio", "Single", "EP", "Álbum ao Vivo"};
        for (String tipo : ordem) {
            List<Musica> grupo = lista.stream()
                .filter(m -> tipo.equals(m.getTipo())).collect(Collectors.toList());
            if (!grupo.isEmpty()) containerCentral.getChildren().add(criarSecao(tipo, grupo));
        }
    }

    private void filtrarPorTipo(String tipo) {
        List<Musica> lista = tipo.equals("Todos") ? ctrl.getBiblioteca()
            : ctrl.getBiblioteca().stream()
                .filter(m -> tipo.equals(m.getTipo())).collect(Collectors.toList());
        containerCentral.getChildren().clear();
        renderizar(lista);
    }

    private VBox criarSecao(String titulo, List<Musica> itens) {
        VBox secao = new VBox(6);
        Label lbl = new Label(titulo);
        lbl.setStyle("-fx-font-size:14px;-fx-font-weight:bold;-fx-text-fill:" + ACCENT2 + ";");

        HBox linha = new HBox(5);
        linha.setAlignment(Pos.CENTER);
        HBox cards = new HBox(12);
        cards.setPadding(new Insets(4));
        for (Musica m : itens) cards.getChildren().add(criarCard(m));

        ScrollPane sp = new ScrollPane(cards);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        sp.setFitToHeight(true);
        sp.setStyle("-fx-background-color:transparent;-fx-background:transparent;");
        HBox.setHgrow(sp, Priority.ALWAYS);

        Button esq = btn("◀", CARD, TEXT);
        Button dir = btn("▶", CARD, TEXT);
        esq.setOnAction(e -> sp.setHvalue(Math.max(0, sp.getHvalue() - 0.25)));
        dir.setOnAction(e -> sp.setHvalue(Math.min(1, sp.getHvalue() + 0.25)));

        linha.getChildren().addAll(esq, sp, dir);
        secao.getChildren().addAll(lbl, linha);
        return secao;
    }

    private VBox criarCard(Musica m) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(6));
        card.setPrefWidth(136); card.setMaxWidth(136);
        card.setStyle(estiloCard(false));

        ImageView iv = new ImageView();
        iv.setFitWidth(120); iv.setFitHeight(155); iv.setPreserveRatio(false);
        carregarImg(iv, m.getUrlCapa(), 120, 155);

        Label nome = new Label(m.getNome());
        nome.setStyle("-fx-font-weight:bold;-fx-font-size:11px;-fx-text-fill:" + TEXT + ";-fx-text-alignment:center;");
        nome.setWrapText(true); nome.setMaxWidth(122); nome.setAlignment(Pos.CENTER);

        Label artista = new Label(m.getArtista() != null ? m.getArtista() : "");
        artista.setStyle("-fx-font-size:10px;-fx-text-fill:" + MUTED + ";-fx-text-alignment:center;");
        artista.setWrapText(true); artista.setMaxWidth(122); artista.setAlignment(Pos.CENTER);

        card.getChildren().addAll(iv, nome, artista);
        card.setOnMouseClicked(ev -> mostrarDetalhes(m));
        card.setOnMouseEntered(ev -> card.setStyle(estiloCard(true)));
        card.setOnMouseExited(ev  -> card.setStyle(estiloCard(false)));
        return card;
    }

    private String estiloCard(boolean hover) {
        return "-fx-border-color:" + (hover ? ACCENT2 : ACCENT)
             + ";-fx-border-radius:8;-fx-background-color:" + (hover ? ACCENT + "44" : CARD)
             + ";-fx-background-radius:8;-fx-cursor:hand;";
    }

    private void mostrarDetalhes(Musica m) {
        BorderPane painel = new BorderPane();
        painel.setStyle("-fx-background-color:" + BG + ";");
        painel.setPadding(new Insets(20));

        Button voltar = btn("⬅ Voltar", CARD, TEXT);
        voltar.setOnAction(e -> voltarCentro());
        painel.setTop(voltar);
        BorderPane.setMargin(voltar, new Insets(0, 0, 14, 0));

        HBox linha = new HBox(22);
        linha.setAlignment(Pos.TOP_LEFT);

        ImageView iv = new ImageView();
        iv.setFitWidth(160); iv.setFitHeight(210); iv.setPreserveRatio(false);
        carregarImg(iv, m.getUrlCapa(), 160, 210);

        VBox info = new VBox(8);
        info.setStyle("-fx-background-color:" + SURFACE + ";-fx-padding:16;-fx-background-radius:10;");
        info.setPrefWidth(500);

        Label lblNome = new Label(m.getNome());
        lblNome.setStyle("-fx-font-size:20px;-fx-font-weight:bold;-fx-text-fill:" + TEXT + ";");
        lblNome.setWrapText(true);
        info.getChildren().add(lblNome);

        for (String l : m.exibirInformacoes().split("\n")) {
            Label ll = new Label(l);
            ll.setStyle("-fx-font-size:13px;-fx-text-fill:" + TEXT + ";");
            ll.setWrapText(true);
            info.getChildren().add(ll);
        }

        if (!m.getFaixas().isEmpty()) {
            info.getChildren().add(new Separator());
            Label lblTrack = new Label("🎼 Tracklist:");
            lblTrack.setStyle("-fx-font-weight:bold;-fx-text-fill:" + ACCENT2 + ";-fx-font-size:13px;");
            info.getChildren().add(lblTrack);
            for (Faixa f : m.getFaixas()) {
                Label lf = new Label(f.toString());
                lf.setStyle("-fx-font-size:12px;-fx-text-fill:" + MUTED + ";");
                info.getChildren().add(lf);
            }
        }

        HBox.setHgrow(info, Priority.ALWAYS);
        linha.getChildren().addAll(iv, info);

        Button btnAvaliar = btn(m.getNota() > 0 ? "🔄 Revisar Avaliação" : "⭐ Avaliar", GOLD, "#0d0d1a");
        btnAvaliar.setOnAction(e -> { popupAvaliar(m); mostrarDetalhes(m); });

        VBox centro = new VBox(14, linha, btnAvaliar);
        ScrollPane sp = new ScrollPane(centro);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:transparent;-fx-background:transparent;");
        painel.setCenter(sp);
        layoutRaiz.setCenter(painel);
    }

    private void mostrarDashboard() {
        BorderPane painel = new BorderPane();
        painel.setStyle("-fx-background-color:" + BG + ";");
        painel.setPadding(new Insets(24));

        Button voltar = btn("⬅ Voltar", CARD, TEXT);
        voltar.setOnAction(e -> voltarCentro());
        painel.setTop(voltar);
        BorderPane.setMargin(voltar, new Insets(0, 0, 16, 0));

        Label titulo = new Label("📊 Dashboard Musical");
        titulo.setStyle("-fx-font-size:20px;-fx-font-weight:bold;-fx-text-fill:" + ACCENT2 + ";");

        List<Musica> bib = ctrl.getBiblioteca();
        double mediaNota = ctrl.getMediaNotas();

        TilePane tiles = new TilePane(16, 16);
        tiles.setPrefColumns(3);
        tiles.setPadding(new Insets(16, 0, 0, 0));
        tiles.getChildren().addAll(
            statCard("🎵 Total de Obras",    String.valueOf(bib.size())),
            statCard("🎼 Total de Faixas",   String.valueOf(ctrl.getTotalFaixas())),
            statCard("⏱️ Tempo de Escuta",   ctrl.getDuracaoTotalFormatada()),
            statCard("🎤 Artista Top",        ctrl.getArtistaTop()),
            statCard("⭐ Média das Notas",    mediaNota > 0 ? String.format("%.1f / 5", mediaNota) : "—"),
            statCard("📝 Avaliados",          bib.stream().filter(m -> m.getNota() > 0).count() + " de " + bib.size()),
            statCard("💿 Álbuns de Estúdio", String.valueOf(bib.stream().filter(m -> m instanceof AlbumMusical).count())),
            statCard("🎧 Singles",            String.valueOf(bib.stream().filter(m -> m instanceof Single).count())),
            statCard("📀 EPs",               String.valueOf(bib.stream().filter(m -> m instanceof EP).count())),
            statCard("🎤 Álbuns ao Vivo",    String.valueOf(bib.stream().filter(m -> m instanceof LiveAlbum).count()))
        );

        VBox corpo = new VBox(12, titulo, tiles);
        ScrollPane sp = new ScrollPane(corpo);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:transparent;-fx-background:transparent;");
        painel.setCenter(sp);
        layoutRaiz.setCenter(painel);
    }

    private VBox statCard(String label, String valor) {
        VBox card = new VBox(6);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(18));
        card.setMinWidth(180);
        card.setStyle("-fx-background-color:" + CARD + ";-fx-background-radius:12;"
                    + "-fx-border-color:" + ACCENT + ";-fx-border-radius:12;");
        Label lVal = new Label(valor);
        lVal.setStyle("-fx-font-size:24px;-fx-font-weight:bold;-fx-text-fill:" + ACCENT2 + ";");
        Label lLbl = new Label(label);
        lLbl.setStyle("-fx-font-size:12px;-fx-text-fill:" + MUTED + ";");
        lLbl.setWrapText(true); lLbl.setAlignment(Pos.CENTER);
        card.getChildren().addAll(lVal, lLbl);
        return card;
    }

    private void mostrarAvaliados() {
        List<Musica> avaliados = ctrl.getBiblioteca().stream()
            .filter(m -> m.getNota() > 0).collect(Collectors.toList());

        BorderPane painel = new BorderPane();
        painel.setStyle("-fx-background-color:" + BG + ";");
        painel.setPadding(new Insets(20));

        Button voltar = btn("⬅ Voltar", CARD, TEXT);
        voltar.setOnAction(e -> voltarCentro());
        painel.setTop(voltar);
        BorderPane.setMargin(voltar, new Insets(0, 0, 14, 0));

        if (avaliados.isEmpty()) {
            Label msg = new Label("Nenhum item avaliado ainda.");
            msg.setStyle("-fx-font-size:14px;-fx-text-fill:" + MUTED + ";-fx-padding:20;");
            painel.setCenter(msg);
            layoutRaiz.setCenter(painel);
            return;
        }

        HBox cards = new HBox(14);
        cards.setPadding(new Insets(10, 0, 0, 0));
        for (Musica m : avaliados) {
            VBox c = criarCard(m);
            c.setOnMouseClicked(ev -> mostrarDetalheAvaliacao(m));
            c.setOnMouseEntered(ev -> c.setStyle("-fx-border-color:" + GOLD + ";-fx-border-radius:8;"
                + "-fx-background-color:" + ACCENT + "44;-fx-background-radius:8;-fx-cursor:hand;"));
            c.setOnMouseExited(ev -> c.setStyle(estiloCard(false)));
            cards.getChildren().add(c);
        }

        ScrollPane sp = new ScrollPane(cards);
        sp.setFitToHeight(true); sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setStyle("-fx-background-color:transparent;-fx-background:transparent;");

        Label titulo = new Label("⭐ Itens Avaliados");
        titulo.setStyle("-fx-font-size:18px;-fx-font-weight:bold;-fx-text-fill:" + GOLD + ";");

        painel.setCenter(new VBox(10, titulo, sp));
        layoutRaiz.setCenter(painel);
    }

    private void mostrarDetalheAvaliacao(Musica m) {
        BorderPane painel = new BorderPane();
        painel.setStyle("-fx-background-color:" + BG + ";");
        painel.setPadding(new Insets(24));

        Button voltar = btn("⬅ Voltar para Avaliados", CARD, TEXT);
        voltar.setOnAction(e -> mostrarAvaliados());
        painel.setTop(voltar);
        BorderPane.setMargin(voltar, new Insets(0, 0, 16, 0));

        HBox linha = new HBox(24);
        linha.setAlignment(Pos.TOP_LEFT);

        ImageView iv = new ImageView();
        iv.setFitWidth(150); iv.setFitHeight(195); iv.setPreserveRatio(false);
        carregarImg(iv, m.getUrlCapa(), 150, 195);

        VBox lado = new VBox(12);

        Label lblNome = new Label(m.getNome());
        lblNome.setStyle("-fx-font-size:22px;-fx-font-weight:bold;-fx-text-fill:" + TEXT + ";");

        Circle circ = new Circle(38);
        circ.setFill(Color.web(GOLD));
        Text txtNota = new Text(String.valueOf(m.getNota()));
        txtNota.setFont(Font.font("System", FontWeight.BOLD, 30));
        txtNota.setFill(Color.web("#0d0d1a"));
        StackPane circNota = new StackPane(circ, txtNota);
        circNota.setPrefSize(76, 76);

        Label lblNotaLabel = new Label("Nota");
        lblNotaLabel.setStyle("-fx-font-size:12px;-fx-text-fill:" + MUTED + ";");
        VBox blocoNota = new VBox(4, circNota, lblNotaLabel);
        blocoNota.setAlignment(Pos.CENTER);

        Label lblCritTit = new Label("📝 Crítica:");
        lblCritTit.setStyle("-fx-font-weight:bold;-fx-text-fill:" + MUTED + ";");

        String coment = m.getComentario() != null && !m.getComentario().isEmpty()
                      ? m.getComentario() : "Nenhum comentário registrado.";
        Label lblCrit = new Label(coment);
        lblCrit.setWrapText(true); lblCrit.setMaxWidth(420);
        lblCrit.setStyle("-fx-font-style:italic;-fx-text-fill:" + TEXT
                       + ";-fx-background-color:" + SURFACE + ";-fx-padding:12;-fx-background-radius:8;");

        Button btnRevisar = btn("🔄 Revisar Avaliação", GOLD, "#0d0d1a");
        btnRevisar.setOnAction(e -> { popupAvaliar(m); mostrarDetalheAvaliacao(m); });

        lado.getChildren().addAll(lblNome, blocoNota, lblCritTit, lblCrit, btnRevisar);
        linha.getChildren().addAll(iv, lado);

        ScrollPane sp = new ScrollPane(linha);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:transparent;-fx-background:transparent;");
        painel.setCenter(sp);
        layoutRaiz.setCenter(painel);
    }

    private void fluxoCadastroLastFm() {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Adicionar via Last.fm");

        VBox raiz = popupBase();

        Label step1 = labelPopup("1. Digite o nome do álbum/single:");
        TextField txtNome = new TextField(); estilizarCampo(txtNome); txtNome.setPrefWidth(360);

        Label stepTipo = labelPopup("2. Selecione o tipo:");
        ComboBox<String> cbTipo = new ComboBox<>(FXCollections.observableArrayList(
            "Álbum de Estúdio", "Single", "EP", "Álbum ao Vivo"));
        cbTipo.setValue("Álbum de Estúdio");
        cbTipo.setStyle("-fx-background-color:" + CARD + ";-fx-text-fill:" + TEXT + ";");

        Button btnBuscar = btn("🔍 Buscar no Last.fm", ACCENT, TEXT);

        VBox listaResultados = new VBox(6);
        listaResultados.getChildren().add(labelPopup("Aguardando busca..."));

        btnBuscar.setOnAction(e -> {
            String termo = txtNome.getText().trim();
            if (termo.isEmpty()) { new Alert(Alert.AlertType.WARNING, "Digite o nome para buscar.").showAndWait(); return; }

            btnBuscar.setDisable(true); btnBuscar.setText("⏳ Buscando...");
            listaResultados.getChildren().clear();
            listaResultados.getChildren().add(labelPopup("Consultando Last.fm..."));

            new Thread(() -> {
                try {
                    List<ResultadoBusca> resultados = lastFm.buscarAlbum(termo);
                    Platform.runLater(() -> {
                        listaResultados.getChildren().clear();
                        listaResultados.getChildren().add(labelPopup("Clique para selecionar:"));
                        for (ResultadoBusca r : resultados) {
                            HBox itemLinha = new HBox(10);
                            itemLinha.setAlignment(Pos.CENTER_LEFT);
                            itemLinha.setPadding(new Insets(6));
                            itemLinha.setStyle("-fx-background-color:" + CARD
                                            + ";-fx-border-color:" + ACCENT
                                            + ";-fx-border-radius:6;-fx-background-radius:6;-fx-cursor:hand;");

                            ImageView thumb = new ImageView();
                            thumb.setFitWidth(40); thumb.setFitHeight(40); thumb.setPreserveRatio(false);
                            carregarImg(thumb, r.urlCapa, 40, 40);

                            Label lbl = new Label(r.toString());
                            lbl.setStyle("-fx-text-fill:" + TEXT + ";-fx-font-size:12px;");
                            lbl.setWrapText(true);

                            itemLinha.getChildren().addAll(thumb, lbl);
                            itemLinha.setOnMouseClicked(ev -> {
                                popup.close();
                                confirmarEImportar(r, cbTipo.getValue());
                            });
                            itemLinha.setOnMouseEntered(ev -> itemLinha.setStyle(
                                "-fx-background-color:" + ACCENT + "44;-fx-border-color:" + ACCENT2
                                + ";-fx-border-radius:6;-fx-background-radius:6;-fx-cursor:hand;"));
                            itemLinha.setOnMouseExited(ev -> itemLinha.setStyle(
                                "-fx-background-color:" + CARD + ";-fx-border-color:" + ACCENT
                                + ";-fx-border-radius:6;-fx-background-radius:6;-fx-cursor:hand;"));

                            listaResultados.getChildren().add(itemLinha);
                        }
                        btnBuscar.setDisable(false); btnBuscar.setText("🔍 Buscar no Last.fm");
                    });
                } catch (ArquivoNaoEncontradoException ex) {
                    Platform.runLater(() -> {
                        listaResultados.getChildren().clear();
                        listaResultados.getChildren().add(labelPopup("❌ " + ex.getMessage()));
                        btnBuscar.setDisable(false); btnBuscar.setText("🔍 Buscar no Last.fm");
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        listaResultados.getChildren().clear();
                        listaResultados.getChildren().add(labelPopup("❌ Erro de conexão: " + ex.getMessage()));
                        btnBuscar.setDisable(false); btnBuscar.setText("🔍 Buscar no Last.fm");
                    });
                }
            }).start();
        });

        txtNome.setOnAction(e -> btnBuscar.fire());

        ScrollPane sp = new ScrollPane(listaResultados);
        sp.setFitToWidth(true); sp.setPrefHeight(240);
        sp.setStyle("-fx-background-color:transparent;-fx-background:transparent;");

        raiz.getChildren().addAll(step1, txtNome, stepTipo, cbTipo, btnBuscar, new Separator(), sp);
        popup.setScene(new Scene(raiz, 460, 520));
        popup.showAndWait();
    }

    private void confirmarEImportar(ResultadoBusca resultado, String tipo) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Importando...");
        VBox raiz = popupBase();
        Label info = new Label("Importando: " + resultado.nome + " — " + resultado.artista);
        info.setStyle("-fx-font-weight:bold;-fx-text-fill:" + TEXT + ";-fx-font-size:14px;");
        info.setWrapText(true);
        Label loading = new Label("⏳ Buscando faixas e detalhes no Last.fm...");
        loading.setStyle("-fx-text-fill:" + MUTED + ";");
        raiz.getChildren().addAll(info, loading);
        popup.setScene(new Scene(raiz, 380, 140));
        popup.show();

        new Thread(() -> {
            try {
                Musica nova = criarMusicaPorTipo(tipo, resultado.nome, resultado.artista);
                lastFm.importarAlbumCompleto(nova, resultado.nome, resultado.artista);
                ctrl.adicionar(nova);
                Platform.runLater(() -> {
                    popup.close();
                    renderizarBiblioteca();
                    Alert ok = new Alert(Alert.AlertType.INFORMATION);
                    ok.setTitle("Importado!");
                    ok.setHeaderText(resultado.nome + " adicionado com sucesso!");
                    ok.setContentText(nova.getFaixas().size() + " faixas importadas.");
                    ok.showAndWait();
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    popup.close();
                    new Alert(Alert.AlertType.ERROR, "Erro ao importar: " + ex.getMessage()).showAndWait();
                });
            }
        }).start();
    }

    private Musica criarMusicaPorTipo(String tipo, String nome, String artista) {
        switch (tipo) {
            case "Single":        return new Single(nome, artista);
            case "EP":            return new EP(nome, artista);
            case "Álbum ao Vivo": return new LiveAlbum(nome, artista);
            default:              return new AlbumMusical(nome, artista);
        }
    }

    private void fluxoRemover() {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Remover");
        VBox raiz = popupBase();

        TextField campo = new TextField(); estilizarCampo(campo);
        campo.setPromptText("Nome da obra ou artista...");
        Button btnB = btn("🔍 Buscar", ACCENT, TEXT);
        VBox lista = new VBox(6);
        lista.getChildren().add(labelPopup("Aguardando busca..."));

        btnB.setOnAction(e -> {
            try {
                List<Musica> found = ctrl.buscar(campo.getText().trim());
                lista.getChildren().clear();
                for (Musica m : found) {
                    Button bi = new Button("🎵 " + m.getNome() + " — " + m.getArtista());
                    bi.setMaxWidth(Double.MAX_VALUE);
                    bi.setStyle("-fx-background-color:" + CARD + ";-fx-text-fill:" + TEXT
                               + ";-fx-border-color:" + ACCENT + ";-fx-border-radius:6;"
                               + "-fx-background-radius:6;-fx-padding:8;-fx-cursor:hand;");
                    bi.setOnAction(ev -> {
                        Alert c = new Alert(Alert.AlertType.CONFIRMATION,
                            "Remover \"" + m.getNome() + "\"? Esta ação não pode ser desfeita.");
                        c.showAndWait().ifPresent(r -> {
                            if (r == ButtonType.OK) {
                                try { ctrl.remover(m.getNome()); renderizarBiblioteca(); popup.close(); }
                                catch (Exception ex) { new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait(); }
                            }
                        });
                    });
                    lista.getChildren().add(bi);
                }
            } catch (ArquivoNaoEncontradoException ex) {
                lista.getChildren().clear();
                lista.getChildren().add(labelPopup("❌ " + ex.getMessage()));
            }
        });
        campo.setOnAction(e -> btnB.fire());

        ScrollPane sp = new ScrollPane(lista);
        sp.setFitToWidth(true); sp.setPrefHeight(180);
        sp.setStyle("-fx-background-color:transparent;-fx-background:transparent;");
        raiz.getChildren().addAll(labelPopup("Buscar obra para remover:"), campo, btnB, new Separator(), sp);
        popup.setScene(new Scene(raiz, 400, 370)); popup.showAndWait();
    }

    private void fluxoEditar() {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Editar");
        VBox raiz = popupBase();

        TextField campo = new TextField(); estilizarCampo(campo);
        campo.setPromptText("Nome da obra ou artista...");
        Button btnB = btn("🔍 Buscar", ACCENT, TEXT);
        VBox lista = new VBox(6);
        lista.getChildren().add(labelPopup("Aguardando busca..."));

        btnB.setOnAction(e -> {
            try {
                List<Musica> found = ctrl.buscar(campo.getText().trim());
                lista.getChildren().clear();
                for (Musica m : found) {
                    Button bi = new Button("🎵 " + m.getNome() + " — " + m.getArtista());
                    bi.setMaxWidth(Double.MAX_VALUE);
                    bi.setStyle("-fx-background-color:" + CARD + ";-fx-text-fill:" + TEXT
                               + ";-fx-border-color:" + ACCENT + ";-fx-border-radius:6;"
                               + "-fx-background-radius:6;-fx-padding:8;-fx-cursor:hand;");
                    bi.setOnAction(ev -> { popup.close(); popupEdicao(m); });
                    lista.getChildren().add(bi);
                }
            } catch (ArquivoNaoEncontradoException ex) {
                lista.getChildren().clear();
                lista.getChildren().add(labelPopup("❌ " + ex.getMessage()));
            }
        });
        campo.setOnAction(e -> btnB.fire());

        ScrollPane sp = new ScrollPane(lista);
        sp.setFitToWidth(true); sp.setPrefHeight(180);
        sp.setStyle("-fx-background-color:transparent;-fx-background:transparent;");
        raiz.getChildren().addAll(labelPopup("Buscar obra para editar:"), campo, btnB, new Separator(), sp);
        popup.setScene(new Scene(raiz, 400, 370)); popup.showAndWait();
    }

    private void popupEdicao(Musica m) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Editando: " + m.getNome());
        VBox form = popupBase();

        TextField tNome    = campo(m.getNome());
        TextField tArtista = campo(m.getArtista());
        TextField tAno     = campo(m.getAnoLancamento() > 0 ? String.valueOf(m.getAnoLancamento()) : "");
        TextField tGenero  = campo(m.getGenero());
        TextField tCapa    = campo(m.getUrlCapa());

        form.getChildren().addAll(
            labelPopup("Título:"),   tNome,
            labelPopup("Artista:"),  tArtista,
            labelPopup("Ano:"),      tAno,
            labelPopup("Gênero:"),   tGenero,
            labelPopup("URL Capa:"), tCapa
        );

        if (m instanceof LiveAlbum) {
            TextField tLocal  = campo(((LiveAlbum) m).getLocalShow());
            TextField tCidade = campo(((LiveAlbum) m).getCidadeShow());
            form.getChildren().addAll(labelPopup("Local do Show:"), tLocal, labelPopup("Cidade:"), tCidade);
            Button salvar = btnSalvar();
            salvar.setOnAction(e -> {
                try {
                    String orig = m.getNome();
                    aplicarEdicaoBase(m, tNome, tArtista, tAno, tGenero, tCapa);
                    ((LiveAlbum) m).setLocalShow(tLocal.getText().trim());
                    ((LiveAlbum) m).setCidadeShow(tCidade.getText().trim());
                    ctrl.editar(orig, m); renderizarBiblioteca(); popup.close();
                } catch (Exception ex) { new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait(); }
            });
            form.getChildren().add(salvar);
        } else {
            Button salvar = btnSalvar();
            salvar.setOnAction(e -> {
                try {
                    String orig = m.getNome();
                    aplicarEdicaoBase(m, tNome, tArtista, tAno, tGenero, tCapa);
                    ctrl.editar(orig, m); renderizarBiblioteca(); popup.close();
                } catch (Exception ex) { new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait(); }
            });
            form.getChildren().add(salvar);
        }

        ScrollPane sp = new ScrollPane(form);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:" + SURFACE + ";-fx-background:" + SURFACE + ";");
        popup.setScene(new Scene(sp, 420, 420));
        popup.show();
    }

    private void aplicarEdicaoBase(Musica m, TextField tNome, TextField tArtista,
                                    TextField tAno, TextField tGenero, TextField tCapa) {
        m.setNome(tNome.getText().trim());
        m.setArtista(tArtista.getText().trim());
        m.setGenero(tGenero.getText().trim());
        m.setUrlCapa(tCapa.getText().trim());
        if (!tAno.getText().trim().isEmpty())
            m.setAnoLancamento(Integer.parseInt(tAno.getText().trim()));
    }

    private void popupAvaliar(Musica m) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Avaliar: " + m.getNome());
        VBox box = popupBase();

        ComboBox<Integer> cbNota = new ComboBox<>(FXCollections.observableArrayList(1, 2, 3, 4, 5));
        cbNota.setValue(m.getNota() > 0 ? m.getNota() : 5);
        cbNota.setStyle("-fx-background-color:" + CARD + ";-fx-text-fill:" + TEXT + ";");

        TextArea txtC = new TextArea(m.getComentario() != null ? m.getComentario() : "");
        txtC.setPromptText("Escreva sua crítica...");
        txtC.setPrefRowCount(4);
        txtC.setStyle("-fx-background-color:" + CARD + ";-fx-text-fill:" + TEXT
                    + ";-fx-control-inner-background:" + CARD + ";");

        Button salvar = btn("💾 Salvar Avaliação", GOLD, "#0d0d1a");
        salvar.setDefaultButton(true);
        salvar.setOnAction(e -> {
            try {
                m.avaliar(cbNota.getValue(), txtC.getText());
                ctrl.editar(m.getNome(), m);
                popup.close();
            } catch (Exception ex) { new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait(); }
        });

        box.getChildren().addAll(
            labelPopup("Nota (1-5):"), cbNota,
            labelPopup("Comentário (opcional):"), txtC, salvar
        );
        popup.setScene(new Scene(box, 360, 320));
        popup.showAndWait();
    }

    private void acaoBuscar() {
        String termo = txtBusca.getText().trim();
        if (termo.isEmpty()) { renderizarBiblioteca(); return; }
        try {
            renderizar(ctrl.buscar(termo));
        } catch (ArquivoNaoEncontradoException ex) {
            containerCentral.getChildren().clear();
            Label msg = new Label("❌ " + ex.getMessage());
            msg.setStyle("-fx-text-fill:" + DANGER + ";-fx-padding:20;");
            containerCentral.getChildren().add(msg);
        }
    }

    private void voltarCentro() {
        containerCentral = new VBox(18);
        containerCentral.setPadding(new Insets(10, 0, 10, 0));
        layoutRaiz.setCenter(scrollTransparente(containerCentral));
        renderizarBiblioteca();
    }

    // ── Utilitários ──────────────────────────────────────────────────────
    private VBox popupBase() {
        VBox v = new VBox(8); v.setPadding(new Insets(18));
        v.setStyle("-fx-background-color:" + SURFACE + ";"); return v;
    }
    private TextField campo(String val) {
        TextField tf = new TextField(val != null ? val : "");
        tf.setPrefWidth(340); tf.setMinWidth(340); estilizarCampo(tf); return tf;
    }
    private Button btn(String t, String bg, String fg) {
        Button b = new Button(t);
        b.setStyle("-fx-background-color:" + bg + ";-fx-text-fill:" + fg
                 + ";-fx-font-weight:bold;-fx-padding:7 14 7 14;-fx-background-radius:6;-fx-cursor:hand;");
        return b;
    }
    private Button btnSalvar() { return btn("💾 Salvar Alterações", "#27ae60", TEXT); }
    private Label labelPopup(String t) {
        Label l = new Label(t); l.setStyle("-fx-font-size:12px;-fx-text-fill:" + MUTED + ";"); return l;
    }
    private void estilizarCampo(TextField tf) {
        tf.setStyle("-fx-background-color:" + CARD + ";-fx-text-fill:" + TEXT
                  + ";-fx-prompt-text-fill:" + MUTED + ";-fx-border-color:" + ACCENT
                  + ";-fx-border-radius:5;-fx-background-radius:5;-fx-padding:5 8 5 8;");
    }
    private ScrollPane scrollTransparente(VBox c) {
        ScrollPane sp = new ScrollPane(c); sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:transparent;-fx-background:transparent;"); return sp;
    }
    private void carregarImg(ImageView iv, String url, double w, double h) {
        iv.setFitWidth(w); iv.setFitHeight(h); iv.setPreserveRatio(false);
        try {
            iv.setImage(new Image((url != null && !url.isEmpty()) ? url : PH, w, h, false, true, true));
        } catch (Exception ex) { iv.setImage(new Image(PH, w, h, false, true, true)); }
    }
}