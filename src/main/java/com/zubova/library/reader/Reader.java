package com.zubova.library.reader;

import com.zubova.library.common.persistence.AbstractPerson;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "readers")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reader extends AbstractPerson {

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, length = 16)
    private String phone;

    @Column(name = "registration_date", nullable = false)
    private Instant registrationDate;

}