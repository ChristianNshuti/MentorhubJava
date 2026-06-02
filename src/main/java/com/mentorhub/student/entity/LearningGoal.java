package com.mentorhub.student.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mentorhub.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "learning_goals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnore
    private User student;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    private int progressPercent = 0;

    @OneToMany(mappedBy = "goal", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Milestone> milestones = new ArrayList<>();
}
