package exchange_rate.country.database;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.assertj.core.util.Sets;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import country.database.DatabaseCountryRepository;
import country.database.entity.CountryEntity;
import country.database.mapper.CountryMapper;
import country.dto.Country;
import database.Database;
import database.UnitOfWork;
import enums.CountryName;
import exchange_rate.exception.checked.NotFoundException;
import exchange_rate.exception.unchecked.BadRequestException;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class DatabaseCountryRepositoryTestSuite {

	@Test
	public void test_get() {
		// Given
		Database databaseMock = Mockito.mock(Database.class);
		Session sessionMock = Mockito.mock(Session.class);
		CountryMapper mapperMock = Mockito.mock(CountryMapper.class);
		Query queryMock = mockQuery(sessionMock);

		CountryEntity exceptedEntity = new CountryEntity(CountryName.DEUTSCHLAND, Sets.newHashSet());
		when(queryMock.uniqueResultOptional()).thenReturn(Optional.of(exceptedEntity));

		DatabaseCountryRepository repository = new DatabaseCountryRepository(databaseMock);
		repository.setMapper(mapperMock);

		UnitOfWork<CountryEntity> unitOfWork = captUnitOfWork(databaseMock,
				() -> repository.get(CountryName.DEUTSCHLAND));

		// When
		CountryEntity result = unitOfWork.run(sessionMock);

		// Then
		assertThat(result).isEqualTo(exceptedEntity);

	}

	@Test
	public void test_get_noData() {

		// Given
		Database databaseMock = Mockito.mock(Database.class);
		Session sessionMock = Mockito.mock(Session.class);
		CountryMapper mapperMock = Mockito.mock(CountryMapper.class);
		Query queryMock = mockQuery(sessionMock);
		when(queryMock.uniqueResultOptional()).thenReturn(Optional.ofNullable(null));

		DatabaseCountryRepository repository = new DatabaseCountryRepository(databaseMock);
		repository.setMapper(mapperMock);

		UnitOfWork<Country> unitOfWork = captUnitOfWork(databaseMock, () -> repository.get(CountryName.DEUTSCHLAND));

		// When
		Exception e = assertThrows(NotFoundException.class, () -> unitOfWork.run(sessionMock));

		// Then
		assertThat(e).hasMessageContaining("Cannot find data for country name:");
	}

	@Test
	public void test_save_correct() {
		// Given
		Database databaseMock = Mockito.mock(Database.class);
		Session sessionMock = Mockito.mock(Session.class);
		CountryMapper mapperMock = Mockito.mock(CountryMapper.class);
		Query queryMock = mockQuery(sessionMock);
		when(queryMock.uniqueResultOptional()).thenReturn(Optional.ofNullable(null));

		DatabaseCountryRepository repository = new DatabaseCountryRepository(databaseMock);
		repository.setMapper(mapperMock);

		UnitOfWork<Void> unitOfWork = captUnitOfWork(databaseMock,
				() -> repository.save(new Country(CountryName.DEUTSCHLAND, Sets.newHashSet())));

		// When
		unitOfWork.run(sessionMock);

		// Then
		verify(sessionMock).save(any());
		verify(mapperMock).map(any(Country.class));
		verify(mapperMock, never()).map(any(CountryEntity.class));
	}

	@Test
	public void test_save_alreadyExists() {
		// Given
		Database databaseMock = Mockito.mock(Database.class);
		Session sessionMock = Mockito.mock(Session.class);
		CountryMapper mapperMock = Mockito.mock(CountryMapper.class);
		Query queryMock = mockQuery(sessionMock);
		CountryEntity countryEntity = new CountryEntity();

		when(queryMock.uniqueResultOptional()).thenReturn(Optional.of(countryEntity));
		DatabaseCountryRepository repository = new DatabaseCountryRepository(databaseMock);
		repository.setMapper(mapperMock);

		UnitOfWork<Void> unitOfWork = captUnitOfWork(databaseMock,
				() -> repository.save(new Country(CountryName.DEUTSCHLAND, Sets.newHashSet())));

		// When
		Exception e = assertThrows(BadRequestException.class, () -> unitOfWork.run(sessionMock));

		// Then
		verify(sessionMock, never()).save(any());
		verify(mapperMock, never()).map(any(CountryEntity.class));
		verify(mapperMock, never()).map(any(Country.class));
		assertThat(e).hasMessageContaining("Cannot save into database, found data for that country name.");
	}

//	@Test
//	public void test_save_correct() {
//		// Given
//		Country country = new Country(CountryName.WAKANDA,
//				Sets.newLinkedHashSet(Currency.EURO, Currency.AMERICAN_DOLAR));
//		Query<ExchangeRate> queryMock = Mockito.mock(Query.class);
//		when(sessionMock.createNamedQuery(Mockito.anyString())).thenReturn(queryMock);
//
//		CountryRepository repository = new DatabaseCountryRepository(databaseMock);
//
//		// When
//		repository.save(country);
//
//		// Then
//		Mockito.verify(sessionMock).save(queryMock);
//
//		// Clean up
//		repository.delete(countryName);
//	}
//
//	@Test
//	public void test_delete() {
//		// Given
//		CountryName countryName = CountryName.WAKANDA;
//		Set<Currency> currencies = new HashSet<>();
//		currencies.add(Currency.EURO);
//		currencies.add(Currency.AMERICAN_DOLAR);
//		Country country = new Country(countryName, currencies);
//
//		repository.save(country);
//
//		// When
//		repository.delete(countryName);
//		Exception e = assertThrows(NotFoundException.class, () -> repository.get(countryName));
//
//		// Then
//		assertThat(e).hasMessageContaining("Cannot find data for country name");
//	}
//
//	@Test
//	public void test_addCurrency() {
//		// Given
//		CountryName countryName = CountryName.WAKANDA;
//		Set<Currency> currencies = new HashSet<>();
//		currencies.add(Currency.EURO);
//		currencies.add(Currency.AMERICAN_DOLAR);
//		repository.save(new Country(countryName, currencies));
//
//		Currency addedCurrency = Currency.POUND_STERLING;
//
//		// When
//		repository.addCurrency(countryName, addedCurrency);
//		Country result = repository.get(countryName);
//
//		// Then
//		assertThat(result.getCurrencies()).contains(addedCurrency);
//
//		// Clean up
//		repository.delete(countryName);
//	}
//
//	@Test
//	public void test_removeCurrency() {
//		// Given
//		CountryName countryName = CountryName.WAKANDA;
//		Set<Currency> currencies = new HashSet<>();
//
//		Currency removedCurrency = Currency.POUND_STERLING;
//
//		currencies.add(Currency.EURO);
//		currencies.add(Currency.AMERICAN_DOLAR);
//		currencies.add(removedCurrency);
//		repository.save(new Country(countryName, currencies));
//
//		// When
//		repository.removeCurrency(countryName, removedCurrency);
//		Country result = repository.get(countryName);
//
//		// Then
//		assertThat(result.getCurrencies()).doesNotContain(removedCurrency);
//
//		// Clean up
//		repository.delete(countryName);
//	}

	private Query mockQuery(Session sessionMock) {
		Query queryMock = Mockito.mock(Query.class);
		when(queryMock.setParameter(Mockito.anyInt(), Mockito.any())).thenReturn(queryMock);

		when(sessionMock.createNamedQuery(Mockito.anyString(), Mockito.any())).thenReturn(queryMock);

		return queryMock;
	}

	private UnitOfWork captUnitOfWork(Database databaseMock, Runnable repositoryAction) {

		ArgumentCaptor<UnitOfWork<Void>> unitOfWorkCaptor = ArgumentCaptor.forClass(UnitOfWork.class);

		repositoryAction.run();

		verify(databaseMock).execute(unitOfWorkCaptor.capture());

		return unitOfWorkCaptor.getValue();
	}
}
