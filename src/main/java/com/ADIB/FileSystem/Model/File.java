package com.ADIB.FileSystem.Model;

import com.ADIB.FileSystem.Enum.FILE_STATUS;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "files")
public class File extends Audit{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Long size;
    private String path;
    private String extension;

//    @ManyToOne
//    @JoinColumn(name="department_id")
//    private Department department;

    @ManyToMany
    @JoinTable(
            name = "file_departments",
            joinColumns = @JoinColumn(name = "file_id"),
            inverseJoinColumns = @JoinColumn(name = "department_id")
    )
    private List<Department> departments;

    @Enumerated(EnumType.STRING)
    private FILE_STATUS status;

    @ManyToOne
    @JoinColumn(name = "file_type_id")
    private FileType fileType;


}
