package rv.development.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.format.annotation.DateTimeFormat;
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

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@WebMvcTest(StudentController.class)
@Import(StudentServiceImpl.class)
public class StudentControllerTest {
    @Autowired
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
    public void getAllActivatedStudents_success() throws Exception{
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
    public void getAllDeactivatedStudents_success() throws Exception{
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
    public void addNewStudent_success() throws Exception{
        Student newStudent = new Student();
        newStudent.setDocType("DNI");
        newStudent.setDocNumber("46657897");
        newStudent.setFirstName("Jose");
        newStudent.setLastName("Ruiz");
        newStudent.setBirthDate(LocalDate.of(1994,8,5));
        newStudent.setEmail("jose.ruiz@gmail.com");
        newStudent.setActivated(true);

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
    public void addExistingStudent_success() throws Exception{
        Mockito.when(studentRepository.existsByEmail("jose.ruiz@gmail.com")).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(student1))
                )
                .andExpect(status().isFound());
    }

    @Test
    public void findStudentByDocument_success() throws Exception{
        Mockito.when(studentRepository.findByDocNumber("46657897")).thenReturn(student1);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/students/documents/46657897")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",notNullValue()));
    }

    @Test
    public void findStudentByDocumentNotFound_success() throws Exception{
        Mockito.when(studentRepository.findByDocNumber("46657897")).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/students/documents/46657897")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void findStudentByEmail_success() throws Exception{
        Mockito.when(studentRepository.findByEmail("jose.ruiz@gmail.com")).thenReturn(student1);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/students/emails/jose.ruiz@gmail.com")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",notNullValue()));
    }

    @Test
    public void findStudentByEmailNotFound_success() throws Exception{
        Mockito.when(studentRepository.findByEmail("jose.ruiz@gmail.com")).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/students/emails/jose.ruiz@gmail.com")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }


    @Test
    public void updateStudentNotFound_success() throws Exception{
        Student newStudent = new Student();
        newStudent.setDocType("DNI");
        newStudent.setDocNumber("46657897");
        newStudent.setFirstName("Jose");
        newStudent.setLastName("Ruiz");
        newStudent.setBirthDate(LocalDate.of(1994,8,5));
        newStudent.setEmail("jose.ruiz@gmail.com");
        newStudent.setActivated(true);
        newStudent.setId(1L);

        Mockito.when(studentRepository.existsById(1L)).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newStudent))
                )
                .andExpect(status().isNotFound());
    }





    @Test
    public void deleteExistingStudentById_success() throws Exception{
        Mockito.when(studentRepository.existsById(1L)).thenReturn(true);
        Mockito.doNothing().when(studentRepository).deleteById(1L);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/students/ids/1")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteExistingStudentByIdNotFound_success() throws Exception{
        Mockito.when(studentRepository.existsById(1L)).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/students/ids/1")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }
}
