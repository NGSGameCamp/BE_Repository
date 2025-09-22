package com.imfine.ngs.community.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class CommunityBoard {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private Long gameId;

  @Column(nullable = false)
  private Long managerId;

  @Column(nullable = false)
  private String title;
  private String description;

  @Column(nullable = false)
  private Boolean isDeleted;

  @CreationTimestamp
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;

  @Builder
  public CommunityBoard(Long gameId, Long managerId, String title, String description, Boolean isDeleted) {
    this.gameId = gameId;
    this.managerId = managerId;
    this.title = title;

    if (isDeleted == null)
      this.isDeleted = false;
    else
      this.isDeleted = isDeleted;

    if (description == null || description.isEmpty())
      this.description = title + "에 관한 게시판입니다.";
    else
      this.description = description;
  }

  @Builder(builderMethodName = "allBuilder", builderClassName = "AllBuilder")
  public CommunityBoard(Long id, Long gameId, Long managerId, String title, String description, Boolean isDeleted) {
    this(gameId, managerId, title, description, isDeleted);

    this.id = id;
  }

  public void updateManagerId(Long managerId) { this.managerId = managerId; }
  public void updateTitle(String title) { this.title = title; }
  public void updateDescription(String description) { this.description = description; }
  public void updateIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof CommunityBoard board)) return false;
    return Objects.equals(getId(), board.getId()) && Objects.equals(getGameId(), board.getGameId()) && Objects.equals(getManagerId(), board.getManagerId()) && Objects.equals(getTitle(), board.getTitle()) && Objects.equals(getIsDeleted(), board.getIsDeleted());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getGameId(), getManagerId(), getTitle(), getIsDeleted());
  }
}
