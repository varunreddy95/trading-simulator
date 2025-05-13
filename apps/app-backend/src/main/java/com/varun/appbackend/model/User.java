package com.varun.appbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
* Represents a user in the trading simulator system.
* */


@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     *  Unique identifier for the user
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     *  Username used to login to display name
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * User's email address
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     *  Hashed password of the user
     */
    @Column(nullable = false)
    private String password;


}
