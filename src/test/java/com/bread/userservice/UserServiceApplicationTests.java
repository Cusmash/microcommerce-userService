package com.bread.userservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class UserServiceApplicationTests {

    @MockBean
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void contextLoads() {
    }
}
