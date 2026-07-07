package com.ADIB.FileSystem.Model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "files")
public class File extends AuditEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String path;

    @Column(nullable = false)
    private String encryptionIV;

    @Column(nullable = false)
    private Long  size;

    @Column(nullable = false)
    private String fileExtension;
    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL)
    private Set<FileAccess> fileAccesses ;

    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL)
    private List<ActivityLog> activity ;
}
