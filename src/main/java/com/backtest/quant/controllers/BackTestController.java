package com.backtest.quant.controllers;

import java.util.List;
import java.util.Map;

import org.apache.hadoop.shaded.org.apache.commons.collections.map.HashedMap;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backtest.quant.entity.BuySellData;
import com.backtest.quant.entity.PriceData;
import com.backtest.quant.entity.StrategyData;
import com.backtest.quant.service.PriceDataLoaderService;
import com.backtest.quant.service.StrategyBuilderService;

@RestController
public class BackTestController {

	private final SparkSession sparkSession;
	private final PriceDataLoaderService priceDataService;
	private final StrategyBuilderService strategyBuilderService;

	public BackTestController(SparkSession sparkSession, PriceDataLoaderService priceDataService,
			StrategyBuilderService strategyBuilderService) {
		this.sparkSession = sparkSession;
		this.priceDataService = priceDataService;
		this.strategyBuilderService = strategyBuilderService;

	}

	@GetMapping("/backtest/1")
	public List<String> backtestRun() {

		String universe = "SP500";
		int slots = 10;

		Dataset<Row> daily_closes = sparkSession.read().option("header", "true").option("inferSchema", "true")
				.option("timestampFormat", "yyyy-MM-dd").csv("C:\\Tharun\\Prices\\Sp500\\daily_closes.csv");

		Dataset<Row> daily_opens = sparkSession.read().option("header", "true").option("inferSchema", "true")
				.option("timestampFormat", "yyyy-MM-dd").csv("C:\\Tharun\\Prices\\Sp500\\daily_opens.csv");

		Dataset<Row> daily_highs = sparkSession.read().option("header", "true").option("inferSchema", "true")
				.option("timestampFormat", "yyyy-MM-dd").csv("C:\\Tharun\\Prices\\Sp500\\daily_highs.csv");

		Dataset<Row> daily_lows = sparkSession.read().option("header", "true").option("inferSchema", "true")
				.option("timestampFormat", "yyyy-MM-dd").csv("C:\\Tharun\\Prices\\Sp500\\daily_lows.csv");

		Dataset<Row> daily_universes = sparkSession.read().option("header", "true").option("inferSchema", "true")
				.option("timestampFormat", "yyyy-MM-dd").csv("C:\\Tharun\\Prices\\Sp500\\daily_universes.csv");

		Dataset<Row> trading_dates = sparkSession.read().option("header", "true").option("inferSchema", "true")
				.option("timestampFormat", "yyyy-MM-dd").csv("C:\\Tharun\\Prices\\Sp500\\trading_dates.csv");

		Dataset<Row> all_dates = sparkSession.read().option("header", "true").option("inferSchema", "true")
				.option("timestampFormat", "yyyy-MM-dd").csv("C:\\Tharun\\Prices\\Sp500\\all_dates.csv");

		Dataset<Row> rsi_14 = sparkSession.read().option("header", "true").option("inferSchema", "true")
				.option("timestampFormat", "yyyy-MM-dd").csv("C:\\Tharun\\Prices\\Sp500\\rsi_14_sp500.csv");

		PriceData priceData = this.priceDataService.loadPricesMarketData(daily_closes, daily_opens, daily_highs,
				daily_lows, daily_universes, trading_dates, all_dates);

		Map<String, Dataset<Row>> entryMap = new HashedMap();
		entryMap.put("rsi_14", rsi_14);

		Map<String, Dataset<Row>> exitMap = new HashedMap();
		exitMap.put("rsi_14", rsi_14);

		StrategyData strategyData = StrategyData.builder().entryRules("rsi_14 < 10").exitRules("rsi_14 > 50")
				.entryIndicators(entryMap).exitIndicators(exitMap).build();

		BuySellData buySell = this.strategyBuilderService.generateSignals(strategyData);
		
		System.err.println(buySell);

		// Create DataFrame

		daily_closes.show(2, false); // prints 20 rows, disables column truncation
		daily_universes.show(2, false);
		trading_dates.show(2, false);
		all_dates.show(2, false);
		rsi_14.show(2, false);

		// Convert DataFrame rows to JSON strings
		List<String> jsonList = daily_closes.limit(20).toJSON().collectAsList();

		return jsonList;
	}
}
