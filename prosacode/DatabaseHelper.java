package prosacode;

import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DatabaseHelper {

    // Configurações de Conexão com o MySQL
    private static final String URL = "jdbc:mysql://localhost:3306/livraria_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root"; 
    private static final String PASS = "04170318l";

    public static Connection conectar() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC do MySQL não encontrado no Build Path!");
        }
        return DriverManager.getConnection(URL, USER, PASS);
    }

    private static String gerarSufixoUnico() {
        return String.valueOf(System.currentTimeMillis()).substring(5);
    }

    public static void inicializarBanco() {
        String sqlCategoria = "CREATE TABLE IF NOT EXISTS Categoria (id_categoria INT AUTO_INCREMENT PRIMARY KEY, nome_categoria VARCHAR(100) NOT NULL);";
        String sqlAutor = "CREATE TABLE IF NOT EXISTS Autor (id_autor INT AUTO_INCREMENT PRIMARY KEY, nome_autor VARCHAR(100) NOT NULL, bi VARCHAR(50));";
        String sqlLivro = "CREATE TABLE IF NOT EXISTS Livro (id_livro INT AUTO_INCREMENT PRIMARY KEY, nome_livro VARCHAR(150) NOT NULL, encadernacao VARCHAR(50), ano INT, id_categoria INT NOT NULL, FOREIGN KEY (id_categoria) REFERENCES Categoria(id_categoria));";
        String sqlLivroAutor = "CREATE TABLE IF NOT EXISTS Livro_Autor (id_livro INT NOT NULL, id_autor INT NOT NULL, PRIMARY KEY (id_livro, id_autor), FOREIGN KEY (id_livro) REFERENCES Livro(id_livro) ON DELETE CASCADE, FOREIGN KEY (id_autor) REFERENCES Autor(id_autor) ON DELETE CASCADE);";
        String sqlComprador = "CREATE TABLE IF NOT EXISTS Comprador (id_comprador INT AUTO_INCREMENT PRIMARY KEY, nome VARCHAR(100) NOT NULL, cpf_nif VARCHAR(20) UNIQUE NOT NULL, email VARCHAR(100), telefone VARCHAR(20), senha VARCHAR(100) NOT NULL);";
        String sqlVendedor = "CREATE TABLE IF NOT EXISTS Vendedor (id_vendedor INT AUTO_INCREMENT PRIMARY KEY, nome VARCHAR(100) NOT NULL, cpf_nif VARCHAR(20) UNIQUE NOT NULL, comissao DECIMAL(5,2) DEFAULT 0.00, login VARCHAR(50) UNIQUE NOT NULL, senha VARCHAR(100) NOT NULL);";
        String sqlVenda = "CREATE TABLE IF NOT EXISTS Venda (id_venda INT AUTO_INCREMENT PRIMARY KEY, data_venda DATETIME DEFAULT CURRENT_TIMESTAMP, valor_total DECIMAL(10,2) DEFAULT 0.00, id_comprador INT NOT NULL, id_vendedor INT NOT NULL, FOREIGN KEY (id_comprador) REFERENCES Comprador(id_comprador), FOREIGN KEY (id_vendedor) REFERENCES Vendedor(id_vendedor));";
        String sqlItemVenda = "CREATE TABLE IF NOT EXISTS Item_Venda (id_venda INT NOT NULL, id_livro INT NOT NULL, quantidade INT NOT NULL DEFAULT 1, preco_unitario DECIMAL(10,2) NOT NULL, PRIMARY KEY (id_venda, id_livro), FOREIGN KEY (id_venda) REFERENCES Venda(id_venda) ON DELETE CASCADE, FOREIGN KEY (id_livro) REFERENCES Livro(id_livro));";

        try (Connection conn = conectar(); Statement stmt = conn.createStatement()) {
            stmt.execute(sqlCategoria);
            stmt.execute(sqlAutor);
            stmt.execute(sqlLivro);
            stmt.execute(sqlLivroAutor);
            stmt.execute(sqlComprador);
            stmt.execute(sqlVendedor);
            stmt.execute(sqlVenda);
            stmt.execute(sqlItemVenda);
            System.out.println("-> Conexão e estrutura do MySQL validadas com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao inicializar tabelas no MySQL: " + e.getMessage());
        }
    }

    // --- VENDEDORES ---
    public static void salvarVendedor(TelaLivraria.Vendedor v) {
        String sufixo = gerarSufixoUnico();
        String cpfDefault = "000.000.000-" + sufixo.substring(0, 2);
        String loginDefault = "usr_" + sufixo;

        String sql = "INSERT INTO Vendedor (nome, cpf_nif, login, senha) VALUES (?, ?, ?, ?);";
        try (Connection conn = conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, v.getNome());
            pstmt.setString(2, cpfDefault);
            pstmt.setString(3, loginDefault);
            pstmt.setString(4, v.getId());
            pstmt.executeUpdate();
            System.out.println("Vendedor salvo com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao salvar vendedor: " + e.getMessage());
        }
    }

    public static ObservableList<TelaLivraria.Vendedor> carregarVendedores() {
        ObservableList<TelaLivraria.Vendedor> lista = FXCollections.observableArrayList();
        String sql = "SELECT id_vendedor, nome FROM Vendedor;";
        try (Connection conn = conectar(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new TelaLivraria.Vendedor(
                    String.valueOf(rs.getInt("id_vendedor")),
                    rs.getString("nome")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao carregar vendedores: " + e.getMessage());
        }
        return lista;
    }

    // --- COMPRADORES ---
    public static void salvarComprador(TelaLivraria.Comprador c) {
        String sufixo = gerarSufixoUnico();
        String cpfDefault = "111.111.111-" + sufixo.substring(0, 2);

        String sql = "INSERT INTO Comprador (nome, cpf_nif, email, senha) VALUES (?, ?, ?, ?);";
        try (Connection conn = conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, c.getNome());
            pstmt.setString(2, cpfDefault);
            pstmt.setString(3, c.getEmail());
            pstmt.setString(4, c.getSenha());
            pstmt.executeUpdate();
            System.out.println("Comprador salvo com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao salvar comprador: " + e.getMessage());
        }
    }

    public static ObservableList<TelaLivraria.Comprador> carregarCompradores() {
        ObservableList<TelaLivraria.Comprador> lista = FXCollections.observableArrayList();
        String sql = "SELECT nome, email, senha FROM Comprador;";
        try (Connection conn = conectar(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new TelaLivraria.Comprador(
                    rs.getString("nome"),
                    rs.getString("email"),
                    rs.getString("senha")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao carregar compradores: " + e.getMessage());
        }
        return lista;
    }

    // --- LIVROS ---
    public static void salvarLivro(TelaLivraria.Livro l) {
        int idCategoria = buscarOuCriarCategoria(l.getCategoria());

        String sqlLivro = "INSERT INTO Livro (nome_livro, encadernacao, ano, id_categoria) VALUES (?, 'Brochura', 2024, ?);";
        try (Connection conn = conectar(); PreparedStatement pstmt = conn.prepareStatement(sqlLivro, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, l.getTitulo());
            pstmt.setInt(2, idCategoria);
            pstmt.executeUpdate();

            ResultSet rsKeys = pstmt.getGeneratedKeys();
            if (rsKeys.next()) {
                int idLivro = rsKeys.getInt(1);
                int idAutor = buscarOuCriarAutor(l.getAutor());
                vincularLivroAutor(idLivro, idAutor);
            }
            System.out.println("Livro e relacionamentos salvos no MySQL!");
        } catch (SQLException e) {
            System.err.println("Erro ao salvar livro: " + e.getMessage());
        }
    }

    public static ObservableList<TelaLivraria.Livro> carregarLivros() {
        ObservableList<TelaLivraria.Livro> lista = FXCollections.observableArrayList();

        String sql = "SELECT l.id_livro, l.nome_livro, c.nome_categoria, " +
                     "COALESCE(GROUP_CONCAT(a.nome_autor SEPARATOR ', '), 'Autor Desconhecido') AS autor_nome " +
                     "FROM Livro l " +
                     "LEFT JOIN Categoria c ON l.id_categoria = c.id_categoria " +
                     "LEFT JOIN Livro_Autor la ON l.id_livro = la.id_livro " +
                     "LEFT JOIN Autor a ON la.id_autor = a.id_autor " +
                     "GROUP BY l.id_livro, l.nome_livro, c.nome_categoria;";

        try (Connection conn = conectar(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new TelaLivraria.Livro(
                    rs.getString("nome_livro"),
                    rs.getString("autor_nome"),
                    rs.getString("nome_categoria") != null ? rs.getString("nome_categoria") : "Geral",
                    29.90,
                    10,
                    "1",
                    "Livraria Central"
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao carregar livros do MySQL: " + e.getMessage());
        }
        return lista;
    }

    public static void atualizarEstoqueLivro(TelaLivraria.Livro l) {
        System.out.println("Estoque processado localmente.");
    }

    public static void removerLivro(TelaLivraria.Livro l) {
        String sql = "DELETE FROM Livro WHERE nome_livro = ?;";
        try (Connection conn = conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, l.getTitulo());
            pstmt.executeUpdate();
            System.out.println("Livro removido com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao remover livro: " + e.getMessage());
        }
    }

    // --- MÉTODOS AUXILIARES ---
    private static int buscarOuCriarCategoria(String nomeCategoria) {
        String sqlBusca = "SELECT id_categoria FROM Categoria WHERE nome_categoria = ?;";
        try (Connection conn = conectar(); PreparedStatement pstmt = conn.prepareStatement(sqlBusca)) {
            pstmt.setString(1, nomeCategoria);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("id_categoria");
        } catch (SQLException e) { e.printStackTrace(); }

        String sqlInsere = "INSERT INTO Categoria (nome_categoria) VALUES (?);";
        try (Connection conn = conectar(); PreparedStatement pstmt = conn.prepareStatement(sqlInsere, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, nomeCategoria);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 1;
    }

    private static int buscarOuCriarAutor(String nomeAutor) {
        String sqlBusca = "SELECT id_autor FROM Autor WHERE nome_autor = ?;";
        try (Connection conn = conectar(); PreparedStatement pstmt = conn.prepareStatement(sqlBusca)) {
            pstmt.setString(1, nomeAutor);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("id_autor");
        } catch (SQLException e) { e.printStackTrace(); }

        String sqlInsere = "INSERT INTO Autor (nome_autor) VALUES (?);";
        try (Connection conn = conectar(); PreparedStatement pstmt = conn.prepareStatement(sqlInsere, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, nomeAutor);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 1;
    }

    private static void vincularLivroAutor(int idLivro, int idAutor) {
        String sql = "INSERT IGNORE INTO Livro_Autor (id_livro, id_autor) VALUES (?, ?);";
        try (Connection conn = conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idLivro);
            pstmt.setInt(2, idAutor);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}