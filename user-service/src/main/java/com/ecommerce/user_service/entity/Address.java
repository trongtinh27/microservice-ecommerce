package com.ecommerce.user_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbl_address")
public class Address extends AbstractEntity<Long> {

    @Column(name = "apartment_number")
    private String apartmentNumber;

    @Column(name = "floor")
    private String floor;

    @Column(name = "building")
    private String building;

    @Column(name = "street_number")
    @NonNull
    private String streetNumber;

    @Column(name = "street")
    @NonNull
    private String street;

    @NonNull
    @Column(name = "city")
    private String city;

    @Column(name = "country")
    @NonNull
    private String country;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "address_type")
    private Integer addressType;
}