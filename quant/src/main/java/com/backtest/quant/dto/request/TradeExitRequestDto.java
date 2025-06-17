package com.backtest.quant.dto.request;

import java.util.Date;

import lombok.Data;
@Data
public class TradeExitRequestDto {
	private String tradeId;
	private Date tradeDate;
	private float exitPrice;
	private String exitReason;
	private String priceUsed;
}
