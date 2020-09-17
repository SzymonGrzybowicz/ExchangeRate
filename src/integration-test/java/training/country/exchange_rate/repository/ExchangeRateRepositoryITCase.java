package training.country.exchange_rate.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;

import training.database.Database;
import training.enums.Currency;
import training.exchange_rate.database.entity.ExchangeRateEntity;
import training.exchange_rate.database.mapper.ExchangeRateEntityMapper;
import training.exchange_rate.dto.ExchangeRate;
import training.exchange_rate.repository.ExchangeRateRepository;

public class ExchangeRateRepositoryITCase {

	private ExchangeRateRepository repository;
	private Database database;

	@Before
	public void init() {
		database = prepareDatabase();
		ExchangeRateEntityMapper mapper = new ExchangeRateEntityMapper();
		repository = new ExchangeRateRepository(database, mapper);

		List<ExchangeRateEntity> startValues = database.execute((session) -> {
			return session.createQuery("from ExchangeRateEntity", ExchangeRateEntity.class).getResultList();
		});

		assertThat(startValues).isEmpty();
	}

	@Test
	public void test_save() {
		// Given
		LocalDate expectedDate = LocalDate.now();
		Currency expectedCurrency = Currency.EURO;
		BigDecimal expectedRate = new BigDecimal("1234.1234");
		ExchangeRate exchangeRate = new ExchangeRate(expectedDate, expectedCurrency, expectedRate);

		// When
		repository.save(exchangeRate);
		ExchangeRateEntity result = database.execute((session) -> {
			return session.createQuery("from ExchangeRateEntity", ExchangeRateEntity.class).uniqueResult();
		});

		// Then
		assertThat(result.getCurrency()).isEqualTo(expectedCurrency);
		assertThat(result.getDate()).isEqualTo(expectedDate);
		assertThat(result.getRate()).isEqualByComparingTo(expectedRate);
	}

	@Test
	public void test_get() {
		// Given
		LocalDate expectedDate = LocalDate.now();
		Currency expectedCurrency = Currency.EURO;
		BigDecimal expectedRate = new BigDecimal("1234.1234");
		ExchangeRateEntity entity = new ExchangeRateEntity(expectedRate, expectedDate, expectedCurrency);

		database.execute((session) -> {
			session.save(entity);
			return null;
		});

		// When
		ExchangeRate result = repository.get(expectedCurrency, expectedDate);

		// Then
		assertThat(result.getCurrency()).isEqualTo(expectedCurrency);
		assertThat(result.getDate()).isEqualTo(expectedDate);
		assertThat(result.getRate()).isEqualByComparingTo(expectedRate);
	}

	@Test
	public void test_delete() {
		// Given
		LocalDate date = LocalDate.now();
		Currency currency = Currency.EURO;
		BigDecimal rate = new BigDecimal("1234.1234");
		ExchangeRateEntity entity = new ExchangeRateEntity(rate, date, currency);

		database.execute((session) -> {
			session.save(entity);
			return null;
		});

		// When
		repository.delete(new ExchangeRate(date, currency, rate));

		ExchangeRateEntity result = database.execute((session) -> {
			return session.createQuery("from ExchangeRateEntity", ExchangeRateEntity.class).uniqueResult();
		});

		// Then
		assertThat(result).isNull();
	}

	@Test
	public void test_update() {
		// Given
		LocalDate expectedDate = LocalDate.now();
		Currency expectedCurrency = Currency.EURO;
		BigDecimal startRate = new BigDecimal("1111.1111");
		ExchangeRateEntity entity = new ExchangeRateEntity(startRate, expectedDate, expectedCurrency);

		database.execute((session) -> {
			session.save(entity);
			return null;
		});

		// When
		BigDecimal expectedRate = new BigDecimal("1234.1234");
		repository.update(new ExchangeRate(expectedDate, expectedCurrency, expectedRate));

		ExchangeRateEntity result = database.execute((session) -> {
			return session.createQuery("from ExchangeRateEntity", ExchangeRateEntity.class).uniqueResult();
		});

		// Then
		assertThat(result.getCurrency()).isEqualTo(expectedCurrency);
		assertThat(result.getDate()).isEqualTo(expectedDate);
		assertThat(result.getRate()).isEqualByComparingTo(expectedRate);
	}

