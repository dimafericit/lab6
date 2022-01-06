package repository;

import java.sql.SQLException;
import java.util.List;

public interface ICrudRepository<T> {
    void create(T obj) throws SQLException;

    List<T> getAll() throws SQLException;

    void update(T obj) throws SQLException;

    void delete(T obj) throws SQLException;
}
