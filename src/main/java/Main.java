import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import exampleHQL.ExampleCountryRepo;
import exchange_rate.ExchangeRateFasade;
import exchange_rate.enums.CountryName;
import exchange_rate.enums.Currency;

public class Main {

	public static void main(String[] args) {
		ExchangeRateFasade fasade = new ExchangeRateFasade();

		System.out.println("max: " + fasade.getMaximumRateForPeroid(Currency.AMERICAN_DOLAR, LocalDate.of(2018, 1, 1),
				LocalDate.of(2019, 1, 1)));

		System.out.println("min: " + fasade.getMinimumRateForPeroid(Currency.AMERICAN_DOLAR, LocalDate.of(2018, 1, 1),
				LocalDate.of(2019, 1, 1)));

		System.out.println(fasade.getBiggestRateDifferenceInPeroid(Currency.AMERICAN_DOLAR, LocalDate.of(2018, 1, 1),
				LocalDate.of(2019, 1, 1)));

		List<Currency> currencies = Arrays.asList(Currency.EURO, Currency.POUND_STERLING);
		System.out.println(fasade.getCurrencyWithBiggestRateDifferenceInPeroid(currencies, LocalDate.of(2018, 1, 1),
				LocalDate.of(2019, 1, 1)));
	}

	private void showNplus1() {
		ExampleCountryRepo repo = new ExampleCountryRepo();

		repo.getWithoutNPlus1(CountryName.DEUTSCHLAND);

		repo.getWithNPlus1(CountryName.DEUTSCHLAND);
	}

}