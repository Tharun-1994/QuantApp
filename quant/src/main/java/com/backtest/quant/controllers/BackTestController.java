package com.backtest.quant.controllers;

import java.util.Arrays;
import java.util.List;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backtest.quant.controllers.SparkDemoController.Person;

@RestController
public class BackTestController {

	private final SparkSession sparkSession;

	public BackTestController(SparkSession sparkSession) {
		this.sparkSession = sparkSession;
		
	}

	@GetMapping("/backtest/1")
	public List<String> backtestRun() {
		// Example data
		Dataset<Row> df = sparkSession.read().option("header", "true").option("inferSchema", "true")
				.option("timestampFormat", "yyyy-MM-dd").csv("C:\\Tharun\\Prices\\Sp500\\daily_closes.csv");

		// Create DataFrame

		df.show(2, false); // prints 20 rows, disables column truncation

		// Convert DataFrame rows to JSON strings
		List<String> jsonList = df.limit(20).toJSON().collectAsList();

		return jsonList;
	}
}
