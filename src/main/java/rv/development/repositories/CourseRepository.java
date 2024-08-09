package rv.development.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rv.development.entities.Course;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository <Course, Long >{
    List<Course> findByActivated(boolean activated);
    Course findByCourseName(String courseName);
    Course findByAcronymName(String acronymName);
    boolean existsByCourseName(String courseName);
    boolean existsById(long id);
}

