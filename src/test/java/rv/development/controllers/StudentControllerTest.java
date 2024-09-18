package rv.development.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import rv.development.entities.Student;
import rv.development.repositories.StudentRepository;
import rv.development.services.impls.StudentServiceImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//@WebMvcTest(StudentController.class)
//@Import(StudentServiceImpl.class)
class StudentControllerTest {
    /*@Autowired
    MockMvc mockMvc;

    @MockBean
    StudentRepository studentRepository;

    @Autowired
    ObjectMapper mapper;

    Student  student1 = new Student(1L,"DNI", "46657897", "Jose", "Ruiz", LocalDate.of(1994,8,5), "jose.ruiz@gmail.com", true );
    Student  student2 = new Student(2L,"DNI", "46789548", "Maria", "Cruz", LocalDate.of(1996,4,11), "maria.cruz@gmail.com", true );
    Student  student3 = new Student(3L,"DNI", "46543728", "Luis", "Robles", LocalDate.of(1990,10,7), "luis.robles@gmail.com", true );
    Student  student4 = new Student(4L,"DNI", "46876954", "Rosa", "Perez", LocalDate.of(1991,8,4), "rosa.perez@gmail.com", false );
    Student  student5 = new Student(5L,"DNI", "46874219", "Frank", "Castillo", LocalDate.of(1998,9,12), "frank.castillo@gmail.com", false );

    @Test
    void getAllActivatedStudents_success() throws Exception{
        List<Student> students= new ArrayList<>(Arrays.asList(student1,student2,student3));
        Mockito.when(studentRepository.findByActivated(true)).thenReturn(students);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/students?activated=true")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(3)));
    }

    @Test
    void getAllDeactivatedStudents_success() throws Exception{
        List<Student> students= new ArrayList<>(Arrays.asList(student4,student5));
        Mockito.when(studentRepository.findByActivated(false)).thenReturn(students);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/students?activated=false")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(2)));
    }

    @Test
    void addNewStudent_success() throws Exception{
        Student newStudent = buildStudentRequest(0L, true);

        Mockito.when(studentRepository.existsByEmail(newStudent.getEmail())).thenReturn(false);
        Mockito.when(studentRepository.existsByDocNumber(newStudent.getDocNumber())).thenReturn(false);
        Mockito.when(studentRepository.save(any())).thenReturn(student1);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newStudent))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$",notNullValue()))
                .andExpect(jsonPath("$.docType").value(newStudent.getDocType()))
                .andExpect(jsonPath("$.docNumber").value(newStudent.getDocNumber()))
                .andExpect(jsonPath("$.firstName").value(newStudent.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(newStudent.getLastName()))
                .andExpect(jsonPath("$.birthDate").value(newStudent.getBirthDate().toString()))
                .andExpect(jsonPath("$.email").value(newStudent.getEmail()))
                .andExpect(jsonPath("$.activated").value(newStudent.getActivated()));
    }

    @Test
    void addNewStudent_existsEmailAndDocNumber() throws Exception{
        addNewStudent_notFound_parametrized(true, true);
    }

    @Test
    void addNewStudent_existsEmailAndNoExistsDocNumber() throws Exception{
        addNewStudent_notFound_parametrized(true, false);
    }

    @Test
    void addNewStudent_noExistsEmailAndExistsDocNumber() throws Exception{
        addNewStudent_notFound_parametrized(false, true);
    }

    void addNewStudent_notFound_parametrized(boolean existsEmail, boolean existsDocNumber) throws Exception {
        Student newStudent = buildStudentRequest(0L, true);

        Mockito.when(studentRepository.existsByEmail(newStudent.getEmail())).thenReturn(existsEmail);
        Mockito.when(studentRepository.existsByDocNumber(newStudent.getDocNumber())).thenReturn(existsDocNumber);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newStudent))
                )
                .andExpect(status().isFound());
    }

    @Test
    void addExistingStudent_success() throws Exception{
        Mockito.when(studentRepository.existsByEmail("jose.ruiz@gmail.com")).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(student1))
                )
                .andExpect(status().isFound());
    }

    @Test
    void findStudentByDocument_success() throws Exception{
        Mockito.when(studentRepository.findByDocNumber("46657897")).thenReturn(student1);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/students/documents/46657897")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",notNullValue()));
    }

    @Test
    void findStudentByDocument_notFound() throws Exception{
        Mockito.when(studentRepository.findByDocNumber("46657897")).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/students/documents/46657897")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void findStudentByEmail_success() throws Exception{
        Mockito.when(studentRepository.findByEmail("jose.ruiz@gmail.com")).thenReturn(student1);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/students/emails/jose.ruiz@gmail.com")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",notNullValue()));
    }

    @Test
    void findStudentByEmail_notFound() throws Exception{
        Mockito.when(studentRepository.findByEmail("jose.ruiz@gmail.com")).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/students/emails/jose.ruiz@gmail.com")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void updateStudent_success() throws Exception{
        Student newStudent = buildStudentRequest(1L, true);
        newStudent.setLastName("Ruiz Suarez");

        Mockito.when(studentRepository.existsByDocNumber(newStudent.getDocNumber())).thenReturn(true);
        Mockito.when(studentRepository.findByDocNumber(newStudent.getDocNumber())).thenReturn(student1);
        Mockito.when(studentRepository.save(newStudent)).thenReturn(newStudent);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newStudent))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Ruiz Suarez"));
    }

    void updateStudent_notFound_parametrized(Student student, boolean existsDocNumber) throws Exception {
        Mockito.when(studentRepository.existsByDocNumber(student.getDocNumber())).thenReturn(existsDocNumber);
        if(existsDocNumber){
            Mockito.when(studentRepository.findByDocNumber(student.getDocNumber())).thenReturn(student1);
        }

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(student))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void updateStudent_docNumberPresentButStudentIdNotPresent() throws Exception{
        Student newStudent = buildStudentRequest(0L, true);
        updateStudent_notFound_parametrized(newStudent, true);
    }

    @Test
    void updateStudent_docNumberNotPresentButStudentIdPresent() throws Exception{
        Student newStudent = buildStudentRequest(1L, true);
        newStudent.setDocNumber("12345678");
        updateStudent_notFound_parametrized(newStudent, true);
    }

    @Test
    void updateStudent_docNumberNotPresentAndStudentIdNotPresent() throws Exception{
        Student newStudent = buildStudentRequest(0L, false);
        updateStudent_notFound_parametrized(newStudent, false);
    }

    @Test
    void deleteExistingStudentById_success() throws Exception{
        Student newStudent = buildStudentRequest(1L, false);

        Mockito.when(studentRepository.existsById(1L)).thenReturn(true);
        Mockito.when(studentRepository.findById(1L)).thenReturn(Optional.of(student1));
        Mockito.when(studentRepository.save(newStudent)).thenReturn(newStudent);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/students/ids/1")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteExistingStudentById_noPresent() throws Exception{
        Mockito.when(studentRepository.existsById(1L)).thenReturn(true);
        Mockito.when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/students/ids/1")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteExistingStudentById_notFound() throws Exception{
        Mockito.when(studentRepository.existsById(1L)).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/students/ids/1")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteExistingStudentByDocNumber_success() throws Exception{
        String docNumber = "46657897";
        Student newStudent = buildStudentRequest(1L, false);

        Mockito.when(studentRepository.existsByDocNumber(docNumber)).thenReturn(true);
        Mockito.when(studentRepository.findByDocNumber(docNumber)).thenReturn(student1);
        Mockito.when(studentRepository.save(newStudent)).thenReturn(newStudent);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/students/documents/"+docNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteExistingStudentByDocNumber_notFound() throws Exception{
        String docNumber = "12345678";
        Mockito.when(studentRepository.existsByDocNumber(docNumber)).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/students/documents/"+docNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    Student buildStudentRequest(Long id, boolean activated){
        Student newStudent = new Student();
        newStudent.setDocType("DNI");
        newStudent.setDocNumber("46657897");
        newStudent.setFirstName("Jose");
        newStudent.setLastName("Ruiz");
        newStudent.setBirthDate(LocalDate.of(1994,8,5));
        newStudent.setEmail("jose.ruiz@gmail.com");
        newStudent.setActivated(activated);
        newStudent.setId(id);
        return newStudent;
    }*/
}
