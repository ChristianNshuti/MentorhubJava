package com.mentorhub.student.repository;

import com.mentorhub.student.entity.LearningGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LearningGoalRepository extends JpaRepository<LearningGoal, Long> {

    @Query("""
            SELECT DISTINCT g FROM LearningGoal g
            LEFT JOIN FETCH g.milestones
            WHERE g.student.id = :studentId
            ORDER BY g.id
            """)
    List<LearningGoal> findByStudentIdWithMilestones(@Param("studentId") Long studentId);
}
