package foo.bar;

import org.h2.jdbcx.JdbcConnectionPool;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Singleton;
import java.sql.Connection;
import java.sql.SQLException;

@Singleton
public class DBCManager {

    private JdbcConnectionPool connectionPool;

    private static final String INIT_SCRIPT = "create table ACCOUNT (\n" +
            "\t\t\tACCOUNT_ID bigint IDENTITY not null,\n" +
            "\t\t\tCREATE_TIME TIMESTAMP DEFAULT NOW(),\n" +
            "\t\t\tBALANCE DECIMAL(16,2),\n" +
            "\t\t\tprimary key (ACCOUNT_ID)\n" +
            ");\n" +
            "\n" +
            "create table PAYMENT (\n" +
            "  PAYMENT_ID bigint IDENTITY not null,\n" +
            "  CREATE_TIME TIMESTAMP DEFAULT NOW(),\n" +
            "  AMOUNT DECIMAL(16,2),\n" +
            "  LEFT_ACCOUNT_ID bigint not null,\n" +
            "  RIGHT_ACCOUNT_ID bigint not null,\n" +
            "  primary key (PAYMENT_ID)\n" +
            ");\n" +
            "\n" +
            "ALTER TABLE PAYMENT\n" +
            "ADD FOREIGN KEY (LEFT_ACCOUNT_ID)\n" +
            "REFERENCES ACCOUNT(ACCOUNT_ID);\n" +
            "\n" +
            "ALTER TABLE PAYMENT\n" +
            "ADD FOREIGN KEY (RIGHT_ACCOUNT_ID)\n" +
            "REFERENCES ACCOUNT(ACCOUNT_ID);\n" +
            "\n" +
            "ALTER TABLE PAYMENT\n" +
            "ADD CONSTRAINT PMNT_AMNT\n" +
            "CHECK  (AMOUNT > 0);\n" +
            "\n" +
            "ALTER TABLE ACCOUNT\n" +
            "ADD CONSTRAINT ACCNT_BLNC\n" +
            "CHECK  (BALANCE >= 0);\n" +
            "\n" +
            "ALTER TABLE PAYMENT\n" +
            "ADD CHECK (LEFT_ACCOUNT_ID <> RIGHT_ACCOUNT_ID)";

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
            connection.createStatement().execute(INIT_SCRIPT);
            connection.commit();
        } catch (SQLException e) {
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

}
