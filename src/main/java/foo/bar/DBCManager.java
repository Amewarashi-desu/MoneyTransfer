package foo.bar;

import org.h2.jdbcx.JdbcConnectionPool;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Singleton;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.stream.Collectors;

@Singleton
public class DBCManager {

    private JdbcConnectionPool connectionPool;

    private static String getInitialScript() {
        try (InputStream resourceAsStream = DBCManager.class.getClassLoader().getResourceAsStream("init.sql")) {

            return new BufferedReader(new InputStreamReader(resourceAsStream))
                    .lines().collect(Collectors.joining("\n"));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PreDestroy
    void preDestroy() {
        connectionPool.dispose();
    }

    public Connection getConnection() {
        try {
            return connectionPool.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @PostConstruct
    void postConstruct() {

        connectionPool = JdbcConnectionPool.create(
                "jdbc:h2:mem:mtapp;DB_CLOSE_DELAY=-1;autocommit=off",
                "sa",
                "sa"
        );
        connectionPool.setMaxConnections(20);

        try {
            Connection connection = connectionPool.getConnection();
            connection.createStatement().execute(getInitialScript());
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}
