package dev.codescreen.library.mysql;


import java.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MysqlClient {
    private Connection conn;
    private Statement stm = null;
    private static final Logger LOGGER = LogManager.getLogger(MysqlClient.class);

    public MysqlClient() throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
    }

    public void connect(String host, String user, String password) throws SQLException {
        try {
            this.conn = DriverManager.getConnection(host, user, password);
            this.stm = this.conn.createStatement();
        } catch (SQLException ex) {
            LOGGER.error("SQLException: " + ex.getMessage());
            LOGGER.error("SQLState: " + ex.getSQLState());
            LOGGER.error("VendorError: " + ex.getErrorCode());
            throw ex;
        }
    }

    public ResultSet executeQuery(
            String query
    ) throws SQLException {
        try {
            this.stm.execute(query);
            return this.stm.getResultSet();
        } catch (SQLException ex) {
            LOGGER.error("SQLException: " + ex.getMessage());
            throw ex;
        }
    }

    public int executeUpdate(String query) throws SQLException {
        try {
            return this.stm.executeUpdate(query);
        } catch (SQLException ex) {
            LOGGER.error("SQLException: " + ex.getMessage());
            throw ex;
        }
    }

    public void close() throws SQLException {
        try {
            if (this.stm != null) {
                this.stm.close();
                this.stm = null;
            }
            this.conn.close();
        } catch (SQLException ex) {
            LOGGER.error("SQLException: " + ex.getMessage());
            throw ex;
        }
    }

    public void setAutoCommit(boolean auto) throws SQLException {
        this.conn.setAutoCommit(auto);
    }

    public void commit() throws SQLException {
        this.conn.commit();
    }

    public void rollback() throws SQLException {
        this.conn.rollback();
    }
}
