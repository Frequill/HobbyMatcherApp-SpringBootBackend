package com.workshop.Lisa.service;

import com.workshop.Lisa.Dao.RegionDao;
import com.workshop.Lisa.Dao.UserDao;
import com.workshop.Lisa.Dto.AuthRequestEmail;
import com.workshop.Lisa.Dto.AuthenticationRequest;
import com.workshop.Lisa.Dto.UserRegisterDto;
import com.workshop.Lisa.Entity.*;
import com.workshop.Lisa.Utils.DateConverter;
import com.workshop.Lisa.Utils.GenderEnum;
import com.workshop.Lisa.config.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager manager;
    private final JpaUserDetailsService jpaUDS;
    private final ContactInformationService contactInformationService;
    private final PreferenceService preferenceService;
    private final JwtUtils jwtU;
    private final UserDao userDao;
    private final RegionDao regionDao;

    public String generateToken(AuthenticationRequest req){
        manager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUserName(), req.getUserPassword())
        );
        final UserDetails userDetails = jpaUDS.loadUserByUsername(req.getUserName());
        if(userDetails != null){
            return jwtU.generateToken(userDetails);
        }
        return "400";
    }

    public String generateTokenByEmail(AuthRequestEmail req){
        User user = userDao.findByContactInformationUserEmail(req.getUserEmail()).orElseThrow(() -> new EntityNotFoundException("No user found!"));
        manager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUserName(), req.getUserPassword())
        );
        final UserDetails userDetails = jpaUDS.loadUserByEmail(req.getUserEmail());
        if(userDetails != null){
            return jwtU.generateToken(userDetails);
        }
        return "400";
    }

    public String registerAndLogin(UserRegisterDto userRegisterDto) {
        String password = new BCryptPasswordEncoder().encode(userRegisterDto.getUserPassword());
        GenderEnum gender = GenderEnum.valueOf(userRegisterDto.getGender());
        ContactInformation contactInfo = this.contactInformationService.createContactInfo( new ContactInformation(
                null,
                userRegisterDto.getUserEmail(),
                "",
                "",
                "",
                "",
                ""));

        Set<Hobby> hobbies= new HashSet<Hobby>();
        Set<Region> regions= new HashSet<Region>();
        Preference pref = new Preference(contactInfo.getUserID(), "18", "100", "Samtliga", regions ,hobbies);
        this.preferenceService.createPreference(pref);

        User user = new User(
                contactInfo.getUserID(),
                userRegisterDto.getUserName(),
                "",
                userRegisterDto.getUserFirstname(),
                userRegisterDto.getUserLastName(),
                password,
                "USER",
                gender,
                userRegisterDto.getBirthDate(),
                contactInfo,
                pref,
                regionDao.findByName(userRegisterDto.getRegion()).orElseThrow(() -> new EntityNotFoundException("Could not find region")));

        //create new user
        System.out.println("user: "+user);
        try{

            userDao.save(user);
        }catch(Exception error){
            return error.getMessage();
           // return "Användarnamnet eller emejl finns redan!";
        }
        return this.generateToken(new AuthenticationRequest(userRegisterDto.getUserName(), userRegisterDto.getUserPassword()));
    }

}
