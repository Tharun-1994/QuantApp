package com.backtest.quant.service.impl;

import static org.apache.spark.sql.functions.expr;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.functions;
import org.springframework.stereotype.Service;

import com.backtest.quant.entity.BuySellData;
import com.backtest.quant.entity.StrategyData;
import com.backtest.quant.service.StrategyBuilderService;

@Service
public class StrategyBuilderServiceImpl implements StrategyBuilderService {

	@Override
	public BuySellData generateSignals(StrategyData strategyData) {
		Dataset<Row> buys = generateBuySignals(strategyData);
		buys.show(2, false);
		Dataset<Row> sells = generateSellSignals(strategyData);

		return BuySellData.builder().sells(sells).buys(buys).build();
	}

	private Dataset<Row> generateBuySignals(StrategyData strategyData) {

		Pattern pattern = Pattern.compile("(\\w+)\\s*([<>=!]+)\\s*(\\d+(\\.\\d+)?)");

		Dataset<Row> buySignals = null;
		for (String key : strategyData.getEntryIndicators().keySet()) {
			Matcher matcher = pattern.matcher(strategyData.getEntryRules());
			if (matcher.find()) {
				String indicator = matcher.group(1); // rsi_14
				String operator = matcher.group(2); // <
				String value = matcher.group(3); // 10

				if (strategyData.getEntryRules().contains(key)) {
					String[] symbolColumns = Arrays.stream(strategyData.getEntryIndicators().get(key).columns())
							.filter(c -> !c.equalsIgnoreCase("Date")).toArray(String[]::new);

					String condition = Arrays.stream(symbolColumns)
							.map(col -> String.format("`%s` %s %s", col, operator, value))
							.collect(Collectors.joining(" OR "));

					buySignals = strategyData.getEntryIndicators().get(key).filter(condition);
				}

			}

		}

		return buySignals;
	}

	private Dataset<Row> generateSellSignals(StrategyData strategyData) {
		Dataset<Row> sellSignals = null;
		for (String key : strategyData.getExitIndicators().keySet()) {
			if (key.contains(strategyData.getExitRules())) {
				sellSignals = strategyData.getExitIndicators().get(key).filter(expr(strategyData.getExitRules()))
						.withColumn("signal", functions.lit("SELL"));
			}

		}

		return sellSignals;
	}

}
