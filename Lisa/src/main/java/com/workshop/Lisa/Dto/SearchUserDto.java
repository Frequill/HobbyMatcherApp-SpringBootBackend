package com.workshop.Lisa.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchUserDto {
        private String username;
        private String description;
        private String gender;
        private String birthDate;
        private SearchPreferenceDto preferences;
        private String region;
        private String matchPercentage;
}
