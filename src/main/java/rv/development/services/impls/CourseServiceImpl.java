package rv.development.services.impls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rv.development.entities.Course;
import rv.development.repositories.CourseRepository;
import rv.development.services.CourseService;
import java.util.List;
import java.util.Optional;

@Service
public class CourseServiceImpl implements CourseService {

    CourseRepository courseRepository;

    @Autowired
    CourseServiceImpl(CourseRepository courseRepository){
        this.courseRepository = courseRepository;
    }

    public List<Course> showAllCourses(boolean activated) {
        return courseRepository.findByActivated(activated);
    }

    public Course findByCourseName(String courseType) {
        return courseRepository.findByCourseName(courseType);
    }

    public Course findByAcronymName(String acronymName) {
        return courseRepository.findByAcronymName(acronymName);
    }

    public Course saveCourse(Course course) {
        boolean existsName = courseRepository.existsByCourseName(course.getCourseName());
        if (!existsName) {
            return courseRepository.save(course);
        }else{
            return null;
        }
    }

    public Course updateCourse(Course course) {
        boolean existsId = courseRepository.existsById(course.getId());
        if(course.getId() > 0 && existsId){
            return courseRepository.save(course);
        }
        return null;
    }

    public Boolean deleteCourseById(long id) {
        boolean isDeleted  = false;
        boolean existsId=courseRepository.existsById(id);
        if(existsId){
            Optional<Course> course = courseRepository.findById(id);
            if(course.isPresent()){
                course.get().setActivated(false);
                courseRepository.save(course.get());
                isDeleted = true;
            }
        }
        return isDeleted;
    }

    public Boolean deleteCourseByCourseName(String courseName) {
        boolean isDeleted  = false;
        boolean existsName=courseRepository.existsByCourseName(courseName);
        if(existsName){
            Course course = courseRepository.findByCourseName(courseName);
            if(course != null){
                course.setActivated(false);
                courseRepository.save(course);
                isDeleted = true;
            }
        }
        return isDeleted;
    }
}
