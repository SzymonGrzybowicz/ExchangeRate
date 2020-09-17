package integration.training.country.database;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.dbcp.BasicDataSource;
import org.assertj.core.util.Sets;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;

import training.country.database.DatabaseCountryRepository;
import training.country.database.entity.CountryEntity;
import training.country.database.mapper.CountryMapper;
import training.country.dto.Country;
import training.database.Database;
import training.database.UnitOfWork;
import training.enums.CountryName;
import training.enums.Currency;

public class DatabaseCountryRepositoryITCase {

	private DatabaseCountryRepository repository;
	private Database database;

	@Before
	public void init() {
		database = prepareDatabase();
		CountryMapper mapper = new CountryMapper();
		repository = new DatabaseCountryRepository(mapper, database);
	}

	@Test
	public void test_save() {
		// Given
		CountryName expectedName = CountryName.DEUTSCHLAND;
		Set<Currency> expectedCurrencies = Sets.newLinkedHashSet(Currency.AMERICAN_DOLAR, Currency.EURO);
		repository.save(new Country(expectedName, expectedCurrencies));
		UnitOfWork<CountryEntity> unitOfWork = (Session session) -> {
			return session.createQuery("FROM CountryEntity e INNER JOIN FETCH e.currencies", CountryEntity.class)
					.uniqueResult();
		};

		// When
		CountryEntity result = database.execute(unitOfWork);

		// Then
		assertThat(result.getName()).isEqualTo(expectedName);
		assertThat(result.getCurrencies()).isEqualTo(expectedCurrencies);
	}

	@Test
	public void test_get() {
		// Given
		CountryName expectedName = CountryName.DEUTSCHLAND;
		Set<Currency> expectedCurrencies = Sets.newLinkedHashSet(Currency.AMERICAN_DOLAR, Currency.EURO);
		UnitOfWork<Void> unitOfWork = (Session session) -> {
			session.save(new CountryEntity(expectedName, expectedCurrencies));
			return null;
		};
		database.execute(unitOfWork);

		// When
		Country result = repository.get(expectedName);

		// Then
		assertThat(result.getName()).isEqualTo(expectedName);
		assertThat(result.getCurrencies()).isEqualTo(expectedCurrencies);
	}

	@Test
	public void test_addCurrency() {
		// Given
		CountryName expectedName = CountryName.DEUTSCHLAND;
		Set<Currency> currencies = Sets.newLinkedHashSet(Currency.AMERICAN_DOLAR, Currency.EURO);
		Currency expectedCurrency = Currency.POUND_STERLING;
		UnitOfWork<Void> saveUnitOfWork = (Session session) -> {
			session.save(new CountryEntity(expectedName, currencies));
			return null;
		};
		database.execute(saveUnitOfWork);

		// When
		repository.addCurrency(expectedName, expectedCurrency);
		UnitOfWork<CountryEntity> readUnitOfWork = (Session session) -> {
			return session.createQuery("FROM CountryEntity e INNER JOIN FETCH e.currencies", CountryEntity.class)
					.uniqueResult();
		};
		CountryEntity result = database.execute(readUnitOfWork);

		// Then
		assertThat(result.getName()).isEqualTo(expectedName);
		assertThat(result.getCurrencies()).hasSize(3);
		assertThat(result.getCurrencies()).contains(expectedCurrency);
	}

	@Test
	public void test_removeCurrency() {
		// Given
		CountryName expectedName = CountryName.DEUTSCHLAND;
		Currency notExpectedCurrency = Currency.AMERICAN_DOLAR;
		Set<Currency> currencies = Sets.newLinkedHashSet(notExpectedCurrency, Currency.EURO);
		UnitOfWork<Void> saveUnitOfWork = (Session session) -> {
			session.save(new CountryEntity(expectedName, currencies));
			return null;
		};
		database.execute(saveUnitOfWork);

		// When
		repository.removeCurrency(expectedName, notExpectedCurrency);
		UnitOfWork<CountryEntity> readUnitOfWork = (Session session) -> {
			return session.createQuery("FROM CountryEntity e INNER JOIN FETCH e.currencies", CountryEntity.class)
					.uniqueResult();
		};
		CountryEntity result = database.execute(readUnitOfWork);

		// Then
		assertThat(result.getName()).isEqualTo(expectedName);
		assertThat(result.getCurrencies()).hasSize(1);
		assertThat(result.getCurrencies()).doesNotContain(notExpectedCurrency);
	}

	@Test
	public void test_delete() {
		// Given
		CountryName countryName = CountryName.DEUTSCHLAND;
		Set<Currency> currencies = Sets.newLinkedHashSet(Currency.EURO);
		UnitOfWork<Void> saveUnitOfWork = (Session session) -> {
			session.save(new CountryEntity(countryName, currencies));
			return null;
		};
		database.execute(saveUnitOfWork);

		// When
		repository.delete(countryName);
		UnitOfWork<CountryEntity> readUnitOfWork = (Session session) -> {
			return session.createQuery("FROM CountryEntity", CountryEntity.class).uniqueResult();
		};
		CountryEntity result = database.execute(readUnitOfWork);

		// Then
		assertThat(result).isNull();
	}

	@Test
	public void test_getCountriesHasMoreThanOneCurrency_onlyOneCountry() {
		// Given
		CountryName expectedName = CountryName.DEUTSCHLAND;
		Set<Currency> currencies = Sets.newLinkedHashSet(Currency.AMERICAN_DOLAR, Currency.EURO,
				Currency.POUND_STERLING);
		UnitOfWork<Void> saveUnitOfWork = (Session session) -> {
			session.save(new CountryEntity(expectedName, currencies));
			return null;
		};
		database.execute(saveUnitOfWork);

		// When
		List<Country> result = repository.getCountriesHasMoreThanOneCurrency();

		// Then
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getName()).isEqualTo(expectedName);

	}

	@Test
	public void test_getCountriesHasMoreThanOneCurrency_threeCountry() {
		// Given
		Set<Currency> currencies = Sets.newLinkedHashSet(Currency.AMERICAN_DOLAR, Currency.EURO,
				Currency.POUND_STERLING);
		UnitOfWork<Void> saveUnitOfWork = (Session session) -> {
			session.save(new CountryEntity(CountryName.DEUTSCHLAND, currencies));
			session.save(new CountryEntity(CountryName.UNITED_STATES, currencies));
			session.save(new CountryEntity(CountryName.WAKANDA, Sets.newHashSet()));
			return null;
		};
		database.execute(saveUnitOfWork);

		// When
		List<Country> result = repository.getCountriesHasMoreThanOneCurrency();

		// Then
		assertThat(result).hasSize(2);
		assertThat(result).allMatch(c -> c.getCurrencies().size() > 1);
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
