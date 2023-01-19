package com.workshop.Lisa.service;

import com.workshop.Lisa.Dao.UserDao;
import com.workshop.Lisa.Dto.AuthenticationRequest;
import com.workshop.Lisa.Dto.UserDto;
import com.workshop.Lisa.Entity.User;
import com.workshop.Lisa.Utils.DateConverter;
import com.workshop.Lisa.Utils.GenderEnum;
import com.workshop.Lisa.config.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Date;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager manager;
    private final JpaUserDetailsService jpaUDS;
    private final JwtUtils jwtU;
    private final UserDao userDao;

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

    public String registerAndLogin(UserDto userDto) {
        String password = new BCryptPasswordEncoder().encode(userDto.getUserPassword());
        Date age = new DateConverter().getDate(userDto.getBirthDate());
        GenderEnum gender = GenderEnum.valueOf(userDto.getGender());
        User user = new User(
                null,
                userDto.getUserName(),
                userDto.getUserEmail(),
                userDto.getUserFirstname(),
                userDto.getUserLastName(),
                password,
                "USER",
                gender,
                age);

        //create new user

        try{
            userDao.save(user);
        }catch(Exception error){
            return "Användarnamnet eller emejl finns redan!";
        }
        return this.generateToken(new AuthenticationRequest(userDto.getUserName(), userDto.getUserPassword()));
    }

}
