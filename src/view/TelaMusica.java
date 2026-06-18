package view;

import model.*;
import controller.LastFmService;
import controller.LastFmService.ResultadoBusca;
import controller.MusicaController;
import controller.ArtistaController;
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
    private ArtistaController ctrlArtista;
    private controller.LastFmService lastFm;
    private BorderPane layoutRaiz;
    private VBox containerCentral;
    private TextField txtBusca;

    // Paleta
    private static final String BG      = "#0d0d1a";
    private static final String SURFACE = "#12122a";
    private static final String CARD    = "#1a1a3e";
    private static final String ACCENT  = "#7c3aed";
    private static final String ACCENT2 = "#a78bfa";
    private static final String GOLD    = "#f6c90e";
    private static final String TEXT    = "#f1f0ff";
    private static final String MUTED   = "#8b87b5";
    private static final String DANGER  = "#e74c3c";
    private static final String GREEN   = "#27ae60";
    private static final String PH      = "https://placehold.co/120x160/1a1a3e/a78bfa?text=🎵";
    private static final String PH_ART  = "https://placehold.co/120x120/1a1a3e/a78bfa?text=🎤";

    public TelaMusica(Stage palco, Scene cenaAnterior) {
        this.palco        = palco;
        this.cenaAnterior = cenaAnterior;
        this.ctrl         = new MusicaController();
        this.ctrlArtista  = new ArtistaController();
        this.lastFm       = new controller.LastFmService();
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

    // ── TOPO ─────────────────────────────────────────────────────────────
    private VBox criarTopo() {
        VBox topo = new VBox(10);
        topo.setPadding(new Insets(0, 0, 12, 0));

        // Linha 1: voltar + título
        HBox l1 = new HBox(12);
        l1.setAlignment(Pos.CENTER_LEFT);
        Button btnVoltar = btn("⬅ Biblioteca", CARD, TEXT);
        btnVoltar.setOnAction(e -> palco.setScene(cenaAnterior));
        Label titulo = new Label("🎵 Módulo Musical");
        titulo.setStyle("-fx-font-size:22px;-fx-font-weight:bold;-fx-text-fill:" + ACCENT2 + ";");
        l1.getChildren().addAll(btnVoltar, titulo);

        // Linha 2: busca + filtros + ações
        HBox l2 = new HBox(8);
        l2.setAlignment(Pos.CENTER_LEFT);

        txtBusca = new TextField();
        txtBusca.setPromptText("Buscar por título ou artista...");
        txtBusca.setPrefWidth(230);
        estilizarCampo(txtBusca);
        txtBusca.setOnAction(e -> acaoBuscar());

        Button btnBuscar = btn("🔍", ACCENT, TEXT);
        btnBuscar.setOnAction(e -> acaoBuscar());

        // Filtro tipo
        MenuButton menuTipo = new MenuButton("🎵 Todos ▾");
        menuTipo.setStyle("-fx-background-color:" + CARD + ";-fx-text-fill:" + TEXT
                + ";-fx-border-color:" + ACCENT + ";-fx-border-radius:6;"
                + "-fx-background-radius:6;-fx-font-weight:bold;-fx-padding:6 10 6 10;");
        for (String tipo : new String[]{"Todos","Álbum de Estúdio","Single","EP","Álbum ao Vivo"}) {
            MenuItem mi = new MenuItem(tipo);
            mi.setOnAction(e -> {
                menuTipo.setText(tipo.equals("Todos") ? "🎵 Todos ▾" : tipo + " ▾");
                filtrarPorTipo(tipo);
            });
            menuTipo.getItems().add(mi);
        }

        Button btnArtistas  = btn("🎤 Artistas",   "#533483", TEXT);
        Button btnPlaylist  = btn("▶ Playlist",    GREEN,     TEXT);
        Button btnAvaliados = btn("⭐ Avaliados",  GOLD,      "#0d0d1a");
        Button btnDash      = btn("📊 Dashboard",  "#1a6b3a", TEXT);
        Button btnEditar    = btn("✏️ Editar",     "#2980b9", TEXT);
        Button btnRemover   = btn("🗑️ Remover",   DANGER,    TEXT);

        btnArtistas.setOnAction(e  -> mostrarArtistas());
        btnPlaylist.setOnAction(e  -> popupPlaylist());
        btnAvaliados.setOnAction(e -> mostrarAvaliados());
        btnDash.setOnAction(e      -> mostrarDashboard());
        btnEditar.setOnAction(e    -> fluxoEditar());
        btnRemover.setOnAction(e   -> fluxoRemover());

        l2.getChildren().addAll(menuTipo, txtBusca, btnBuscar,
                btnArtistas, btnPlaylist, btnAvaliados, btnDash, btnEditar, btnRemover);

        topo.getChildren().addAll(l1, l2);
        return topo;
    }

    private HBox criarRodape() {
        HBox r = new HBox();
        r.setPadding(new Insets(10, 0, 0, 0));
        Button btnAdd = btn("➕ Adicionar via Last.fm", GREEN, TEXT);
        btnAdd.setOnAction(e -> fluxoCadastroLastFm());
        r.getChildren().add(btnAdd);
        return r;
    }

    // ── RENDERIZAÇÃO ─────────────────────────────────────────────────────
    private void renderizarBiblioteca() {
        renderizar(ctrl.getBiblioteca(), true);
    }

    private void renderizar(List<Musica> lista, boolean comHistorico) {
        containerCentral.getChildren().clear();

        // Seção "Ouvidos Recentemente"
        if (comHistorico) {
            List<Musica> recentes = ctrl.getHistoricoRecente(6);
            if (!recentes.isEmpty()) {
                containerCentral.getChildren().add(criarSecao("🕐 Ouvidos Recentemente", recentes));
            }
        }

        if (lista.isEmpty()) {
            Label msg = new Label("Nenhuma obra cadastrada ainda.\nClique em ➕ Adicionar via Last.fm para começar.");
            msg.setStyle("-fx-font-size:14px;-fx-text-fill:" + MUTED + ";-fx-padding:30;-fx-text-alignment:center;");
            msg.setWrapText(true);
            containerCentral.getChildren().add(msg);
            return;
        }

        for (String tipo : new String[]{"Álbum de Estúdio","Single","EP","Álbum ao Vivo"}) {
            List<Musica> grupo = lista.stream()
                .filter(m -> tipo.equals(m.getTipo())).collect(Collectors.toList());
            if (!grupo.isEmpty())
                containerCentral.getChildren().add(criarSecao(tipo, grupo));
        }
    }

    private void filtrarPorTipo(String tipo) {
        List<Musica> lista = tipo.equals("Todos") ? ctrl.getBiblioteca()
            : ctrl.getBiblioteca().stream()
                .filter(m -> tipo.equals(m.getTipo())).collect(Collectors.toList());
        containerCentral.getChildren().clear();
        renderizar(lista, false);
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

        ScrollPane sp = scrollH(cards);
        HBox.setHgrow(sp, Priority.ALWAYS);

        Button esq = btn("◀", CARD, TEXT);
        Button dir = btn("▶", CARD, TEXT);
        esq.setOnAction(e -> sp.setHvalue(Math.max(0, sp.getHvalue() - 0.25)));
        dir.setOnAction(e -> sp.setHvalue(Math.min(1, sp.getHvalue() + 0.25)));

        linha.getChildren().addAll(esq, sp, dir);
        secao.getChildren().addAll(lbl, linha);
        return secao;
    }

    // ── CARD MINIATURA ───────────────────────────────────────────────────
    private VBox criarCard(Musica m) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(6));
        card.setPrefWidth(136); card.setMaxWidth(136);
        card.setStyle(estiloCard(false));

        ImageView iv = new ImageView();
        iv.setFitWidth(120); iv.setFitHeight(155); iv.setPreserveRatio(false);
        carregarImg(iv, m.getUrlCapa(), 120, 155, PH);

        Label nome = new Label(m.getNome());
        nome.setStyle("-fx-font-weight:bold;-fx-font-size:11px;-fx-text-fill:" + TEXT
                    + ";-fx-text-alignment:center;");
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

    // ── DETALHES ─────────────────────────────────────────────────────────
    private void mostrarDetalhes(Musica m) {
        // Registra escuta automaticamente
        try { ctrl.registrarEscuta(m); } catch (IOException ignored) {}

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
        carregarImg(iv, m.getUrlCapa(), 160, 210, PH);

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

        // Última escuta
        Label lblEscuta = new Label("🕐 Última escuta: " + m.getUltimaEscutaFormatada()
            + (m.getTotalEscutas() > 0 ? "  (" + m.getTotalEscutas() + "x)" : ""));
        lblEscuta.setStyle("-fx-font-size:11px;-fx-text-fill:" + MUTED + ";-fx-font-style:italic;");
        info.getChildren().add(lblEscuta);

        // Tracklist
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

    // ── PLAYLIST POR CRITÉRIO ────────────────────────────────────────────
    private void popupPlaylist() {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Gerar Playlist");

        VBox raiz = popupBase();

        Label instrucao = new Label("Configure sua playlist:");
        instrucao.setStyle("-fx-font-size:15px;-fx-font-weight:bold;-fx-text-fill:" + TEXT + ";");

        TextField txtGenero = new TextField();
        txtGenero.setPromptText("Ex: Rock, Pop, Jazz... (deixe vazio para todos)");
        estilizarCampo(txtGenero); txtGenero.setPrefWidth(340);

        TextField txtDuracao = new TextField();
        txtDuracao.setPromptText("Duração máxima em minutos (ex: 60). Deixe vazio = sem limite");
        estilizarCampo(txtDuracao); txtDuracao.setPrefWidth(340);

        Button btnGerar = btn("▶ Gerar Playlist", GREEN, TEXT);
        btnGerar.setDefaultButton(true);
        btnGerar.setPrefWidth(340);

        VBox resultadoBox = new VBox(8);

        btnGerar.setOnAction(e -> {
            try {
                String genero = txtGenero.getText().trim();
                int durMax = 0;
                if (!txtDuracao.getText().trim().isEmpty())
                    durMax = Integer.parseInt(txtDuracao.getText().trim());

                List<Musica> playlist = ctrl.gerarPlaylist(genero, durMax);
                popup.close();
                mostrarPlaylist(playlist, genero, durMax);

            } catch (NumberFormatException ex) {
                new Alert(Alert.AlertType.ERROR, "Duração deve ser um número inteiro.").showAndWait();
            } catch (ArquivoNaoEncontradoException ex) {
                resultadoBox.getChildren().clear();
                Label err = labelPopup("❌ " + ex.getMessage());
                resultadoBox.getChildren().add(err);
            }
        });

        raiz.getChildren().addAll(
            instrucao,
            labelPopup("Gênero (opcional):"), txtGenero,
            labelPopup("Duração máxima (minutos, opcional):"), txtDuracao,
            btnGerar, resultadoBox
        );

        popup.setScene(new Scene(raiz, 400, 320));
        popup.showAndWait();
    }

    private void mostrarPlaylist(List<Musica> playlist, String genero, int durMax) {
        BorderPane painel = new BorderPane();
        painel.setStyle("-fx-background-color:" + BG + ";");
        painel.setPadding(new Insets(20));

        Button voltar = btn("⬅ Voltar", CARD, TEXT);
        voltar.setOnAction(e -> voltarCentro());
        painel.setTop(voltar);
        BorderPane.setMargin(voltar, new Insets(0, 0, 14, 0));

        // Cabeçalho da playlist
        String criterio = "";
        if (genero != null && !genero.isEmpty()) criterio += "Gênero: " + genero + "  ";
        if (durMax > 0) criterio += "Máx: " + durMax + "min";
        if (criterio.isEmpty()) criterio = "Todos os gêneros";

        int totalSeg = playlist.stream().mapToInt(Musica::getDuracaoTotalSegundos).sum();
        String durTotal = totalSeg > 0
            ? (totalSeg / 3600) + "h " + ((totalSeg % 3600) / 60) + "m" : "—";

        Label lblTitulo = new Label("▶ Playlist Gerada");
        lblTitulo.setStyle("-fx-font-size:20px;-fx-font-weight:bold;-fx-text-fill:" + GREEN + ";");

        Label lblInfo = new Label("Critério: " + criterio + "   |   " + playlist.size()
            + " obras   |   Duração total: " + durTotal);
        lblInfo.setStyle("-fx-font-size:12px;-fx-text-fill:" + MUTED + ";");

        // Lista numerada das músicas
        VBox listaItens = new VBox(6);
        for (int i = 0; i < playlist.size(); i++) {
            Musica m = playlist.get(i);
            HBox item = new HBox(12);
            item.setAlignment(Pos.CENTER_LEFT);
            item.setPadding(new Insets(8));
            item.setStyle("-fx-background-color:" + CARD + ";-fx-background-radius:8;"
                        + "-fx-border-color:" + ACCENT + ";-fx-border-radius:8;-fx-cursor:hand;");

            // Número
            Label num = new Label(String.valueOf(i + 1));
            num.setStyle("-fx-font-size:16px;-fx-font-weight:bold;-fx-text-fill:" + ACCENT2
                       + ";-fx-min-width:30;");

            // Capa pequena
            ImageView iv = new ImageView();
            iv.setFitWidth(48); iv.setFitHeight(48); iv.setPreserveRatio(false);
            carregarImg(iv, m.getUrlCapa(), 48, 48, PH);

            // Info
            VBox textos = new VBox(2);
            Label lNome = new Label(m.getNome());
            lNome.setStyle("-fx-font-weight:bold;-fx-text-fill:" + TEXT + ";-fx-font-size:13px;");
            Label lArt = new Label(safe(m.getArtista(), "") + "  •  " + safe(m.getTipo(), "")
                + (m.getNota() > 0 ? "  ⭐" + m.getNota() : ""));
            lArt.setStyle("-fx-font-size:11px;-fx-text-fill:" + MUTED + ";");
            Label lDur = new Label(m.getDuracaoTotalSegundos() > 0 ? m.getDuracaoTotalFormatada() : "");
            lDur.setStyle("-fx-font-size:11px;-fx-text-fill:" + ACCENT2 + ";");
            textos.getChildren().addAll(lNome, lArt, lDur);
            HBox.setHgrow(textos, Priority.ALWAYS);

            item.getChildren().addAll(num, iv, textos);
            item.setOnMouseClicked(ev -> mostrarDetalhes(m));
            item.setOnMouseEntered(ev -> item.setStyle(
                "-fx-background-color:" + ACCENT + "44;-fx-background-radius:8;"
                + "-fx-border-color:" + ACCENT2 + ";-fx-border-radius:8;-fx-cursor:hand;"));
            item.setOnMouseExited(ev -> item.setStyle(
                "-fx-background-color:" + CARD + ";-fx-background-radius:8;"
                + "-fx-border-color:" + ACCENT + ";-fx-border-radius:8;-fx-cursor:hand;"));

            listaItens.getChildren().add(item);
        }

        VBox corpo = new VBox(10, lblTitulo, lblInfo, new Separator(), listaItens);
        ScrollPane sp = new ScrollPane(corpo);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:transparent;-fx-background:transparent;");
        painel.setCenter(sp);
        layoutRaiz.setCenter(painel);
    }

    // ── ARTISTAS FAVORITOS ───────────────────────────────────────────────
    private void mostrarArtistas() {
        BorderPane painel = new BorderPane();
        painel.setStyle("-fx-background-color:" + BG + ";");
        painel.setPadding(new Insets(20));

        Button voltar = btn("⬅ Voltar", CARD, TEXT);
        voltar.setOnAction(e -> voltarCentro());
        painel.setTop(voltar);
        BorderPane.setMargin(voltar, new Insets(0, 0, 14, 0));

        Label titulo = new Label("🎤 Artistas Favoritos");
        titulo.setStyle("-fx-font-size:18px;-fx-font-weight:bold;-fx-text-fill:" + ACCENT2 + ";");

        Button btnAddArtista = btn("➕ Adicionar Artista", GREEN, TEXT);
        btnAddArtista.setOnAction(e -> { popupAdicionarArtista(); mostrarArtistas(); });

        HBox topBar = new HBox(12, titulo, btnAddArtista);
        topBar.setAlignment(Pos.CENTER_LEFT);

        // Grid de cards de artistas
        List<Artista> lista = ctrlArtista.getLista();
        if (lista.isEmpty()) {
            Label msg = new Label("Nenhum artista favorito ainda.\nClique em ➕ Adicionar Artista para começar.");
            msg.setStyle("-fx-font-size:13px;-fx-text-fill:" + MUTED + ";-fx-text-alignment:center;");
            msg.setWrapText(true);
            painel.setCenter(new VBox(14, topBar, msg));
            layoutRaiz.setCenter(painel);
            return;
        }

        // Carrossel de cards de artistas
        HBox cards = new HBox(14);
        cards.setPadding(new Insets(10, 0, 0, 0));
        for (Artista a : lista) cards.getChildren().add(criarCardArtista(a));

        ScrollPane sp = scrollH(cards);

        VBox corpo = new VBox(14, topBar, sp);
        painel.setCenter(corpo);
        layoutRaiz.setCenter(painel);
    }

    private VBox criarCardArtista(Artista a) {
        VBox card = new VBox(6);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(10));
        card.setPrefWidth(140); card.setMaxWidth(140);
        card.setStyle("-fx-border-color:" + ACCENT + ";-fx-border-radius:10;"
                    + "-fx-background-color:" + CARD + ";-fx-background-radius:10;-fx-cursor:hand;");

        // Foto do artista em círculo
        ImageView iv = new ImageView();
        iv.setFitWidth(100); iv.setFitHeight(100); iv.setPreserveRatio(false);
        carregarImg(iv, a.getUrlFoto(), 100, 100, PH_ART);

        // Clip circular
        Circle clip = new Circle(50, 50, 50);
        iv.setClip(clip);

        StackPane fotoCirculo = new StackPane(iv);
        fotoCirculo.setPrefSize(100, 100);
        fotoCirculo.setStyle("-fx-background-radius:50;-fx-border-radius:50;");

        Label nome = new Label(a.getNome());
        nome.setStyle("-fx-font-weight:bold;-fx-font-size:12px;-fx-text-fill:" + TEXT
                    + ";-fx-text-alignment:center;");
        nome.setWrapText(true); nome.setMaxWidth(125); nome.setAlignment(Pos.CENTER);

        Label genero = new Label(safe(a.getGenero(), ""));
        genero.setStyle("-fx-font-size:10px;-fx-text-fill:" + MUTED + ";");

        card.getChildren().addAll(fotoCirculo, nome, genero);
        card.setOnMouseClicked(ev -> mostrarDetalheArtista(a));
        card.setOnMouseEntered(ev -> card.setStyle(
            "-fx-border-color:" + ACCENT2 + ";-fx-border-radius:10;"
            + "-fx-background-color:" + ACCENT + "44;-fx-background-radius:10;-fx-cursor:hand;"));
        card.setOnMouseExited(ev -> card.setStyle(
            "-fx-border-color:" + ACCENT + ";-fx-border-radius:10;"
            + "-fx-background-color:" + CARD + ";-fx-background-radius:10;-fx-cursor:hand;"));
        return card;
    }

    private void mostrarDetalheArtista(Artista a) {
        BorderPane painel = new BorderPane();
        painel.setStyle("-fx-background-color:" + BG + ";");
        painel.setPadding(new Insets(24));

        Button voltar = btn("⬅ Artistas", CARD, TEXT);
        voltar.setOnAction(e -> mostrarArtistas());
        painel.setTop(voltar);
        BorderPane.setMargin(voltar, new Insets(0, 0, 16, 0));

        HBox linha = new HBox(24);
        linha.setAlignment(Pos.TOP_LEFT);

        // Foto grande
        ImageView iv = new ImageView();
        iv.setFitWidth(160); iv.setFitHeight(160); iv.setPreserveRatio(false);
        carregarImg(iv, a.getUrlFoto(), 160, 160, PH_ART);
        Circle clip = new Circle(80, 80, 80);
        iv.setClip(clip);
        StackPane foto = new StackPane(iv);
        foto.setPrefSize(160, 160);

        VBox lado = new VBox(10);
        Label lblNome = new Label(a.getNome());
        lblNome.setStyle("-fx-font-size:24px;-fx-font-weight:bold;-fx-text-fill:" + TEXT + ";");

        if (a.getGenero() != null && !a.getGenero().isEmpty()) {
            Label lblGen = new Label("🏷️ " + a.getGenero());
            lblGen.setStyle("-fx-font-size:13px;-fx-text-fill:" + MUTED + ";");
            lado.getChildren().add(lblGen);
        }
        if (a.getPais() != null && !a.getPais().isEmpty()) {
            Label lblPais = new Label("🌎 " + a.getPais());
            lblPais.setStyle("-fx-font-size:13px;-fx-text-fill:" + MUTED + ";");
            lado.getChildren().add(lblPais);
        }

        Label lblDescTit = new Label("📝 Sobre:");
        lblDescTit.setStyle("-fx-font-weight:bold;-fx-text-fill:" + ACCENT2 + ";");

        String desc = safe(a.getDescricao(), "Nenhuma descrição adicionada.");
        Label lblDesc = new Label(desc);
        lblDesc.setWrapText(true); lblDesc.setMaxWidth(440);
        lblDesc.setStyle("-fx-font-size:13px;-fx-text-fill:" + TEXT
                       + ";-fx-background-color:" + SURFACE + ";-fx-padding:12;"
                       + "-fx-background-radius:8;");

        Button btnEditar = btn("✏️ Editar Artista", "#2980b9", TEXT);
        btnEditar.setOnAction(e -> { popupEditarArtista(a); mostrarDetalheArtista(a); });

        Button btnRemover = btn("🗑️ Remover", DANGER, TEXT);
        btnRemover.setOnAction(e -> {
            Alert c = new Alert(Alert.AlertType.CONFIRMATION,
                "Remover \"" + a.getNome() + "\" dos favoritos?");
            c.showAndWait().ifPresent(r -> {
                if (r == ButtonType.OK) {
                    try { ctrlArtista.remover(a.getNome()); mostrarArtistas(); }
                    catch (Exception ex) { new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait(); }
                }
            });
        });

        HBox acoes = new HBox(10, btnEditar, btnRemover);

        lado.getChildren().addAll(0, List.of(lblNome));
        lado.getChildren().addAll(lblDescTit, lblDesc, acoes);

        linha.getChildren().addAll(foto, lado);
        ScrollPane sp = new ScrollPane(linha);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:transparent;-fx-background:transparent;");
        painel.setCenter(sp);
        layoutRaiz.setCenter(painel);
    }

    private void popupAdicionarArtista() {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Adicionar Artista");
        VBox form = popupBase();

        TextField tNome  = campo(""); tNome.setPromptText("Obrigatório");
        TextField tGenero= campo("");
        TextField tPais  = campo("");
        TextField tFoto  = campo(""); tFoto.setPromptText("URL da foto (opcional)");
        TextArea  tDesc  = new TextArea();
        tDesc.setPromptText("Escreva sobre o artista...");
        tDesc.setPrefRowCount(3);
        tDesc.setStyle("-fx-background-color:" + CARD + ";-fx-text-fill:" + TEXT
                     + ";-fx-control-inner-background:" + CARD + ";");

        Button salvar = btn("💾 Salvar", GREEN, TEXT);
        salvar.setDefaultButton(true);
        salvar.setOnAction(e -> {
            try {
                if (tNome.getText().trim().isEmpty())
                    throw new DadosInvalidosException("O nome é obrigatório.");
                Artista a = new Artista(tNome.getText().trim());
                a.setGenero(tGenero.getText().trim());
                a.setPais(tPais.getText().trim());
                a.setUrlFoto(tFoto.getText().trim());
                a.setDescricao(tDesc.getText().trim());
                ctrlArtista.adicionar(a);
                popup.close();
            } catch (DadosInvalidosException | IOException ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
            }
        });

        form.getChildren().addAll(
            labelPopup("Nome *:"),   tNome,
            labelPopup("Gênero:"),   tGenero,
            labelPopup("País:"),     tPais,
            labelPopup("URL Foto:"), tFoto,
            labelPopup("Descrição:"),tDesc,
            salvar
        );
        ScrollPane sp = new ScrollPane(form);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:" + SURFACE + ";-fx-background:" + SURFACE + ";");
        popup.setScene(new Scene(sp, 400, 420));
        popup.showAndWait();
    }

    private void popupEditarArtista(Artista a) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Editando: " + a.getNome());
        VBox form = popupBase();

        TextField tNome  = campo(a.getNome());
        TextField tGenero= campo(safe(a.getGenero(), ""));
        TextField tPais  = campo(safe(a.getPais(), ""));
        TextField tFoto  = campo(safe(a.getUrlFoto(), ""));
        TextArea  tDesc  = new TextArea(safe(a.getDescricao(), ""));
        tDesc.setPrefRowCount(3);
        tDesc.setStyle("-fx-background-color:" + CARD + ";-fx-text-fill:" + TEXT
                     + ";-fx-control-inner-background:" + CARD + ";");

        Button salvar = btn("💾 Salvar", GREEN, TEXT);
        salvar.setDefaultButton(true);
        salvar.setOnAction(e -> {
            try {
                String orig = a.getNome();
                a.setNome(tNome.getText().trim());
                a.setGenero(tGenero.getText().trim());
                a.setPais(tPais.getText().trim());
                a.setUrlFoto(tFoto.getText().trim());
                a.setDescricao(tDesc.getText().trim());
                ctrlArtista.editar(orig, a);
                popup.close();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
            }
        });

        form.getChildren().addAll(
            labelPopup("Nome *:"),   tNome,
            labelPopup("Gênero:"),   tGenero,
            labelPopup("País:"),     tPais,
            labelPopup("URL Foto:"), tFoto,
            labelPopup("Descrição:"),tDesc,
            salvar
        );
        ScrollPane sp = new ScrollPane(form);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:" + SURFACE + ";-fx-background:" + SURFACE + ";");
        popup.setScene(new Scene(sp, 400, 420));
        popup.showAndWait();
    }

    // ── DASHBOARD ────────────────────────────────────────────────────────
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

        // Top 3 mais ouvidos
        List<Musica> top3 = ctrl.getHistoricoRecente(3);

        TilePane tiles = new TilePane(16, 16);
        tiles.setPrefColumns(3);
        tiles.setPadding(new Insets(12, 0, 0, 0));
        tiles.getChildren().addAll(
            statCard("🎵 Total de Obras",    String.valueOf(bib.size())),
            statCard("🎼 Total de Faixas",   String.valueOf(ctrl.getTotalFaixas())),
            statCard("⏱️ Tempo de Escuta",   ctrl.getDuracaoTotalFormatada()),
            statCard("🎤 Artista Top",        ctrl.getArtistaTop()),
            statCard("⭐ Média das Notas",    mediaNota > 0 ? String.format("%.1f/5",mediaNota) : "—"),
            statCard("📝 Avaliados",          bib.stream().filter(m->m.getNota()>0).count() + "/" + bib.size()),
            statCard("💿 Álbuns",            String.valueOf(bib.stream().filter(m->m instanceof AlbumMusical).count())),
            statCard("🎧 Singles",            String.valueOf(bib.stream().filter(m->m instanceof Single).count())),
            statCard("📀 EPs",               String.valueOf(bib.stream().filter(m->m instanceof EP).count())),
            statCard("🎤 Live Albums",        String.valueOf(bib.stream().filter(m->m instanceof LiveAlbum).count()))
        );

        // Mais ouvidos
        VBox maisOuvidos = new VBox(6);
        Label lblMO = new Label("🔥 Mais Ouvidos:");
        lblMO.setStyle("-fx-font-weight:bold;-fx-font-size:14px;-fx-text-fill:" + ACCENT2 + ";");
        maisOuvidos.getChildren().add(lblMO);
        if (top3.isEmpty()) {
            maisOuvidos.getChildren().add(labelPopup("Nenhuma obra ouvida ainda."));
        } else {
            for (int i = 0; i < top3.size(); i++) {
                Musica m = top3.get(i);
                Label l = new Label((i+1) + ". " + m.getNome() + " — " + safe(m.getArtista(),"")
                    + "  (" + m.getTotalEscutas() + "x)");
                l.setStyle("-fx-font-size:13px;-fx-text-fill:" + TEXT + ";");
                maisOuvidos.getChildren().add(l);
            }
        }

        VBox corpo = new VBox(12, titulo, tiles, maisOuvidos);
        ScrollPane sp = new ScrollPane(corpo);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:transparent;-fx-background:transparent;");
        painel.setCenter(sp);
        layoutRaiz.setCenter(painel);
    }

    private VBox statCard(String label, String valor) {
        VBox card = new VBox(6);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(16));
        card.setMinWidth(170);
        card.setStyle("-fx-background-color:" + CARD + ";-fx-background-radius:12;"
                    + "-fx-border-color:" + ACCENT + ";-fx-border-radius:12;");
        Label lVal = new Label(valor);
        lVal.setStyle("-fx-font-size:22px;-fx-font-weight:bold;-fx-text-fill:" + ACCENT2 + ";");
        Label lLbl = new Label(label);
        lLbl.setStyle("-fx-font-size:12px;-fx-text-fill:" + MUTED + ";");
        lLbl.setWrapText(true); lLbl.setAlignment(Pos.CENTER);
        card.getChildren().addAll(lVal, lLbl);
        return card;
    }

    // ── AVALIADOS ────────────────────────────────────────────────────────
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
            painel.setCenter(new VBox(msg));
            layoutRaiz.setCenter(painel);
            return;
        }

        HBox cards = new HBox(14);
        cards.setPadding(new Insets(10, 0, 0, 0));
        for (Musica m : avaliados) {
            VBox c = criarCard(m);
            c.setOnMouseClicked(ev -> mostrarDetalheAvaliacao(m));
            c.setOnMouseEntered(ev -> c.setStyle("-fx-border-color:" + GOLD
                + ";-fx-border-radius:8;-fx-background-color:" + ACCENT + "44"
                + ";-fx-background-radius:8;-fx-cursor:hand;"));
            c.setOnMouseExited(ev -> c.setStyle(estiloCard(false)));
            cards.getChildren().add(c);
        }

        Label titulo = new Label("⭐ Itens Avaliados");
        titulo.setStyle("-fx-font-size:18px;-fx-font-weight:bold;-fx-text-fill:" + GOLD + ";");

        painel.setCenter(new VBox(10, titulo, scrollH(cards)));
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
        carregarImg(iv, m.getUrlCapa(), 150, 195, PH);

        VBox lado = new VBox(12);
        Label lblNome = new Label(m.getNome());
        lblNome.setStyle("-fx-font-size:22px;-fx-font-weight:bold;-fx-text-fill:" + TEXT + ";");

        Circle circ = new Circle(38); circ.setFill(Color.web(GOLD));
        Text txtNota = new Text(String.valueOf(m.getNota()));
        txtNota.setFont(Font.font("System", FontWeight.BOLD, 30));
        txtNota.setFill(Color.web("#0d0d1a"));
        StackPane circNota = new StackPane(circ, txtNota);
        circNota.setPrefSize(76, 76);
        VBox blocoNota = new VBox(4, circNota, new Label("Nota") {{
            setStyle("-fx-font-size:12px;-fx-text-fill:" + MUTED + ";");
        }});
        blocoNota.setAlignment(Pos.CENTER);

        Label lblCritTit = new Label("📝 Crítica:");
        lblCritTit.setStyle("-fx-font-weight:bold;-fx-text-fill:" + MUTED + ";");

        Label lblCrit = new Label(safe(m.getComentario(), "Nenhum comentário registrado."));
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

    // ── CADASTRO VIA LAST.FM ─────────────────────────────────────────────
    private void fluxoCadastroLastFm() {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Adicionar via Last.fm");
        VBox raiz = popupBase();

        TextField txtNome = new TextField(); estilizarCampo(txtNome);
        txtNome.setPrefWidth(360);

        ComboBox<String> cbTipo = new ComboBox<>(FXCollections.observableArrayList(
            "Álbum de Estúdio","Single","EP","Álbum ao Vivo"));
        cbTipo.setValue("Álbum de Estúdio");
        cbTipo.setStyle("-fx-background-color:" + CARD + ";-fx-text-fill:" + TEXT + ";");

        Button btnBuscar = btn("🔍 Buscar no Last.fm", ACCENT, TEXT);
        VBox listaResultados = new VBox(6);
        listaResultados.getChildren().add(labelPopup("Aguardando busca..."));

        btnBuscar.setOnAction(e -> {
            String termo = txtNome.getText().trim();
            if (termo.isEmpty()) { new Alert(Alert.AlertType.WARNING,"Digite o nome.").showAndWait(); return; }
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
                            itemLinha.setStyle("-fx-background-color:" + CARD + ";-fx-border-color:" + ACCENT
                                + ";-fx-border-radius:6;-fx-background-radius:6;-fx-cursor:hand;");
                            ImageView thumb = new ImageView();
                            thumb.setFitWidth(40); thumb.setFitHeight(40); thumb.setPreserveRatio(false);
                            carregarImg(thumb, r.urlCapa, 40, 40, PH);
                            Label lbl = new Label(r.toString());
                            lbl.setStyle("-fx-text-fill:" + TEXT + ";-fx-font-size:12px;");
                            lbl.setWrapText(true);
                            itemLinha.getChildren().addAll(thumb, lbl);
                            itemLinha.setOnMouseClicked(ev -> { popup.close(); confirmarEImportar(r, cbTipo.getValue()); });
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
                        listaResultados.getChildren().add(labelPopup("❌ Erro: " + ex.getMessage()));
                        btnBuscar.setDisable(false); btnBuscar.setText("🔍 Buscar no Last.fm");
                    });
                }
            }).start();
        });

        txtNome.setOnAction(e -> btnBuscar.fire());

        ScrollPane sp = new ScrollPane(listaResultados);
        sp.setFitToWidth(true); sp.setPrefHeight(240);
        sp.setStyle("-fx-background-color:transparent;-fx-background:transparent;");

        raiz.getChildren().addAll(
            labelPopup("Nome do álbum/single:"), txtNome,
            labelPopup("Tipo:"), cbTipo,
            btnBuscar, new Separator(), sp);
        popup.setScene(new Scene(raiz, 460, 520));
        popup.showAndWait();
    }

    private void confirmarEImportar(ResultadoBusca resultado, String tipo) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Importando...");
        VBox raiz = popupBase();
        Label info = new Label("Importando: " + resultado.nome + " — " + resultado.artista);
        info.setStyle("-fx-font-weight:bold;-fx-text-fill:" + TEXT + ";"); info.setWrapText(true);
        Label loading = new Label("⏳ Buscando detalhes no Last.fm...");
        loading.setStyle("-fx-text-fill:" + MUTED + ";");
        raiz.getChildren().addAll(info, loading);
        popup.setScene(new Scene(raiz, 380, 120)); popup.show();

        new Thread(() -> {
            try {
                Musica nova = criarMusicaPorTipo(tipo, resultado.nome, resultado.artista);
                lastFm.importarAlbumCompleto(nova, resultado.nome, resultado.artista);
                ctrl.adicionar(nova);
                Platform.runLater(() -> {
                    popup.close(); renderizarBiblioteca();
                    new Alert(Alert.AlertType.INFORMATION,
                        resultado.nome + " adicionado! " + nova.getFaixas().size() + " faixas importadas.")
                        .showAndWait();
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    popup.close();
                    new Alert(Alert.AlertType.ERROR, "Erro: " + ex.getMessage()).showAndWait();
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

    // ── REMOVER / EDITAR ─────────────────────────────────────────────────
    private void fluxoRemover() {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Remover");
        VBox raiz = popupBase();
        TextField campo = new TextField(); estilizarCampo(campo);
        campo.setPromptText("Nome da obra ou artista...");
        Button btnB = btn("🔍 Buscar", ACCENT, TEXT);
        VBox lista = new VBox(6);
        lista.getChildren().add(labelPopup("Aguardando..."));
        btnB.setOnAction(e -> {
            try {
                List<Musica> found = ctrl.buscar(campo.getText().trim());
                lista.getChildren().clear();
                for (Musica m : found) {
                    Button bi = new Button("🎵 " + m.getNome() + " — " + safe(m.getArtista(),""));
                    bi.setMaxWidth(Double.MAX_VALUE);
                    bi.setStyle("-fx-background-color:" + CARD + ";-fx-text-fill:" + TEXT
                               + ";-fx-border-color:" + ACCENT + ";-fx-border-radius:6;"
                               + "-fx-background-radius:6;-fx-padding:8;-fx-cursor:hand;");
                    bi.setOnAction(ev -> {
                        Alert c = new Alert(Alert.AlertType.CONFIRMATION, "Remover \"" + m.getNome() + "\"?");
                        c.showAndWait().ifPresent(r -> {
                            if (r == ButtonType.OK) {
                                try { ctrl.remover(m.getNome()); renderizarBiblioteca(); popup.close(); }
                                catch (Exception ex) { new Alert(Alert.AlertType.ERROR,ex.getMessage()).showAndWait(); }
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
        raiz.getChildren().addAll(labelPopup("Buscar para remover:"), campo, btnB, new Separator(), sp);
        popup.setScene(new Scene(raiz, 400, 360)); popup.showAndWait();
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
        lista.getChildren().add(labelPopup("Aguardando..."));
        btnB.setOnAction(e -> {
            try {
                List<Musica> found = ctrl.buscar(campo.getText().trim());
                lista.getChildren().clear();
                for (Musica m : found) {
                    Button bi = new Button("🎵 " + m.getNome() + " — " + safe(m.getArtista(),""));
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
        raiz.getChildren().addAll(labelPopup("Buscar para editar:"), campo, btnB, new Separator(), sp);
        popup.setScene(new Scene(raiz, 400, 360)); popup.showAndWait();
    }

    private void popupEdicao(Musica m) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Editando: " + m.getNome());
        VBox form = popupBase();
        TextField tN = campo(m.getNome()), tA = campo(safe(m.getArtista(),"")),
                  tAno = campo(m.getAnoLancamento()>0 ? String.valueOf(m.getAnoLancamento()) : ""),
                  tG = campo(safe(m.getGenero(),"")), tC = campo(safe(m.getUrlCapa(),""));
        form.getChildren().addAll(
            labelPopup("Título:"), tN, labelPopup("Artista:"), tA,
            labelPopup("Ano:"), tAno, labelPopup("Gênero:"), tG, labelPopup("URL Capa:"), tC);
        if (m instanceof LiveAlbum) {
            TextField tL = campo(safe(((LiveAlbum)m).getLocalShow(),"")),
                      tCi = campo(safe(((LiveAlbum)m).getCidadeShow(),""));
            form.getChildren().addAll(labelPopup("Local:"), tL, labelPopup("Cidade:"), tCi);
            Button s = btn("💾 Salvar", GREEN, TEXT); s.setDefaultButton(true);
            s.setOnAction(e -> {
                try { String orig=m.getNome(); aplicarBase(m,tN,tA,tAno,tG,tC);
                    ((LiveAlbum)m).setLocalShow(tL.getText().trim());
                    ((LiveAlbum)m).setCidadeShow(tCi.getText().trim());
                    ctrl.editar(orig,m); renderizarBiblioteca(); popup.close();
                } catch(Exception ex){new Alert(Alert.AlertType.ERROR,ex.getMessage()).showAndWait();}
            });
            form.getChildren().add(s);
        } else {
            Button s = btn("💾 Salvar", GREEN, TEXT); s.setDefaultButton(true);
            s.setOnAction(e -> {
                try { String orig=m.getNome(); aplicarBase(m,tN,tA,tAno,tG,tC);
                    ctrl.editar(orig,m); renderizarBiblioteca(); popup.close();
                } catch(Exception ex){new Alert(Alert.AlertType.ERROR,ex.getMessage()).showAndWait();}
            });
            form.getChildren().add(s);
        }
        ScrollPane sp = new ScrollPane(form);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:" + SURFACE + ";-fx-background:" + SURFACE + ";");
        popup.setScene(new Scene(sp, 420, 400)); popup.show();
    }

    private void aplicarBase(Musica m, TextField tN, TextField tA,
                              TextField tAno, TextField tG, TextField tC) {
        m.setNome(tN.getText().trim()); m.setArtista(tA.getText().trim());
        m.setGenero(tG.getText().trim()); m.setUrlCapa(tC.getText().trim());
        if (!tAno.getText().trim().isEmpty())
            m.setAnoLancamento(Integer.parseInt(tAno.getText().trim()));
    }

    private void popupAvaliar(Musica m) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Avaliar: " + m.getNome());
        VBox box = popupBase();
        ComboBox<Integer> cb = new ComboBox<>(FXCollections.observableArrayList(1,2,3,4,5));
        cb.setValue(m.getNota()>0 ? m.getNota() : 5);
        cb.setStyle("-fx-background-color:" + CARD + ";-fx-text-fill:" + TEXT + ";");
        TextArea ta = new TextArea(safe(m.getComentario(),""));
        ta.setPromptText("Escreva sua crítica..."); ta.setPrefRowCount(4);
        ta.setStyle("-fx-background-color:" + CARD + ";-fx-text-fill:" + TEXT
                  + ";-fx-control-inner-background:" + CARD + ";");
        Button s = btn("💾 Salvar", GOLD, "#0d0d1a"); s.setDefaultButton(true);
        s.setOnAction(e -> {
            try { m.avaliar(cb.getValue(), ta.getText()); ctrl.editar(m.getNome(),m); popup.close(); }
            catch(Exception ex){new Alert(Alert.AlertType.ERROR,ex.getMessage()).showAndWait();}
        });
        box.getChildren().addAll(labelPopup("Nota (1-5):"),cb,labelPopup("Comentário:"),ta,s);
        popup.setScene(new Scene(box,360,300)); popup.showAndWait();
    }

    // ── BUSCA ────────────────────────────────────────────────────────────
    private void acaoBuscar() {
        String termo = txtBusca.getText().trim();
        if (termo.isEmpty()) { renderizarBiblioteca(); return; }
        try { renderizar(ctrl.buscar(termo), false); }
        catch (ArquivoNaoEncontradoException ex) {
            containerCentral.getChildren().clear();
            Label msg = new Label("❌ " + ex.getMessage());
            msg.setStyle("-fx-text-fill:" + DANGER + ";-fx-padding:20;");
            containerCentral.getChildren().add(msg);
        }
    }

    private void voltarCentro() {
        containerCentral = new VBox(18);
        containerCentral.setPadding(new Insets(10,0,10,0));
        layoutRaiz.setCenter(scrollTransparente(containerCentral));
        renderizarBiblioteca();
    }

    // ── UTILITÁRIOS ──────────────────────────────────────────────────────
    private VBox popupBase() {
        VBox v = new VBox(8); v.setPadding(new Insets(18));
        v.setStyle("-fx-background-color:" + SURFACE + ";"); return v;
    }
    private TextField campo(String val) {
        TextField tf = new TextField(val!=null?val:"");
        tf.setPrefWidth(340); tf.setMinWidth(340); estilizarCampo(tf); return tf;
    }
    private Button btn(String t, String bg, String fg) {
        Button b = new Button(t);
        b.setStyle("-fx-background-color:" + bg + ";-fx-text-fill:" + fg
                 + ";-fx-font-weight:bold;-fx-padding:7 12 7 12;-fx-background-radius:6;-fx-cursor:hand;");
        return b;
    }
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
    private ScrollPane scrollH(HBox c) {
        ScrollPane sp = new ScrollPane(c);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        sp.setFitToHeight(true);
        sp.setStyle("-fx-background-color:transparent;-fx-background:transparent;"); return sp;
    }
    private void carregarImg(ImageView iv, String url, double w, double h, String ph) {
        iv.setFitWidth(w); iv.setFitHeight(h); iv.setPreserveRatio(false);
        try { iv.setImage(new Image((url!=null&&!url.isEmpty())?url:ph, w, h, false, true, true)); }
        catch (Exception ex) { iv.setImage(new Image(ph, w, h, false, true, true)); }
    }
    private String safe(String v, String fb) {
        return (v==null||v.trim().isEmpty()) ? fb : v.trim();
    }
}