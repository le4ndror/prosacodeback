package prosacode;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SistemaLivraria {

    private static final String URL = "jdbc:mysql://localhost:3306/livraria_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "04170318l";

    public static Connection obterConexao() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // ==========================================
    // CONSULTAS (Retornam List<String> para o JavaFX)
    // ==========================================
    public static List<String> buscarLivros(Connection conn, String termo, String tipo) throws SQLException {
        List<String> resultados = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT L.id_livro, L.nome_livro, L.encadernacao, L.ano, C.nome_categoria, " +
            "GROUP_CONCAT(A.nome_autor SEPARATOR ', ') AS autores " +
            "FROM Livro L " +
            "JOIN Categoria C ON L.id_categoria = C.id_categoria " +
            "LEFT JOIN Livro_Autor LA ON L.id_livro = LA.id_livro " +
            "LEFT JOIN Autor A ON LA.id_autor = A.id_autor "
        );

        if ("TITULO".equalsIgnoreCase(tipo)) {
            sql.append("WHERE L.nome_livro LIKE ? ");
        } else if ("CATEGORIA".equalsIgnoreCase(tipo)) {
            sql.append("WHERE C.nome_categoria LIKE ? ");
        } else if ("AUTOR".equalsIgnoreCase(tipo)) {
            sql.append("WHERE A.nome_autor LIKE ? ");
        }

        sql.append("GROUP BY L.id_livro, L.nome_livro, L.encadernacao, L.ano, C.nome_categoria");

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            if (!"TODOS".equalsIgnoreCase(tipo)) {
                stmt.setString(1, "%" + termo + "%");
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String item = String.format("ID: %d | Livro: %s | Categoria: %s | Autor(es): %s | Ano: %d | Encadernação: %s",
                            rs.getInt("id_livro"),
                            rs.getString("nome_livro"),
                            rs.getString("nome_categoria"),
                            rs.getString("autores"),
                            rs.getInt("ano"),
                            rs.getString("encadernacao"));
                    resultados.add(item);
                }
            }
        }
        return resultados;
    }

    // Retorna categorias ou autores formatados como "ID - Nome" para preencher seletores (ComboBox)
    public static List<String> listarTabela(Connection conn, String tabela, String colId, String colNome) throws SQLException {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT * FROM " + tabela;
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(rs.getInt(colId) + " - " + rs.getString(colNome));
            }
        }
        return lista;
    }

    // ==========================================
    // OPERAÇÕES DE CADASTRO
    // ==========================================
    public static boolean cadastrarCategoria(Connection conn, String nomeCategoria) throws SQLException {
        String sql = "INSERT INTO Categoria (nome_categoria) VALUES (?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nomeCategoria);
            return stmt.executeUpdate() > 0;
        }
    }

    public static boolean cadastrarAutor(Connection conn, String nomeAutor, String bi) throws SQLException {
        String sql = "INSERT INTO Autor (nome_autor, bi) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nomeAutor);
            stmt.setString(2, bi);
            return stmt.executeUpdate() > 0;
        }
    }

    public static boolean cadastrarLivro(Connection conn, String nomeLivro, String encadernacao, int ano, int idCategoria, int idAutor) throws SQLException {
        String sqlLivro = "INSERT INTO Livro (nome_livro, encadernacao, ano, id_categoria) VALUES (?, ?, ?, ?)";
        int idLivroGerado = -1;

        try (PreparedStatement stmt = conn.prepareStatement(sqlLivro, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, nomeLivro);
            stmt.setString(2, encadernacao);
            stmt.setInt(3, ano);
            stmt.setInt(4, idCategoria);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    idLivroGerado = rs.getInt(1);
                }
            }
        }

        if (idLivroGerado != -1) {
            String sqlRelacao = "INSERT INTO Livro_Autor (id_livro, id_autor) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sqlRelacao)) {
                stmt.setInt(1, idLivroGerado);
                stmt.setInt(2, idAutor);
                return stmt.executeUpdate() > 0;
            }
        }
        return false;
    }
}