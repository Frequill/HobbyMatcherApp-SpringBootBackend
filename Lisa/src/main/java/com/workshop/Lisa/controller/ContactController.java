package com.workshop.Lisa.controller;

import com.google.gson.Gson;
import com.workshop.Lisa.Dto.ContactRequestDto;
import com.workshop.Lisa.Dto.StatusUpdateDto;
import com.workshop.Lisa.config.JwtUtils;
import com.workshop.Lisa.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService service;
    private final JwtUtils jwtHelper;

    @GetMapping("/getContacts")
    @PreAuthorize("hasAnyAuthority('USER')")
    public String getContacts(@RequestHeader("Authorization") String token){
        token = token.substring(7);
        String username = jwtHelper.extractUsername(token);
        Gson gson = new Gson();
        return gson.toJson(service.getFriendsInfo(username));
    }

    @PutMapping("/updateStatus")
    @PreAuthorize("hasAnyAuthority('USER')")
    public String updateStatus(@RequestHeader("Authorization") String token, @RequestBody StatusUpdateDto dto){
        token = token.substring(7);
        String username = jwtHelper.extractUsername(token);
        return service.updateStatus(username, dto);
    }

    @DeleteMapping("/unblock")
    @PreAuthorize("hasAnyAuthority('USER')")
    public String unblockUser(@RequestHeader("Authorization") String token, @RequestBody String id) {
        token = token.substring(7);
        String username = jwtHelper.extractUsername(token);
        return service.deleteEntry(username, id);
    }

    @PostMapping("/sendFriendRequest")
    @PreAuthorize("hasAnyAuthority('USER')")
    public String sendFriendRequest(@RequestHeader("Authorization") String token, @RequestBody ContactRequestDto contactRequestDto) {
        token = token.substring(7);
        String username = jwtHelper.extractUsername(token);
        return service.createFriendRequest(username, contactRequestDto);
    }

}
