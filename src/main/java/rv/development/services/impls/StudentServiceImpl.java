package rv.development.services.impls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rv.development.entities.Student;
import rv.development.repositories.StudentRepository;
import rv.development.services.StudentService;

import java.util.List;
@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentRepository studentRepository;


    public List<Student> showAllStudents(boolean activated) {
        return studentRepository.findByActivated(activated);
    }


    public Student findByDocNumber(String docNumber) {
        return studentRepository.findByDocNumber(docNumber);
    }


    public Student findByEmail(String email) {
        return studentRepository.findByEmail(email);
    }


    public Student saveStudent(Student student) {
        boolean existsEmail=studentRepository.existsByEmail(student.getEmail());
        boolean existsDocNumber=studentRepository.existsByDocNumber(student.getDocNumber());
        if(!existsEmail && !existsDocNumber){
            return studentRepository.save(student);
        }
      return null;
    }


    public Student updateStudent(Student student) {

        boolean existsDocNumber=studentRepository.existsByDocNumber(student.getDocNumber());
        Student currentStudent = studentRepository.findByDocNumber(student.getDocNumber());
        if(student.getId() > 0 && existsDocNumber && student.getDocNumber().equals(currentStudent.getDocNumber())){
            return studentRepository.save(student);
        }
        return null;
    }


    public Boolean deleteStudentById(long id) {
        boolean isDeleted  = false;
        boolean existsId=studentRepository.existsById(id);
        try {
            if(existsId){
                studentRepository.deleteById(id);
                isDeleted = true;
            }

        }catch (Exception e){
          e.printStackTrace();
        }
        return isDeleted;
    }


    public Boolean deleteStudentByDocNumber(String docNumber) {
        boolean isDeleted  = false;
        boolean existsDocNumber=studentRepository.existsByDocNumber(docNumber);
        try {
            if(existsDocNumber){
                studentRepository.deleteByDocNumber(docNumber);
                isDeleted = true;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return isDeleted;
    }
}
