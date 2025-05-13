package com.example.tomatomall.po;

import com.example.tomatomall.enums.CircleEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class CircleMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Integer memberId;

    @Basic
    @Column(name = "circle_id")
    private Integer circleId;

    @Basic
    @Column(name = "account_id")
    private Integer accountId;

    @Basic
    @Column(name = "join_time")
    private LocalDateTime joinTime;

    @Basic
    @Column(name = "circle_role")
    @Enumerated(EnumType.STRING)
    private CircleEnum circleRole;
}
