package training.country.database;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.util.Sets;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import training.country.CountryRepository;
import training.country.database.entity.CountryEntity;
import training.country.database.mapper.CountryMapper;
import training.country.dto.Country;
import training.database.Database;
import training.database.UnitOfWork;
import training.enums.CountryName;
import training.enums.Currency;
import training.exchange_rate.exception.checked.NotFoundException;
import training.exchange_rate.exception.unchecked.BadRequestException;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class DatabaseCountryRepositoryTest {

	@Mock
	private Session sessionMock;

	@Mock
	private Query queryMock;

	@Mock
	private Database databaseMock;

	@Mock
	private CountryMapper mapperMock;

	private CountryRepository repository;

	@Before
	public void init() {
		MockitoAnnotations.openMocks(this);

		when(sessionMock.createNamedQuery(Mockito.anyString(), Mockito.any())).thenReturn(queryMock);
		when(queryMock.setParameter(Mockito.anyString(), Mockito.any())).thenReturn(queryMock);
		repository = new DatabaseCountryRepository(mapperMock, databaseMock);
	}

	@Test
	public void test_get() {
		// Given
		CountryEntity exceptedEntity = new CountryEntity(CountryName.DEUTSCHLAND, Sets.newHashSet());
		when(queryMock.uniqueResultOptional()).thenReturn(Optional.of(exceptedEntity));

		UnitOfWork<CountryEntity> unitOfWork = captUnitOfWork(() -> repository.get(CountryName.DEUTSCHLAND));

		// When
		CountryEntity result = unitOfWork.run(sessionMock);

		// Then
		assertThat(result).isEqualTo(exceptedEntity);
	}

	@Test
	public void test_get_notFound() {
		// Given
		when(queryMock.uniqueResultOptional()).thenReturn(Optional.ofNullable(null));

		UnitOfWork<Country> unitOfWork = captUnitOfWork(() -> repository.get(CountryName.DEUTSCHLAND));

		// When
		Exception e = assertThrows(NotFoundException.class, () -> unitOfWork.run(sessionMock));

		// Then
		assertThat(e).hasMessageContaining("Cannot find data for country name:");
	}

	@Test
	public void test_save() {
		// Given
		when(queryMock.uniqueResultOptional()).thenReturn(Optional.ofNullable(null));

		UnitOfWork<Void> unitOfWork = captUnitOfWork(
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
		CountryEntity countryEntity = new CountryEntity();

		when(queryMock.uniqueResultOptional()).thenReturn(Optional.of(countryEntity));

		UnitOfWork<Void> unitOfWork = captUnitOfWork(
				() -> repository.save(new Country(CountryName.DEUTSCHLAND, Sets.newHashSet())));

		// When
		Exception e = assertThrows(BadRequestException.class, () -> unitOfWork.run(sessionMock));

		// Then
		verify(sessionMock, never()).save(any());
		verify(mapperMock, never()).map(any(CountryEntity.class));
		verify(mapperMock, never()).map(any(Country.class));
		assertThat(e).hasMessageContaining("Cannot save into database, found data for that country name.");
	}

	@Test
	public void test_addCurrency() {
		// Given
		Currency expectedCurrency = Currency.EURO;
		CountryName expectedName = CountryName.DEUTSCHLAND;
		CountryEntity entity = new CountryEntity(expectedName, Sets.newHashSet());
		when(queryMock.uniqueResultOptional()).thenReturn(Optional.of(entity));

		UnitOfWork<CountryEntity> unitOfWork = captUnitOfWork(
				() -> repository.addCurrency(expectedName, expectedCurrency));

		// When
		unitOfWork.run(sessionMock);

		// Then
		ArgumentCaptor<CountryEntity> entityCaptor = ArgumentCaptor.forClass(CountryEntity.class);
		verify(sessionMock).update(entityCaptor.capture());
		CountryEntity resultEntity = entityCaptor.getValue();
		assertThat(resultEntity.getName()).isEqualTo(expectedName);
		assertThat(resultEntity.getCurrencies()).containsOnly(expectedCurrency);
	}

	@Test
	public void test_addCurrency_notFound() {
		// Given
		when(queryMock.uniqueResultOptional()).thenReturn(Optional.ofNullable(null));

		UnitOfWork<Country> unitOfWork = captUnitOfWork(
				() -> repository.addCurrency(CountryName.DEUTSCHLAND, Currency.EURO));

		// When
		Exception e = assertThrows(NotFoundException.class, () -> unitOfWork.run(sessionMock));

		// Then
		assertThat(e).hasMessageContaining("cannot find country.");
	}

	@Test
	public void test_addCurrency_alreadyExists() {
		// Given
		Currency currency = Currency.EURO;
		CountryName name = CountryName.DEUTSCHLAND;
		CountryEntity entity = new CountryEntity(name, Sets.newLinkedHashSet(currency));
		when(queryMock.uniqueResultOptional()).thenReturn(Optional.of(entity));

		UnitOfWork<CountryEntity> unitOfWork = captUnitOfWork(() -> repository.addCurrency(name, currency));

		// When
		Exception e = assertThrows(BadRequestException.class, () -> unitOfWork.run(sessionMock));

		// Then
		assertThat(e).hasMessageContaining("country already have this currency.");
	}

	@Test
	public void test_removeCurrency() {
		// Given
		Currency currency = Currency.EURO;
		CountryName name = CountryName.DEUTSCHLAND;
		CountryEntity entity = new CountryEntity(name, Sets.newLinkedHashSet(currency));
		when(queryMock.uniqueResultOptional()).thenReturn(Optional.of(entity));

		UnitOfWork<CountryEntity> unitOfWork = captUnitOfWork(() -> repository.removeCurrency(name, currency));

		// When
		unitOfWork.run(sessionMock);

		// Then
		ArgumentCaptor<CountryEntity> entityCaptor = ArgumentCaptor.forClass(CountryEntity.class);
		verify(sessionMock).update(entityCaptor.capture());
		CountryEntity resultEntity = entityCaptor.getValue();
		assertThat(resultEntity.getName()).isEqualTo(name);
		assertThat(resultEntity.getCurrencies()).doesNotContain(currency);
	}

	@Test
	public void test_removeCurrency_notFoundCountry() {
		// Given
		when(queryMock.uniqueResultOptional()).thenReturn(Optional.ofNullable(null));

		UnitOfWork<Country> unitOfWork = captUnitOfWork(
				() -> repository.removeCurrency(CountryName.DEUTSCHLAND, Currency.EURO));

		// When
		Exception e = assertThrows(NotFoundException.class, () -> unitOfWork.run(sessionMock));

		// Then
		assertThat(e).hasMessageContaining("cannot find country.");
	}

	@Test
	public void test_addCurrency_notContainsCurrency() {
		// Given
		Currency currency = Currency.EURO;
		CountryName name = CountryName.DEUTSCHLAND;
		CountryEntity entity = new CountryEntity(name, Sets.newLinkedHashSet());
		when(queryMock.uniqueResultOptional()).thenReturn(Optional.of(entity));

		UnitOfWork<CountryEntity> unitOfWork = captUnitOfWork(() -> repository.removeCurrency(name, currency));

		// When
		Exception e = assertThrows(BadRequestException.class, () -> unitOfWork.run(sessionMock));

		// Then
		assertThat(e).hasMessageContaining("country do not have this currency.");
	}

	@Test
	public void test_delete() {
		// Given
		CountryName name = CountryName.DEUTSCHLAND;
		CountryEntity entity = new CountryEntity(name, Sets.newLinkedHashSet());
		when(queryMock.uniqueResultOptional()).thenReturn(Optional.of(entity));

		UnitOfWork<Void> unitOfWork = captUnitOfWork(() -> repository.delete(name));

		// When
		unitOfWork.run(sessionMock);

		// Then
		ArgumentCaptor<CountryEntity> entityCaptor = ArgumentCaptor.forClass(CountryEntity.class);
		verify(sessionMock).remove(entityCaptor.capture());
		CountryEntity resultEntity = entityCaptor.getValue();
		assertThat(resultEntity).isEqualTo(entity);
		verify(mapperMock, never()).map(any(Country.class));
		verify(mapperMock, never()).map(any(CountryEntity.class));
	}

	@Test
	public void test_delete_notFound() {
		// Given
		when(queryMock.uniqueResultOptional()).thenReturn(Optional.ofNullable(null));

		UnitOfWork<Void> unitOfWork = captUnitOfWork(() -> repository.delete(CountryName.DEUTSCHLAND));

		// When
		Exception e = assertThrows(NotFoundException.class, () -> unitOfWork.run(sessionMock));

		// Then
		assertThat(e).hasMessageContaining("Cannot remove country with name:");
	}

	@Test
	public void test_getCountriesHasMoreThanOneCurrency() {
		// Given
		List<CountryEntity> expectedList = new ArrayList<>();
		expectedList.add(new CountryEntity(CountryName.DEUTSCHLAND,
				Sets.newLinkedHashSet(Currency.AMERICAN_DOLAR, Currency.EURO)));
		expectedList.add(new CountryEntity(CountryName.DEUTSCHLAND,
				Sets.newLinkedHashSet(Currency.AMERICAN_DOLAR, Currency.EURO)));
		expectedList.add(new CountryEntity(CountryName.DEUTSCHLAND,
				Sets.newLinkedHashSet(Currency.AMERICAN_DOLAR, Currency.EURO)));

		when(queryMock.getResultList()).thenReturn(expectedList);
		when(databaseMock.execute(any())).thenReturn(expectedList);

		UnitOfWork<List<CountryEntity>> unitOfWork = captUnitOfWork(
				() -> repository.getCountriesHasMoreThanOneCurrency());

		// When
		List<CountryEntity> result = unitOfWork.run(sessionMock);

		// Then
		assertThat(result).isEqualTo(expectedList);
		verify(mapperMock, times(3)).map(any(CountryEntity.class));
		verify(mapperMock, never()).map(any(Country.class));
	}

	private UnitOfWork captUnitOfWork(Runnable repositoryAction) {

		ArgumentCaptor<UnitOfWork<Void>> unitOfWorkCaptor = ArgumentCaptor.forClass(UnitOfWork.class);

		repositoryAction.run();

		verify(databaseMock).execute(unitOfWorkCaptor.capture());

		return unitOfWorkCaptor.getValue();
	}
}
