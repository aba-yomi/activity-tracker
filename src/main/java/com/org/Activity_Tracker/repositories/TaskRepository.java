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
        WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :text, '%'))
           OR LOWER(t.description) LIKE LOWER(CONCAT('%', :text, '%'))
        """)
    List<Task> searchByTitleOrDescription(@Param("text") String text);

//    @Query(value= "SELECT * FROM tasks WHERE title LIKE %:question% OR description LIKE %:question%")
//    Optional<List<Task>> findTaskBySearch(String question);



}
