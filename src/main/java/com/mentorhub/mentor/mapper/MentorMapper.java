package com.mentorhub.mentor.mapper;

import com.mentorhub.mentor.dto.MentorProfileResponse;
import com.mentorhub.mentor.entity.MentorProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MentorMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.email", target = "email")
    @Mapping(target = "averageRating", ignore = true)
    MentorProfileResponse toResponse(MentorProfile profile);
}
