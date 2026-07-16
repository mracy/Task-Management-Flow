package com.taskmanagement.repository;

import com.taskmanagement.entity.Project;
import com.taskmanagement.enums.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    Page<Project> findByDeletedFalse(Pageable pageable);

    Page<Project> findByStatusAndDeletedFalse(ProjectStatus status, Pageable pageable);

    Page<Project> findByOwnerIdAndDeletedFalse(Long ownerId, Pageable pageable);

    @Query("SELECT p FROM Project p JOIN p.members m WHERE m.id = :userId AND p.deleted = false")
    Page<Project> findByMemberIdAndDeletedFalse(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT p FROM Project p WHERE p.deleted = false AND " +
            "(LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Project> searchProjects(@Param("query") String query, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Project p WHERE p.deleted = false")
    long countActiveProjects();

    @Query("SELECT COUNT(p) FROM Project p WHERE p.deleted = false AND p.status = :status")
    long countByStatus(@Param("status") ProjectStatus status);

    @Query("SELECT p FROM Project p JOIN p.members m WHERE m.id = :userId AND p.deleted = false")
    List<Project> findProjectsByMemberId(@Param("userId") Long userId);
}
