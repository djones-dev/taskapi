package dev.danieljones.taskapi.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import dev.danieljones.taskapi.model.Task;
import dev.danieljones.taskapi.model.Status;
import dev.danieljones.taskapi.model.Priority;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    List<Task> findByUserId(Long userId);
    
    List<Task> findByUserIdAndStatus(Long userId, Status status);
    
    List<Task> findByUserIdAndPriority(Long userId, Priority priority);
    
    List<Task> findByUserIdAndStatusAndPriority(Long userId, Status status, Priority priority);
    
    Optional<Task> findByIdAndUserId(Long taskId, Long userId);
    
    List<Task> findByUserIdOrderByDueDateAsc(Long userId);
    
    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.dueDate < :today AND t.status != 'COMPLETED'")
    List<Task> findOverdueTasks(@Param("userId") Long userId, @Param("today") LocalDate today);
    
    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.dueDate BETWEEN :startDate AND :endDate")
    List<Task> findTasksDueBetween(
        @Param("userId") Long userId, 
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate
    );
    
    // Count queries
    long countByUserIdAndStatus(Long userId, Status status);
}