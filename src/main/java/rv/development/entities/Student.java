package rv.development.entities;

        import jakarta.persistence.Entity;
        import jakarta.persistence.Id;
        import jakarta.persistence.Table;
        import lombok.AllArgsConstructor;
        import lombok.Data;
        import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.boot.context.properties.bind.Name;

import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

@Entity
@Table(name = "student")
public class Student {
   @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private  long id;

    @NotNull
    @Column (name ="doc_type" , length = 50)
    private String docType;

    @NotNull
    @Column(name ="doc_number" , length = 10)
    private  String docNumber;

    @NotNull
    @Column (name ="first_name" , length = 50)
    private  String firstName;

   @NotNull
   @Column (name ="last_name" , length = 50)
   private  String lastName;

    @Column (name ="birth_date")
    private  LocalDate birthDate;

    @NotNull
    @Email
    @Column (length = 120)
    private  String email;

    private Boolean activated = false;


}

