package com.techrent.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "materiels")
public class Materiel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String reference;

    @Column(nullable = false)
    private String nom;

    @Column(name = "chemin_image")
    private String cheminImage;


    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "categorie_id", nullable = false)
    @ToString.Exclude
    private Categorie categorie;

    @Column(name = "prix_par_jour", nullable = false)
    private Double prixParJour;

    @Column(nullable = false)
    private String etat;

    @OneToMany(mappedBy = "materiel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Location> historiqueLocations = new ArrayList<>();


}