import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import country.CountryRepository;
import country.database.DatabaseCountryRepository;
import country.dto.Country;
import database.Database;
import enums.Currency;
import exchange_rate.dto.ExchangeRate;
import exchange_rate.repository.ExchangeRateRepository;

public class Main {

	public static void main(String[] args) {

		ExchangeRateRepository repository = new ExchangeRateRepository(Database.getInstance());

		System.out.println(repository.getFiveMaximumExchangeRate(Currency.EURO));
		System.out.println(repository.getFiveMinimumExchangeRate(Currency.EURO));

		CountryRepository repo = new DatabaseCountryRepository(Database.getInstance());

//		Set<Currency> currencies = new HashSet<>();
//		currencies.add(Currency.AMERICAN_DOLAR);
//		currencies.add(Currency.EURO);
//		repo.save(new Country(CountryName.DEUTSCHLAND, currencies));
//		currencies.clear();
//		currencies.add(Currency.AMERICAN_DOLAR);
//		repo.save(new Country(CountryName.UNITED_STATES, currencies));
//
//		repo.save(new Country(CountryName.WAKANDA, new HashSet<>()));
		List<Country> countries = repo.getCountriesHasMoreThanOneCurrency();

		countries.forEach(c -> System.out.println(c.getName()));
	}

	public static void fillDatabase(ExchangeRateRepository repository) {
		LocalDate date = LocalDate.now();
		for (int i = 0; i < 100; i++) {
			repository.save(new ExchangeRate(date, Currency.EURO, new BigDecimal("" + i + "." + i)));
			date = date.minusDays(1);
		}
	}

}