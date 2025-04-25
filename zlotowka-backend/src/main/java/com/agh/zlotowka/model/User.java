package com.agh.zlotowka.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "first_name", length = 64)
    private String firstName;

    @Column(name = "last_name", length = 256)
    private String lastName;

    @Column(name = "email", length = 32, nullable = false)
    private String email;

    @Column(name = "phone_number", length = 64)
    private String phoneNumber;

    @Column(name = "password")
    private String password;

    @Column(name = "date_of_joining")
    private LocalDate dateOfJoining;

    @Column(name = "current_budget")
    private BigDecimal currentBudget;

    @ManyToOne
    @JoinColumn(name = "currency_id", referencedColumnName = "currency_id")
    private Currency currency;

    @Column(name = "dark_mode")
    private Boolean darkMode;

    @Column(name = "notifications_by_email")
    private Boolean notificationsByEmail;

    @Column(name = "notifications_by_phone")
    private Boolean notificationsByPhone;
}
