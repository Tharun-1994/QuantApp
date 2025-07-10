package com.backtest.quant.entity;

import java.util.Map;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class StrategyData {

	private String entryRules;
	private String exitRules;
	private Map<String, Dataset<Row>> entryIndicators;
	private Map<String, Dataset<Row>> exitIndicators;
	private int stopLossPct;
	private int takeProfitPct;
	

}
