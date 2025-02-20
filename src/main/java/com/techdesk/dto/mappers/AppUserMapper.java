package com.techdesk.dto.mappers;


import com.techdesk.dto.AuthResponseDTO;
import com.techdesk.dto.RegisterRequestDTO;
import com.techdesk.entities.AppUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AppUserMapper {
    AppUser toEntity(RegisterRequestDTO dto);
    AuthResponseDTO toResponseDto(AppUser appUser);
}
