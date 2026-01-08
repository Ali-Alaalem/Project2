package com.project.hospital.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "treatmentType")
public class TreatmentType {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String type;

    @OneToOne(mappedBy = "roomTreatmentType",fetch = FetchType.LAZY)
    private Room room;

    @OneToMany(mappedBy = "userTreatmentType",fetch = FetchType.LAZY)
    private List<User> user;

    @Column
    @CreationTimestamp
    private LocalDateTime createdDate;


    @Column
    @UpdateTimestamp
    private LocalDateTime updatedDate;
}
