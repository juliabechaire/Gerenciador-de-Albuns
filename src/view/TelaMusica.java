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
    private final Scene cenaAnterior; // null se for a tela inicial
    private MusicaController ctrl;
    private ArtistaController ctrlArtista;
    private LastFmService lastFm;

    private BorderPane layoutRaiz;
    private VBox containerCentral;
    private ScrollPane scrollCentral;   // referência direta para trocar o conteúdo com segurança
    private TextField txtBusca;
    private VBox sidebar;

    // Fallback sem CSS (caso o arquivo não carregue)
    private static final String BG      = "#141420";
    private static final String CARD    = "#1a1a3e";
    private static final String ACCENT  = "#7c3aed";
    private static final String ACCENT2 = "#a78bfa";
    private static final String GOLD    = "#f6c90e";
    private static final String TEXT    = "#e8e6f0";
    private static final String MUTED   = "#9d9ab5";
    private static final String DANGER  = "#7f1d1d";
    private static final String GREEN   = "#14532d";
    private static final String PH      = "https://placehold.co/120x160/1a1a3e/a78bfa?text=🎵";
    private static final String PH_ART  = "https://placehold.co/100x100/1a1a3e/a78bfa?text=🎤";

    public TelaMusica(Stage palco, Scene cenaAnterior) {
        this.palco        = palco;
        this.cenaAnterior = cenaAnterior;
        this.ctrl         = new MusicaController();
        this.ctrlArtista  = new ArtistaController();
        this.lastFm       = new LastFmService();
    }

    public void mostrar() {
        // Layout raiz: sidebar à esquerda + conteúdo à direita
        layoutRaiz = new BorderPane();
        layoutRaiz.setStyle("-fx-background-color:" + BG + ";");

        sidebar = criarSidebar();
        layoutRaiz.setLeft(sidebar);

        VBox areaConteudo = new VBox(0);

        containerCentral = new VBox(18);
        containerCentral.setPadding(new Insets(16));
        scrollCentral = scrollTransparente(containerCentral);
        VBox.setVgrow(scrollCentral, Priority.ALWAYS);

        areaConteudo.getChildren().addAll(criarTopo(), scrollCentral);
        layoutRaiz.setCenter(areaConteudo);

        renderizarHome();

        Scene cena = new Scene(layoutRaiz, 1050, 680);
        aplicarCss(cena);

        palco.setTitle("Cofre Cultural 🎵");
        palco.setScene(cena);
        palco.show();
    }

    // ── SIDEBAR ──────────────────────────────────────────────────────────
    private VBox criarSidebar() {
        VBox sb = new VBox(2);
        sb.getStyleClass().add("sidebar");
        sb.setStyle("-fx-background-color:#0f0f1e; -fx-min-width:160px; -fx-max-width:160px;"
                  + "-fx-border-color:#1e1e40; -fx-border-width:0 1 0 0;");

        // Logo
        Label logo = new Label("🎵 COFRE");
        logo.getStyleClass().add("sidebar-logo");
        logo.setStyle("-fx-font-family:Impact; -fx-font-size:18px; -fx-text-fill:#a78bfa;"
                    + "-fx-padding:16 0 20 16;");

        // Separador visual
        Region divTop = new Region();
        divTop.setStyle("-fx-background-color:#1e1e40; -fx-min-height:1px; -fx-max-height:1px;");

        // Seção BIBLIOTECA
        Label lblBib = new Label("BIBLIOTECA");
        lblBib.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:10px; -fx-text-fill:#6b6890;"
                      + "-fx-font-weight:bold; -fx-padding:14 0 4 16;");

        Button btnBiblioteca = sidebarBtn("📚  Biblioteca");
        btnBiblioteca.setOnAction(e -> {
            if (cenaAnterior != null) {
                palco.setScene(cenaAnterior);
            } else {
                // Cena anterior não disponível — abre TelaPrincipal
                view.TelaPrincipal tp = new view.TelaPrincipal();
                try {
                    tp.start(palco);
                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR, "Erro ao abrir Biblioteca.").showAndWait();
                }
            }
        });

        // Seção GERENCIAR
        Label lblGer = new Label("GERENCIAR");
        lblGer.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:10px; -fx-text-fill:#6b6890;"
                      + "-fx-font-weight:bold; -fx-padding:14 0 4 16;");

        Button btnAdicionar = sidebarBtn("➕  Adicionar");
        Button btnEditar    = sidebarBtn("✏️  Editar");
        Button btnRemover   = sidebarBtn("🗑️  Remover");

        btnAdicionar.setOnAction(e -> fluxoCadastroLastFm());
        btnEditar.setOnAction(e    -> fluxoEditar());
        btnRemover.setOnAction(e   -> fluxoRemover());

        // Seção HISTÓRICO
        Label lblHist = new Label("HISTÓRICO");
        lblHist.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:10px; -fx-text-fill:#6b6890;"
                        + "-fx-font-weight:bold; -fx-padding:14 0 4 16;");

        Button btnLimparHistorico = sidebarBtn("🧹  Limpar Histórico");
        btnLimparHistorico.setOnAction(e -> {
            Alert c = new Alert(Alert.AlertType.CONFIRMATION,
                "Limpar todo o histórico de \"Ouvidos Recentemente\"?\nIsso zera as contagens de escuta de todas as obras.");
            c.showAndWait().ifPresent(r -> {
                if (r == ButtonType.OK) {
                    try {
                        ctrl.limparHistorico();
                        voltarHome();
                        new Alert(Alert.AlertType.INFORMATION, "Histórico limpo com sucesso!").showAndWait();
                    } catch (IOException ex) {
                        new Alert(Alert.AlertType.ERROR, "Erro ao limpar histórico.").showAndWait();
                    }
                }
            });
        });

        // Espaço empurrando para baixo
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Versão no rodapé da sidebar
        Label lblVersao = new Label("v1.0  •  Last.fm");
        lblVersao.setStyle("-fx-font-size:10px; -fx-text-fill:#3d3b5e; -fx-padding:0 0 8 16;");

        sb.getChildren().addAll(
            logo, divTop,
            lblBib, btnBiblioteca,
            lblGer, btnAdicionar, btnEditar, btnRemover,
            lblHist, btnLimparHistorico,
            spacer, lblVersao
        );
        return sb;
    }

    private Button sidebarBtn(String texto) {
        Button b = new Button(texto);
        b.getStyleClass().add("sidebar-btn");
        b.setStyle("-fx-background-color:transparent; -fx-text-fill:#9d9ab5;"
                 + "-fx-font-family:'Segoe UI'; -fx-font-size:13px;"
                 + "-fx-alignment:CENTER_LEFT; -fx-padding:10 12 10 16;"
                 + "-fx-cursor:hand; -fx-min-width:160px; -fx-max-width:160px;"
                 + "-fx-border-width:0; -fx-background-radius:0;");
        b.setOnMouseEntered(e -> b.setStyle(b.getStyle()
            .replace("-fx-background-color:transparent", "-fx-background-color:#1e1e40")
            .replace("-fx-text-fill:#9d9ab5", "-fx-text-fill:#e8e6f0")));
        b.setOnMouseExited(e -> b.setStyle(b.getStyle()
            .replace("-fx-background-color:#1e1e40", "-fx-background-color:transparent")
            .replace("-fx-text-fill:#e8e6f0", "-fx-text-fill:#9d9ab5")));
        return b;
    }

    // ── TOPO ─────────────────────────────────────────────────────────────
    private VBox criarTopo() {
        VBox topo = new VBox(10);
        topo.setPadding(new Insets(16, 16, 0, 16));
        topo.setStyle("-fx-background-color:" + BG + ";");

        // Linha 1: busca + filtro
        HBox linhaBusca = new HBox(10);
        linhaBusca.setAlignment(Pos.CENTER_LEFT);

        txtBusca = new TextField();
        txtBusca.setPromptText("🔍  Buscar por título ou artista...");
        txtBusca.setPrefWidth(300);
        txtBusca.getStyleClass().add("search-field");
        txtBusca.setStyle("-fx-background-color:#1a1a3e; -fx-text-fill:#e8e6f0;"
                        + "-fx-prompt-text-fill:#6b6890; -fx-border-color:#2d2b5e;"
                        + "-fx-border-radius:20; -fx-background-radius:20;"
                        + "-fx-padding:7 14 7 14; -fx-font-family:'Segoe UI';");
        txtBusca.setOnAction(e -> acaoBuscar());

        Button btnBuscar = new Button("Buscar");
        btnBuscar.getStyleClass().add("btn-primary");
        estilizarBtn(btnBuscar, ACCENT, TEXT);
        btnBuscar.setOnAction(e -> acaoBuscar());

        MenuButton menuTipo = new MenuButton("Todos  ▾");
        menuTipo.getStyleClass().add("filter-menu");
        menuTipo.setStyle("-fx-background-color:#1a1a3e; -fx-text-fill:#e8e6f0;"
                        + "-fx-border-color:#2d2b5e; -fx-border-radius:20;"
                        + "-fx-background-radius:20; -fx-font-weight:bold; -fx-cursor:hand;");
        for (String tipo : new String[]{"Todos","Álbum de Estúdio","Single","EP","Álbum ao Vivo"}) {
            MenuItem mi = new MenuItem(tipo);
            mi.setOnAction(e -> {
                menuTipo.setText(tipo.equals("Todos") ? "Todos  ▾" : tipo + "  ▾");
                filtrarPorTipo(tipo);
            });
            menuTipo.getItems().add(mi);
        }

        Button btnHome = new Button("⌂ Início");
        estilizarBtn(btnHome, CARD, MUTED);
        btnHome.setOnAction(e -> renderizarHome());

        linhaBusca.getChildren().addAll(txtBusca, btnBuscar, menuTipo, btnHome);

        // Linha 2: 4 cards de ação em destaque
        HBox linhaAcoes = new HBox(12);
        linhaAcoes.setPadding(new Insets(8, 0, 12, 0));
        linhaAcoes.setAlignment(Pos.CENTER_LEFT);

        linhaAcoes.getChildren().addAll(
            actionCard("🎤", "Artistas",  "Favoritos",        false, e -> mostrarArtistas()),
            actionCard("✨", "Recomendação", "Sugestões por critério", false, e -> popupPlaylist()),
            actionCard("📊", "Dashboard", "Estatísticas",     false, e -> mostrarDashboard()),
            actionCard("⭐", "Avaliados", "Suas avaliações",  true,  e -> mostrarAvaliados())
        );

        topo.getChildren().addAll(linhaBusca, linhaAcoes);
        return topo;
    }

    /** Card de ação visual (Artistas, Playlist, Dashboard, Avaliados) */
    private VBox actionCard(String icone, String titulo, String sub,
                             boolean gold, javafx.event.EventHandler<javafx.event.ActionEvent> acao) {
        VBox card = new VBox(4);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(14, 20, 14, 16));
        card.setPrefWidth(175);
        card.setMinWidth(150);

        String bordaBase = gold ? "#f6c90e44" : "#2d2b5e";
        String bgHover   = gold ? "#2a2000"   : "#2d1b69";
        String bordaHover= gold ? "#f6c90e"   : ACCENT;

        card.setStyle("-fx-background-color:" + CARD + "; -fx-background-radius:14;"
                    + "-fx-border-color:" + bordaBase + "; -fx-border-radius:14;"
                    + "-fx-border-width:1; -fx-cursor:hand;");

        Label lblIcone  = new Label(icone);
        lblIcone.setStyle("-fx-font-size:26px;");

        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:14px;"
                         + "-fx-font-weight:bold; -fx-text-fill:" + (gold ? GOLD : TEXT) + ";");

        Label lblSub    = new Label(sub);
        lblSub.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:11px; -fx-text-fill:" + MUTED + ";");

        card.getChildren().addAll(lblIcone, lblTitulo, lblSub);
        card.setOnMouseClicked(e -> acao.handle(new javafx.event.ActionEvent()));
        card.setOnMouseEntered(e -> card.setStyle(
            "-fx-background-color:" + bgHover + "; -fx-background-radius:14;"
            + "-fx-border-color:" + bordaHover + "; -fx-border-radius:14;"
            + "-fx-border-width:1; -fx-cursor:hand;"
            + "-fx-effect:dropshadow(gaussian," + bordaHover + "55,12,0,0,0);"));
        card.setOnMouseExited(e -> card.setStyle(
            "-fx-background-color:" + CARD + "; -fx-background-radius:14;"
            + "-fx-border-color:" + bordaBase + "; -fx-border-radius:14;"
            + "-fx-border-width:1; -fx-cursor:hand;"));
        return card;
    }

    // (containerCentral é criado diretamente em mostrar())

    // ── RENDERIZAÇÃO ─────────────────────────────────────────────────────
    private void renderizarHome() {
        containerCentral.getChildren().clear();

        // Saudação
        Label saudacao = new Label("Bem-vindo de volta 👋");
        saudacao.setStyle("-fx-font-family:Impact; -fx-font-size:22px; -fx-text-fill:#a78bfa;");
        containerCentral.getChildren().add(saudacao);

        // Ouvidos recentemente
        List<Musica> recentes = ctrl.getHistoricoRecente(6);
        if (!recentes.isEmpty()) {
            containerCentral.getChildren().add(criarSecao("🕐 Ouvidos Recentemente", recentes));
        }

        renderizar(ctrl.getBiblioteca(), false);
    }

    private void renderizar(List<Musica> lista, boolean limparPrimeiro) {
        if (limparPrimeiro) containerCentral.getChildren().clear();

        if (lista.isEmpty()) {
            Label msg = new Label("Nenhuma obra cadastrada ainda.\nUse ➕ Adicionar na sidebar para começar.");
            msg.setStyle("-fx-font-size:14px; -fx-text-fill:" + MUTED + "; -fx-text-alignment:center;");
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
        containerCentral.getChildren().clear();
        List<Musica> lista = tipo.equals("Todos") ? ctrl.getBiblioteca()
            : ctrl.getBiblioteca().stream()
                .filter(m -> tipo.equals(m.getTipo())).collect(Collectors.toList());
        renderizar(lista, false);
    }

    private VBox criarSecao(String titulo, List<Musica> itens) {
        VBox secao = new VBox(8);

        Label lbl = new Label(titulo);
        lbl.getStyleClass().add("section-title");
        lbl.setStyle("-fx-font-family:Impact; -fx-font-size:15px; -fx-text-fill:#c4b5fd;");

        HBox linha = new HBox(6);
        linha.setAlignment(Pos.CENTER);

        HBox cards = new HBox(12);
        cards.setPadding(new Insets(4, 0, 4, 0));
        for (Musica m : itens) cards.getChildren().add(criarCard(m));

        ScrollPane sp = scrollH(cards);
        HBox.setHgrow(sp, Priority.ALWAYS);

        Button esq = new Button("◀");
        Button dir = new Button("▶");
        estilizarBtn(esq, CARD, MUTED);
        estilizarBtn(dir, CARD, MUTED);
        esq.setOnAction(e -> sp.setHvalue(Math.max(0, sp.getHvalue() - 0.25)));
        dir.setOnAction(e -> sp.setHvalue(Math.min(1, sp.getHvalue() + 0.25)));

        linha.getChildren().addAll(esq, sp, dir);
        secao.getChildren().addAll(lbl, linha);
        return secao;
    }

    // ── CARD DE MÍDIA ────────────────────────────────────────────────────
    private VBox criarCard(Musica m) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(8));
        card.setPrefWidth(136); card.setMaxWidth(136);
        card.getStyleClass().add("media-card");
        estiloCardMusica(card, false);

        ImageView iv = new ImageView();
        iv.setFitWidth(120); iv.setFitHeight(155); iv.setPreserveRatio(false);
        carregarImg(iv, m.getUrlCapa(), 120, 155, PH);

        Label nome = new Label(m.getNome());
        nome.getStyleClass().add("media-card-title");
        nome.setStyle("-fx-font-family:'Segoe UI'; -fx-font-weight:bold; -fx-font-size:11px;"
                    + "-fx-text-fill:#e8e6f0; -fx-text-alignment:center;");
        nome.setWrapText(true); nome.setMaxWidth(122); nome.setAlignment(Pos.CENTER);

        Label artista = new Label(safe(m.getArtista(), ""));
        artista.getStyleClass().add("media-card-artist");
        artista.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:10px;"
                       + "-fx-text-fill:#9d9ab5; -fx-text-alignment:center;");
        artista.setWrapText(true); artista.setMaxWidth(122); artista.setAlignment(Pos.CENTER);

        card.getChildren().addAll(iv, nome, artista);
        card.setOnMouseClicked(ev -> mostrarDetalhes(m));
        card.setOnMouseEntered(ev -> estiloCardMusica(card, true));
        card.setOnMouseExited(ev  -> estiloCardMusica(card, false));
        return card;
    }

    private void estiloCardMusica(VBox card, boolean hover) {
        card.setStyle(hover
            ? "-fx-background-color:#2d1b6944; -fx-background-radius:10;"
            + "-fx-border-color:#7c3aed; -fx-border-radius:10; -fx-border-width:1; -fx-cursor:hand;"
            : "-fx-background-color:#1a1a3e; -fx-background-radius:10;"
            + "-fx-border-color:#2d2b5e; -fx-border-radius:10; -fx-border-width:1; -fx-cursor:hand;");
    }

    // ── DETALHES ─────────────────────────────────────────────────────────
    private void mostrarDetalhes(Musica m) {
        try { ctrl.registrarEscuta(m); } catch (IOException ignored) {}

        Button voltar = new Button("⬅  Voltar");
        estilizarBtn(voltar, CARD, MUTED);
        voltar.setOnAction(e -> voltarHome());

        // Capa + info lado a lado
        HBox linha = new HBox(22);
        linha.setAlignment(Pos.TOP_LEFT);

        ImageView iv = new ImageView();
        iv.setFitWidth(180); iv.setFitHeight(230); iv.setPreserveRatio(false);
        carregarImg(iv, m.getUrlCapa(), 180, 230, PH);

        VBox info = new VBox(8);
        info.setStyle("-fx-background-color:#1a1a3e; -fx-padding:18; -fx-background-radius:14;");
        info.setPrefWidth(520);

        Label lblNome = new Label(m.getNome());
        lblNome.setStyle("-fx-font-family:Impact; -fx-font-size:26px; -fx-text-fill:#e8e6f0;");
        lblNome.setWrapText(true);
        info.getChildren().add(lblNome);

        for (String l : m.exibirInformacoes().split("\n")) {
            if (l.trim().isEmpty()) continue;
            Label ll = new Label(l);
            ll.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:13px; -fx-text-fill:#e8e6f0;");
            ll.setWrapText(true);
            info.getChildren().add(ll);
        }

        // Última escuta
        Label lblEscuta = new Label("🕐  " + m.getUltimaEscutaFormatada()
            + (m.getTotalEscutas() > 0 ? "  (" + m.getTotalEscutas() + "x)" : ""));
        lblEscuta.setStyle("-fx-font-size:11px; -fx-font-style:italic; -fx-text-fill:#6b6890;");
        info.getChildren().add(lblEscuta);

        // Tracklist
        if (!m.getFaixas().isEmpty()) {
            Separator sep = new Separator();
            Label lblTrack = new Label("🎼  Tracklist");
            lblTrack.setStyle("-fx-font-family:'Segoe UI'; -fx-font-weight:bold;"
                            + "-fx-font-size:13px; -fx-text-fill:#a78bfa;");
            info.getChildren().addAll(sep, lblTrack);
            for (Faixa f : m.getFaixas()) {
                Label lf = new Label(f.toString());
                lf.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:12px; -fx-text-fill:#9d9ab5;");
                info.getChildren().add(lf);
            }
        }

        HBox.setHgrow(info, Priority.ALWAYS);
        linha.getChildren().addAll(iv, info);

        // Botão avaliar
        Button btnAvaliar = new Button(m.getNota() > 0 ? "🔄  Revisar Avaliação" : "⭐  Avaliar");
        estilizarBtn(btnAvaliar, GOLD, "#0d0d1a");
        btnAvaliar.setOnAction(e -> { popupAvaliar(m); mostrarDetalhes(m); });

        // Botão "Ver no Last.fm" — abre a página do álbum no navegador,
        // mesmo padrão do botão "Executar mídia" da Biblioteca
        Button btnLastFm = new Button("🌐  Ver no Last.fm");
        estilizarBtn(btnLastFm, ACCENT, TEXT);
        btnLastFm.setOnAction(e -> abrirNoLastFm(m));

        HBox acoes = new HBox(10, btnAvaliar, btnLastFm);

        VBox centro = new VBox(14, voltar, linha, acoes);
        trocarCentro(centro);
    }

    /** Abre a página do álbum/artista no site do Last.fm no navegador padrão */
    private void abrirNoLastFm(Musica m) {
        try {
            String artista = m.getArtista() != null ? m.getArtista().trim() : "";
            String album   = m.getNome() != null ? m.getNome().trim() : "";
            if (artista.isEmpty() || album.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Dados insuficientes para abrir no Last.fm.").showAndWait();
                return;
            }
            String artistaCod = java.net.URLEncoder.encode(artista.replace(" ", "+"), "UTF-8");
            String albumCod   = java.net.URLEncoder.encode(album.replace(" ", "+"), "UTF-8");
            String url = "https://www.last.fm/music/" + artistaCod + "/" + albumCod;
            java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Não foi possível abrir o Last.fm.").showAndWait();
        }
    }

    // ── PLAYLIST ─────────────────────────────────────────────────────────
    private void popupPlaylist() {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Recomendação Musical");
        VBox raiz = popupBase();

        Label lblTitulo = new Label("✨  Gerar Recomendação");
        lblTitulo.setStyle("-fx-font-family:Impact; -fx-font-size:18px; -fx-text-fill:#a78bfa;");

        TextField txtGenero  = inputField("Ex: Rock, Pop, Jazz... (vazio = todos os gêneros)");
        TextField txtDuracao = inputField("Duração máxima em minutos (vazio = sem limite)");

        Button btnGerar = new Button("▶  Gerar");
        estilizarBtn(btnGerar, ACCENT, TEXT);
        btnGerar.setPrefWidth(360); btnGerar.setDefaultButton(true);

        Label lblErro = new Label("");
        lblErro.setStyle("-fx-text-fill:#e74c3c; -fx-font-size:12px;");

        btnGerar.setOnAction(e -> {
            try {
                String genero = txtGenero.getText().trim();
                int durMax = txtDuracao.getText().trim().isEmpty() ? 0
                    : Integer.parseInt(txtDuracao.getText().trim());
                List<Musica> playlist = ctrl.gerarPlaylist(genero, durMax);
                popup.close();
                mostrarPlaylist(playlist, genero, durMax);
            } catch (NumberFormatException ex) {
                lblErro.setText("Duração deve ser um número inteiro.");
            } catch (ArquivoNaoEncontradoException ex) {
                lblErro.setText(ex.getMessage());
            }
        });

        raiz.getChildren().addAll(
            lblTitulo,
            labelPopup("Gênero (opcional):"), txtGenero,
            labelPopup("Duração máxima em minutos (opcional):"), txtDuracao,
            btnGerar, lblErro
        );
        popup.setScene(new Scene(raiz, 420, 300));
        aplicarCssPopup(popup);
        popup.showAndWait();
    }

    private void mostrarPlaylist(List<Musica> playlist, String genero, int durMax) {
        VBox painel = new VBox(14);
        painel.setPadding(new Insets(20));
        painel.setStyle("-fx-background-color:" + BG + ";");

        Button voltar = new Button("⬅  Voltar");
        estilizarBtn(voltar, CARD, MUTED);
        voltar.setOnAction(e -> voltarHome());

        String criterio = (!genero.isEmpty() ? "Gênero: " + genero : "Todos os gêneros")
            + (durMax > 0 ? "  •  Máx: " + durMax + "min" : "");
        int totalSeg = playlist.stream().mapToInt(Musica::getDuracaoTotalSegundos).sum();
        String durTotal = totalSeg > 0
            ? (totalSeg/3600) + "h " + ((totalSeg%3600)/60) + "m" : "—";

        Label lblT = new Label("✨  Recomendação Gerada");
        lblT.setStyle("-fx-font-family:Impact; -fx-font-size:22px; -fx-text-fill:#27ae60;");
        Label lblInfo = new Label(criterio + "   •   " + playlist.size() + " obras   •   " + durTotal);
        lblInfo.setStyle("-fx-font-size:12px; -fx-text-fill:" + MUTED + ";");

        VBox lista = new VBox(6);
        for (int i = 0; i < playlist.size(); i++) {
            Musica m = playlist.get(i);
            HBox item = new HBox(12);
            item.setAlignment(Pos.CENTER_LEFT);
            item.setPadding(new Insets(10));
            item.setStyle("-fx-background-color:#1a1a3e; -fx-background-radius:8;"
                        + "-fx-border-color:#2d2b5e; -fx-border-radius:8; -fx-cursor:hand;");

            Label num = new Label(String.valueOf(i + 1));
            num.setStyle("-fx-font-family:Impact; -fx-font-size:18px; -fx-text-fill:#a78bfa;"
                       + "-fx-min-width:28px;");

            ImageView iv = new ImageView();
            iv.setFitWidth(48); iv.setFitHeight(48); iv.setPreserveRatio(false);
            carregarImg(iv, m.getUrlCapa(), 48, 48, PH);

            VBox textos = new VBox(2);
            Label lN = new Label(m.getNome());
            lN.setStyle("-fx-font-family:'Segoe UI'; -fx-font-weight:bold;"
                      + "-fx-text-fill:#e8e6f0; -fx-font-size:13px;");
            Label lA = new Label(safe(m.getArtista(), "") + "  •  " + safe(m.getTipo(), "")
                + (m.getNota() > 0 ? "  ⭐" + m.getNota() : ""));
            lA.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:11px; -fx-text-fill:#9d9ab5;");
            Label lD = new Label(m.getDuracaoTotalSegundos() > 0 ? m.getDuracaoTotalFormatada() : "");
            lD.setStyle("-fx-font-size:11px; -fx-text-fill:#a78bfa;");
            textos.getChildren().addAll(lN, lA, lD);
            HBox.setHgrow(textos, Priority.ALWAYS);

            item.getChildren().addAll(num, iv, textos);
            item.setOnMouseClicked(ev -> mostrarDetalhes(m));
            item.setOnMouseEntered(ev -> item.setStyle(
                "-fx-background-color:#2d1b6944; -fx-background-radius:8;"
                + "-fx-border-color:#7c3aed; -fx-border-radius:8; -fx-cursor:hand;"));
            item.setOnMouseExited(ev -> item.setStyle(
                "-fx-background-color:#1a1a3e; -fx-background-radius:8;"
                + "-fx-border-color:#2d2b5e; -fx-border-radius:8; -fx-cursor:hand;"));
            lista.getChildren().add(item);
        }

        ScrollPane sp = new ScrollPane(new VBox(10, voltar, lblT, lblInfo, new Separator(), lista));
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:transparent; -fx-background:transparent;");
        trocarCentro(sp);
    }

    // ── ARTISTAS ─────────────────────────────────────────────────────────
    private void mostrarArtistas() {
        VBox painel = new VBox(16);
        painel.setPadding(new Insets(20));
        painel.setStyle("-fx-background-color:" + BG + ";");

        Button voltar = new Button("⬅  Voltar");
        estilizarBtn(voltar, CARD, MUTED);
        voltar.setOnAction(e -> voltarHome());

        Label titulo = new Label("🎤  Artistas Favoritos");
        titulo.setStyle("-fx-font-family:Impact; -fx-font-size:22px; -fx-text-fill:#a78bfa;");

        Button btnAdd = new Button("➕  Adicionar Artista");
        estilizarBtn(btnAdd, GREEN, "#bbf7d0");
        btnAdd.setOnAction(e -> { popupAdicionarArtista(); mostrarArtistas(); });

        HBox cabecalho = new HBox(12, titulo, btnAdd);
        cabecalho.setAlignment(Pos.CENTER_LEFT);

        List<Artista> lista = ctrlArtista.getLista();
        if (lista.isEmpty()) {
            Label msg = new Label("Nenhum artista favorito ainda.\nClique em ➕ para adicionar.");
            msg.setStyle("-fx-font-size:13px; -fx-text-fill:" + MUTED + "; -fx-text-alignment:center;");
            msg.setWrapText(true);
            ScrollPane sp = new ScrollPane(new VBox(14, voltar, cabecalho, msg));
            sp.setFitToWidth(true);
            sp.setStyle("-fx-background-color:transparent; -fx-background:transparent;");
            trocarCentro(sp);
            return;
        }

        HBox cards = new HBox(14);
        cards.setPadding(new Insets(8, 0, 0, 0));
        for (Artista a : lista) cards.getChildren().add(criarCardArtista(a));

        ScrollPane spCards = scrollH(cards);
        ScrollPane sp = new ScrollPane(new VBox(14, voltar, cabecalho, spCards));
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:transparent; -fx-background:transparent;");
        trocarCentro(sp);
    }

    private VBox criarCardArtista(Artista a) {
        VBox card = new VBox(6);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(12));
        card.setPrefWidth(148); card.setMaxWidth(148);
        card.setStyle("-fx-background-color:#1a1a3e; -fx-background-radius:12;"
                    + "-fx-border-color:#2d2b5e; -fx-border-radius:12; -fx-cursor:hand;");

        ImageView iv = new ImageView();
        iv.setFitWidth(100); iv.setFitHeight(100); iv.setPreserveRatio(false);
        carregarImg(iv, a.getUrlFoto(), 100, 100, PH_ART);
        Circle clip = new Circle(50, 50, 50);
        iv.setClip(clip);
        StackPane foto = new StackPane(iv);
        foto.setPrefSize(100, 100);

        Label nome = new Label(a.getNome());
        nome.setStyle("-fx-font-family:'Segoe UI'; -fx-font-weight:bold; -fx-font-size:12px;"
                    + "-fx-text-fill:#e8e6f0; -fx-text-alignment:center;");
        nome.setWrapText(true); nome.setMaxWidth(130); nome.setAlignment(Pos.CENTER);

        Label genero = new Label(safe(a.getGenero(), ""));
        genero.setStyle("-fx-font-size:10px; -fx-text-fill:#9d9ab5;");

        card.getChildren().addAll(foto, nome, genero);
        card.setOnMouseClicked(ev -> mostrarDetalheArtista(a));
        card.setOnMouseEntered(ev -> card.setStyle(
            "-fx-background-color:#2d1b69; -fx-background-radius:12;"
            + "-fx-border-color:#7c3aed; -fx-border-radius:12; -fx-cursor:hand;"));
        card.setOnMouseExited(ev -> card.setStyle(
            "-fx-background-color:#1a1a3e; -fx-background-radius:12;"
            + "-fx-border-color:#2d2b5e; -fx-border-radius:12; -fx-cursor:hand;"));
        return card;
    }

    private void mostrarDetalheArtista(Artista a) {
        VBox painel = new VBox(0);
        painel.setStyle("-fx-background-color:" + BG + ";");

        Button voltar = new Button("⬅  Artistas");
        estilizarBtn(voltar, CARD, MUTED);
        voltar.setOnAction(e -> mostrarArtistas());

        HBox linha = new HBox(24);
        linha.setPadding(new Insets(16));
        linha.setAlignment(Pos.TOP_LEFT);

        ImageView iv = new ImageView();
        iv.setFitWidth(160); iv.setFitHeight(160); iv.setPreserveRatio(false);
        carregarImg(iv, a.getUrlFoto(), 160, 160, PH_ART);
        Circle clip = new Circle(80, 80, 80);
        iv.setClip(clip);
        StackPane foto = new StackPane(iv);
        foto.setPrefSize(160, 160);

        VBox lado = new VBox(10);
        Label lblNome = new Label(a.getNome());
        lblNome.setStyle("-fx-font-family:Impact; -fx-font-size:26px; -fx-text-fill:#e8e6f0;");

        if (!safe(a.getGenero(),"").isEmpty()) {
            Label lg = new Label("🏷️  " + a.getGenero());
            lg.setStyle("-fx-font-size:13px; -fx-text-fill:#9d9ab5;");
            lado.getChildren().add(lg);
        }
        if (!safe(a.getPais(),"").isEmpty()) {
            Label lp = new Label("🌎  " + a.getPais());
            lp.setStyle("-fx-font-size:13px; -fx-text-fill:#9d9ab5;");
            lado.getChildren().add(lp);
        }

        Label lblDescT = new Label("📝  Sobre:");
        lblDescT.setStyle("-fx-font-weight:bold; -fx-text-fill:#a78bfa;");

        Label lblDesc = new Label(safe(a.getDescricao(), "Nenhuma descrição."));
        lblDesc.setWrapText(true); lblDesc.setMaxWidth(440);
        lblDesc.setStyle("-fx-font-size:13px; -fx-text-fill:#e8e6f0;"
                       + "-fx-background-color:#1a1a3e; -fx-padding:12; -fx-background-radius:8;");

        Button btnEd = new Button("✏️  Editar");
        Button btnRm = new Button("🗑️  Remover");
        estilizarBtn(btnEd, "#2980b9", TEXT);
        estilizarBtn(btnRm, DANGER, "#fecaca");

        btnEd.setOnAction(e -> { popupEditarArtista(a); mostrarDetalheArtista(a); });
        btnRm.setOnAction(e -> {
            Alert c = new Alert(Alert.AlertType.CONFIRMATION, "Remover \"" + a.getNome() + "\"?");
            c.showAndWait().ifPresent(r -> {
                if (r == ButtonType.OK) {
                    try { ctrlArtista.remover(a.getNome()); mostrarArtistas(); }
                    catch (Exception ex) { new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait(); }
                }
            });
        });

        lado.getChildren().addAll(0, java.util.List.of(lblNome));
        lado.getChildren().addAll(lblDescT, lblDesc, new HBox(10, btnEd, btnRm));
        linha.getChildren().addAll(foto, lado);

        VBox corpo = new VBox(14, voltar, linha);
        corpo.setPadding(new Insets(16));
        ScrollPane sp = new ScrollPane(corpo);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:transparent; -fx-background:transparent;");
        trocarCentro(sp);
    }

    // ── DASHBOARD ────────────────────────────────────────────────────────
    private void mostrarDashboard() {
        VBox painel = new VBox(16);
        painel.setPadding(new Insets(20));
        painel.setStyle("-fx-background-color:" + BG + ";");

        Button voltar = new Button("⬅  Voltar");
        estilizarBtn(voltar, CARD, MUTED);
        voltar.setOnAction(e -> voltarHome());

        Label titulo = new Label("📊  Dashboard Musical");
        titulo.setStyle("-fx-font-family:Impact; -fx-font-size:22px; -fx-text-fill:#a78bfa;");

        List<Musica> bib = ctrl.getBiblioteca();
        double mediaNota = ctrl.getMediaNotas();

        TilePane tiles = new TilePane(16, 16);
        tiles.setPrefColumns(4);
        tiles.getChildren().addAll(
            statCard("🎵", "Obras",         String.valueOf(bib.size())),
            statCard("🎼", "Faixas",        String.valueOf(ctrl.getTotalFaixas())),
            statCard("⏱️", "Tempo total",   ctrl.getDuracaoTotalFormatada()),
            statCard("🎤", "Artista top",   ctrl.getArtistaTop()),
            statCard("⭐", "Média notas",   mediaNota > 0 ? String.format("%.1f/5", mediaNota) : "—"),
            statCard("📝", "Avaliados",     bib.stream().filter(m->m.getNota()>0).count() + "/" + bib.size()),
            statCard("💿", "Álbuns",        String.valueOf(bib.stream().filter(m->m instanceof AlbumMusical).count())),
            statCard("🎧", "Singles",       String.valueOf(bib.stream().filter(m->m instanceof Single).count())),
            statCard("📀", "EPs",          String.valueOf(bib.stream().filter(m->m instanceof EP).count())),
            statCard("🎤", "Live",          String.valueOf(bib.stream().filter(m->m instanceof LiveAlbum).count()))
        );

        // Top mais ouvidos
        List<Musica> top3 = ctrl.getHistoricoRecente(3);
        VBox topOuvidos = new VBox(6);
        Label lblTO = new Label("🔥  Mais Ouvidos");
        lblTO.setStyle("-fx-font-family:Impact; -fx-font-size:16px; -fx-text-fill:#c4b5fd;");
        topOuvidos.getChildren().add(lblTO);
        if (top3.isEmpty()) {
            topOuvidos.getChildren().add(labelPopup("Nenhuma obra ouvida ainda."));
        } else {
            for (int i = 0; i < top3.size(); i++) {
                Musica m = top3.get(i);
                Label l = new Label((i+1) + ".  " + m.getNome()
                    + "  —  " + safe(m.getArtista(),"") + "  (" + m.getTotalEscutas() + "x)");
                l.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:13px; -fx-text-fill:#e8e6f0;");
                topOuvidos.getChildren().add(l);
            }
        }

        ScrollPane sp = new ScrollPane(new VBox(14, voltar, titulo, tiles, topOuvidos));
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:transparent; -fx-background:transparent;");
        trocarCentro(sp);
    }

    private VBox statCard(String icone, String label, String valor) {
        VBox card = new VBox(4);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(16));
        card.setMinWidth(160);
        card.setStyle("-fx-background-color:#1a1a3e; -fx-background-radius:12;"
                    + "-fx-border-color:#2d2b5e; -fx-border-radius:12;");
        Label lIco = new Label(icone);
        lIco.setStyle("-fx-font-size:20px;");
        Label lVal = new Label(valor);
        lVal.setStyle("-fx-font-family:Impact; -fx-font-size:22px; -fx-text-fill:#a78bfa;");
        Label lLbl = new Label(label);
        lLbl.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:11px; -fx-text-fill:#9d9ab5;");
        card.getChildren().addAll(lIco, lVal, lLbl);
        return card;
    }

    // ── AVALIADOS ────────────────────────────────────────────────────────
    private void mostrarAvaliados() {
        List<Musica> avaliados = ctrl.getBiblioteca().stream()
            .filter(m -> m.getNota() > 0).collect(Collectors.toList());

        Button voltar = new Button("⬅  Voltar");
        estilizarBtn(voltar, CARD, MUTED);
        voltar.setOnAction(e -> voltarHome());

        Label titulo = new Label("⭐  Avaliados");
        titulo.setStyle("-fx-font-family:Impact; -fx-font-size:22px; -fx-text-fill:#f6c90e;");

        if (avaliados.isEmpty()) {
            Label msg = new Label("Nenhum item avaliado ainda.");
            msg.setStyle("-fx-font-size:14px; -fx-text-fill:" + MUTED + ";");
            ScrollPane sp = new ScrollPane(new VBox(14, voltar, titulo, msg));
            sp.setFitToWidth(true);
            sp.setStyle("-fx-background-color:transparent; -fx-background:transparent;");
            trocarCentro(sp);
            return;
        }

        HBox cards = new HBox(14);
        cards.setPadding(new Insets(8, 0, 0, 0));
        for (Musica m : avaliados) {
            VBox c = criarCard(m);
            c.setOnMouseClicked(ev -> mostrarDetalheAvaliacao(m));
            c.setOnMouseEntered(ev -> c.setStyle(
                "-fx-background-color:#2a2000; -fx-background-radius:10;"
                + "-fx-border-color:#f6c90e; -fx-border-radius:10; -fx-cursor:hand;"));
            c.setOnMouseExited(ev -> estiloCardMusica(c, false));
            cards.getChildren().add(c);
        }

        ScrollPane sp = new ScrollPane(new VBox(14, voltar, titulo, scrollH(cards)));
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:transparent; -fx-background:transparent;");
        trocarCentro(sp);
    }

    private void mostrarDetalheAvaliacao(Musica m) {
        Button voltar = new Button("⬅  Avaliados");
        estilizarBtn(voltar, CARD, MUTED);
        voltar.setOnAction(e -> mostrarAvaliados());

        HBox linha = new HBox(24);
        linha.setPadding(new Insets(16));
        linha.setAlignment(Pos.TOP_LEFT);

        ImageView iv = new ImageView();
        iv.setFitWidth(150); iv.setFitHeight(195); iv.setPreserveRatio(false);
        carregarImg(iv, m.getUrlCapa(), 150, 195, PH);

        VBox lado = new VBox(12);

        Label lblNome = new Label(m.getNome());
        lblNome.setStyle("-fx-font-family:Impact; -fx-font-size:24px; -fx-text-fill:#e8e6f0;");

        // Círculo de nota
        Circle circ = new Circle(40); circ.setFill(Color.web(GOLD));
        Text txtNota = new Text(String.valueOf(m.getNota()));
        txtNota.setFont(Font.font("Impact", FontWeight.BOLD, 32));
        txtNota.setFill(Color.web("#0d0d1a"));
        StackPane circNota = new StackPane(circ, txtNota);
        circNota.setPrefSize(80, 80);
        Label lblNotaLabel = new Label("Nota");
        lblNotaLabel.setStyle("-fx-font-size:11px; -fx-text-fill:#9d9ab5;");
        VBox blocoNota = new VBox(4, circNota, lblNotaLabel);
        blocoNota.setAlignment(Pos.CENTER);

        Label lblCT = new Label("📝  Crítica:");
        lblCT.setStyle("-fx-font-weight:bold; -fx-text-fill:#a78bfa;");
        Label lblC = new Label(safe(m.getComentario(), "Nenhum comentário."));
        lblC.setWrapText(true); lblC.setMaxWidth(440);
        lblC.setStyle("-fx-font-family:'Segoe UI'; -fx-font-style:italic; -fx-text-fill:#e8e6f0;"
                    + "-fx-background-color:#1a1a3e; -fx-padding:12; -fx-background-radius:8;");

        Button btnR = new Button("🔄  Revisar Avaliação");
        estilizarBtn(btnR, GOLD, "#0d0d1a");
        btnR.setOnAction(e -> { popupAvaliar(m); mostrarDetalheAvaliacao(m); });

        lado.getChildren().addAll(lblNome, blocoNota, lblCT, lblC, btnR);
        linha.getChildren().addAll(iv, lado);

        ScrollPane sp = new ScrollPane(new VBox(14, voltar, linha));
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:transparent; -fx-background:transparent;");
        trocarCentro(sp);
    }

    // ── CADASTRO LAST.FM ─────────────────────────────────────────────────
    private void fluxoCadastroLastFm() {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Adicionar via Last.fm");
        VBox raiz = popupBase();

        Label lblT = new Label("➕  Adicionar via Last.fm");
        lblT.setStyle("-fx-font-family:Impact; -fx-font-size:18px; -fx-text-fill:#a78bfa;");

        TextField txtNome = inputField("Nome do álbum, EP, single...");
        txtNome.setPrefWidth(380);

        ComboBox<String> cbTipo = new ComboBox<>(FXCollections.observableArrayList(
            "Álbum de Estúdio","Single","EP","Álbum ao Vivo"));
        cbTipo.setValue("Álbum de Estúdio");
        cbTipo.setStyle("-fx-background-color:#1a1a3e; -fx-text-fill:#e8e6f0;");

        Label lblDica = new Label("");
        lblDica.setStyle("-fx-font-size:11px; -fx-text-fill:#9d9ab5; -fx-font-style:italic;");
        cbTipo.setOnAction(e -> {
            if ("Single".equals(cbTipo.getValue())) {
                lblDica.setText("💡 Busca por faixa individual (não por álbum)");
                txtNome.setPromptText("Nome da música...");
            } else {
                lblDica.setText("💡 Busca por álbum completo");
                txtNome.setPromptText("Nome do álbum, EP...");
            }
        });

        Button btnBuscar = new Button("🔍  Buscar no Last.fm");
        estilizarBtn(btnBuscar, ACCENT, TEXT);

        VBox listaResultados = new VBox(6);
        listaResultados.getChildren().add(labelPopup("Aguardando busca..."));

        btnBuscar.setOnAction(e -> {
            String termo = txtNome.getText().trim();
            if (termo.isEmpty()) { new Alert(Alert.AlertType.WARNING,"Digite o nome.").showAndWait(); return; }
            btnBuscar.setDisable(true); btnBuscar.setText("⏳  Buscando...");
            listaResultados.getChildren().clear();
            listaResultados.getChildren().add(labelPopup("Consultando Last.fm..."));

            boolean ehSingle = "Single".equals(cbTipo.getValue());

            new Thread(() -> {
                try {
                    if (ehSingle) {
                        // Busca por FAIXA individual via track.search
                        List<controller.LastFmService.ResultadoBuscaFaixa> resultados = lastFm.buscarFaixa(termo);
                        Platform.runLater(() -> {
                            listaResultados.getChildren().clear();
                            listaResultados.getChildren().add(labelPopup("Clique para selecionar:"));
                            for (controller.LastFmService.ResultadoBuscaFaixa r : resultados) {
                                HBox item = montarItemResultado(r.toString(), r.urlCapa);
                                item.setOnMouseClicked(ev -> {
                                    popup.close();
                                    Platform.runLater(() -> confirmarEImportarFaixa(r));
                                });
                                listaResultados.getChildren().add(item);
                            }
                            btnBuscar.setDisable(false); btnBuscar.setText("🔍  Buscar no Last.fm");
                        });
                    } else {
                        // Busca por ÁLBUM via album.search
                        List<ResultadoBusca> resultados = lastFm.buscarAlbum(termo);
                        Platform.runLater(() -> {
                            listaResultados.getChildren().clear();
                            listaResultados.getChildren().add(labelPopup("Clique para selecionar:"));
                            for (ResultadoBusca r : resultados) {
                                HBox item = montarItemResultado(r.toString(), r.urlCapa);
                                item.setOnMouseClicked(ev -> {
                                    popup.close();
                                    Platform.runLater(() -> confirmarEImportar(r, cbTipo.getValue()));
                                });
                                listaResultados.getChildren().add(item);
                            }
                            btnBuscar.setDisable(false); btnBuscar.setText("🔍  Buscar no Last.fm");
                        });
                    }
                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        listaResultados.getChildren().clear();
                        listaResultados.getChildren().add(labelPopup("❌  " + ex.getMessage()));
                        btnBuscar.setDisable(false); btnBuscar.setText("🔍  Buscar no Last.fm");
                    });
                }
            }).start();
        });
        txtNome.setOnAction(e -> btnBuscar.fire());

        ScrollPane sp = new ScrollPane(listaResultados);
        sp.setFitToWidth(true); sp.setPrefHeight(250);
        sp.setStyle("-fx-background-color:transparent; -fx-background:transparent;");

        raiz.getChildren().addAll(lblT,
            labelPopup("Nome:"), txtNome,
            labelPopup("Tipo:"), cbTipo, lblDica,
            btnBuscar, new Separator(), sp);
        popup.setScene(new Scene(raiz, 480, 560));
        aplicarCssPopup(popup);
        popup.showAndWait();
    }

    /** Cria o item visual (capa + texto) usado nas listas de resultado de busca */
    private HBox montarItemResultado(String texto, String urlCapa) {
        HBox item = new HBox(10);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(8));
        item.setStyle("-fx-background-color:#1a1a3e; -fx-border-color:#2d2b5e;"
                   + "-fx-border-radius:8; -fx-background-radius:8; -fx-cursor:hand;");
        ImageView thumb = new ImageView();
        thumb.setFitWidth(40); thumb.setFitHeight(40);
        carregarImg(thumb, urlCapa, 40, 40, PH);
        Label lbl = new Label(texto);
        lbl.setStyle("-fx-text-fill:#e8e6f0; -fx-font-family:'Segoe UI'; -fx-font-size:12px;");
        lbl.setWrapText(true);
        item.getChildren().addAll(thumb, lbl);
        item.setOnMouseEntered(ev -> item.setStyle(
            "-fx-background-color:#2d1b69; -fx-border-color:#7c3aed;"
            + "-fx-border-radius:8; -fx-background-radius:8; -fx-cursor:hand;"));
        item.setOnMouseExited(ev -> item.setStyle(
            "-fx-background-color:#1a1a3e; -fx-border-color:#2d2b5e;"
            + "-fx-border-radius:8; -fx-background-radius:8; -fx-cursor:hand;"));
        return item;
    }

    /** Confirma e importa uma faixa individual (Single via track.getInfo) */
    private void confirmarEImportarFaixa(controller.LastFmService.ResultadoBuscaFaixa resultado) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Importando...");
        VBox raiz = popupBase();
        Label info = new Label("Importando: " + resultado.nome + " — " + resultado.artista);
        info.setStyle("-fx-font-weight:bold; -fx-text-fill:#e8e6f0;"); info.setWrapText(true);
        Label loading = new Label("⏳  Buscando detalhes da faixa no Last.fm...");
        loading.setStyle("-fx-text-fill:#9d9ab5;");
        raiz.getChildren().addAll(info, loading);
        popup.setScene(new Scene(raiz, 380, 120));
        aplicarCssPopup(popup);
        popup.show();

        new Thread(() -> {
            try {
                Single single = new Single(resultado.nome, resultado.artista);
                lastFm.importarFaixaIndividual(single, resultado.nome, resultado.artista);
                ctrl.adicionar(single);
                Platform.runLater(() -> {
                    popup.close(); voltarHome();
                    new Alert(Alert.AlertType.INFORMATION,
                        resultado.nome + " adicionado como Single!").showAndWait();
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    popup.close();
                    new Alert(Alert.AlertType.ERROR, "Erro: " + ex.getMessage()).showAndWait();
                });
            }
        }).start();
    }

    private void confirmarEImportar(ResultadoBusca resultado, String tipo) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        VBox raiz = popupBase();
        Label info = new Label("Importando: " + resultado.nome + " — " + resultado.artista);
        info.setStyle("-fx-font-weight:bold; -fx-text-fill:#e8e6f0;"); info.setWrapText(true);
        Label loading = new Label("⏳  Buscando faixas no Last.fm...");
        loading.setStyle("-fx-text-fill:#9d9ab5;");
        raiz.getChildren().addAll(info, loading);
        popup.setScene(new Scene(raiz, 380, 120));
        aplicarCssPopup(popup);
        popup.show();

        new Thread(() -> {
            try {
                Musica nova = criarPorTipo(tipo, resultado.nome, resultado.artista);
                lastFm.importarAlbumCompleto(nova, resultado.nome, resultado.artista);
                ctrl.adicionar(nova);
                Platform.runLater(() -> {
                    popup.close(); voltarHome();
                    new Alert(Alert.AlertType.INFORMATION,
                        resultado.nome + " adicionado!\n" + nova.getFaixas().size() + " faixas importadas.")
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

    private Musica criarPorTipo(String tipo, String nome, String artista) {
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

        TextField campo = inputField("Nome da obra ou artista...");
        Button btnB = new Button("🔍  Buscar"); estilizarBtn(btnB, ACCENT, TEXT);
        VBox lista = new VBox(6);
        lista.getChildren().add(labelPopup("Aguardando..."));

        btnB.setOnAction(e -> {
            try {
                List<Musica> found = ctrl.buscar(campo.getText().trim());
                lista.getChildren().clear();
                for (Musica m : found) {
                    Button bi = new Button("🎵  " + m.getNome() + "  —  " + safe(m.getArtista(),""));
                    bi.setMaxWidth(Double.MAX_VALUE);
                    estilizarBtn(bi, CARD, TEXT);
                    bi.setOnAction(ev -> {
                        Alert c = new Alert(Alert.AlertType.CONFIRMATION,
                            "Remover \"" + m.getNome() + "\"? Não pode ser desfeito.");
                        c.showAndWait().ifPresent(r -> {
                            if (r == ButtonType.OK) {
                                try { ctrl.remover(m.getNome()); voltarHome(); popup.close(); }
                                catch (Exception ex) { new Alert(Alert.AlertType.ERROR,ex.getMessage()).showAndWait(); }
                            }
                        });
                    });
                    lista.getChildren().add(bi);
                }
            } catch (ArquivoNaoEncontradoException ex) {
                lista.getChildren().clear();
                lista.getChildren().add(labelPopup("❌  " + ex.getMessage()));
            }
        });
        campo.setOnAction(e -> btnB.fire());

        ScrollPane sp = new ScrollPane(lista);
        sp.setFitToWidth(true); sp.setPrefHeight(200);
        sp.setStyle("-fx-background-color:transparent; -fx-background:transparent;");

        raiz.getChildren().addAll(labelPopup("Buscar para remover:"), campo, btnB, new Separator(), sp);
        popup.setScene(new Scene(raiz, 440, 380));
        aplicarCssPopup(popup);
        popup.showAndWait();
    }

    private void fluxoEditar() {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Editar");
        VBox raiz = popupBase();

        TextField campo = inputField("Nome da obra ou artista...");
        Button btnB = new Button("🔍  Buscar"); estilizarBtn(btnB, ACCENT, TEXT);
        VBox lista = new VBox(6);
        lista.getChildren().add(labelPopup("Aguardando..."));

        btnB.setOnAction(e -> {
            try {
                List<Musica> found = ctrl.buscar(campo.getText().trim());
                lista.getChildren().clear();
                for (Musica m : found) {
                    Button bi = new Button("🎵  " + m.getNome() + "  —  " + safe(m.getArtista(),""));
                    bi.setMaxWidth(Double.MAX_VALUE);
                    estilizarBtn(bi, CARD, TEXT);
                    bi.setOnAction(ev -> {
                        popup.close();
                        Platform.runLater(() -> popupEdicao(m));
                    });
                    lista.getChildren().add(bi);
                }
            } catch (ArquivoNaoEncontradoException ex) {
                lista.getChildren().clear();
                lista.getChildren().add(labelPopup("❌  " + ex.getMessage()));
            }
        });
        campo.setOnAction(e -> btnB.fire());

        ScrollPane sp = new ScrollPane(lista);
        sp.setFitToWidth(true); sp.setPrefHeight(200);
        sp.setStyle("-fx-background-color:transparent; -fx-background:transparent;");

        raiz.getChildren().addAll(labelPopup("Buscar para editar:"), campo, btnB, new Separator(), sp);
        popup.setScene(new Scene(raiz, 440, 380));
        aplicarCssPopup(popup);
        popup.showAndWait();
    }

    private void popupEdicao(Musica m) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Editando: " + m.getNome());
        VBox form = popupBase();

        TextField tN  = inputFieldVal(m.getNome());
        TextField tA  = inputFieldVal(safe(m.getArtista(),""));
        TextField tAn = inputFieldVal(m.getAnoLancamento()>0 ? String.valueOf(m.getAnoLancamento()) : "");
        TextField tG  = inputFieldVal(safe(m.getGenero(),""));
        TextField tC  = inputFieldVal(safe(m.getUrlCapa(),""));

        form.getChildren().addAll(
            labelPopup("Título:"), tN, labelPopup("Artista:"), tA,
            labelPopup("Ano:"), tAn, labelPopup("Gênero:"), tG,
            labelPopup("URL Capa:"), tC
        );

        if (m instanceof LiveAlbum) {
            TextField tL  = inputFieldVal(safe(((LiveAlbum)m).getLocalShow(),""));
            TextField tCi = inputFieldVal(safe(((LiveAlbum)m).getCidadeShow(),""));
            form.getChildren().addAll(labelPopup("Local:"), tL, labelPopup("Cidade:"), tCi);
            Button s = new Button("💾  Salvar"); estilizarBtn(s, GREEN, "#bbf7d0"); s.setDefaultButton(true);
            s.setOnAction(e -> {
                try {
                    String orig = m.getNome(); aplicarBase(m,tN,tA,tAn,tG,tC);
                    ((LiveAlbum)m).setLocalShow(tL.getText().trim());
                    ((LiveAlbum)m).setCidadeShow(tCi.getText().trim());
                    ctrl.editar(orig,m); voltarHome(); popup.close();
                } catch(Exception ex){new Alert(Alert.AlertType.ERROR,ex.getMessage()).showAndWait();}
            });
            form.getChildren().add(s);
        } else {
            Button s = new Button("💾  Salvar"); estilizarBtn(s, GREEN, "#bbf7d0"); s.setDefaultButton(true);
            s.setOnAction(e -> {
                try { String orig=m.getNome(); aplicarBase(m,tN,tA,tAn,tG,tC); ctrl.editar(orig,m); voltarHome(); popup.close(); }
                catch(Exception ex){new Alert(Alert.AlertType.ERROR,ex.getMessage()).showAndWait();}
            });
            form.getChildren().add(s);
        }

        ScrollPane sp = new ScrollPane(form);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:#12122a; -fx-background:#12122a;");
        popup.setScene(new Scene(sp, 440, 420));
        aplicarCssPopup(popup);
        popup.show();
    }

    private void aplicarBase(Musica m, TextField tN, TextField tA,
                              TextField tAn, TextField tG, TextField tC) {
        m.setNome(tN.getText().trim()); m.setArtista(tA.getText().trim());
        m.setGenero(tG.getText().trim()); m.setUrlCapa(tC.getText().trim());
        if (!tAn.getText().trim().isEmpty())
            m.setAnoLancamento(Integer.parseInt(tAn.getText().trim()));
    }

    private void popupAvaliar(Musica m) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Avaliar: " + m.getNome());
        VBox box = popupBase();

        ComboBox<Integer> cb = new ComboBox<>(FXCollections.observableArrayList(1,2,3,4,5));
        cb.setValue(m.getNota()>0 ? m.getNota() : 5);
        cb.setStyle("-fx-background-color:#1a1a3e; -fx-text-fill:#e8e6f0;");

        TextArea ta = new TextArea(safe(m.getComentario(),""));
        ta.setPromptText("Escreva sua crítica..."); ta.setPrefRowCount(4);
        ta.setStyle("-fx-control-inner-background:#1a1a3e; -fx-text-fill:#e8e6f0;");

        Button s = new Button("💾  Salvar Avaliação");
        estilizarBtn(s, GOLD, "#0d0d1a"); s.setDefaultButton(true);
        s.setOnAction(e -> {
            try { m.avaliar(cb.getValue(), ta.getText()); ctrl.editar(m.getNome(),m); popup.close(); }
            catch(Exception ex){new Alert(Alert.AlertType.ERROR,ex.getMessage()).showAndWait();}
        });

        box.getChildren().addAll(labelPopup("Nota (1-5):"),cb,labelPopup("Comentário:"),ta,s);
        popup.setScene(new Scene(box,380,320));
        aplicarCssPopup(popup);
        popup.showAndWait();
    }

    // ── ARTISTAS — popups ────────────────────────────────────────────────
    private void popupAdicionarArtista() {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Adicionar Artista");
        VBox form = popupBase();

        TextField tN = inputField("Obrigatório");
        TextField tG = inputField("Gênero");
        TextField tP = inputField("País");
        TextField tF = inputField("URL da foto (opcional)");
        TextArea  tD = new TextArea();
        tD.setPromptText("Escreva sobre o artista...");
        tD.setPrefRowCount(3);
        tD.setStyle("-fx-control-inner-background:#1a1a3e; -fx-text-fill:#e8e6f0;");

        Button s = new Button("💾  Salvar"); estilizarBtn(s, GREEN, "#bbf7d0"); s.setDefaultButton(true);
        s.setOnAction(e -> {
            try {
                if (tN.getText().trim().isEmpty()) throw new DadosInvalidosException("Nome obrigatório.");
                Artista a = new Artista(tN.getText().trim());
                a.setGenero(tG.getText().trim()); a.setPais(tP.getText().trim());
                a.setUrlFoto(tF.getText().trim()); a.setDescricao(tD.getText().trim());
                ctrlArtista.adicionar(a); popup.close();
            } catch (Exception ex) { new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait(); }
        });

        form.getChildren().addAll(
            labelPopup("Nome *:"), tN, labelPopup("Gênero:"), tG,
            labelPopup("País:"), tP, labelPopup("URL Foto:"), tF,
            labelPopup("Descrição:"), tD, s
        );
        ScrollPane sp = new ScrollPane(form);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:#12122a; -fx-background:#12122a;");
        popup.setScene(new Scene(sp, 420, 440));
        aplicarCssPopup(popup);
        popup.showAndWait();
    }

    private void popupEditarArtista(Artista a) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Editando: " + a.getNome());
        VBox form = popupBase();

        TextField tN = inputFieldVal(a.getNome());
        TextField tG = inputFieldVal(safe(a.getGenero(),""));
        TextField tP = inputFieldVal(safe(a.getPais(),""));
        TextField tF = inputFieldVal(safe(a.getUrlFoto(),""));
        TextArea  tD = new TextArea(safe(a.getDescricao(),""));
        tD.setPrefRowCount(3);
        tD.setStyle("-fx-control-inner-background:#1a1a3e; -fx-text-fill:#e8e6f0;");

        Button s = new Button("💾  Salvar"); estilizarBtn(s, GREEN, "#bbf7d0"); s.setDefaultButton(true);
        s.setOnAction(e -> {
            try {
                String orig = a.getNome();
                a.setNome(tN.getText().trim()); a.setGenero(tG.getText().trim());
                a.setPais(tP.getText().trim()); a.setUrlFoto(tF.getText().trim());
                a.setDescricao(tD.getText().trim());
                ctrlArtista.editar(orig, a); popup.close();
            } catch (Exception ex) { new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait(); }
        });

        form.getChildren().addAll(
            labelPopup("Nome *:"), tN, labelPopup("Gênero:"), tG,
            labelPopup("País:"), tP, labelPopup("URL Foto:"), tF,
            labelPopup("Descrição:"), tD, s
        );
        ScrollPane sp = new ScrollPane(form);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:#12122a; -fx-background:#12122a;");
        popup.setScene(new Scene(sp, 420, 440));
        aplicarCssPopup(popup);
        popup.showAndWait();
    }

    // ── BUSCA ────────────────────────────────────────────────────────────
    private void acaoBuscar() {
        String termo = txtBusca.getText().trim();
        if (termo.isEmpty()) { voltarHome(); return; }
        try {
            containerCentral.getChildren().clear();
            renderizar(ctrl.buscar(termo), false);
        } catch (ArquivoNaoEncontradoException ex) {
            containerCentral.getChildren().clear();
            Label msg = new Label("❌  " + ex.getMessage());
            msg.setStyle("-fx-text-fill:#e74c3c; -fx-font-size:14px; -fx-padding:20;");
            containerCentral.getChildren().add(msg);
        }
    }

    // ── UTILITÁRIOS ──────────────────────────────────────────────────────
    private void voltarHome() {
        scrollCentral.setContent(containerCentral);
        renderizarHome();
    }

    /** Troca o conteúdo exibido na área central, mantendo o ScrollPane principal.
     *  Se receber um ScrollPane (padrão usado em vários métodos), desembrulha
     *  o conteúdo dele para evitar scroll duplicado. */
    private void trocarCentro(javafx.scene.Node novoConteudo) {
        if (novoConteudo instanceof ScrollPane) {
            scrollCentral.setContent(((ScrollPane) novoConteudo).getContent());
        } else {
            scrollCentral.setContent(novoConteudo);
        }
        scrollCentral.setFitToWidth(true);
    }

    private VBox popupBase() {
        VBox v = new VBox(8); v.setPadding(new Insets(20));
        v.setStyle("-fx-background-color:#12122a;"); return v;
    }

    private TextField inputField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefWidth(380); tf.setMinWidth(300);
        estilizarInput(tf); return tf;
    }

    private TextField inputFieldVal(String valor) {
        TextField tf = new TextField(valor != null ? valor : "");
        tf.setPrefWidth(380); tf.setMinWidth(300);
        estilizarInput(tf); return tf;
    }

    private void estilizarInput(TextField tf) {
        tf.setStyle("-fx-background-color:#1a1a3e; -fx-text-fill:#e8e6f0;"
                  + "-fx-prompt-text-fill:#6b6890; -fx-border-color:#2d2b5e;"
                  + "-fx-border-radius:6; -fx-background-radius:6;"
                  + "-fx-padding:6 10 6 10; -fx-font-family:'Segoe UI';");
    }

    private void estilizarBtn(Button b, String bg, String fg) {
        b.setStyle("-fx-background-color:" + bg + "; -fx-text-fill:" + fg
                 + "; -fx-font-family:'Segoe UI'; -fx-font-weight:bold;"
                 + "-fx-padding:8 16 8 16; -fx-background-radius:8; -fx-cursor:hand;");
        b.setOnMouseEntered(e -> b.setOpacity(0.85));
        b.setOnMouseExited(e  -> b.setOpacity(1.0));
    }

    private Label labelPopup(String t) {
        Label l = new Label(t);
        l.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:12px; -fx-text-fill:#9d9ab5;");
        return l;
    }

    private ScrollPane scrollTransparente(VBox c) {
        ScrollPane sp = new ScrollPane(c); sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:transparent; -fx-background:transparent;"); return sp;
    }

    private ScrollPane scrollH(HBox c) {
        ScrollPane sp = new ScrollPane(c);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        sp.setFitToHeight(true);
        sp.setStyle("-fx-background-color:transparent; -fx-background:transparent;"); return sp;
    }

    private void carregarImg(ImageView iv, String url, double w, double h, String ph) {
        iv.setFitWidth(w); iv.setFitHeight(h); iv.setPreserveRatio(false);
        try { iv.setImage(new Image((url!=null&&!url.isEmpty())?url:ph, w, h, false, true, true)); }
        catch (Exception ex) { iv.setImage(new Image(ph, w, h, false, true, true)); }
    }

    private String safe(String v, String fb) {
        return (v==null||v.trim().isEmpty()) ? fb : v.trim();
    }

    /** Aplica o estilos.css a uma Scene, com diagnóstico no console.
     *  Procura em /resources/estilos.css na raiz do classpath compilado
     *  (deve corresponder a src/resources/estilos.css). */
    private void aplicarCss(Scene cena) {
        java.net.URL recurso = getClass().getResource("/resources/estilos.css");
        if (recurso == null) {
            System.out.println("[CSS] estilos.css NÃO encontrado em /resources/estilos.css — "
                + "verifique se o arquivo está em src/resources/estilos.css");
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