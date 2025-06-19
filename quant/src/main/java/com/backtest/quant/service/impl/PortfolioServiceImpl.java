package com.backtest.quant.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.backtest.quant.dto.request.TradeEnterRequestDto;
import com.backtest.quant.dto.request.TradeExitRequestDto;
import com.backtest.quant.entity.EquityLog;
import com.backtest.quant.entity.LiveHoldingsTracker;
import com.backtest.quant.entity.TradeLog;
import com.backtest.quant.service.PortfolioService;

public class PortfolioServiceImpl implements PortfolioService {
	@Value("${starting.capital}")
	private int startingCapital;
	@Value("${max.slots}")
	private int maxSlots;
	
	private Map<String, TradeLog> tradeLogger = new HashMap<>();
	private Map<String, List<LiveHoldingsTracker>> liveHoldingsLogger = new HashMap<>();
	private Map<Date, EquityLog> equityLogger = new HashMap<>();

	private float maxEquity = Float.MIN_VALUE;
	private Date maxEquityDate = null;

	private final AtomicLong tradeCounter = new AtomicLong();

	private float unusedCapital = startingCapital;

//	private static final Logger log = LoggerFactory.getLogger(PortfolioServiceImpl.class);

	@Override
	public void enterTrade(TradeEnterRequestDto tradeEnterRequest) {
		TradeLog tradeLog;
		if (tradeEnterRequest != null) {
			tradeLog = TradeLog.builder().entryDate(tradeEnterRequest.getTradeDate())
					.entryPrice(tradeEnterRequest.getEntryprice()).entryReason(tradeEnterRequest.getReason())
					.entryTiming(tradeEnterRequest.getEntryTiming())
					.entryValue((tradeEnterRequest.getQuantity() * tradeEnterRequest.getEntryprice()))
					.direction(tradeEnterRequest.getDirection()).symbol(tradeEnterRequest.getTicker())
					.quantity(tradeEnterRequest.getQuantity()).capital(tradeEnterRequest.getCapital()).build();

			String id = tradeEnterRequest.getTicker() + "_" + System.currentTimeMillis() + "_"
					+ tradeCounter.incrementAndGet();

			this.tradeLogger.put(id, tradeLog);
			List<LiveHoldingsTracker> liveHoldings = new ArrayList<>();
			this.liveHoldingsLogger.put(id, liveHoldings);

			this.unusedCapital -= Math.round(tradeEnterRequest.getEntryprice() * tradeEnterRequest.getQuantity());

		}

	}

	@Override
	public void exitTrade(TradeExitRequestDto tradeExitRequest) {
		String tradeId = tradeExitRequest.getTradeId();
		if (tradeId != null && this.tradeLogger.containsKey(tradeId)) {
			TradeLog tradeLog = this.tradeLogger.get(tradeId);

			if (tradeLog == null) {
//				log.warn("Trade not found for ID: {}", tradeId);
				return;
			}

			tradeLog.setExitDate(tradeExitRequest.getTradeDate());
			tradeLog.setExitPrice(tradeExitRequest.getExitPrice());
			tradeLog.setExitValue(Math.round(tradeExitRequest.getExitPrice() * tradeLog.getQuantity()));
			tradeLog.setExitReason(tradeExitRequest.getExitReason());
			tradeLog.setExitTiming(tradeExitRequest.getPriceUsed());
			tradeLog.setProfit(tradeLog.getExitValue() - tradeLog.getEntryValue());
			tradeLog.setProfitPercentage(tradeLog.getProfit() / tradeLog.getEntryValue());

			tradeLog.setValueTracker(new ConcurrentHashMap<>());
			tradeLog.getValueTracker().put(tradeId, this.liveHoldingsLogger.get(tradeId));

			this.liveHoldingsLogger.remove(tradeId);

			this.unusedCapital += tradeLog.getExitValue();
		}

	}

	@Override
	public void markToMarket(Date tradeDate) {
		float todayEquity = this.unusedCapital;
		if (!this.liveHoldingsLogger.isEmpty()) {

			for (String tradeId : this.liveHoldingsLogger.keySet()) {
				List<LiveHoldingsTracker> eachTradeList = this.liveHoldingsLogger.get(tradeId);
				TradeLog tradeRow = this.tradeLogger.get(tradeId);
				String symbol = tradeRow.getSymbol();
				int amount = tradeRow.getQuantity();
				float closePrice = 0;

				todayEquity += amount * closePrice;

				eachTradeList.add(LiveHoldingsTracker.builder().symbol(symbol).endOfDayValue(amount * closePrice)
						.tradeDate(tradeDate).build());

			}
			if (todayEquity > this.maxEquity) {
				this.maxEquity = todayEquity;
				this.maxEquityDate = tradeDate;
			}
			EquityLog eqLog = new EquityLog();
			eqLog.setDailyDrawdown(this.maxEquity - todayEquity);
			eqLog.setEquityValue(todayEquity);
			eqLog.setDayEndUtility(this.liveHoldingsLogger.size());
			eqLog.setDayEndUtilityValue(this.liveHoldingsLogger.size() * (this.startingCapital / this.maxSlots));

			this.equityLogger.put(tradeDate, eqLog);
		}

	}

	@Override
	public void endOfBacktest(Date tradeDate, List<LiveHoldingsTracker> liveHoldingsTracker) {

		int closePriceSeries = 0;
		List<String> liveTradeIds = new ArrayList<>(this.liveHoldingsLogger.keySet());

		for (String tradeId : liveTradeIds) {

			TradeExitRequestDto tradeExitRequest = new TradeExitRequestDto();
			tradeExitRequest.setExitPrice(closePriceSeries);
			tradeExitRequest.setTradeDate(new Date());
			tradeExitRequest.setTradeId(tradeId);
			tradeExitRequest.setExitReason("End Of Backtest");
			tradeExitRequest.setPriceUsed("close");

			this.exitTrade(tradeExitRequest);
		}
		EquityLog eqLog = new EquityLog();
		eqLog.setDailyDrawdown(this.maxEquity - this.unusedCapital);
		eqLog.setEquityValue(this.unusedCapital);
		eqLog.setDayEndUtility(this.liveHoldingsLogger.size());
		eqLog.setDayEndUtilityValue(this.liveHoldingsLogger.size() * (this.startingCapital / this.maxSlots));

		this.equityLogger.put(tradeDate, eqLog);

	}

}
