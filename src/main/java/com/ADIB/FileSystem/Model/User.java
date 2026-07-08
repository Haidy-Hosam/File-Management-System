package com.ADIB.FileSystem.Model;


import jakarta.persistence.*;
import lombok.*;

@Data
@Entity(name = "users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends Audit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long u_id;
    private String name;
    private String email;
    private String password;
    private String username;

}


