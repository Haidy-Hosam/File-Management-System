package com.ADIB.FileSystem.Business.Model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Entity(name = "users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends Audit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "u_id")
    private Long id;
    private String name;
    private String email;
    private String password;
    private String username;
    private Boolean deleted = false;
    private LocalDateTime lastLogin;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "department_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Department department;



    @ManyToOne
    @JoinColumn(name = "previous_department_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Department previousDepartment;

}



