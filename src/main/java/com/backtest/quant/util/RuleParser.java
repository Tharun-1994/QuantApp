package com.backtest.quant.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.backtest.quant.entity.RuleCondition;

public class RuleParser {
	private static final Pattern CONDITION_PATTERN = Pattern.compile("(\\w+)\\s*([<>=!]+)\\s*(\\d+(\\.\\d+)?)");

	public static List<RuleCondition> parseConditions(String ruleString) {
		Matcher matcher = CONDITION_PATTERN.matcher(ruleString);
		List<RuleCondition> conditions = new ArrayList<>();

		while (matcher.find()) {
			String indicator = matcher.group(1);
			String operator = matcher.group(2);
			String value = matcher.group(3);

			conditions.add(RuleCondition.builder().indicator(indicator).operator(operator).value(value).build());
		}

		return conditions;
	}
}
