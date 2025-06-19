package com.backtest.quant.entity;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BuySellData {
	private Dataset<Row> buys;
	private Dataset<Row> sells;
}
