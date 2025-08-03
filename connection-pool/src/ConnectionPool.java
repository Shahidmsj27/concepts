import java.io.IOException;
import java.io.InvalidObjectException;
import java.sql.SQLException;
import java.util.*;
import java.sql.DriverManager;
import java.sql.Connection;

public class ConnectionPool {

    private final String url;
    private final String user;
    private final String password;
    private final int maxConns;
    private final List<Connection> availableConns;
    private final List<Connection> usedConns;

    public ConnectionPool(String url, String user, String password, int maxConns, List<Connection> availableConns, List<Connection> usedConns)
            throws SQLException {
        this.url = url;
        this.user = user;
        this.password = password;
        this.maxConns = maxConns;
        this.availableConns = availableConns;
        this.usedConns = usedConns;
        initConnections();
    }

    public void initConnections() throws SQLException {
        for (int i = 0; i < this.maxConns; i++) {
            createConnection();
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(this.url, this.user, this.password);
    }

    public synchronized Connection addConnection() throws SQLException {
        try {
            if (getCurrentOpenConnections() >= this.maxConns) {
                System.out.println("Cannot add more connections, Max connections will exceeds");
                return null;
            }
            return createConnection();
        } catch (Exception e) {
            System.out.println("Error while adding connections");
            throw new SQLException(e);
        }
    }

    public void addConnectionBackToPool(Connection connection) throws IOException, SQLException {
        if (connection == null) {
            throw new InvalidObjectException("Invalid connection");
        }
        if (getCurrentOpenConnections() >= this.maxConns){
            throw new IOException("Cannot add more connections, pool already full");
        }
        synchronized (this) {
//          Because only if the connection is removed is when we should be adding the connection back.
            if (this.usedConns.remove(connection)) {
                this.availableConns.add(connection);
            } else {
                throw new SQLException("Connection is not used");
            }
        }
    }

    public synchronized void closePool() throws SQLException {
        for (Connection connection : this.availableConns) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new SQLException(e);
            }
        }
        for (Connection connection: this.usedConns) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new SQLException(e);
            }
        }
        this.availableConns.clear();
        this.usedConns.clear();
    }

    public synchronized int getCurrentOpenConnections() {
        return this.availableConns.size();
    }

    public List<Connection> getAvailableConnectionsList(){
        return this.availableConns;
    }

    public List<Connection> getUsedConnectionsList(){
        return this.usedConns;
    }

    public synchronized int getUsedConnections() {
        return this.usedConns.size();
    }

    public synchronized int getTotalConnections() {
        return this.availableConns.size() + this.usedConns.size();
    }

    private synchronized Connection createConnection() throws SQLException {
        Connection connection = getConnection();
        this.availableConns.add(connection);
        return connection;
    }

    public synchronized Connection getConnectionForOperation(){
        Connection c = this.availableConns.get(0);
        if (c == null){
            System.out.println("No available connections");
            return null;
        }
        if (this.availableConns.remove(c)){
            this.usedConns.add(c);
        }
        return c;
    }

    public static void main(String[] args) throws SQLException, IOException {
        ConnectionPool pool = new ConnectionPool("jdbc:h2:mem:testdb", "root", "password", 5, new ArrayList<>(), new ArrayList<>());
        System.out.println("Current open connections: " + pool.getCurrentOpenConnections());
        System.out.println("Used connections: " + pool.getUsedConnections());
        System.out.println("Total connections: " + pool.getTotalConnections());

        pool.getCurrentOpenConnections();
        List<Connection> availableConns = pool.getAvailableConnectionsList();
        Connection conn1 = availableConns.get(0);

        System.out.println(conn1);

        Connection conn2 = pool.getConnectionForOperation();
        System.out.println(pool.getUsedConnectionsList());
        System.out.println(pool.getAvailableConnectionsList());

        pool.addConnectionBackToPool(conn2);
        System.out.println(pool.getUsedConnectionsList());
        System.out.println(pool.getAvailableConnectionsList());

        pool.addConnection();

        pool.getTotalConnections();
        pool.closePool();
    }
}