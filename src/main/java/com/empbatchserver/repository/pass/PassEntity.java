package com.empbatchserver.repository.pass;

import com.empbatchserver.repository.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter @Setter @ToString
@Entity
@Table(name = "pass")
public class PassEntity extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer passSeq;

    @Column(nullable = false)
    private Integer packageSeq;

    @Column(nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    private PassStatus status;

    @Column(nullable = false)
    private Integer remainingCount;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private LocalDateTime expiredAt;
}
