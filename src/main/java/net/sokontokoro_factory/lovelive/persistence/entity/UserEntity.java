package net.sokontokoro_factory.lovelive.persistence.entity;

import lombok.Data;
import lombok.Getter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "USER")
@Data
public class UserEntity{

    public enum DELETED {
        TRUE('1'), FALSE('0');

        @Getter
        private char value;

        DELETED(char value) {
            this.value = value;
        }
    }

    public enum ADMIN {
        TRUE('1'), FALSE('0');

        @Getter
        private char value;

        ADMIN(char value) {
            this.value = value;
        }
    }

    @Id
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "CREATE_DATE", nullable = false)
    private Long createDate;

    @Column(name = "UPDATE_DATE", nullable = true)
    private Long updateDate;

    @Column(name = "FAVORITE_ID", nullable = true)
    private Integer favoriteId;

    @Column(name = "DELETED", nullable = false)
    private Character deleted;

    @Column(name = "ADMIN", nullable = false)
    private Character admin;

    /*************************************
     * relation
     */

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userEntity")
    private List<ScoreEntity> scoreEntities;
}
