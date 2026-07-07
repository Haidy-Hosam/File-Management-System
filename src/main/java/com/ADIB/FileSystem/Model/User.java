package com.ADIB.FileSystem.Model;

import com.ADIB.FileSystem.Enum.USER_ROLE;
import com.ADIB.FileSystem.Enum.USER_STATUS;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long u_id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String password;

    private USER_STATUS status;
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToMany
    @JoinTable(
            name="user_roles",
            joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Roles> roles;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created by")
    private User created_by;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated by")
    private User updated_by;
    @CreationTimestamp
    private LocalDateTime created_at;
    @CreationTimestamp
    private LocalDateTime updated_at;
    @OneToMany (mappedBy = "user",cascade =CascadeType.ALL )
    private List<ActivityLog> fileActivity;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<FileAccess> fileAccesses ;

}


