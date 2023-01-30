package com.workshop.Lisa.service;

import com.workshop.Lisa.Dao.UserDao;
import com.workshop.Lisa.Dto.UpdatePreferenceDto;
import com.workshop.Lisa.Dto.UpdateUserDto;
import com.workshop.Lisa.Dto.UpdateUserInformationDto;
import com.workshop.Lisa.Entity.*;
import com.workshop.Lisa.Utils.GenderEnum;
import com.workshop.Lisa.Utils.HobbyEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDao dao;
    private final ContactInformationService contactInformationService;

    public User findUserByUsername(String userName){
        return this.dao.findByUserName(userName).orElseThrow(() -> new EntityNotFoundException("Could not cast Optional into User"));
    }

    public User updateUser(UpdateUserDto updateUserDto, String username){
        User existingUser = this.dao.findByUserName(username)
                .orElseThrow(() -> new EntityNotFoundException("Could not cast Optional into User"));
        long userId = existingUser.getUserId();
        ContactInformation ci = contactInformationService.getContactInformation(userId);
        // Maybe add a try catch to check if CI is null.
        ci.setUserEmail(updateUserDto.getUserEmail());
        ci.setDiscord(updateUserDto.getDiscord());
        ci.setFacebook(updateUserDto.getFacebook());
        ci.setSnapchat(updateUserDto.getSnapchat());
        ci.setInstagram(updateUserDto.getInstagram());
        ci.setUserPhoneNumber(updateUserDto.getUserPhoneNumber());

        Preference pref = preferenceService.getPrefById(userId);
        pref.setGender(updateUserDto.getPreferedGender());
        pref.setMaxAge(updateUserDto.getMaxAge());
        pref.setMinAge(updateUserDto.getMinAge());

        pref.setHobbies(stringArrayToSet(updateUserDto.getHobbies(), "HOBBY"));
        pref.setRegion(stringArrayToSet(updateUserDto.getRegions(), "REGION"));

        existingUser.setPreferences(pref);
        existingUser.setContactInformation(ci);
        existingUser.setUserFirstname(updateUserDto.getUserFirstname());
        existingUser.setUserLastName(updateUserDto.getUserLastName());
        existingUser.setUserPassword(new BCryptPasswordEncoder().encode(updateUserDto.getUserPassword()));
        existingUser.setBirthDate(Date.valueOf(updateUserDto.getBirthDate()));
        existingUser.setGender(GenderEnum.valueOf(updateUserDto.getGender()));

        return this.dao.save(existingUser);
    }

    public User findById(Long id){
        return this.dao.findById(id).orElseThrow(() -> new EntityNotFoundException("Could not find user with that id!"));
    }

    public User getUserById(String userId){
        long id = Long.parseLong(userId);
        User user = this.findById(id);
        //there will be a better way but to lazy to write a new class
        user.setUserPassword("");
        user.setRoles("");
        //maybe add check if they are friends show contactInfo
        user.setContactInformation(new ContactInformation());
        return user;
    }

    public String updateUserPreference(UpdatePreferenceDto dto, String username){
        User existingUser = this.dao.findByUserName(username)
                .orElseThrow(() -> new EntityNotFoundException("Could not cast Optional into User"));
        long userId = existingUser.getUserId();

        Preference pref = preferenceService.getPrefById(userId);
        pref.setGender(dto.getPreferedGender());
        pref.setMaxAge(dto.getMaxAge());
        pref.setMinAge(dto.getMinAge());

        pref.setHobbies(stringArrayToSet(dto.getHobbies(), "HOBBY"));
        pref.setRegion(stringArrayToSet(dto.getRegions(), "REGION"));

        existingUser.setPreferences(pref);
        this.dao.save(existingUser);

        return "update was successful";
    }

    public String updateUserInformation(UpdateUserInformationDto updateUserInformationDto, String username) {
        User existingUser = this.dao.findByUserName(username)
                .orElseThrow(() -> new EntityNotFoundException("Could not cast Optional into User"));

        long userId = existingUser.getUserId();
        ContactInformation ci = contactInformationService.getContactInformation(userId);
        // Maybe add a try catch to check if CI is null.
        ci.setUserEmail(updateUserInformationDto.getUserEmail());
        ci.setDiscord(updateUserInformationDto.getDiscord());
        ci.setFacebook(updateUserInformationDto.getFacebook());
        ci.setSnapchat(updateUserInformationDto.getSnapchat());
        ci.setInstagram(updateUserInformationDto.getInstagram());
        ci.setUserPhoneNumber(updateUserInformationDto.getUserPhoneNumber());


        existingUser.setContactInformation(ci);
        existingUser.setUserFirstname(updateUserInformationDto.getUserFirstname());
        existingUser.setUserLastName(updateUserInformationDto.getUserLastName());
        existingUser.setUserPassword(new BCryptPasswordEncoder().encode(updateUserInformationDto.getUserPassword()));
        existingUser.setBirthDate(Date.valueOf(updateUserInformationDto.getBirthDate()));
        existingUser.setGender(GenderEnum.valueOf(updateUserInformationDto.getGender()));

        this.dao.save(existingUser);

        return "update was successful!";
    }

    public HashSet stringArrayToSet(String[] array, String type){
        if(type.equals("HOBBY")){
            HashSet<Hobby> prefSet = new HashSet<>();
            for(String strHobby : array) {
                Hobby hobby = hobbyService.findByHobby(HobbyEnum.valueOf(strHobby));
                prefSet.add(hobby);
            }
            return prefSet;
        }
        if(type.equals("REGION")){
            HashSet<Region> prefSet = new HashSet<>();
            for(String strRegion : array) {
                Region region = regionService.findRegionByName(strRegion);
                prefSet.add(region);
            }
            return prefSet;
        }

        return new HashSet();

    }

   //fix a method for updatePreferences and update UserInformation to abstract those two things


}
