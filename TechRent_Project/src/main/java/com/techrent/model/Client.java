package com.techrent.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, length = 100)
    private String prenom;


    @Column(length = 100)
    private String societe;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 20)
    private String telephone;


    @Column(length = 100)
    private String ville;


    @Column(length = 20)
    private String statut = "ACTIF";

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Location> locations = new ArrayList<>();

    public String getNomComplet() {
        if (prenom == null || nom == null) return "";
        return prenom + " " + nom.toUpperCase();
    }

    public void addLocation(Location location) {
        locations.add(location);
        location.setClient(this);
    }
}