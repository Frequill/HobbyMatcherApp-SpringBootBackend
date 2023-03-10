package com.workshop.Lisa.controller;

import com.workshop.Lisa.Dto.LoginDto;
import com.workshop.Lisa.Dto.UserRegisterDto;
import com.workshop.Lisa.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/authUser")
    public ResponseEntity<String> authenticateUser(@RequestBody LoginDto loginDto){

        String token = service.generateToken(loginDto);
        if(!token.equals("400")){
            return ResponseEntity.ok(token);
        }else{
            return ResponseEntity.status(400).body("An error occurred");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRegisterDto dto){
        String token = service.registerAndLogin(dto);
        if(!token.equals("400")){
            return ResponseEntity.ok(token);
        }else{
            return ResponseEntity.status(400).body("Something went wrong!");
        }
    }
}
