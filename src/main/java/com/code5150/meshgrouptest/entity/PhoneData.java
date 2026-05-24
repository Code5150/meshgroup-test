package com.code5150.meshgrouptest.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "phone_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhoneData {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "phone_data_id_gen")
    @SequenceGenerator(name = "phone_data_id_gen", sequenceName = "phone_data_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "phone", length = 13, unique = true, nullable = false)
    private String phone;
}
