package com.ADIB.FileSystem.Model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data // (to getter and setter)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Department extends AuditEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long d_id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private Boolean isActive;

    @OneToMany(mappedBy = "d_id",cascade =CascadeType.ALL)
    private List<File> files;

}
