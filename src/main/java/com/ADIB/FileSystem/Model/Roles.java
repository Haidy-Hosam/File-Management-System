package com.ADIB.FileSystem.Model;


import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity(name = "roles")
@NoArgsConstructor
@AllArgsConstructor
public class Roles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long r_id;
    private String name;
}
