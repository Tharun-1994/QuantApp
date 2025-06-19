package com.backtest.quant.service.impl;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.stereotype.Service;

import com.backtest.quant.entity.PriceData;
import com.backtest.quant.service.PriceDataLoaderService;
@Service
public class PriceDataLoaderServiceImpl implements PriceDataLoaderService {

	@Override
	public PriceData loadPricesMarketData(Dataset<Row> daily_closes, Dataset<Row> daily_opens, Dataset<Row> daily_highs,
			Dataset<Row> daily_lows, Dataset<Row> daily_universes, Dataset<Row> trading_dates, Dataset<Row> all_dates) {

		return PriceData.builder().daily_closes(daily_closes).all_dates(all_dates).daily_highs(daily_highs)
				.daily_lows(daily_lows).daily_opens(daily_opens).daily_universes(daily_universes).build();
	}

}
