package com.imfine.ngs.community.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.imfine.ngs._global.config.security.jwt.JwtUserPrincipal;
import com.imfine.ngs.community.dto.request.CommunityPostCreateRequest;
import com.imfine.ngs.community.dto.request.CommunityPostUpdateRequest;
import com.imfine.ngs.community.dto.response.CommunityPostResponse;
import com.imfine.ngs.community.entity.CommunityPost;
import com.imfine.ngs.community.entity.CommunityTag;
import com.imfine.ngs.community.enums.SearchType;
import com.imfine.ngs.community.service.CommunityPostService;
import com.imfine.ngs.community.service.CommunityTagService;
import com.imfine.ngs.user.entity.User;
import com.imfine.ngs.user.entity.UserRole;
import com.imfine.ngs.user.entity.UserStatus;
import com.imfine.ngs.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class CommunityPostControllerTest {

  @Mock
  private CommunityPostService communityPostService;

  @Mock
  private CommunityTagService communityTagService;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private CommunityPostController communityPostController;

  private User mockUser;
  private JwtUserPrincipal principal;

  @BeforeEach
  void setUp() {
    principal = new JwtUserPrincipal(1L, "USER");
    mockUser = User.builder()
        .id(1L)
        .email("test@example.com")
        .pwd("pwd")
        .name("Tester")
        .nickname("Tester")
        .role(UserRole.builder().role("USER").description("User role").build())
        .status(UserStatus.builder().name("ACTIVE").description("Active user").build())
        .build();
  }

  @Test
  void createPost_returnsCreatedResponse() {
    CommunityPostCreateRequest request = new CommunityPostCreateRequest();
    request.setTitle("Title");
    request.setContent("Content");
    request.setTags(List.of("tag"));

    CommunityTag tag = CommunityTag.builder().id(1L).name("tag").build();
    CommunityPost createdPost = CommunityPost.allBuilder()
        .id(10L)
        .boardId(5L)
        .authorId(mockUser.getId())
        .title("Title")
        .content("Content")
        .tags(List.of(tag))
        .isDeleted(false)
        .build();

    when(userRepository.findById(principal.getUserId())).thenReturn(Optional.of(mockUser));
    when(communityTagService.getTagByName("tag")).thenReturn(tag);
    when(communityPostService.addPost(any(), any())).thenReturn(10L);
    when(communityPostService.getPostById(any(), eq(10L))).thenReturn(createdPost);

    ResponseEntity<CommunityPostResponse> response =
        communityPostController.createPost(5L, request, principal);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getId()).isEqualTo(10L);
    assertThat(response.getBody().getBoardId()).isEqualTo(5L);
    assertThat(response.getBody().getAuthor()).isNotNull();
    assertThat(response.getBody().getAuthor().getId()).isEqualTo(mockUser.getId());
    assertThat(response.getBody().getAuthor().getNickname()).isEqualTo(mockUser.getName());
    assertThat(response.getBody().getTags()).containsExactly("tag");
    verify(communityPostService).addPost(any(), any());
  }

  @Test
  void createPost_withoutPrincipal_throwsUnauthorized() {
    CommunityPostCreateRequest request = new CommunityPostCreateRequest();
    request.setTitle("Title");
    request.setContent("Content");

    assertThatThrownBy(() -> communityPostController.createPost(1L, request, null))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("인증이 필요합니다.");
  }

  @Test
  void getPost_returnsResponseForAnonymous() {
    CommunityTag tag = CommunityTag.builder().id(2L).name("tag").build();
    CommunityPost post = CommunityPost.allBuilder()
        .id(11L)
        .boardId(3L)
        .authorId(5L)
        .title("Sample")
        .content("Sample content")
        .tags(List.of(tag))
        .isDeleted(false)
        .build();

    User author = User.builder()
        .id(5L)
        .email("author@example.com")
        .pwd("pwd")
        .name("Author")
        .nickname("Author")
        .role(UserRole.builder().role("USER").description("User role").build())
        .status(UserStatus.builder().name("ACTIVE").description("Active user").build())
        .build();

    when(userRepository.findById(5L)).thenReturn(Optional.of(author));
    when(communityPostService.getPostById(any(), eq(11L))).thenReturn(post);

    ResponseEntity<CommunityPostResponse> response =
        communityPostController.getPost(11L, null);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getTitle()).isEqualTo("Sample");
    assertThat(response.getBody().getAuthor()).isNotNull();
    assertThat(response.getBody().getAuthor().getId()).isEqualTo(5L);
    assertThat(response.getBody().getTags()).containsExactly("tag");
  }

  @Test
  void updatePost_returnsUpdatedResponse() {
    CommunityPostUpdateRequest request = new CommunityPostUpdateRequest();
    request.setTitle("New title");
    request.setContent("New content");
    request.setTags(List.of("tag-1", "tag-2"));

    CommunityTag tag1 = CommunityTag.builder().id(31L).name("tag-1").build();
    CommunityTag tag2 = CommunityTag.builder().id(32L).name("tag-2").build();
    CommunityPost updatedPost = CommunityPost.allBuilder()
        .id(99L)
        .boardId(7L)
        .authorId(mockUser.getId())
        .title("New title")
        .content("New content")
        .tags(List.of(tag1, tag2))
        .isDeleted(false)
        .build();

    when(userRepository.findById(principal.getUserId())).thenReturn(Optional.of(mockUser));
    when(communityTagService.getTagByName("tag-1")).thenReturn(tag1);
    when(communityTagService.getTagByName("tag-2")).thenReturn(tag2);
    when(communityPostService.editPost(any(), eq(99L), any())).thenReturn(99L);
    when(communityPostService.getPostById(any(), eq(99L))).thenReturn(updatedPost);

    ResponseEntity<CommunityPostResponse> response =
        communityPostController.updatePost(99L, request, principal);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getContent()).isEqualTo("New content");
    assertThat(response.getBody().getAuthor().getId()).isEqualTo(mockUser.getId());
    assertThat(response.getBody().getTags()).containsExactlyInAnyOrder("tag-1", "tag-2");
  }

  @Test
  void getPosts_returnsPagedResponse() {
    CommunityTag tag = CommunityTag.builder().id(55L).name("tag").build();
    CommunityPost post = CommunityPost.allBuilder()
        .id(44L)
        .boardId(12L)
        .authorId(1L)
        .title("T")
        .content("C")
        .tags(List.of(tag))
        .isDeleted(false)
        .build();

    Page<CommunityPost> page = new PageImpl<>(List.of(post));
    when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
    when(communityPostService.getPostsWithSearch(any(), eq(12L), any())).thenReturn(page);

    ResponseEntity<Page<CommunityPostResponse>> response =
        communityPostController.getPosts(12L, null, SearchType.TITLE_ONLY,
            "keyword", List.of("tag"), 0, 10);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(1);
    CommunityPostResponse body = response.getBody().getContent().get(0);
    assertThat(body.getId()).isEqualTo(44L);
    assertThat(body.getAuthor()).isNotNull();
    assertThat(body.getAuthor().getId()).isEqualTo(mockUser.getId());
  }
}
