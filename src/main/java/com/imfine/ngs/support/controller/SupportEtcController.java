package com.imfine.ngs.support.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/support/etc")
@RequiredArgsConstructor
public class SupportEtcController {

    @GetMapping
    public void getEtc() throws Exception {

    }

    @PostMapping
    public void setEtc() throws Exception {

    }
}
