package cf.study.oo.design.pattern;

import java.math.BigInteger;
import java.util.Currency;
import java.util.Locale;

import org.junit.Assert;


class Money implements Comparable<Money> {
	private BigInteger amount;
	private Currency currency;
	
	@Override
	public int compareTo(Money o) {
		return amount.compareTo(o.amount);
	}

	public double getAmount() {
		return amount.doubleValue() / 100;
	}

	public Currency getCurrency() {
		return currency;
	}
	
	public Money(double amount, Currency currency) {
		this.amount = BigInteger.valueOf(Math.round(amount * 100));
		this.currency = currency;
	}
	
	public Money(long amount, Currency currency) {
		this.amount = BigInteger.valueOf(amount * 100);
		this.currency = currency;
	}
	
	public static Money dollars(double amount) {
		return new Money(amount, Currency.getInstance(Locale.US));
	}
	
	void assertSameCurrency(Money o) {
		Assert.assertEquals(this.currency, o.currency);
	}
	
	private Money(BigInteger amountInPennies, Currency currency, boolean privacyMaker) {
		Assert.assertNotNull(amountInPennies);
		Assert.assertNotNull(currency);
		this.amount = amountInPennies;
		this.currency = currency;
	}
	
	public Money add(Money o) {
		assertSameCurrency(o);
		return new Money(amount.add(o.amount), currency, true);
	}
	
	public Money negate() {
		return new Money(amount.negate(), currency, true);
	}
	
	public Money substract(Money o) {
		return this.add(o.negate());
	}
	
	public Money multiply(double arg) {
		return new Money(getAmount() * arg, currency);
	}
	
	public Money[] divide(int denominator) {
		BigInteger bigDenominator = BigInteger.valueOf(denominator);
		Money[] res = new Money[denominator];
		BigInteger simpleRe = amount.divide(bigDenominator);
		for (int i = 0; i < denominator; i++) {
			res[i] = new Money(simpleRe, currency, true);
		}
		int remainder = amount.subtract(simpleRe.multiply(bigDenominator)).intValue();
		for (int i = 0; i < remainder; i++) {
			res[i] = res[i].add(new Money(BigInteger.valueOf(1), currency, true));
		}
		return res;
	}
	
	public boolean greaterThan(Money arg) {
		return this.compareTo(arg) == 1;
	}
	
	public boolean lessThan(Money arg) {
		return this.compareTo(arg) == -1;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result + ((currency == null) ? 0 : currency.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Money other = (Money) obj;
		if (amount == null) {
			if (other.amount != null)
				return false;
		} else if (!amount.equals(other.amount))
			return false;
		if (currency == null) {
			if (other.currency != null)
				return false;
		} else if (!currency.equals(other.currency))
			return false;
		return true;
	}
	
	
}

public class Quantity {
}
