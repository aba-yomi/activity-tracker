package com.org.Activity_Tracker.repositories;

import com.org.Activity_Tracker.entities.Task;
import com.org.Activity_Tracker.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByStatus(Status status);

    @Query("""
        SELECT t FROM Task t
        WHERE (LOWER(t.title) LIKE LOWER(CONCAT('%', :text, '%'))
           OR LOWER(t.description) LIKE LOWER(CONCAT('%', :text, '%')))
          AND t.user.id = :userId
        """)
    List<Task> searchByTitleOrDescriptionAndUserId(@Param("text") String text, @Param("userId") Long userId);

    @Query("""
        SELECT t FROM Task t
        WHERE t.user.id = :userId
          AND t.status = :status
        """)
    List<Task> findAllByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Status status);

    @Query("""
    SELECT t FROM Task t
    WHERE t.user.id = :userId
    """)
    List<Task> findAllByUserId(@Param("userId") Long userId);





}
