package com.ADIB.FileSystem.Model;

import com.ADIB.FileSystem.Enum.USER_ROLE;
import com.ADIB.FileSystem.Enum.USER_STATUS;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
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

    public Long getU_id() {
        return u_id;
    }

    public void setU_id(Long u_id) {
        this.u_id = u_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public USER_STATUS getStatus() {
        return status;
    }

    public void setStatus(USER_STATUS status) {
        this.status = status;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Set<Roles> getRoles() {
        return roles;
    }

    public void setRoles(Set<Roles> roles) {
        this.roles = roles;
    }

    public User getCreated_by() {
        return created_by;
    }

    public void setCreated_by(User created_by) {
        this.created_by = created_by;
    }

    public User getUpdated_by() {
        return updated_by;
    }

    public void setUpdated_by(User updated_by) {
        this.updated_by = updated_by;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }

    public List<ActivityLog> getFileActivity() {
        return fileActivity;
    }

    public void setFileActivity(List<ActivityLog> fileActivity) {
        this.fileActivity = fileActivity;
    }

    public Set<FileAccess> getFileAccesses() {
        return fileAccesses;
    }

    public void setFileAccesses(Set<FileAccess> fileAccesses) {
        this.fileAccesses = fileAccesses;
    }
}


