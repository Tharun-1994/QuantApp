package com.backtest.quant.service;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import com.backtest.quant.entity.BuySellData;
import com.backtest.quant.entity.StrategyData;

public interface StrategyBuilderService {

	public BuySellData generateSignals(StrategyData strategyData);


}
