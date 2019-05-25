package com.myself.everythingP.core.dao;

import com.alibaba.druid.pool.DruidDataSource;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DataSourceFactory {
    private static volatile DruidDataSource dataSource;

    private DataSourceFactory() {

    }

    public static DataSource getInstance() {
        if (dataSource == null) {
            synchronized (DataSourceFactory.class) {
                if (dataSource == null) {
                    dataSource = new DruidDataSource();
                    dataSource.setDriverClassName("org.h2.Driver");
                    String workDir = System.getProperty("user.dir");
                    dataSource.setUrl("jdbc:h2:" + workDir + File.separator + "everythingP");
                    dataSource.setTestWhileIdle(false);
                }
            }
        }
        return dataSource;
    }

    public static void initDatabase() {
        DataSource dataSource = DataSourceFactory.getInstance();
        try (InputStream in = DataSourceFactory.class.getClassLoader().getResourceAsStream("everythingP.sql");) {
            if (in == null) {
                throw new RuntimeException("can not read database script, please check it");
            }
            StringBuilder sqlBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in));) {
                String line;
                while (((line = reader.readLine()) != null)) {
                    if(!line.startsWith("--")){
                        sqlBuilder.append(line);
                    }
                }
            }
            String sql=sqlBuilder.toString();
            Connection connection=dataSource.getConnection();
            PreparedStatement statement=connection.prepareStatement(sql);
            statement.execute();

            statement.close();
            connection.close();

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

    }
}
