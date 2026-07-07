package com.ADIB.FileSystem.Model;
import com.ADIB.FileSystem.Enum.FILE_STATUS;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "code_status")
public class CodeStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private FILE_STATUS description;

    @OneToMany(mappedBy = "status")
    private List<File> files;
}
