package com.backtest.quant.entity;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import lombok.Builder;
import lombok.Data;
@Builder
@Data
public class PriceData {
	private final Dataset<Row> daily_closes;
	private final Dataset<Row> daily_opens;
	private final Dataset<Row> daily_highs;
	private final Dataset<Row> daily_lows;
	private final Dataset<Row> daily_universes;
	private final Dataset<Row> trading_dates;
	private final Dataset<Row> all_dates;
	
	
}
