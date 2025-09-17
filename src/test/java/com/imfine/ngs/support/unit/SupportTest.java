package com.imfine.ngs.support.unit;

import com.imfine.ngs.support.entity.Support;
import com.imfine.ngs.support.repository.SupportRepo;
import com.imfine.ngs.support.service.SupportService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SupportTest {

    @Mock
    private SupportRepo supportRepo;

    @InjectMocks
    private SupportService supportService;

    @Test
    @DisplayName("고객센터에서 글 저장 시 Repo.save 호출된다.")
    void insertSupportRepo() {
        //given : 준비
        Support support = Support.builder()
                .userId(1L)
                .orderId(1L)
                .categoryId(2L)
                .content("test 입력")
                .createdAt(LocalDateTime.now())
                .build();


        when(supportRepo.save(any(Support.class))).thenAnswer(invocationOnMock -> {
                    Support s = invocationOnMock.getArgument(0);
                    s.setId(1L);
                    return s;
                });

        //when : 실행
        Support resultSupport = supportService.insertSupportRepo(support);

        //then : 검증
        assertThat(resultSupport.getId()).isEqualTo(1L);
        assertThat(resultSupport.getContent()).isEqualTo("test 입력");
    }

    @Test
    @DisplayName("UserId 기반으로 조회가 가능하다.")
    void findSupportByUserId() {
        long userId = 2L;
        //given

        //when
        List<Support> supports = supportService.findSupportByUserId(userId);

        //then
        supports.forEach(support -> assertThat(support.getUserId()).isEqualTo(userId));

    }
}
