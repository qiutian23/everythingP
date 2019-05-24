package com.myself.everythingP.core.dao.impl;

import com.myself.everythingP.core.dao.FileIndexDao;
import com.myself.everythingP.core.model.Condition;
import com.myself.everythingP.core.model.FileType;
import com.myself.everythingP.core.model.Thing;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FileIndexDaoImpl implements FileIndexDao {
    private final DataSource dataSource;

    public FileIndexDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void insert(Thing thing) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = dataSource.getConnection();
            String sql = "insert into file_index(name, path, depth, file_type, length, last_modify_time) values(?,?,?,?,?,?);";
            statement = connection.prepareStatement(sql);
            statement.setString(1, thing.getName());
            statement.setString(2, thing.getPath());
            statement.setInt(3, thing.getDepth());
            statement.setString(4, thing.getFileType().name());
            statement.setLong(5,thing.getLength());
            statement.setString(6,thing.getLastModifyTime());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseSource(null, statement, connection);
        }
    }

    @Override
    public List<Thing> search(Condition condition) {
        List<Thing> list = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = dataSource.getConnection();
            StringBuilder sqlBuilder = new StringBuilder(" select name, path, depth, file_type, length, last_modify_time from file_index ");
            sqlBuilder.append(" where name like '%").append(condition.getName()).append("%' ");
            if (condition.getFileType() != null) {
                sqlBuilder.append(" and file_type = '").append(condition.getFileType().toUpperCase()).append("' ");
            }
            if (condition.getOrderByAsc() != null) {
                sqlBuilder.append(" order by depth ").append(condition.getOrderByAsc() ? "asc" : "desc");
            }
            if (condition.getLimit() != null) {
                sqlBuilder.append(" limit ").append(condition.getLimit()).append(" offset 0");
            }
            String sql = sqlBuilder.toString();
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Thing thing = new Thing();
                thing.setName(resultSet.getString("name"));
                thing.setPath(resultSet.getString("path"));
                thing.setDepth(resultSet.getInt("depth"));
                String fileType = resultSet.getString("file_type");
                thing.setFileType(FileType.lookupByName(fileType));
                thing.setLength(resultSet.getLong("length"));
                thing.setLastModifyTime(resultSet.getString("last_modify_time"));
                list.add(thing);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseSource(resultSet, statement, connection);
        }
        return list;
    }

    @Override
    public void delete(Thing thing) {
        //todo
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = dataSource.getConnection();
            String sql = "delete from file_index where path like '"+thing.getPath()+"%' ";
            statement = connection.prepareStatement(sql);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseSource(null, statement, connection);
        }
    }

    private void releaseSource(ResultSet resultSet, Statement statement, Connection connection) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
