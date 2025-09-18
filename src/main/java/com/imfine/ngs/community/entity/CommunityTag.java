package com.imfine.ngs.community.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "community_board_tags")
public class CommunityTag {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof CommunityTag tags)) return false;
    return Objects.equals(id, tags.id) && Objects.equals(name, tags.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name);
  }
}
