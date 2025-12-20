package com.omsoft.retail.user.entiry;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.omsoft.retail.user.entiry.type.AddressType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@ToString(exclude = "user")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String country;
    private String postalCode;

    @Enumerated(EnumType.STRING)
    private AddressType addressType;

    private boolean isDefault;
}
