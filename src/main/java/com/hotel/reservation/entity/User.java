package com.hotel.reservation.entity;


import com.hotel.reservation.entity.enums.Gender;
import com.hotel.reservation.entity.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "user_tbl")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseClass implements UserDetails, Serializable {

    @NotBlank(message = "First Name is required")
    @Size(min = 3, max = 50, message = "First name must be between 3 and 50 Characters")
    private String firstName;

    @NotBlank(message = "Last Name is required")
    @Size(min = 3, max = 50, message = "Last name must be between 3 and 50 Characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid Email Address")
    private String email;

    @NotBlank(message = "Phone Number is required")
    @Digits(message="Number should contain 11 digits.", fraction = 0, integer = 11)
    @Pattern(regexp = "\\d{11}", message = "Phone number must be 11 digits")
    private String phoneNumber;

    @NotBlank(message = "Address is required")
    @Size(min = 5, max = 100, message = "Address must be between 5 and 50 Characters")
    private String address;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private int loyaltyPoints;

    private double discountPercentage;

    @Enumerated(EnumType.STRING)
    private Role role;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
            message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, one special character, and no spaces"
    )
    private String password;

    @Column(nullable = false)
    private boolean isActive = false;

    @Column
    private String resetToken;

    @Column
    private LocalDateTime resetTokenExpiry;

    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;

    @Column(name = "last_logout_time")
    private LocalDateTime lastLogoutTime;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
