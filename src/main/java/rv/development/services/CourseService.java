package rv.development.services;

import rv.development.entities.Course;
import java.util.List;

public interface CourseService {
    List<Course> showAllCourses(boolean activated);
    Course findByCourseName(String courseName);
    Course findByAcronymName(String acronymName);
    Course saveCourse(Course course);
    Course updateCourse(Course course);
    Boolean deleteCourseById(long id);
    Boolean deleteCourseByCourseName(String courseName);
}
