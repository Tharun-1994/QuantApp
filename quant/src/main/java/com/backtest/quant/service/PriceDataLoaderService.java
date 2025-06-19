package com.backtest.quant.service;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import com.backtest.quant.entity.PriceData;

public interface PriceDataLoaderService {
	
	public PriceData loadPricesMarketData(Dataset<Row> daily_closes,Dataset<Row> daily_opens,
			Dataset<Row> daily_highs,Dataset<Row> daily_lows,Dataset<Row> daily_universes,Dataset<Row> trading_dates,Dataset<Row> all_dates);

}
