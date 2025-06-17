package com.backtest.quant.entity;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LiveHoldingsTracker {
	private String symbol;
	private Date tradeDate;
	private float endOfDayValue;
}
