package com.mentorhub.mentor.entity;

import com.mentorhub.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mentor_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private Integer experienceYears;

    private BigDecimal hourlyRate;

    @ElementCollection
    @CollectionTable(name = "mentor_expertise", joinColumns = @JoinColumn(name = "mentor_profile_id"))
    @Column(name = "expertise")
    @Builder.Default
    private List<String> expertise = new ArrayList<>();

    @Builder.Default
    private boolean verified = false;

    private String certificateUrl;
}
