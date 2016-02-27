package com.oleg.sparepartsviewer;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.beans.PropertyVetoException;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user1 on 25.02.2016.
 */
public class DBWrapper {
    Connection conn;
    String connectuinUrl = "jdbc:h2:~/h2db";
    ComboPooledDataSource cpds;

    public DBWrapper() throws ClassNotFoundException, SQLException, PropertyVetoException {
        Class.forName("org.h2.Driver");
        ComboPooledDataSource cpds = new ComboPooledDataSource();
        cpds.setMaxStatementsPerConnection(10);
        cpds.setDriverClass("org.h2.Driver");
        cpds.setJdbcUrl(connectuinUrl);
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {

    }

    public void importCSV(File file, String tableName, String idColumn, String priceColumn) throws Exception {
        String query = "CREATE TABLE sp_"+tableName+" AS SELECT * FROM CSVREAD(?)";
        conn = cpds.getConnection();
                //DriverManager.getConnection(connectuinUrl);
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setString(1, file.getAbsolutePath());
        statement.execute(query);
        statement.close();

        query = "CREATE TABLE IF NOT EXISTS SETTINGS(TABLE_NAME VARCHAR(255) PRIMARY KEY, ID_COLUMN VARCHAR(255), PRICE_COLUMN VARCHAR(255));";
        Statement st = conn.createStatement();
        st.execute(query);
        st.close();

        query = "INSERT INTO SETTINGS VALUES('"+tableName+"', '"+idColumn+"', '"+priceColumn+"')";
        st = conn.createStatement();
        st.execute(query);
        st.close();

        conn.close();
    }

    public List<String> getExistTables() throws SQLException {
        String query = "SELECT table_name FROM information_schema.tables where table_name like 'SP_%'";
        List<String> res = new ArrayList<>();
        conn = DriverManager.getConnection(connectuinUrl);
        Statement statement = conn.createStatement();
        ResultSet rs = statement.executeQuery(query);
        while(rs.next()){
            res.add(rs.getString(1));
        }
        conn.close();
        return res;
    }
}
