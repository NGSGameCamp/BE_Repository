package com.imfine.ngs.community.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CommunityPost {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long boardId;

  @Column(nullable = false)
  @Setter
  private Long authorId;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String content;

  @Column(nullable = false)
  private Boolean isDeleted;

  @ManyToMany
  @JoinTable(
          name = "post_tag",
          joinColumns = @JoinColumn(name = "community_post_id"),
          inverseJoinColumns = @JoinColumn(name = "community_tag_id")
  )
  private List<CommunityTag> tags;

  @CreationTimestamp
  private LocalDateTime createdAt;
  @UpdateTimestamp
  private LocalDateTime updatedAt;

  @Builder
  public CommunityPost(Long boardId, Long authorId, String title, String content, List<CommunityTag> tags) {
    this.boardId = boardId;
    this.authorId = authorId;
    this.title = title;
    this.content = content;
    this.tags = tags;

    this.isDeleted = false;
  }

  @Builder(builderMethodName = "allBuilder", builderClassName = "AllBuilder")
  public CommunityPost(Long id, Long boardId, Long authorId, String title, String content, List<CommunityTag> tags, Boolean isDeleted) {
    this(boardId, authorId, title, content, tags);

    this.id = id;
    this.isDeleted = isDeleted;
  }

  public void updateTitle(String title) { this.title = title; }
  public void updateContent(String content) { this.content = content; }
  public void updateIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }
  public void updateTags(List<CommunityTag> tags) { this.tags = tags; }
  public void insertTags(List<CommunityTag> tags) { this.tags = tags; }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof CommunityPost post)) return false;
    return Objects.equals(id, post.id) && Objects.equals(boardId, post.boardId) && Objects.equals(authorId, post.authorId) && Objects.equals(title, post.title) && Objects.equals(content, post.content) && Objects.equals(isDeleted, post.isDeleted);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, boardId, authorId, title, content, isDeleted);
  }
}
