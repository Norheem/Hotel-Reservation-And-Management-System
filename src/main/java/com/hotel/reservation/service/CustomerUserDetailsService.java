package com.hotel.reservation.service;

import com.hotel.reservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerUserDetailsService implements UserDetailsService {

    public final UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(()-> new UsernameNotFoundException(username + " Not Found"));
    }
}
