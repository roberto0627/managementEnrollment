package rv.development.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import lombok.*;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

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
    private LocalDate birthDate;


    @NotNull
    @Email
    @Column (length = 120)
    private  String email;

    private Boolean activated = false;

   @Override
   public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Student student = (Student) o;
    return id == student.id && Objects.equals(docType, student.docType) && Objects.equals(docNumber, student.docNumber) && Objects.equals(firstName, student.firstName) && Objects.equals(lastName, student.lastName) && Objects.equals(birthDate, student.birthDate) && Objects.equals(email, student.email) && Objects.equals(activated, student.activated);
   }

   @Override
   public int hashCode() {
    return Objects.hash(id, docType, docNumber, firstName, lastName, birthDate, email, activated);
   }
}

