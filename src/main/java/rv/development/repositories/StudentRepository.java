package rv.development.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rv.development.entities.Student;

import java.util.List;
@Repository
public interface StudentRepository extends JpaRepository <Student , Long > {
    List<Student> findByActivated(boolean activated);
    Student findByDocNumber(String docNumber);
    Student findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByDocNumber(String docNumber);
@Transactional
    void deleteByDocNumber(String docNumber);
}
