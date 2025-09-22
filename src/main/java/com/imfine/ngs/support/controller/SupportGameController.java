package com.imfine.ngs.support.controller;

import com.imfine.ngs.order.service.OrderService;
import com.imfine.ngs.support.service.SupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/support/game")
@RequiredArgsConstructor
public class SupportGameController {

    private final SupportService supportService;
    private final OrderService orderService;


}
