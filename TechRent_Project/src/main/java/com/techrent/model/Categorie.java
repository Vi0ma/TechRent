package com.techrent.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
public class Categorie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String libelle;


    @OneToMany(mappedBy = "categorie", cascade = CascadeType.PERSIST)
    @ToString.Exclude
    private List<Materiel> materiels = new ArrayList<>();


    @Override
    public String toString() {
        return this.libelle;
    }
}