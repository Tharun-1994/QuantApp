package com.backtest.quant.service.impl;

import org.springframework.stereotype.Service;

import com.backtest.quant.service.BacktestService;
import com.backtest.quant.service.PortfolioService;
import com.backtest.quant.service.PriceDataLoaderService;
import com.backtest.quant.service.StrategyBuilderService;

@Service
public class BacktestServiceImpl implements BacktestService{

	@Override
	public void runBacktest(PriceDataLoaderService priceData, StrategyBuilderService strategyBuilder,
			PortfolioService portfolioService) {

		
	}

}
