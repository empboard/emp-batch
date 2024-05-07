package com.empbatchserver.repository.user;

import com.empbatchserver.repository.BaseEntity;
import com.empbatchserver.utils.JsonToMapConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter @Setter @ToString
@Entity
@Table(name = "user")
public class UserEntity extends BaseEntity {
    @Id
    private String userId;

    @Column(nullable = false, length = 50)
    private String userName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private UserStatus status;

    @Column(length = 50)
    private String phone;

    @Convert(converter = JsonToMapConverter.class)
    private Map<String, Object> meta;

    public String getUuid() {
        if (meta.containsKey("uuid")) {
            return String.valueOf(meta.get("uuid"));
        }

        return null;
    }
}
