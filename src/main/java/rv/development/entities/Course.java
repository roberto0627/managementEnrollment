package rv.development.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  long id;

    @NotNull
    @Column(name ="course_name" , length = 50)
    private  String courseName = "";

    @NotNull
    @Column(name ="acronym_name" , length = 20)
    private  String acronymName;

    private Boolean activated = false;
}
