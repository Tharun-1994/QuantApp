package com.backtest.quant.service;

public interface BacktestService {
	
	public void runBacktest(PriceDataLoaderService priceData, StrategyBuilderService strategyBuilder, PortfolioService portfolioService);

}
