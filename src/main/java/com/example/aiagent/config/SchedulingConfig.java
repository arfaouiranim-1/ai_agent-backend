package com.example.aiagent.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class SchedulingConfig {
    // Active le @Scheduled dans GraphExecutionEngine pour le timeout
}