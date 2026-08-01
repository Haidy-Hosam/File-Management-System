package com.ADIB.FileSystem.Business.Model;

import com.ADIB.FileSystem.Business.Enum.FILE_STATUS;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "files")
public class File extends Audit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Long size;
    private String path;
    private String extension;

    @ManyToMany
    @JoinTable(
            name = "file_departments",
            joinColumns = @JoinColumn(name = "file_id"),
            inverseJoinColumns = @JoinColumn(name = "department_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Department> departments;

    @Enumerated(EnumType.STRING)
    private FILE_STATUS status;

    @ManyToOne
    @JoinColumn(name = "file_type_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private FileType fileType;

    @Column(nullable=false,columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isDeleted  ;
}
