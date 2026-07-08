package com.ADIB.FileSystem.Model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Department extends Audit{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long d_id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private Boolean isActive;


}