	@Test
	public void test_getMaximumRateInPeriod() {
		// Given
		LocalDate startDate = LocalDate.now();
		LocalDate expectedDate = startDate.plusDays(1);
		LocalDate endDate = startDate.plusDays(5);
		Currency expectedCurrency = Currency.EURO;
		BigDecimal maxRate = new BigDecimal("1111.1111");

		database.execute((session) -> {
			session.save(new ExchangeRateEntity(maxRate, expectedDate, expectedCurrency));
			session.save(new ExchangeRateEntity(maxRate.subtract(BigDecimal.ONE), startDate, expectedCurrency));
			session.save(new ExchangeRateEntity(maxRate.subtract(new BigDecimal(2)), startDate.plusDays(2),
					expectedCurrency));
			session.save(
					new ExchangeRateEntity(maxRate.add(new BigDecimal(2)), startDate.minusDays(1), expectedCurrency));
			session.save(
					new ExchangeRateEntity(maxRate.add(new BigDecimal(1)), startDate.minusDays(2), expectedCurrency));
			return null;
		});

		// When
		ExchangeRate result = repository.getMaximumRateInPeriod(expectedCurrency, startDate, endDate);

		// Then
		assertThat(result.getDate()).isEqualTo(expectedDate);
		assertThat(result.getCurrency()).isEqualTo(expectedCurrency);
		assertThat(result.getRate()).isEqualByComparingTo(maxRate);
	}

	@Test
	public void test_getMinimumRateInPeriod() {
		// Given
		LocalDate startDate = LocalDate.now();
		LocalDate expectedDate = startDate.plusDays(1);
		LocalDate endDate = startDate.plusDays(5);
		Currency expectedCurrency = Currency.EURO;
		BigDecimal minRate = new BigDecimal("1111.1111");

		database.execute((session) -> {
			session.save(new ExchangeRateEntity(minRate, expectedDate, expectedCurrency));
			session.save(new ExchangeRateEntity(minRate.add(BigDecimal.ONE), startDate, expectedCurrency));
			session.save(
					new ExchangeRateEntity(minRate.add(new BigDecimal(2)), startDate.plusDays(2), expectedCurrency));
			session.save(new ExchangeRateEntity(minRate.subtract(new BigDecimal(2)), startDate.minusDays(1),
					expectedCurrency));
			session.save(new ExchangeRateEntity(minRate.subtract(new BigDecimal(1)), startDate.minusDays(2),
					expectedCurrency));
			return null;
		});

		// When
		ExchangeRate result = repository.getMinimumRateInPeriod(expectedCurrency, startDate, endDate);

		// Then
		assertThat(result.getDate()).isEqualTo(expectedDate);
		assertThat(result.getCurrency()).isEqualTo(expectedCurrency);
		assertThat(result.getRate()).isEqualByComparingTo(minRate);
	}

	@Test
	public void test_getCurrencyWithHighestExchangeRateDifferenceInPeriod() {
		// Given
		LocalDate startDate = LocalDate.now();
		LocalDate endDate = startDate.plusDays(5);
		Currency expectedCurrency = Currency.EURO;
		BigDecimal minRate = new BigDecimal("1.0");
		BigDecimal maxRate = new BigDecimal("1000.0");

		database.execute((session) -> {
			session.save(new ExchangeRateEntity(minRate, startDate, expectedCurrency));
			session.save(new ExchangeRateEntity(maxRate, endDate, expectedCurrency));

			session.save(new ExchangeRateEntity(minRate.add(BigDecimal.ONE), startDate, Currency.AMERICAN_DOLAR));
			session.save(new ExchangeRateEntity(maxRate, endDate, Currency.AMERICAN_DOLAR));

			session.save(new ExchangeRateEntity(minRate.subtract(BigDecimal.ONE), startDate.minusDays(1),
					Currency.POUND_STERLING));
			session.save(new ExchangeRateEntity(maxRate.add(BigDecimal.ONE), endDate, Currency.POUND_STERLING));
			return null;
		});

		// When
		Currency result = repository.getCurrencyWithHighestExchangeRateDifferenceInPeriod(startDate, endDate);

		// Then
		assertThat(result).isEqualTo(expectedCurrency);
	}

