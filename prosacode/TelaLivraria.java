package prosacode;

import java.net.URL;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class TelaLivraria extends Application {

    private Stage janelaPrincipal;
    private Scene cenaInicial;

    // =========================================================================
    // MODELOS DE DADOS
    // =========================================================================

    // MODELO: VENDEDOR
    public static class Vendedor {
        private String id; // ID único / Senha
        private String nome;

        public Vendedor(String id, String nome) {
            this.id = id;
            this.nome = nome;
        }

        public String getId() { return id; }
        public String getNome() { return nome; }
    }

    // MODELO: COMPRADOR
    public static class Comprador {
        private String nome;
        private String email;
        private String senha;

        public Comprador(String nome, String email, String senha) {
            this.nome = nome;
            this.email = email;
            this.senha = senha;
        }

        public String getNome() { return nome; }
        public String getEmail() { return email; }
        public String getSenha() { return senha; }
    }

    // MODELO: LIVRO
    public static class Livro {
        private String titulo;
        private String autor;
        private String categoria;
        private double preco;
        private int estoque;
        private String vendedorId;
        private String vendedorNome;

        public Livro(String titulo, String autor, String categoria, double preco, int estoque, String vendedorId, String vendedorNome) {
            this.titulo = titulo;
            this.autor = autor;
            this.categoria = categoria;
            this.preco = preco;
            this.estoque = estoque;
            this.vendedorId = vendedorId;
            this.vendedorNome = vendedorNome;
        }

        public String getTitulo() { return titulo; }
        public String getAutor() { return autor; }
        public String getCategoria() { return categoria; }
        public double getPreco() { return preco; }
        public int getEstoque() { return estoque; }
        public void setEstoque(int estoque) { this.estoque = estoque; }
        public String getVendedorId() { return vendedorId; }
        public String getVendedorNome() { return vendedorNome; }
    }

    // =========================================================================
    // LISTAS GLOBAIS EM MEMÓRIA
    // =========================================================================
    private ObservableList<Vendedor> listaVendedores = FXCollections.observableArrayList(
        new Vendedor("123", "Livraria Central"),
        new Vendedor("456", "Sebo do Pixel")
    );

    private ObservableList<Comprador> listaCompradores = FXCollections.observableArrayList(
        new Comprador("Maria Silva", "maria@email.com", "1234")
    );

    private ObservableList<Livro> listaLivros = FXCollections.observableArrayList(
        new Livro("O Hobbit", "J.R.R. Tolkien", "Fantasia", 49.90, 5, "123", "Livraria Central"),
        new Livro("1984", "George Orwell", "Ficção", 35.00, 2, "456", "Sebo do Pixel"),
        new Livro("O Alquimista", "Paulo Coelho", "Romance", 29.90, 8, "123", "Livraria Central"),
        new Livro("Clean Code", "Robert C. Martin", "Técnico", 89.90, 4, "456", "Sebo do Pixel")
    );

    // USUÁRIOS LOGADOS
    private Vendedor vendedorLogado = null;
    private Comprador compradorLogado = null;

    @Override
    public void start(Stage primaryStage) {
        janelaPrincipal = primaryStage;
        cenaInicial = criarTelaInicial();
        janelaPrincipal.setTitle("Sistema de Livraria Pixel");
        janelaPrincipal.setScene(cenaInicial);
        janelaPrincipal.show();
    }

    // -------------------------------------------------------------------------
    // 1. TELA INICIAL (MENU DE SELEÇÃO DE PERFIL)
    // -------------------------------------------------------------------------
    private Scene criarTelaInicial() {
        Label badgeTag = new Label("+ Acervo Digital Seleção");
        badgeTag.getStyleClass().add("badge-top");

        Label lblTitulo = new Label("SISTEMA DE LIVRARIA");
        lblTitulo.getStyleClass().add("label-titulo-pixel");

        HBox placaTexto = new HBox(lblTitulo);
        placaTexto.getStyleClass().add("placa-titulo");

        VBox molduraMadeira = new VBox(placaTexto);
        molduraMadeira.getStyleClass().add("moldura-madeira");

        Label lblInstrucao = new Label("Selecione seu perfil de acesso ao acervo:");
        lblInstrucao.setStyle("-fx-text-fill: #281920; -fx-font-weight: bold; -fx-font-size: 11px;");

        Button btnComprador = criarBotaoPerfil("", "Entrar como Comprador", "Login & Compras no marketplace", "btn-pixel-primary");
        btnComprador.setOnAction(e -> janelaPrincipal.setScene(criarTelaLoginComprador()));

        Button btnVendedor = criarBotaoPerfil("", "Entrar como Vendedor", "Login & Gestão de estoque da loja", "btn-pixel-secondary");
        btnVendedor.setOnAction(e -> janelaPrincipal.setScene(criarTelaLoginVendedor()));

        Label lblStatus = new Label(" Marketplace Pixel v3.0");
        lblStatus.setStyle("-fx-font-weight: bold; -fx-text-fill: #281920; -fx-font-size: 11px;");

        Label badgeOnline = new Label("ONLINE");
        badgeOnline.getStyleClass().add("badge-green");

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);

        HBox boxStatus = new HBox(lblStatus, spacer1, badgeOnline);
        boxStatus.setAlignment(Pos.CENTER);
        boxStatus.getStyleClass().add("box-info");

        VBox cardContainer = new VBox(12, molduraMadeira, lblInstrucao, btnComprador, btnVendedor, boxStatus);
        cardContainer.setAlignment(Pos.CENTER);
        cardContainer.getStyleClass().add("card-principal");
        cardContainer.setMaxWidth(380);

        StackPane root = new StackPane();
        root.setPadding(new Insets(35, 20, 20, 20));
        root.getChildren().addAll(cardContainer, badgeTag);
        StackPane.setAlignment(badgeTag, Pos.TOP_CENTER);
        StackPane.setMargin(badgeTag, new Insets(-13, 0, 0, 0));

        Scene scene = new Scene(root, 520, 520);
        carregarCss(scene);
        return scene;
    }

    // -------------------------------------------------------------------------
    // 2. LOGIN E CADASTRO DO COMPRADOR
    // -------------------------------------------------------------------------
    private Scene criarTelaLoginComprador() {
        Label lblTitulo = new Label(" ÁREA DO COMPRADOR");
        lblTitulo.getStyleClass().add("label-titulo-pixel");

        HBox placaTitulo = new HBox(lblTitulo);
        placaTitulo.getStyleClass().add("placa-titulo");

        TabPane tabPane = new TabPane();

        // --- ABA 1: LOGIN
        TextField txtEmailLogin = new TextField();
        txtEmailLogin.setPromptText("Seu E-mail");

        PasswordField txtSenhaLogin = new PasswordField();
        txtSenhaLogin.setPromptText("Sua Senha");

        Button btnEntrar = new Button("Entrar e Comprar →");
        btnEntrar.getStyleClass().add("btn-pixel-primary");

        VBox boxLogin = new VBox(10,
            new Label("E-mail:"), txtEmailLogin,
            new Label("Senha:"), txtSenhaLogin,
            btnEntrar
        );
        boxLogin.setPadding(new Insets(15));
        Tab tabLogin = new Tab("Entrar", boxLogin);
        tabLogin.setClosable(false);

        // --- ABA 2: CADASTRO ---
        TextField txtNomeCad = new TextField();
        txtNomeCad.setPromptText("Seu Nome Completo");

        TextField txtEmailCad = new TextField();
        txtEmailCad.setPromptText("Seu E-mail");

        PasswordField txtSenhaCad = new PasswordField();
        txtSenhaCad.setPromptText("Crie uma Senha");

        Button btnCadastrar = new Button("Criar Conta");
        btnCadastrar.getStyleClass().add("btn-pixel-success");

        VBox boxCadastro = new VBox(10,
            new Label("Nome Completo:"), txtNomeCad,
            new Label("E-mail:"), txtEmailCad,
            new Label("Senha:"), txtSenhaCad,
            btnCadastrar
        );
        boxCadastro.setPadding(new Insets(15));
        Tab tabCadastro = new Tab("Criar Conta", boxCadastro);
        tabCadastro.setClosable(false);

        tabPane.getTabs().addAll(tabLogin, tabCadastro);

        // Ações
        btnEntrar.setOnAction(e -> {
            String email = txtEmailLogin.getText().trim();
            String senha = txtSenhaLogin.getText().trim();
            Comprador c = buscarComprador(email, senha);
            if (c != null) {
                compradorLogado = c;
                janelaPrincipal.setScene(criarTelaComprador());
            } else {
                exibirAlerta("Erro de Acesso", "E-mail ou senha incorretos.");
            }
        });

        btnCadastrar.setOnAction(e -> {
            String nome = txtNomeCad.getText().trim();
            String email = txtEmailCad.getText().trim();
            String senha = txtSenhaCad.getText().trim();

            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                exibirAlerta("Erro", "Preencha todos os campos do cadastro.");
                return;
            }

            if (existeCompradorEmail(email)) {
                exibirAlerta("Erro", "Este e-mail já está cadastrado.");
                return;
            }

            Comprador novoC = new Comprador(nome, email, senha);
            listaCompradores.add(novoC);
            compradorLogado = novoC;
            exibirAlerta("Sucesso", "Conta criada com sucesso! Bem-vindo(a), " + nome);
            janelaPrincipal.setScene(criarTelaComprador());
        });

        Button btnVoltar = new Button(" Voltar ao Menu");
        btnVoltar.getStyleClass().add("btn-voltar");
        btnVoltar.setOnAction(e -> janelaPrincipal.setScene(cenaInicial));

        VBox layout = new VBox(15, placaTitulo, tabPane, btnVoltar);
        layout.setPadding(new Insets(20));
        layout.getStyleClass().add("card-principal");

        StackPane root = new StackPane(layout);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 480, 520);
        carregarCss(scene);
        return scene;
    }

    // -------------------------------------------------------------------------
    // 3. TELA DO COMPRADOR (CATÁLOGO COM BUSCA MULTICRITÉRIO E REDUÇÃO DE ESTOQUE)
    // -------------------------------------------------------------------------
    private Scene criarTelaComprador() {
        Label lblTitulo = new Label(" MARKETPLACE - BEM-VINDO(A), " +
            compradorLogado.getNome().toUpperCase());
        lblTitulo.getStyleClass().add("label-titulo-pixel");

        HBox placaTitulo = new HBox(lblTitulo);
        placaTitulo.getStyleClass().add("placa-titulo");

        // BARRA DE PESQUISA E FILTROS
        ComboBox<String> cbCategoria = new ComboBox<>();
        cbCategoria.getItems().addAll("Todas Categorias", "Fantasia", "Ficção", "Romance", "Técnico");
        cbCategoria.setValue("Todas Categorias");

        TextField txtBusca = new TextField();
        txtBusca.setPromptText("Buscar por Título, Autor ou Nome da Loja...");
        HBox.setHgrow(txtBusca, Priority.ALWAYS);

        Button btnPesquisar = new Button("Buscar ");
        btnPesquisar.getStyleClass().add("btn-pixel-secondary");

        HBox barraBusca = new HBox(10, cbCategoria, txtBusca, btnPesquisar);

        // TABELA DE LIVROS
        TableView<Livro> tabela = new TableView<>();

        TableColumn<Livro, String> colTitulo = new TableColumn<>("Título");
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colTitulo.setPrefWidth(140);

        TableColumn<Livro, String> colAutor = new TableColumn<>("Autor");
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colAutor.setPrefWidth(120);

        TableColumn<Livro, String> colCategoria = new TableColumn<>("Categoria");
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colCategoria.setPrefWidth(100);

        TableColumn<Livro, String> colLoja = new TableColumn<>("Loja/Vendedor");
        colLoja.setCellValueFactory(new PropertyValueFactory<>("vendedorNome"));
        colLoja.setPrefWidth(130);

        TableColumn<Livro, Double> colPreco = new TableColumn<>("Preço");
        colPreco.setCellValueFactory(new PropertyValueFactory<>("preco"));
        colPreco.setPrefWidth(70);

        TableColumn<Livro, Integer> colEstoque = new TableColumn<>("Qtd.");
        colEstoque.setCellValueFactory(new PropertyValueFactory<>("estoque"));
        colEstoque.setPrefWidth(50);

        tabela.getColumns().addAll(colTitulo, colAutor, colCategoria, colLoja, colPreco, colEstoque);

        // FILTRAGEM DINÂMICA
        FilteredList<Livro> listaFiltrada = new FilteredList<>(listaLivros, p -> true);
        tabela.setItems(listaFiltrada);

        Runnable aplicarFiltro = () -> {
            String catSel = cbCategoria.getValue();
            String termo = txtBusca.getText().trim().toLowerCase();

            listaFiltrada.setPredicate(livro -> {
                boolean bateCategoria = catSel.equals("Todas Categorias") ||
                    livro.getCategoria().equalsIgnoreCase(catSel);
                boolean bateTexto = termo.isEmpty() ||
                    livro.getTitulo().toLowerCase().contains(termo) ||
                    livro.getAutor().toLowerCase().contains(termo) ||
                    livro.getVendedorNome().toLowerCase().contains(termo);
                return bateCategoria && bateTexto;
            });
        };

        btnPesquisar.setOnAction(e -> aplicarFiltro.run());
        cbCategoria.setOnAction(e -> aplicarFiltro.run());

        // BOTÃO DE COMPRAR (DIMINUI O ESTOQUE)
        Button btnComprar = new Button("Comprar Selecionado");
        btnComprar.getStyleClass().add("btn-pixel-success");
        btnComprar.setOnAction(e -> {
            Livro livroSel = tabela.getSelectionModel().getSelectedItem();
            if (livroSel == null) {
                exibirAlerta("Aviso", "Selecione um livro na tabela para comprar.");
                return;
            }

            if (livroSel.getEstoque() > 0) {
                // Diminui 1 unidade no estoque do livro
                livroSel.setEstoque(livroSel.getEstoque() - 1);
                tabela.refresh(); // Atualiza a exibição na tabela
                exibirAlerta("Compra Realizada!",
                    "Você comprou: " + livroSel.getTitulo() +
                    "\nLoja: " + livroSel.getVendedorNome() +
                    "\nValor: R$ " + String.format("%.2f", livroSel.getPreco()) +
                    "\nEstoque restante: " + livroSel.getEstoque() + " unid."
                );
            } else {
                exibirAlerta("Produto Esgotado!", "Infelizmente o estoque deste livro acabou.");
            }
        });

        Button btnSair = new Button(" Sair");
        btnSair.getStyleClass().add("btn-voltar");
        btnSair.setOnAction(e -> {
            compradorLogado = null;
            janelaPrincipal.setScene(cenaInicial);
        });

        HBox barraBotoes = new HBox(15, btnSair, btnComprar);
        barraBotoes.setAlignment(Pos.CENTER_RIGHT);

        VBox layout = new VBox(15, placaTitulo, barraBusca, tabela, barraBotoes);
        layout.setPadding(new Insets(20));
        layout.getStyleClass().add("card-principal");

        StackPane root = new StackPane(layout);
        root.setPadding(new Insets(15));

        Scene scene = new Scene(root, 720, 560);
        carregarCss(scene);
        return scene;
    }

    // -------------------------------------------------------------------------
    // 4. LOGIN E CADASTRO DO VENDEDOR
    // -------------------------------------------------------------------------
    private Scene criarTelaLoginVendedor() {
        Label lblTitulo = new Label(" ÁREA DO VENDEDOR");
        lblTitulo.getStyleClass().add("label-titulo-pixel");

        HBox placaTitulo = new HBox(lblTitulo);
        placaTitulo.getStyleClass().add("placa-titulo");

        TabPane tabPane = new TabPane();

        // --- ABA 1: LOGIN
        TextField txtLoginId = new TextField();
        txtLoginId.setPromptText("ID / Senha do Vendedor");

        Button btnEntrar = new Button("Entrar no Painel →");
        btnEntrar.getStyleClass().add("btn-pixel-primary");

        VBox boxLogin = new VBox(12, new Label("ID / Senha de Acesso:"), txtLoginId, btnEntrar);
        boxLogin.setPadding(new Insets(15));
        Tab tabLogin = new Tab("Entrar", boxLogin);
        tabLogin.setClosable(false);

        // --- ABA 2: CADASTRO
        TextField txtNovoNome = new TextField();
        txtNovoNome.setPromptText("Nome da Loja/Vendedor");

        TextField txtNovoId = new TextField();
        txtNovoId.setPromptText("Crie seu ID / Senha");

        Button btnCadastrar = new Button("Cadastrar Loja ++");
        btnCadastrar.getStyleClass().add("btn-pixel-success");

        VBox boxCadastro = new VBox(10,
            new Label("Nome da Loja:"), txtNovoNome,
            new Label("ID/Senha de Acesso:"), txtNovoId,
            btnCadastrar
        );
        boxCadastro.setPadding(new Insets(15));
        Tab tabCadastro = new Tab("Nova Loja", boxCadastro);
        tabCadastro.setClosable(false);

        tabPane.getTabs().addAll(tabLogin, tabCadastro);

        btnEntrar.setOnAction(e -> {
            String id = txtLoginId.getText().trim();
            Vendedor v = buscarVendedorPorId(id);
            if (v != null) {
                vendedorLogado = v;
                janelaPrincipal.setScene(criarTelaVendedor());
            } else {
                exibirAlerta("Acesso Negado", "ID/Senha não encontrado.");
            }
        });

        btnCadastrar.setOnAction(e -> {
            String nome = txtNovoNome.getText().trim();
            String id = txtNovoId.getText().trim();

            if (nome.isEmpty() || id.isEmpty()) {
                exibirAlerta("Erro", "Preencha todos os campos.");
                return;
            }

            if (buscarVendedorPorId(id) != null) {
                exibirAlerta("Erro", "Este ID/Senha já está cadastrado.");
                return;
            }

            Vendedor novoV = new Vendedor(id, nome);
            listaVendedores.add(novoV);
            vendedorLogado = novoV;
            exibirAlerta("Sucesso", "Loja cadastrada com sucesso!");
            janelaPrincipal.setScene(criarTelaVendedor());
        });

        Button btnVoltar = new Button(" Voltar ao Menu");
        btnVoltar.getStyleClass().add("btn-voltar");
        btnVoltar.setOnAction(e -> janelaPrincipal.setScene(cenaInicial));

        VBox layout = new VBox(15, placaTitulo, tabPane, btnVoltar);
        layout.setPadding(new Insets(20));
        layout.getStyleClass().add("card-principal");

        StackPane root = new StackPane(layout);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 480, 500);
        carregarCss(scene);
        return scene;
    }

    // -------------------------------------------------------------------------
    // 5. PAINEL DO VENDEDOR (GERENCIAR ESTOQUE DA LOJA)
    // -------------------------------------------------------------------------
    private Scene criarTelaVendedor() {
        Label lblTitulo = new Label(" LOJA: " + vendedorLogado.getNome().toUpperCase());
        lblTitulo.getStyleClass().add("label-titulo-pixel");

        HBox placaTitulo = new HBox(lblTitulo);
        placaTitulo.getStyleClass().add("placa-titulo");

        // FORMULÁRIO DE NOVO LIVRO
        TextField txtTitulo = new TextField(); txtTitulo.setPromptText("Título");
        TextField txtAutor = new TextField(); txtAutor.setPromptText("Autor");

        ComboBox<String> cbCategoria = new ComboBox<>();
        cbCategoria.getItems().addAll("Fantasia", "Ficção", "Romance", "Técnico");
        cbCategoria.setValue("Fantasia");

        TextField txtPreco = new TextField(); txtPreco.setPromptText("Preço (R$)");
        TextField txtEstoque = new TextField(); txtEstoque.setPromptText("Qtd. Estoque");

        Button btnAdicionar = new Button("+ Adicionar ao Estoque");
        btnAdicionar.getStyleClass().add("btn-pixel-success");

        GridPane form = new GridPane();
        form.setHgap(8); form.setVgap(8);
        form.add(new Label("Título:"), 0, 0); form.add(txtTitulo, 1, 0);
        form.add(new Label("Autor:"), 0, 1); form.add(txtAutor, 1, 1);
        form.add(new Label("Cat:"), 2, 0); form.add(cbCategoria, 3, 0);
        form.add(new Label("Preço:"), 2, 1); form.add(txtPreco, 3, 1);
        form.add(new Label("Qtd:"), 0, 2); form.add(txtEstoque, 1, 2);

        VBox boxForm = new VBox(10, form, btnAdicionar);
        boxForm.getStyleClass().add("card-conteudo");

        // TABELA EXIBINDO LIVROS DO VENDEDOR LOGADO
        TableView<Livro> tabela = new TableView<>();

        TableColumn<Livro, String> colTitulo = new TableColumn<>("Título");
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colTitulo.setPrefWidth(150);

        TableColumn<Livro, String> colAutor = new TableColumn<>("Autor");
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colAutor.setPrefWidth(130);

        TableColumn<Livro, String> colCat = new TableColumn<>("Categoria");
        colCat.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colCat.setPrefWidth(100);

        TableColumn<Livro, Double> colPreco = new TableColumn<>("Preço (R$)");
        colPreco.setCellValueFactory(new PropertyValueFactory<>("preco"));
        colPreco.setPrefWidth(90);

        TableColumn<Livro, Integer> colEstoque = new TableColumn<>("Qtd. Disponível");
        colEstoque.setCellValueFactory(new PropertyValueFactory<>("estoque"));
        colEstoque.setPrefWidth(110);

        tabela.getColumns().addAll(colTitulo, colAutor, colCat, colPreco, colEstoque);

        FilteredList<Livro> meusLivros = new FilteredList<>(listaLivros,
            l -> l.getVendedorId().equals(vendedorLogado.getId())
        );
        tabela.setItems(meusLivros);

        btnAdicionar.setOnAction(e -> {
            try {
                String t = txtTitulo.getText().trim();
                String a = txtAutor.getText().trim();
                String c = cbCategoria.getValue();
                double p = Double.parseDouble(txtPreco.getText().trim());
                int q = Integer.parseInt(txtEstoque.getText().trim());

                if (!t.isEmpty() && !a.isEmpty()) {
                    listaLivros.add(new Livro(t, a, c, p, q, vendedorLogado.getId(), vendedorLogado.getNome()));
                    txtTitulo.clear(); txtAutor.clear(); txtPreco.clear(); txtEstoque.clear();
                }
            } catch (Exception ex) {
                exibirAlerta("Erro", "Insira valores numéricos válidos para preço e estoque.");
            }
        });

        Button btnRemover = new Button("X Remover do Estoque");
        btnRemover.getStyleClass().add("btn-pixel-danger");
        btnRemover.setOnAction(e -> {
            Livro sel = tabela.getSelectionModel().getSelectedItem();
            if (sel != null) listaLivros.remove(sel);
        });

        Button btnSair = new Button(" Sair da Loja");
        btnSair.getStyleClass().add("btn-voltar");
        btnSair.setOnAction(e -> {
            vendedorLogado = null;
            janelaPrincipal.setScene(cenaInicial);
        });

        HBox barraBotoes = new HBox(15, btnSair, btnRemover);
        barraBotoes.setAlignment(Pos.CENTER_RIGHT);

        VBox layout = new VBox(12, placaTitulo, boxForm, tabela, barraBotoes);
        layout.setPadding(new Insets(20));
        layout.getStyleClass().add("card-principal");

        StackPane root = new StackPane(layout);
        root.setPadding(new Insets(15));

        Scene scene = new Scene(root, 680, 600);
        carregarCss(scene);
        return scene;
    }

    // =========================================================================
    // MÉTODOS AUXILIARES E AUTENTICAÇÃO
    // =========================================================================
    private Vendedor buscarVendedorPorId(String id) {
        for (Vendedor v : listaVendedores) {
            if (v.getId().equalsIgnoreCase(id)) return v;
        }
        return null;
    }

    private Comprador buscarComprador(String email, String senha) {
        for (Comprador c : listaCompradores) {
            if (c.getEmail().equalsIgnoreCase(email) && c.getSenha().equals(senha)) return c;
        }
        return null;
    }

    private boolean existeCompradorEmail(String email) {
        for (Comprador c : listaCompradores) {
            if (c.getEmail().equalsIgnoreCase(email)) return true;
        }
        return false;
    }

    private Button criarBotaoPerfil(String icone, String titulo, String subtitulo, String estiloClasse) {
        Button btn = new Button();
        btn.getStyleClass().add(estiloClasse);
        btn.setMaxWidth(Double.MAX_VALUE);

        Label lblIcone = new Label(icone);
        lblIcone.setStyle("-fx-font-size: 16px;");

        HBox boxIcone = new HBox(lblIcone);
        boxIcone.getStyleClass().add("box-icone");
        boxIcone.setAlignment(Pos.CENTER);

        Label lblTitulo = new Label(titulo);
        lblTitulo.getStyleClass().add("text-btn-titulo");

        Label lblSubtitulo = new Label(subtitulo);
        lblSubtitulo.getStyleClass().add("text-btn-subtitulo");

        VBox textGroup = new VBox(2, lblTitulo, lblSubtitulo);
        textGroup.setAlignment(Pos.CENTER_LEFT);

        Label lblSeta = new Label("");
        lblSeta.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 16px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox layoutBotao = new HBox(10, boxIcone, textGroup, spacer, lblSeta);
        layoutBotao.setAlignment(Pos.CENTER);

        btn.setGraphic(layoutBotao);
        return btn;
    }

    private void carregarCss(Scene scene) {
        try {
            URL cssUrl = getClass().getResource("style.css");
            if (cssUrl == null) cssUrl = getClass().getResource("/prosacode/style.css");
            if (cssUrl != null) {
                scene.getStylesheets().clear();
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }
        } catch (Exception e) {
            System.err.println("Aviso: style.css não encontrado.");
        }
    }

    private void exibirAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}