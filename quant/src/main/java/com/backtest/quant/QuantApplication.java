package com.backtest.quant;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class QuantApplication extends SpringBootServletInitializer {

    @Value("${spark.master:local[*]}")
    private String sparkMaster;

    public static void main(String[] args) {
        SpringApplication.run(QuantApplication.class, args);
    }

	@Bean(destroyMethod = "stop") // Spring will call stop() on context shutdown
	public SparkSession sparkSession() {
		SparkSession.clearActiveSession();
		SparkConf conf = new SparkConf().setAppName("quant-backtest").setMaster(sparkMaster)
				.set("spark.driver.host", "localhost").set("spark.driver.bindAddress", "127.0.0.1")
				.set("spark.ui.enabled", "false");
		return SparkSession.builder().config(conf).getOrCreate();
	}
}