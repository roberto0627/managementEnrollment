package rv.development.services.impls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rv.development.entities.Student;
import rv.development.repositories.StudentRepository;
import rv.development.services.StudentService;

import java.util.List;
import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {

    StudentRepository studentRepository;

    @Autowired
    StudentServiceImpl(StudentRepository studentRepository){
        this.studentRepository = studentRepository;
    }

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
        if(existsDocNumber){
            Student currentStudent = studentRepository.findByDocNumber(student.getDocNumber());
            if(student.getId() > 0 && student.getDocNumber().equals(currentStudent.getDocNumber())){
                return studentRepository.save(student);
            }
        }
        return null;
    }


    public Boolean deleteStudentById(long id) {
        boolean isDeleted  = false;
        boolean existsId=studentRepository.existsById(id);
        if(existsId){
            Optional<Student> student = studentRepository.findById(id);
            if(student.isPresent()){
                student.get().setActivated(false);
                studentRepository.save(student.get());
                isDeleted = true;
            }
        }
        return isDeleted;
    }


    public Boolean deleteStudentByDocNumber(String docNumber) {
        boolean isDeleted  = false;

        boolean existsDocNumber=studentRepository.existsByDocNumber(docNumber);
        if(existsDocNumber){
            Student student = studentRepository.findByDocNumber(docNumber);
            student.setActivated(false);
            studentRepository.save(student);
            isDeleted = true;
        }
        return isDeleted;
    }
}
