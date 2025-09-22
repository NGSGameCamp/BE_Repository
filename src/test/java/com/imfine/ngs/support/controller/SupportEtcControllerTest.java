package com.imfine.ngs.support.controller;

import com.imfine.ngs.support.entity.Support;
import com.imfine.ngs.support.repository.SupportRepository;
import com.imfine.ngs.support.service.SupportService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SupportEtcControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    SupportRepository supportRepository;

    @Autowired
    private SupportService supportService;

    @Test
    @DisplayName("getEtc: 게임 문의사항 불러오기")
    void getEtc() throws Exception {
        //given
        final String url = "/support/etc";
        final long userId = 1L;
        Support saveSupport = supportRepository.save(Support.builder()
                .userId(userId)
                .orderId(1L)
                .content("게임환불요청드립니다.")
                .createdAt(LocalDateTime.now())
                .build()
        );

        //when
        //orderDetail이 되면 불러올 예정

        //then
    }
}