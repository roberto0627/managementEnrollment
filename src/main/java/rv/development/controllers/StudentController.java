package rv.development.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rv.development.entities.Student;
import rv.development.services.StudentService;

import java.util.List;

@SuppressWarnings("unused")
@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@RestController
@RequestMapping("/api/students")
public class StudentController {

    StudentService studentService;

    @Autowired
    StudentController(StudentService studentService){
        this.studentService = studentService;
    }

    @GetMapping(value = "")
    ResponseEntity<List<Student>> showAllStudents(@RequestParam boolean activated ){
        List<Student> students = studentService.showAllStudents(activated);
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    @PostMapping(value = "")
    ResponseEntity<Student>  saveStudent(@RequestBody Student student){
        Student newStudent = studentService.saveStudent(student);
        if(newStudent==null){
            return  new ResponseEntity<>(HttpStatus.FOUND);
        } else {
            return  new ResponseEntity<>(newStudent, HttpStatus.CREATED);
        }

    }

    @GetMapping(value = "/documents/{docNumber}")
    ResponseEntity<Student> findByDocNumber(@PathVariable String docNumber ){
        Student foundStudent = studentService.findByDocNumber(docNumber);
        if(foundStudent == null){
             return ResponseEntity.notFound().build();
        }
      return ResponseEntity.status(HttpStatus.OK).body(foundStudent);
    }

    @GetMapping(value = "/emails/{email}")
    ResponseEntity<Student> findByEmail(@PathVariable String email ){
        Student foundStudent = studentService.findByEmail(email);
        if(foundStudent == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(foundStudent);
    }

    @PutMapping (value = "")
    ResponseEntity<Student> updateStudent(@RequestBody Student student){
        Student newStudent = studentService.updateStudent(student);
        if(newStudent == null){
            return ResponseEntity.notFound().build();
        }
        return  new ResponseEntity<>(newStudent, HttpStatus.OK);
    }

    @DeleteMapping (value = "/ids/{id}")
    ResponseEntity<Boolean>  deleteStudentById(@PathVariable long id){
        boolean isDeleted = studentService.deleteStudentById(id);
        if(!isDeleted){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(true);
    }

    @DeleteMapping (value = "/documents/{docNumber}")
    ResponseEntity<Boolean> deleteStudentByDocNumber(@PathVariable String docNumber){
        boolean isDeleted = studentService.deleteStudentByDocNumber(docNumber);
        if(!isDeleted){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(true);
    }
}
