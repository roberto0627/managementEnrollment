package rv.development.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rv.development.entities.Course;
import rv.development.services.CourseService;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@RestController
@RequestMapping("/api/courses")
public class CourseController {

    CourseService courseService;

    @Autowired
    CourseController(CourseService courseService){
        this.courseService = courseService;
    }

    @GetMapping(value = "")
    ResponseEntity<List<Course>> showAllCourses(@RequestParam boolean activated ){
        List<Course> courses = courseService.showAllCourses(activated);
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }

    @PostMapping(value = "")
    ResponseEntity<Course>  saveCourse(@RequestBody Course course){
        Course newCourse = courseService.saveCourse(course);
        if(newCourse==null){
            return  new ResponseEntity<>(HttpStatus.FOUND);
        } else {
            return  new ResponseEntity<>(newCourse, HttpStatus.CREATED);
        }
    }

    @GetMapping(value = "/names/{courseName}")
    ResponseEntity<Course> findByCourseName(@PathVariable String courseName ){
        Course foundCourse = courseService.findByCourseName(courseName);
        if(foundCourse == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(foundCourse);
    }

    @GetMapping(value = "/acronyms/{acronymName}")
    ResponseEntity<Course> findByAcronymName(@PathVariable String acronymName ){
        Course foundCourse = courseService.findByAcronymName(acronymName);
        if(foundCourse == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(foundCourse);
    }

    @PutMapping (value = "")
    ResponseEntity<Course> updateCourse(@RequestBody Course course){
        Course newCourse = courseService.updateCourse(course);
        if(newCourse == null){
            return ResponseEntity.notFound().build();
        }
        return  new ResponseEntity<>(newCourse, HttpStatus.OK);
    }

    @DeleteMapping (value = "/ids/{id}")
    ResponseEntity<Boolean>  deleteCourseById(@PathVariable long id){
        boolean isDeleted = courseService.deleteCourseById(id);
        if(!isDeleted){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(true);
    }

    @DeleteMapping (value = "/names/{courseName}")
    ResponseEntity<Boolean> deleteCourseByCourseName(@PathVariable String courseName){
        boolean isDeleted = courseService.deleteCourseByCourseName(courseName);
        if(!isDeleted){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(true);
    }
}
