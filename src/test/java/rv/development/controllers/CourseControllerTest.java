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
import rv.development.entities.Course;
import rv.development.repositories.CourseRepository;
import rv.development.services.impls.CourseServiceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CourseController.class)
@Import(CourseServiceImpl.class)
class CourseControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    CourseRepository courseRepository;

    @Autowired
    ObjectMapper mapper;

    Course course1 = new Course(1L,"Matematica","MAT",true);
    Course course2 = new Course(2L,"Religion","REL",true);
    Course course3 = new Course(3L,"Comunicacion","COM",true);
    Course course4 = new Course(4L,"Ingles","ING",false);
    Course course5 = new Course(5L,"Arte","ART",false);

    @Test
    void getAllActivatedCourses_success() throws Exception{
        List<Course> courses= new ArrayList<>(Arrays.asList(course1,course2,course3));
        Mockito.when(courseRepository.findByActivated(true)).thenReturn(courses);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/courses?activated=true")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(3)));
    }

    @Test
    void getAllDeactivatedCourses_success() throws Exception{
        List<Course> courses= new ArrayList<>(Arrays.asList(course4,course5));
        Mockito.when(courseRepository.findByActivated(false)).thenReturn(courses);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/courses?activated=false")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(2)));
    }

    @Test
    void addNewCourse_success() throws Exception{
        Course newCourse = new Course();
        newCourse.setCourseName("Matematica");
        newCourse.setAcronymName("MAT");
        newCourse.setActivated(true);

        Mockito.when(courseRepository.existsByCourseName(newCourse.getCourseName())).thenReturn(false);
        Mockito.when(courseRepository.save(any())).thenReturn(course1);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newCourse))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$",notNullValue()))
                .andExpect(jsonPath("$.courseName").value(newCourse.getCourseName()))
                .andExpect(jsonPath("$.acronymName").value(newCourse.getAcronymName()))
                .andExpect(jsonPath("$.activated").value(newCourse.getActivated()));
    }

    @Test
    void addExistingCourse_success() throws Exception{
        Mockito.when(courseRepository.existsByCourseName("Religion")).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(course2))
                )
                .andExpect(status().isFound());
    }

    @Test
    void findCourseByName_success() throws Exception {
        Mockito.when(courseRepository.findByCourseName("Matematica")).thenReturn(course1);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/courses/names/Matematica")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()));
    }

    @Test
    void findCourseByNameNotFound_success() throws Exception{
        Mockito.when(courseRepository.findByCourseName("Matematica")).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/courses/names/Matematica")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());

    }

    @Test
    void findCourseByAcronym_success() throws Exception{
        Mockito.when(courseRepository.findByAcronymName("MAT")).thenReturn(course1);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/courses/acronyms/MAT")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",notNullValue()));
    }

    @Test
    void findCourseByAcronymNotFound_success() throws Exception{
        Mockito.when(courseRepository.findByAcronymName("MAT")).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/courses/acronyms/MAT")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void updateExistingCourse_success() throws Exception{
        Course newCourse = new Course();
        newCourse.setCourseName("Matematica");
        newCourse.setAcronymName("MAT");
        newCourse.setActivated(true);
        newCourse.setId(1L);

        Mockito.when(courseRepository.existsById(1L)).thenReturn(true);
        Mockito.when(courseRepository.save(any())).thenReturn(course1);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newCourse))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",notNullValue()))
                .andExpect(jsonPath("$.courseName").value(newCourse.getCourseName()))
                .andExpect(jsonPath("$.acronymName").value(newCourse.getAcronymName()))
                .andExpect(jsonPath("$.activated").value(newCourse.getActivated()))
                .andExpect(jsonPath("$.id").value(newCourse.getId()));
    }

    @Test
    void updateCourseNotFound_success() throws Exception{
        Course newCourse = new Course();
        newCourse.setCourseName("Matematica");
        newCourse.setAcronymName("MAT");
        newCourse.setActivated(true);
        newCourse.setId(1L);

        Mockito.when(courseRepository.existsById(1L)).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newCourse))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteExistingCourseById_success() throws Exception{
        Mockito.when(courseRepository.existsById(1L)).thenReturn(true);
        Mockito.doNothing().when(courseRepository).deleteById(1L);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/courses/ids/1")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteCourseByIdNotFound_success() throws Exception{
        Mockito.when(courseRepository.existsById(1L)).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/courses/ids/1")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteExistingCourseByName_success() throws Exception{
        Mockito.when(courseRepository.existsByCourseName("Matematica")).thenReturn(true);
        Mockito.doNothing().when(courseRepository).deleteByCourseName("Matematica");

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/courses/names/Matematica")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteCourseByNameNotFound_success() throws Exception{
        Mockito.when(courseRepository.existsByCourseName("Matematica")).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/courses/names/Matematica")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }
}
