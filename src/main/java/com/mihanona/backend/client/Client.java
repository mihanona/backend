package com.mihanona.backend.client;

import com.mihanona.backend.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "client")
public class Client extends BaseEntity {

    @Column(nullable = false)
    private UUID tenantId;

    @Column(nullable = false, length = 20)
    private String type = "individual"; // individual | company

    @Column(nullable = false, length = 150)
    private String fullName;

    private String companyName;

    @Column(nullable = false, length = 20)
    private String leadStatus = "lead"; // lead | active

    private String email;
    private String phone;
    private String address;
    private String city;

    @Column(nullable = false, length = 10)
    private String country = "MA";

    private String ice;
    private String ifNumber;

    @Column(nullable = false, length = 20)
    private String paymentTerms = "net_30";

    private String notes;

    @Column(nullable = false)
    private boolean isActive = true;

    private UUID createdBy;
}