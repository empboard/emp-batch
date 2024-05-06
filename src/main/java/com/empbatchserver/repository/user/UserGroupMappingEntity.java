package com.empbatchserver.repository.user;

import com.empbatchserver.repository.BaseEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Getter @ToString @Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "user_group_mapping")
@IdClass(UserGroupMappingId.class)
public class UserGroupMappingEntity extends BaseEntity {
    @Id
    private String userGroupId;

    @Id
    private String userId;

    private String userGroupName;
    private String description;
}
