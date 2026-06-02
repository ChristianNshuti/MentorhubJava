package com.mentorhub.file.service;

import com.mentorhub.auth.security.SecurityUtils;
import com.mentorhub.common.exception.BadRequestException;
import com.mentorhub.file.entity.LearningResource;
import com.mentorhub.file.repository.LearningResourceRepository;
import com.mentorhub.user.entity.Role;
import com.mentorhub.user.entity.User;
import com.mentorhub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    @Value("${mentorhub.upload.dir}")
    private String uploadDir;

    private final LearningResourceRepository resourceRepository;
    private final UserRepository userRepository;

    @Transactional
    public LearningResource upload(String title, String description, String resourceType,
                                   MultipartFile file, String externalLink) throws IOException {
        if (SecurityUtils.currentUser().getRole() != Role.MENTOR) {
            throw new BadRequestException("Only mentors can upload resources");
        }
        User mentor = userRepository.findById(SecurityUtils.currentUserId()).orElseThrow();
        String fileUrl = null;
        if (file != null && !file.isEmpty()) {
            Path dir = Paths.get(uploadDir);
            Files.createDirectories(dir);
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path target = dir.resolve(filename);
            Files.copy(file.getInputStream(), target);
            fileUrl = "/api/files/download/" + filename;
        }
        return resourceRepository.save(LearningResource.builder()
                .mentor(mentor)
                .title(title)
                .description(description)
                .resourceType(resourceType)
                .fileUrl(fileUrl)
                .externalLink(externalLink)
                .build());
    }

    public List<LearningResource> byMentor(Long mentorId) {
        return resourceRepository.findByMentorId(mentorId);
    }

    public byte[] load(String filename) throws IOException {
        return Files.readAllBytes(Paths.get(uploadDir).resolve(filename));
    }
}
