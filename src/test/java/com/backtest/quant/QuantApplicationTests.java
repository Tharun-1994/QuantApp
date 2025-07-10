package com.backtest.quant;

import org.apache.spark.sql.SparkSession;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class QuantApplicationTests {

    @MockBean
    private SparkSession sparkSession;

    @Test
    void contextLoads() {
    }
}