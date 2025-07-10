package com.backtest.quant.service.impl;

import static org.apache.spark.sql.functions.expr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.functions;
import org.springframework.stereotype.Service;

import com.backtest.quant.entity.BuySellData;
import com.backtest.quant.entity.RuleCondition;
import com.backtest.quant.entity.StrategyData;
import com.backtest.quant.service.StrategyBuilderService;
import com.backtest.quant.util.RuleParser;

@Service
public class StrategyBuilderServiceImpl implements StrategyBuilderService {
	List<Integer> list = new LinkedList<>();

	@Override
	public BuySellData generateSignals(StrategyData strategyData) {

		Dataset<Row> buys = generateBuySignals(strategyData);
		buys.show(200, false);
		Dataset<Row> sells = generateSellSignals(strategyData);

		return BuySellData.builder().sells(sells).buys(buys).build();
	}

	private Column buildCondition(String col, String operator, String value) {
		String safeCol = String.format("`%s`", col);
		switch (operator) {
		case "<":
			return functions.expr(safeCol + " < " + value);
		case "<=":
			return functions.expr(safeCol + " <= " + value);
		case ">":
			return functions.expr(safeCol + " > " + value);
		case ">=":
			return functions.expr(safeCol + " >= " + value);
		case "==":
		case "=":
			return functions.expr(safeCol + " = " + value);
		case "!=":
		case "<>":
			return functions.expr(safeCol + " != " + value);
		default:
			throw new IllegalArgumentException("Unsupported operator: " + operator);
		}
	}

	private Dataset<Row> generateBuySignals(StrategyData strategyData) {

		List<RuleCondition> ruleList = RuleParser.parseConditions(strategyData.getEntryRules());
		Dataset<Row> buySignals = null;
		
		Map<String,Dataset<Row>> map =new HashMap<>();

		for (RuleCondition rc : ruleList) {
			String indicator = rc.getIndicator();
			String operator = rc.getOperator();
			String value = rc.getValue();

			// 1) grab the DF for this indicator
			Dataset<Row> indicatorDf = strategyData.getEntryIndicators().get(indicator);

			if (indicatorDf == null) {
				throw new IllegalArgumentException("Indicator DataFrame not found for: " + indicator);
			}
			// 2) find all symbol columns (everything except Date)
			String[] symbols = Arrays.stream(indicatorDf.columns()).filter(c -> !c.equalsIgnoreCase("Date"))
					.toArray(String[]::new);

			// 3) build one boolean Column per symbol: col(symbol) OP value
			List<Column> masks = new ArrayList<>();
			for (String sym : symbols) {
				masks.add(buildCondition(sym, operator, value));
				
				
			}

			// 4) select those boolean columns back into a boolean mask DataFrame
			buySignals = indicatorDf.select(masks.toArray(new Column[0]));
			
			map.put(indicator, buySignals);

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
