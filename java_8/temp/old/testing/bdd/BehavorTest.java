package cf.study.testing.bdd;

import junit.framework.Assert;
import cucumber.annotation.en.But;
import cucumber.annotation.en.Given;
import cucumber.annotation.en.Then;
import cucumber.annotation.en.When;

public class BehavorTest {
	
	int operand;
	
	@Given(value="^operand is set to (\\d+)$")
	public void initOperand(int v) {
		operand = v;
		Assert.assertEquals(operand, v);
		System.out.println(String.format("operand: %d", operand));
	}
	
	@When(value="^operand is added to (\\d+)$")
	public void add(int v) {
		operand += v;
	}
	
	
	@Then(value="^operand shall be (\\d+)$")
	public void checkResultOfAdd(int v) {
		Assert.assertFalse(operand == v);
		System.out.println(String.format("operand: %d", operand));
	}
	
	@But(value="^operand is added to zero$")
	public void add0() {
		operand += 0;
	}
	
	@Then(value="^operand shall be the same (\\d+)$")
	public void checkResultOfAdd0(int v) {
		Assert.assertTrue(operand == v);
		System.out.println(String.format("operand: %d", operand));
	}
}
