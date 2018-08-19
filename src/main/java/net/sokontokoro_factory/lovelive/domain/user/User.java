package net.sokontokoro_factory.lovelive.domain.user;

import java.util.List;
import java.util.Optional;
import javax.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.sokontokoro_factory.lovelive.domain.score.Score;

@Entity
@Table(name = "USER")
@Getter
public class User {
  @Id private Long id;

  @Setter
  @Column(name = "NAME", nullable = false)
  private String name;

  @Column(name = "CREATE_DATE", nullable = false)
  private Long createDate;

  @Column(name = "UPDATE_DATE", nullable = true)
  private Long updateDate;

  @Setter
  @Column(name = "FAVORITE", nullable = true)
  @Enumerated(EnumType.STRING)
  private FavoriteType favorite;

  @Column(name = "DELETED", nullable = false)
  private boolean deleted;

  @Column(name = "ADMIN", nullable = false)
  private boolean admin;

  /** *********************************** relation */
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
  private List<Score> scores;

  protected User() {}

  public User(@NonNull Long id, @NonNull String name) {
    this.id = id;
    this.name = name;

    this.admin = false;
    this.deleted = false;
  }

  public static Optional<User> get(UserRepository repo, long id) {
    Optional<User> user = repo.findById(id);

    if (!user.isPresent()) {
      return Optional.empty();
    }

    if (user.get().isDeleted()) {
      return Optional.empty();
    }

    return user;
  }

  public static User create(UserRepository repo, @NonNull Long id, @NonNull String name) {
    Optional<User> user = repo.findById(id);

    if (!user.isPresent()) {
      User newUser = new User(id, name);
      return repo.save(newUser);
    }

    if (user.get().deleted) {
      user.get().setName(name);
      user.get().deleted = false;
    }

    throw new IllegalStateException("Provided ID is registered and not deleted.");
  }

  public void delete() {
    this.deleted = true;
  }

  @PrePersist
  protected void prePersist() {
    this.createDate = System.currentTimeMillis();
  }

  @PreUpdate
  protected void preUpdate() {
    this.updateDate = System.currentTimeMillis();
  }
}
