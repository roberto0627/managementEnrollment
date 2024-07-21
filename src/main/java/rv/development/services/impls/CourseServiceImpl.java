package rv.development.services.impls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rv.development.entities.Course;
import rv.development.repositories.CourseRepository;
import rv.development.services.CourseService;
import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    CourseRepository courseRepository;

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
        try {
            if(existsId){
                courseRepository.deleteById(id);
                isDeleted = true;
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        return isDeleted;
    }

    public Boolean deleteCourseByCourseName(String courseName) {
        boolean isDeleted  = false;
        boolean existsName=courseRepository.existsByCourseName(courseName);
        try {
            if(existsName){
                courseRepository.deleteByCourseName(courseName);
                isDeleted = true;
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        return isDeleted;
    }
}
