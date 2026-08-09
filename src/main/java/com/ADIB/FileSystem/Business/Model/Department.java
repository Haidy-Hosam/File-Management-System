package com.ADIB.FileSystem.Business.Model;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Department extends Audit{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "d_id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private Boolean isActive;

    @OneToMany(mappedBy = "department")
    private List<User> users;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SLevel_id")
    private SecurityLevel securityLevels;

}
