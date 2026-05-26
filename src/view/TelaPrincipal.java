package view;

import controller.ArquivoController;
import model.*;
import exception.DadosInvalidosException;

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

import java.net.URI;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class TelaPrincipal extends Application {

    private ArquivoController cerebro; // Nome oficial mantido
    private VBox containerCentral;     // Container para empilhar as seções
    private TextField txtBusca;
    private Stage palco;               
    private Scene cenaPrincipal;       
    
    private final Random random = new Random();

    // Arrays para sorteio de imagens e mídias padrão
    private final String[] imgsFilmes = {"https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=500", "https://images.unsplash.com/photo-1517604931442-7e0c8ed2963c?w=500"};
    private final String[] vidsFilmes = {"https://www.youtube.com/watch?v=aqz-KE-bpKQ", "https://www.youtube.com/watch?v=eRsGyueVLvQ"};
    private final String[] imgsAlbuns = {"https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=500", "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=500"};
    private final String[] vidsAlbuns = {"https://www.youtube.com/watch?v=9X8SGu-sOas", "https://www.youtube.com/watch?v=jfKfPfyJRdk"};
    private final String[] imgsLivros = {"https://images.unsplash.com/photo-1512820790803-83ca734da794?w=500", "https://images.unsplash.com/photo-1495640388908-05fa85288e61?w=500"};
    private final String[] docsLivros = {"https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf", "https://www.orimi.com/pdf-test.pdf"};

    @Override
    public void start(Stage palcoPrincipal) {
        this.palco = palcoPrincipal;
        this.cerebro = new ArquivoController(); // Instanciando o controlador oficial
        palcoPrincipal.setTitle("versão 1.0 - Gerenciador de Arquivos (Cofre Cultural)");

        BorderPane layoutRaiz = new BorderPane();
        layoutRaiz.setPadding(new Insets(15));

        // 1. BARRA SUPERIOR (Busca e Filtros de Categoria)
        VBox barraSuperior = criarBarraSuperiorEtiquetas();
        layoutRaiz.setTop(barraSuperior);

        // 2. CENTRO (Área de exibição em seções roláveis)
        containerCentral = new VBox(25);
        containerCentral.setPadding(new Insets(10, 0, 10, 0));
        
        ScrollPane painelRolagemGeral = new ScrollPane(containerCentral);
        painelRolagemGeral.setFitToWidth(true);
        painelRolagemGeral.setStyle("-fx-background-color: transparent; -fx-background: #f8f9fa;");
        layoutRaiz.setCenter(painelRolagemGeral);

        // 3. BARRA INFERIOR (Botão Obrigatório de Adicionar conforme Layout)
        HBox barraInferior = criarBarraInferiorBotoes();
        layoutRaiz.setBottom(barraInferior);

        // Renderização inicial com "Todos" os arquivos divididos por seções
        renderizarTodasAsSecoes(cerebro.getBiblioteca());

        cenaPrincipal = new Scene(layoutRaiz, 1100, 720);
        palcoPrincipal.setScene(cenaPrincipal);
        palcoPrincipal.show();
    }

    // Cria a barra superior unindo a caixa de pesquisa e os botões seletores de categoria
    private VBox criarBarraSuperiorEtiquetas() {
        VBox painelTopo = new VBox(10);
        painelTopo.setPadding(new Insets(0, 0, 10, 0));

        // Linha da busca por palavra-chave
        HBox linhaBusca = new HBox(10);
        linhaBusca.setAlignment(Pos.CENTER_LEFT);
        
        txtBusca = new TextField();
        txtBusca.setPromptText("Digite o título ou gênero para buscar...");
        txtBusca.setPrefWidth(250);

        Button btnBuscar = new Button("🔍 Buscar");
        btnBuscar.setOnAction(e -> acaoBuscar());
        
        linhaBusca.getChildren().addAll(txtBusca, btnBuscar);

        // Linha de seletores (Todos, Filmes, Álbuns, Livros, Favoritos)
        HBox linhaFiltros = new HBox(12);
        linhaFiltros.setPadding(new Insets(5, 0, 0, 0));

        Button btnTodos = new Button("🌐 Todos");
        Button btnFilmes = new Button("🎬 Filmes");
        Button btnAlbuns = new Button("🎵 Álbuns");
        Button btnLivros = new Button("📚 Livros");
        Button btnAvaliados = new Button("⭐ Avaliados / Reviews");
        btnAvaliados.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold;");

        // Configuração dos filtros utilizando as classes corretas do seu Model
        btnTodos.setOnAction(e -> renderizarTodasAsSecoes(cerebro.getBiblioteca()));
        btnFilmes.setOnAction(e -> renderizarSecaoUnica("🎬 Filmes", 
            cerebro.getBiblioteca().stream().filter(a -> a instanceof Filme).collect(Collectors.toList())));
        btnAlbuns.setOnAction(e -> renderizarSecaoUnica("🎵 Álbuns", 
            cerebro.getBiblioteca().stream().filter(a -> a instanceof Album).collect(Collectors.toList())));
        btnLivros.setOnAction(e -> renderizarSecaoUnica("📚 Livros", 
            cerebro.getBiblioteca().stream().filter(a -> a instanceof Livro).collect(Collectors.toList())));
        
        btnAvaliados.setOnAction(e -> {
            // Filtra os arquivos que possuem nota preenchida ou resenha escrita
            List<Arquivo> avaliados = cerebro.getBiblioteca().stream()
                .filter(a -> a.getNota() > 0 || (a.getComentario() != null && !a.getComentario().trim().isEmpty()))
                .collect(Collectors.toList());
            renderizarTodasAsSecoes(avaliados);
        });

        linhaFiltros.getChildren().addAll(btnTodos, btnFilmes, btnAlbuns, btnLivros, btnAvaliados);
        painelTopo.getChildren().addAll(linhaBusca, linhaFiltros);
        
        return painelTopo;
    }

    private HBox criarBarraInferiorBotoes() {
        HBox barraBotoes = new HBox();
        barraBotoes.setPadding(new Insets(10, 0, 0, 0));
        
        Button btnAdicionar = new Button("➕ Nova Mídia (Adicionar)");
        btnAdicionar.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        btnAdicionar.setOnAction(e -> abrirPopUpCadastro());

        barraBotoes.getChildren().add(btnAdicionar);
        return barraBotoes;
    }

    // Regra: Exibe as seções empilhadas na tela (Álbuns, Filmes e Livros)
    private void renderizarTodasAsSecoes(List<Arquivo> listaFonte) {
        containerCentral.getChildren().clear();

        List<Arquivo> albuns = listaFonte.stream().filter(a -> a instanceof Album).collect(Collectors.toList());
        List<Arquivo> filmes = listaFonte.stream().filter(a -> a instanceof Filme).collect(Collectors.toList());
        List<Arquivo> livros = listaFonte.stream().filter(a -> a instanceof Livro).collect(Collectors.toList());

        if (!albuns.isEmpty()) containerCentral.getChildren().add(criarComponenteSecaoHorizontal("🎵 Álbuns", albuns));
        if (!filmes.isEmpty()) containerCentral.getChildren().add(criarComponenteSecaoHorizontal("🎬 Filmes", filmes));
        if (!livros.isEmpty()) containerCentral.getChildren().add(criarComponenteSecaoHorizontal("📚 Livros", livros));

        if (listaFonte.isEmpty()) {
            containerCentral.getChildren().add(new Label("Nenhum arquivo encontrado para exibição."));
        }
    }

    private void renderizarSecaoUnica(String titulo, List<Arquivo> listaFiltrada) {
        containerCentral.getChildren().clear();
        containerCentral.getChildren().add(criarComponenteSecaoHorizontal(titulo, listaFiltrada));
    }

    // Cria a estrutura de carrossel com botões de setas laterais para navegação horizontal
    private VBox criarComponenteSecaoHorizontal(String tituloSecao, List<Arquivo> itens) {
        VBox secaoRaiz = new VBox(5);
        
        Label lblTituloSecao = new Label(tituloSecao);
        lblTituloSecao.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2d3748;");

        HBox estruturaDeslizante = new HBox(5);
        estruturaDeslizante.setAlignment(Pos.CENTER);

        HBox containerCards = new HBox(15);
        containerCards.setPadding(new Insets(5));

        for (Arquivo arquivo : itens) {
            containerCards.getChildren().add(criarCardMiniatura(arquivo));
        }

        ScrollPane scrollHorizontal = new ScrollPane(containerCards);
        scrollHorizontal.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollHorizontal.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollHorizontal.setFitToHeight(true);
        scrollHorizontal.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        HBox.setHgrow(scrollHorizontal, Priority.ALWAYS);

        Button btnEsquerda = new Button("◀");
        btnEsquerda.setOnAction(e -> scrollHorizontal.setHvalue(Math.max(0, scrollHorizontal.getHvalue() - 0.25)));
        
        Button btnDireita = new Button("▶");
        btnDireita.setOnAction(e -> scrollHorizontal.setHvalue(Math.min(1, scrollHorizontal.getHvalue() + 0.25)));

        estruturaDeslizante.getChildren().addAll(btnEsquerda, scrollHorizontal, btnDireita);
        secaoRaiz.getChildren().addAll(lblTituloSecao, estruturaDeslizante);
        
        return secaoRaiz;
    }

    private VBox criarCardMiniatura(Arquivo arquivo) {
    VBox card = new VBox(10);
    card.setAlignment(Pos.CENTER);
    card.setPadding(new Insets(10));
    
    card.setPrefSize(170, 230); 
    card.setMinSize(170, 230);
    card.setStyle("-fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-background-color: white; -fx-background-radius: 8; -fx-cursor: hand;");

    ImageView capa = new ImageView();
    try {
        Image img = new Image(arquivo.getImagem(), 140, 150, false, true, true);
        capa.setImage(img);
    } catch (Exception e) {
        capa.setImage(new Image("https://i.imgur.com/bCEq7U9.png", 140, 150, false, true));
    }
    // 🚨 ISSO AQUI FAZ O CLIQUE "ATRAVESSAR" A IMAGEM E IR PRO CARD
    capa.setMouseTransparent(true); 

    Label lblTitulo = new Label(arquivo.getNome());
    lblTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-alignment: center; -fx-text-fill: #2d3748;");
    lblTitulo.setWrapText(true);
    lblTitulo.setMaxWidth(150);
    // 🚨 ISSO AQUI FAZ O CLIQUE "ATRAVESSAR" O TEXTO E IR PRO CARD
    lblTitulo.setMouseTransparent(true); 

    card.getChildren().addAll(capa, lblTitulo);
    
    // Configura a ação de clique diretamente no container
    card.setOnMouseClicked(event -> {
        System.out.println("Card clicado: " + arquivo.getNome()); // Log de teste no console do VS Code
        trocarParaTelaDetalhes(arquivo);
    });
    
    return card;
}

    // Tela interna de exibição expandida com botão obrigatório de Voltar
    private void trocarParaTelaDetalhes(Arquivo arquivo) {
    BorderPane layoutDetalhes = new BorderPane();
    layoutDetalhes.setPadding(new Insets(25));
    layoutDetalhes.setStyle("-fx-background-color: #f8f9fa;");

    // BOTÃO VOLTAR (No Topo)
    Button btnVoltar = new Button("⬅ Voltar para a Biblioteca");
    btnVoltar.setStyle("-fx-font-weight: bold; -fx-padding: 8 15 8 15; -fx-cursor: hand;");
    btnVoltar.setOnAction(e -> {
        renderizarTodasAsSecoes(cerebro.getBiblioteca()); // Recarrega os dados do carrossel
        palco.setScene(cenaPrincipal); // Altera o Palco de volta para a tela inicial
    });
    layoutDetalhes.setTop(btnVoltar);

    // CONTEÚDO CENTRAL (Informações Detalhadas)
    VBox conteudo = new VBox(20);
    conteudo.setPadding(new Insets(20, 0, 0, 0));

    Label lblNome = new Label(arquivo.getNome());
    lblNome.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1a202c;");

    HBox dadosLayout = new HBox(30);
    dadosLayout.setAlignment(Pos.TOP_LEFT);

    ImageView grandeCapa = new ImageView();
    try {
        grandeCapa.setImage(new Image(arquivo.getImagem(), 220, 260, false, true, true));
    } catch(Exception e) {
        grandeCapa.setImage(new Image("https://i.imgur.com/bCEq7U9.png", 220, 260, false, true));
    }

    // Bloco de Metadados do Arquivo
    VBox dadosTexto = new VBox(12);
    dadosTexto.setStyle("-fx-background-color: #edf2f7; -fx-padding: 20; -fx-background-radius: 8;");
    dadosTexto.setPrefWidth(500);
    dadosTexto.getChildren().addAll(
        new Label("📅 Ano de Lançamento: " + (arquivo.getAnoLancamento() == 0 ? "Não Informado" : arquivo.getAnoLancamento())),
        new Label("🏷️ Gênero: " + arquivo.getGenero()),
        new Label("📌 Categoria de Mídia: " + arquivo.getTipo()),
        new Label("📝 Informações Extra: " + arquivo.exibirInformacoes())
    );
    
    // Aplicando estilo de fonte nas labels internas de texto
    dadosTexto.getChildren().forEach(no -> no.setStyle("-fx-font-size: 14px; -fx-text-fill: #4a5568;"));
    dadosLayout.getChildren().addAll(grandeCapa, dadosTexto);

    // Bloco da Review / Nota
    VBox boxReview = new VBox(10);
    boxReview.setStyle("-fx-border-color: #cbd5e1; -fx-padding: 15; -fx-border-radius: 8; -fx-background-color: #fffaf0;");
    Label lblNota = new Label("⭐ Sua Classificação: " + (arquivo.getNota() == 0 ? "Não avaliado" : arquivo.getNota() + " / 5 Estrelas"));
    lblNota.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
    Label lblResenha = new Label("📝 Crítica Escrita: " + (arquivo.getComentario() == null || arquivo.getComentario().isEmpty() ? "Nenhum comentário adicionado." : arquivo.getComentario()));
    lblResenha.setWrapText(true);
    lblResenha.setStyle("-fx-font-style: italic; -fx-font-size: 13px;");
    boxReview.getChildren().addAll(lblNota, lblResenha);

    // Link para abrir o arquivo
    Hyperlink linkAcesso = new Hyperlink("🚀 Clique aqui para executar ou abrir a mídia digital");
    linkAcesso.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #3182ce;");
    linkAcesso.setOnAction(e -> {
        try {
            java.awt.Desktop.getDesktop().browse(new java.net.URI(arquivo.getLink()));
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Link ou arquivo indisponível no momento.").showAndWait();
        }
    });

    // 🚨 BARRA DE AÇÕES DO ARQUIVO (Aqui está o seu Botão de Editar e Excluir)
    HBox operacoesItem = new HBox(15);
    operacoesItem.setPadding(new Insets(10, 0, 0, 0));
    
    Button btnEditar = new Button("✏️ EDITAR CADASTRO");
    btnEditar.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20 10 20; -fx-cursor: hand;");
    btnEditar.setOnAction(e -> abrirPopUpEdicao(arquivo)); // Abre o pop-up que já temos pronto

    Button btnAvaliar = new Button("⭐ Adicionar Avaliação");
    btnAvaliar.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-padding: 10 20 10 20; -fx-cursor: hand;");
    btnAvaliar.setOnAction(e -> abrirPopUpAvaliar(arquivo));

    Button btnDeletar = new Button("❌ Remover Arquivo");
    btnDeletar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 10 20 10 20; -fx-cursor: hand;");
    btnDeletar.setOnAction(e -> {
        try {
            cerebro.remover_busca(arquivo.getNome());
            renderizarTodasAsSecoes(cerebro.getBiblioteca());
            palco.setScene(cenaPrincipal); // Força retorno à lista geral
        } catch (Exception ex) {
            System.out.println("Erro ao deletar.");
        }
    });
    
    operacoesItem.getChildren().addAll(btnEditar, btnAvaliar, btnDeletar);

    // Monta o layout
    conteudo.getChildren().addAll(lblNome, dadosLayout, boxReview, linkAcesso, operacoesItem);
    layoutDetalhes.setCenter(conteudo);

    // Aplica a cena de detalhes diretamente no palco principal do sistema
    Scene cenaDetalhes = new Scene(layoutDetalhes, 1100, 720);
    palco.setScene(cenaDetalhes);
}

    private void abrirPopUpEdicao(Arquivo arquivo) {
        Stage popUpEdit = new Stage();
        popUpEdit.initModality(Modality.APPLICATION_MODAL);
        popUpEdit.setTitle("Editar: " + arquivo.getNome());

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10); grid.setVgap(10);

        TextField txtT = new TextField(arquivo.getNome());
        TextField txtA = new TextField(String.valueOf(arquivo.getAnoLancamento()));
        TextField txtG = new TextField(arquivo.getGenero());
        TextField txtI = new TextField(arquivo.getImagem());
        TextField txtL = new TextField(arquivo.getLink());
        TextField txtE = new TextField();

        if (arquivo instanceof Album) txtE.setText(((Album) arquivo).getBanda());
        else if (arquivo instanceof Filme) txtE.setText(((Filme) arquivo).getDiretor());
        else if (arquivo instanceof Livro) txtE.setText(((Livro) arquivo).getAutor());

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
                String nomeOriginal = arquivo.getNome();
                arquivo.setNome(txtT.getText());
                arquivo.setAnoLancamento(Integer.parseInt(txtA.getText().trim()));
                arquivo.setGenero(txtG.getText());
                arquivo.setImagem(txtI.getText());
                arquivo.setLink(txtL.getText());

                if (arquivo instanceof Album) ((Album) arquivo).setBanda(txtE.getText());
                else if (arquivo instanceof Filme) ((Filme) arquivo).setDiretor(txtE.getText());
                else if (arquivo instanceof Livro) ((Livro) arquivo).setAutor(txtE.getText());

                cerebro.editar_busca(nomeOriginal, arquivo); // Método oficial editar_busca
                trocarParaTelaDetalhes(arquivo); // Recarrega tela expandida atualizada
                popUpEdit.close();
            } catch (IOException ex) {
                new Alert(Alert.AlertType.ERROR, "Falha ao gravar alteração no HD.").showAndWait();
            }
        });

        VBox v = new VBox(15, grid, btnSalvar);
        v.setPadding(new Insets(15));
        popUpEdit.setScene(new Scene(v, 400, 350));
        popUpEdit.showAndWait();
    }

    private void abrirPopUpAvaliar(Arquivo arquivo) {
        Stage popUpAv = new Stage();
        popUpAv.initModality(Modality.APPLICATION_MODAL);
        popUpAv.setTitle("Avaliar Mídia");

        VBox box = new VBox(10);
        box.setPadding(new Insets(20));

        Label info = new Label("Dê sua nota para: " + arquivo.getNome());
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
                arquivo.avaliar(cbNota.getValue(), txtResenha.getText());
                cerebro.editar_busca(arquivo.getNome(), arquivo); // Salva a avaliação via controller oficial
                trocarParaTelaDetalhes(arquivo);
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
        TextField txtI = new TextField(); txtI.setPromptText("Opcional (Sorteio)");
        TextField txtL = new TextField(); txtL.setPromptText("Opcional (Sorteio)");
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
        String nome = txtT.getText().trim();
        if (nome.isEmpty()) {
            throw new DadosInvalidosException("O campo 'Título' é estritamente obrigatório!");
        }

        String tipo = cbTipo.getValue();
        Arquivo novo = null;

        // 1. Instancia usando o novo construtor minimalista (Garante os Defaults)
        if ("Álbum".equals(tipo)) {
            novo = new Album(nome);
            if (!txtE.getText().trim().isEmpty()) ((Album) novo).setBanda(txtE.getText().trim());
        } else if ("Filme".equals(tipo)) {
            novo = new Filme(nome);
            if (!txtE.getText().trim().isEmpty()) ((Filme) novo).setDiretor(txtE.getText().trim());
        } else if ("Livro".equals(tipo)) {
            novo = new Livro(nome);
            if (!txtE.getText().trim().isEmpty()) ((Livro) novo).setAutor(txtE.getText().trim());
        }

        // 2. Aplica as informações opcionais gerais apenas se o usuário as preencheu
        if (!txtA.getText().trim().isEmpty()) {
            novo.setAnoLancamento(Integer.parseInt(txtA.getText().trim()));
        }
        
        if (!txtG.getText().trim().isEmpty()) {
            novo.setGenero(txtG.getText().trim());
        }

        // Se o usuário digitou uma URL de imagem usa ela, senão faz o sorteio randômico
        String imgDigitada = txtI.getText().trim();
        if (!imgDigitada.isEmpty()) {
            novo.setImagem(imgDigitada);
        } else {
            if ("Álbum".equals(tipo)) novo.setImagem(imgsAlbuns[random.nextInt(imgsAlbuns.length)]);
            else if ("Filme".equals(tipo)) novo.setImagem(imgsFilmes[random.nextInt(imgsFilmes.length)]);
            else if ("Livro".equals(tipo)) novo.setImagem(imgsLivros[random.nextInt(imgsLivros.length)]);
        }

        // Se o usuário digitou um link de acesso usa ele, senão faz o sorteio randômico
        String linkDigitado = txtL.getText().trim();
        if (!linkDigitado.isEmpty()) {
            novo.setLink(linkDigitado);
        } else {
            if ("Álbum".equals(tipo)) novo.setLink(vidsAlbuns[random.nextInt(vidsAlbuns.length)]);
            else if ("Filme".equals(tipo)) novo.setLink(vidsFilmes[random.nextInt(vidsFilmes.length)]);
            else if ("Livro".equals(tipo)) novo.setLink(docsLivros[random.nextInt(docsLivros.length)]);
        }

        // 3. Salva e atualiza
        cerebro.adicionarArquivo(novo);
        renderizarTodasAsSecoes(cerebro.getBiblioteca());
        popUpCad.close();

    } catch (DadosInvalidosException ex) {
        new Alert(Alert.AlertType.WARNING, ex.getMessage()).showAndWait();
    } catch (NumberFormatException ex) {
        new Alert(Alert.AlertType.ERROR, "O campo 'Ano' deve ser um número inteiro válido!").showAndWait();
    } catch (IOException ex) {
        new Alert(Alert.AlertType.ERROR, "Erro ao gravar o arquivo no banco de dados (HD).").showAndWait();
    } catch (Exception ex) {
        System.out.println("Erro inesperado no cadastro: " + ex.getMessage());
        ex.printStackTrace();
    }
});

        VBox b = new VBox(15, grid, btnSalvar);
        b.setPadding(new Insets(15));
        popUpCad.setScene(new Scene(b, 380, 380));
        popUpCad.showAndWait();
    }

    private void acaoBuscar() {
        String termo = txtBusca.getText();
        renderizarTodasAsSecoes(cerebro.buscar_palavra_chave(termo)); // Chamando o método oficial de busca
    }

    private void acaoRemover() {
        String termo = txtBusca.getText();
        if (termo == null || termo.trim().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Digite o título na barra de busca superior para remover por texto.").showAndWait();
            return;
        }
        try {
            cerebro.remover_busca(termo); // Chamando o método oficial remover_busca
            txtBusca.clear();
            renderizarTodasAsSecoes(cerebro.getBiblioteca());
        } catch (IOException e) {
            System.out.println("Erro ao remover via texto.");
        }

}
}