package com.backtest.quant.service;

import java.util.Date;
import java.util.List;

import com.backtest.quant.dto.request.TradeEnterRequestDto;
import com.backtest.quant.dto.request.TradeExitRequestDto;
import com.backtest.quant.entity.LiveHoldingsTracker;

public interface PortfolioService {

	public void enterTrade(TradeEnterRequestDto tradeEnterRequest);

	public void exitTrade(TradeExitRequestDto tradeExitRequest);

	public void markToMarket(Date tradeDate);

	public void endOfBacktest(Date tradeDate, List<LiveHoldingsTracker> liveHoldingsTracker);
}
