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
@Table(name = "plans")
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    private Integer planId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @Column(name = "name", length = 64)
    private String name;

    @Column(name = "required_amount")
    private BigDecimal requiredAmount;

    @ManyToOne
    @JoinColumn(name = "currency_id", referencedColumnName = "currency_id")
    private Currency currency;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "subplans_completed")
    private Double subplansCompleted;

    @Column(name = "description", length = 512)
    private String description;

    @Column(name = "completed")
    private Boolean completed;
}
