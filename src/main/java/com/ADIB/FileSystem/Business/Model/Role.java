package com.ADIB.FileSystem.Business.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class Role extends Audit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "r_id")
    private Long id;

    private String name;

    @OneToMany(mappedBy = "role",cascade = CascadeType.ALL)
    private List<User> users;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RolePagePermission> rolePagePermissions = new java.util.ArrayList<>();

}