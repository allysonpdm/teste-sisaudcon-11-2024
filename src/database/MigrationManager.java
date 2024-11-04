package database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MigrationManager {

    private DataAccessObject dao;

    public MigrationManager() {
        this.dao = new DataAccessObject();
    }

    public void runMigrations() {
        if (!hasTables()) {
            try {
                List<String> migrations = getMigrationFiles("src/database/migrations");

                for (String migration : migrations) {
                    executeMigration(migration);
                }
                System.out.println("Todas as migrations foram executadas com sucesso.");
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Atenção: As migrations não serão executadas porque o banco de dados já possui tabelas.");
        }
    }

    private boolean hasTables() {
        String query = "SELECT to_regclass('public.usuarios');";
        try (ResultSet rs = dao.executeQuery(query)) {
            return rs.next() && rs.getObject(1) != null;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private List<String> getMigrationFiles(String directory) throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get(directory))) {
            return paths.filter(Files::isRegularFile)
                        .filter(path -> path.toString().endsWith(".sql"))
                        .map(Path::toString)
                        .collect(Collectors.toList());
        }
    }

    private void executeMigration(String migration) throws IOException, SQLException {
        StringBuilder sql = new StringBuilder();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(migration))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sql.append(line).append("\n");
            }
        }

        dao.save(sql.toString());
        System.out.println("Executed migration: " + migration);
    }
}
