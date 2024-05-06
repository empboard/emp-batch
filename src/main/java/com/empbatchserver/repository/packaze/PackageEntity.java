package com.empbatchserver.repository.packaze;

import com.empbatchserver.repository.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter @Setter @ToString
@Entity
@Table(name = "package")
public class PackageEntity extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer packageSeq;

    @Column(nullable = false)
    private String packageName;

    @Column(nullable = false)
    private Integer count;

    @Column(nullable = false)
    private Integer period;
}
