package rv.development.services;

import org.springframework.beans.factory.ListableBeanFactory;
import rv.development.entities.Student;

import java.util.List;

public interface StudentService {
   List<Student> showAllStudents(boolean activated );
    Student findByDocNumber(String docNumber );
    Student findByEmail(String email );
   Student saveStudent(Student student);
    Student updateStudent(Student student);
    Boolean deleteStudentById(long id);
    Boolean deleteStudentByDocNumber(String docNumber);
}
