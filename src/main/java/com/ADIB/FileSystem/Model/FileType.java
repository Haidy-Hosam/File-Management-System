package com.ADIB.FileSystem.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileType extends Audit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;

    @OneToMany(mappedBy = "fileType")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<File> files;
}
