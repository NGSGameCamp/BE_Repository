package com.imfine.ngs._global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 비동기 작업을 위한 Virtual Thread Executor 설정
 * Virtual Threads (Java 21+)의 특징:
 * - 경량 스레드 (메모리: 수 KB/스레드)
 * - 수백만 개 생성 가능
 * - I/O 대기 시 자동 언마운트 (CPU 효율 극대화)
 *
 * @author chan
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * 게임 조회용 Virtual Thread Executor
     *
     * @return Virtual Thread 기반 Executor
     */
    @Bean(name = "gameQueryExecutor")
    public Executor gameQueryExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
