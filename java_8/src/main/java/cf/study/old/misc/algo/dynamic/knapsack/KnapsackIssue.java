package cf.study.misc.algo.dynamic.knapsack;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import cf.study.utils.InfoUtils;

public class KnapsackIssue {

	static class Knapsack {
		private int weight;
		private int value;

		public int getWeight() {
			return weight;
		}

		public void setWeight(int weight) {
			this.weight = weight;
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		public Knapsack(int weight, int value) {
			super();
			this.weight = weight;
			this.value = value;
		}

		@Override
		public String toString() {
			return String.format("Knapsack [weight=%s, value=%s]", weight, value);
		}
	}

	static Knapsack[] bags = null;
	static int threshold = 5;

	private Integer[][] bestValues = null;
	private int bestValue = 0;

	@BeforeClass
	public static void initBags() {
		bags = new Knapsack[] { 
				new Knapsack(2, 13), 
				new Knapsack(1, 10), 
				new Knapsack(3, 24), 
				new Knapsack(2, 15), 
				new Knapsack(4, 28), 
				new Knapsack(5, 33), 
				new Knapsack(3, 20), 
				new Knapsack(1, 8) };
	}

	@Before
	public void init() {
		bestValues = new Integer[bags.length + 1][threshold + 1];
		for (Integer[] row : bestValues) {
			for (int i = 0; i < row.length; i++) {
				row[i] = 0;
			}
		}
	}

	@Test
	public void knapsack01Example() {
		int bagNumber = bags.length;
		for (int weight = 0; weight <= threshold; weight++) {
			for (int time = 0; time < bagNumber; time++) {
				if (weight == 0 || time == 0) {
					bestValues[time][weight] = 0;
				} else {
					if (weight < bags[time - 1].weight) {
						bestValues[time][weight] = bestValues[time - 1][weight];
					} else {
						int iweight = bags[time - 1].getWeight();
						int ivalue = bags[time - 1].getValue();
						bestValues[time][weight] = Math.max(bestValues[time - 1][weight], ivalue + bestValues[time - 1][weight - iweight]);
					}
				}
				
			}
			
			System.out.println(InfoUtils.prettyTable(InfoUtils.toStringArray(bestValues), ' ')); 
			
		}

		List<Knapsack> bestSolution = new ArrayList<Knapsack>();
		int tempWeight = threshold;
		for (int i = bagNumber; i >= 1; i--) {
			if (bestValues[i][tempWeight] > bestValues[i - 1][tempWeight]) {
				bestSolution.add(bags[i - 1]); // bags[i-1] 表示第 i 个背包
				tempWeight -= bags[i - 1].getWeight();
			}
			if (tempWeight == 0) {
				break;
			}
		}
		bestValue = bestValues[bagNumber][threshold];
		System.out.println(bestSolution);
	}
	
	@Test
	public void runExample() {
		knapsack01Example();
	}
}
