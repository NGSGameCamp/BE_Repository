package com.imfine.ngs.community.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 필수 요소: postId, authorId, content
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CommunityComment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long postId;

  @Column(nullable = false)
  @Setter
  private Long authorId;

  @Column(nullable = false, columnDefinition = "number default -1")
  private Long parentId;

  @Column(nullable = false)
  private String content;

  @Column(nullable = false)
  @Setter
  private Boolean isDeleted;

  @CreationTimestamp
  private LocalDateTime createdAt;
  @UpdateTimestamp
  private LocalDateTime updatedAt;

  @Builder
  public CommunityComment(Long postId, Long authorId, Long parentId, String content) {
    this.postId = postId;
    this.authorId = authorId;
    this.content = content;

    if (parentId == null) this.parentId = -1L;
    else this.parentId = parentId;

    isDeleted = false;
  }

  @Builder(builderMethodName = "allBuilder", builderClassName = "AllBuilder")
  public CommunityComment(Long postId, Long authorId, Long parentId, String content, Boolean isDeleted) {
    this(postId, authorId, parentId, content);

    this.isDeleted = isDeleted;
  }

  public void updateContent(String content) { this.content = content; }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof CommunityComment comment)) return false;
    return Objects.equals(id, comment.id) && Objects.equals(postId, comment.postId) && Objects.equals(authorId, comment.authorId) && Objects.equals(parentId, comment.parentId) && Objects.equals(content, comment.content);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, postId, authorId, parentId, content);
  }
}
