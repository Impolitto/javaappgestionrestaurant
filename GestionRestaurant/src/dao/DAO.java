
import java.util.List;

public interface DAO<T> {
    // CRUD operations
    boolean create(T obj);
    T read(int id);
    boolean update(T obj);
    boolean delete(int id);
    
    // Read all
    List<T> findAll();
}