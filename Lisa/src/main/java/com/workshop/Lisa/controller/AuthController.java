package com.workshop.Lisa.controller;

import com.workshop.Lisa.Dto.AuthenticationRequest;
import com.workshop.Lisa.Dto.UserDto;
import com.workshop.Lisa.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/authUser")
    public ResponseEntity<String> authenticate(@RequestBody AuthenticationRequest request){
        String token = service.generateToken(request);
        if(!token.equals("400")){
            return ResponseEntity.ok(service.generateToken(request));
        }else{
            return ResponseEntity.status(400).body("An error accured");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserDto dto){
        String token = service.registerAndLogin(dto);
        if(!token.equals("400")){
            return ResponseEntity.ok(token);
        }else{
            return ResponseEntity.status(400).body("Something went wrong!");
        }
    }
}
