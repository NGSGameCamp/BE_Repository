package com.imfine.ngs.community.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name = "community_comment")
public class CommunityComment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long postId;

  @Column(nullable = false)
  private Long authorId;

  private Long parentId;

  @Column(nullable = false)
  private String content;

  @CreationTimestamp
  private LocalDateTime createdAt;
  @UpdateTimestamp
  private LocalDateTime updatedAt;

  @Builder
  public CommunityComment(Long postId, Long authorId, Long parentId, String content) {
    if (parentId == null) { this.parentId = -1L; }

    this.postId = postId;
    this.authorId = authorId;
    this.parentId = parentId;
    this.content = content;
  }

  @Builder(builderMethodName = "allBuilder", builderClassName = "AllBuilder")
  public CommunityComment(Long id, Long postId, Long authorId, Long parentId, String content) {
    this(postId, authorId, parentId, content);

    this.id = id;
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
