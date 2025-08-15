package com.roshlab.savings;

import com.roshlab.savings.repository.AbstractSavingsApplicationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class SavingsApplicationTests extends AbstractSavingsApplicationTest {

    @Test
    void contextLoads() {
    }

}