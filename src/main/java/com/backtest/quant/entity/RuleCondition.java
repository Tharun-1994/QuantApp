package com.backtest.quant.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RuleCondition {
	private final String indicator;
	private final String operator;
	private final String value;
}