	@Test
	public void test_getFiveMaximumExchangeRate() {
		// Given
		BigDecimal maxRate = new BigDecimal("1000.0");
		Currency currency = Currency.EURO;
		LocalDate date = LocalDate.now();

		database.execute((session) -> {

			session.save(new ExchangeRateEntity(maxRate, date, currency));
			session.save(new ExchangeRateEntity(maxRate.subtract(new BigDecimal(1)), date.plusDays(1), currency));
			session.save(new ExchangeRateEntity(maxRate.subtract(new BigDecimal(2)), date.plusDays(2), currency));
			session.save(new ExchangeRateEntity(maxRate.subtract(new BigDecimal(3)), date.plusDays(3), currency));
			session.save(new ExchangeRateEntity(maxRate.subtract(new BigDecimal(4)), date.plusDays(4), currency));

			session.save(new ExchangeRateEntity(maxRate.subtract(new BigDecimal(5)), date.plusDays(5), currency));
			session.save(new ExchangeRateEntity(maxRate.subtract(new BigDecimal(6)), date.plusDays(6), currency));
			session.save(new ExchangeRateEntity(maxRate.subtract(new BigDecimal(7)), date.plusDays(7), currency));

			session.save(new ExchangeRateEntity(maxRate.subtract(new BigDecimal(8)), date.minusDays(1), currency));
			session.save(new ExchangeRateEntity(maxRate.subtract(new BigDecimal(9)), date.minusDays(2), currency));
			session.save(new ExchangeRateEntity(maxRate.subtract(new BigDecimal(10)), date.minusDays(3), currency));
			return null;
		});

		// When
		List<ExchangeRate> result = repository.getFiveMaximumExchangeRate(currency);

		// Then
		assertThat(result).hasSize(5);
		assertThat(result).allMatch(c -> c.getRate().compareTo(maxRate.subtract(new BigDecimal(5))) == 1);

	}

	@Test
	public void test_getFiveMinimumExchangeRate() {
		// Given
		BigDecimal minRate = new BigDecimal("1000.0");
		Currency currency = Currency.EURO;
		LocalDate date = LocalDate.now();

		database.execute((session) -> {

			session.save(new ExchangeRateEntity(minRate, date, currency));
			session.save(new ExchangeRateEntity(minRate.add(new BigDecimal(1)), date.plusDays(1), currency));
			session.save(new ExchangeRateEntity(minRate.add(new BigDecimal(2)), date.plusDays(2), currency));
			session.save(new ExchangeRateEntity(minRate.add(new BigDecimal(3)), date.plusDays(3), currency));
			session.save(new ExchangeRateEntity(minRate.add(new BigDecimal(4)), date.plusDays(4), currency));

			session.save(new ExchangeRateEntity(minRate.add(new BigDecimal(5)), date.plusDays(5), currency));
			session.save(new ExchangeRateEntity(minRate.add(new BigDecimal(6)), date.plusDays(6), currency));
			session.save(new ExchangeRateEntity(minRate.add(new BigDecimal(7)), date.plusDays(7), currency));

			session.save(new ExchangeRateEntity(minRate.add(new BigDecimal(8)), date.minusDays(1), currency));
			session.save(new ExchangeRateEntity(minRate.add(new BigDecimal(9)), date.minusDays(2), currency));
			session.save(new ExchangeRateEntity(minRate.add(new BigDecimal(10)), date.minusDays(3), currency));
			return null;
		});

		// When
		List<ExchangeRate> result = repository.getFiveMinimumExchangeRate(currency);

		// Then
		assertThat(result).hasSize(5);
		assertThat(result).allMatch(c -> c.getRate().compareTo(minRate.add(new BigDecimal(5))) == -1);

	}

	private Database prepareDatabase() {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName("org.h2.Driver");
		dataSource.setUrl("jdbc:h2:mem:myDb;DB_CLOSE_DELAY=-1");

		Properties hibernateProperties = new Properties();
		hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
		hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		hibernateProperties.setProperty("hibernate.show_sql", "true");

		SessionFactory sessionFactory = new LocalSessionFactoryBuilder(dataSource).scanPackages("training")
				.addProperties(hibernateProperties).buildSessionFactory();

		HibernateTransactionManager transactionManager = new HibernateTransactionManager();
		transactionManager.setSessionFactory(sessionFactory);

		return new Database(sessionFactory);
	}
}
