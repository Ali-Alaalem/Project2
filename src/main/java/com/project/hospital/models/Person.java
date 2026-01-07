package com.project.hospital.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Person")
public class Person {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long personId;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private Long contactNumber;

    @Column
    private String address;

    @Column
    private String nationality;

    @Column
    private Character gender;

    @Column
    private Integer age;

    @Column
    private Long cpr;

    @Column
    private String photo;

    @Override
    public String toString() {
        return "Person{" +
                "personId=" + personId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", contactNumber=" + contactNumber +
                ", address='" + address + '\'' +
                ", nationality='" + nationality + '\'' +
                ", gender=" + gender +
                ", age=" + age +
                ", cpr=" + cpr +
                ", photo='" + photo + '\'' +
                '}';
    }

    @JsonIgnore
    @OneToOne(mappedBy = "person")
    private User user;
}
