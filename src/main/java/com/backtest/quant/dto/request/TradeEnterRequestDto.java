package com.backtest.quant.dto.request;

import java.util.Date;

import lombok.Data;

@Data
public class TradeEnterRequestDto {
	private String ticker;
	private Date tradeDate;
	private String direction;
	private int quantity;
	private String reason;
	private String priceUsed;
	private String entryTiming;
	private float entryprice;
	private int capital;
}
