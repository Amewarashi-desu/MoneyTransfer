package foo.bar;

import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.Assert.*;

public class DBCManagerTest extends DBCManager {

    static final DBCManager dbcManager = new DBCManager();

    static {
        dbcManager.postConstruct();
    }

    public static DBCManager getDbcManager() {
        return dbcManager;
    }

    @Test
    public void getConnectionTest() throws Exception {

        final Connection connection = dbcManager.getConnection();

        assertNotNull(connection);
        assertTrue(connection.isValid(10));

        final PreparedStatement preparedStatement = connection.prepareStatement("select 1");
        preparedStatement.execute();
        final ResultSet resultSet = preparedStatement.getResultSet();

        assertTrue(resultSet.next());
        assertEquals(resultSet.getInt(1), 1);

    }
}