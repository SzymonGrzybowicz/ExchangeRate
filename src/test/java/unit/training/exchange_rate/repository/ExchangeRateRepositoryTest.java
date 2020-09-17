package unit.training.exchange_rate.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import training.database.Database;
import training.database.UnitOfWork;
import training.enums.Currency;
import training.exchange_rate.database.entity.ExchangeRateEntity;
import training.exchange_rate.database.mapper.ExchangeRateEntityMapper;
import training.exchange_rate.dto.ExchangeRate;
import training.exchange_rate.exception.checked.NotFoundException;
import training.exchange_rate.exception.unchecked.BadRequestException;
import training.exchange_rate.repository.ExchangeRateRepository;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ExchangeRateRepositoryTest {

	@Mock
	private Session sessionMock;

	@Mock
	private Query queryMock;

	@Mock
	private Database databaseMock;

	@Mock
	private ExchangeRateEntityMapper mapperMock;

	private ExchangeRateRepository repository;

	@Before
	public void init() {
		MockitoAnnotations.openMocks(this);

		when(sessionMock.createNamedQuery(Mockito.anyString(), Mockito.any())).thenReturn(queryMock);
		when(queryMock.setParameter(Mockito.anyString(), Mockito.any())).thenReturn(queryMock);
		repository = new ExchangeRateRepository(databaseMock, mapperMock);
	}

	@Test
	public void test_save() {
		// Given
		ExchangeRateEntity expectedEntity = new ExchangeRateEntity(new BigDecimal("123.321"), LocalDate.MAX,
				Currency.AMERICAN_DOLAR);
		ExchangeRate exchangeRate = new ExchangeRate(LocalDate.MAX, Currency.AMERICAN_DOLAR,
				new BigDecimal("1234.1234"));

		when(queryMock.uniqueResultOptional()).thenReturn(Optional.ofNullable(null));
		when(mapperMock.map(eq(exchangeRate))).thenReturn(expectedEntity);

		UnitOfWork unitOfWork = captUnitOfWork(() -> repository.save(exchangeRate));

		// When
		unitOfWork.run(sessionMock);

		// Then
		verify(sessionMock).save(eq(expectedEntity));
	}

	@Test
	public void test_save_alreadyExists() {
		// Given
		ExchangeRateEntity entity = new ExchangeRateEntity(new BigDecimal("123.321"), LocalDate.MAX,
				Currency.AMERICAN_DOLAR);
		ExchangeRate exchangeRate = new ExchangeRate(LocalDate.MAX, Currency.AMERICAN_DOLAR,
				new BigDecimal("1234.1234"));

		when(queryMock.uniqueResultOptional()).thenReturn(Optional.of(entity));

		UnitOfWork unitOfWork = captUnitOfWork(() -> repository.save(exchangeRate));

		// When
		Exception e = assertThrows(BadRequestException.class, () -> unitOfWork.run(sessionMock));

		// Then
		assertThat(e).hasMessageContaining("Cannot save into database, found data for that currency and date.");
		verify(mapperMock, never()).map(any(ExchangeRate.class));
		verify(mapperMock, never()).map(any(ExchangeRateEntity.class));
	}

	@Test
	public void test_get() {
		// Given
		ExchangeRateEntity exceptedEntity = new ExchangeRateEntity(new BigDecimal("123.321"), LocalDate.MAX,
				Currency.AMERICAN_DOLAR);

		when(queryMock.uniqueResultOptional()).thenReturn(Optional.of(exceptedEntity));
		when(databaseMock.execute(any())).thenReturn(exceptedEntity);

		UnitOfWork<ExchangeRateEntity> unitOfWork = captUnitOfWork(
				() -> repository.get(Currency.AMERICAN_DOLAR, LocalDate.MAX));

		// When
		ExchangeRateEntity result = unitOfWork.run(sessionMock);

		// Then
		assertThat(result).isEqualTo(exceptedEntity);
		verify(mapperMock).map(exceptedEntity);
		verify(mapperMock, never()).map(any(ExchangeRate.class));
	}

	@Test
	public void test_get_notFound() {
		// Given
		when(queryMock.uniqueResultOptional()).thenReturn(Optional.ofNullable(null));

		UnitOfWork<ExchangeRateEntity> unitOfWork = captUnitOfWork(
				() -> repository.get(Currency.AMERICAN_DOLAR, LocalDate.MAX));

		// When
		Exception e = assertThrows(NotFoundException.class, () -> unitOfWork.run(sessionMock));

		// Then
		assertThat(e).hasMessageContaining("Cannot find data for currency:");
		verify(mapperMock, never()).map(any(ExchangeRateEntity.class));
		verify(mapperMock, never()).map(any(ExchangeRate.class));

	}

	@Test
	public void test_delete() {
		// Given
		ExchangeRateEntity exceptedEntity = new ExchangeRateEntity(new BigDecimal("123.321"), LocalDate.MAX,
				Currency.AMERICAN_DOLAR);
		ExchangeRate exchangeRate = new ExchangeRate(LocalDate.MAX, Currency.AMERICAN_DOLAR,
				new BigDecimal("1234.1234"));

		when(queryMock.uniqueResultOptional()).thenReturn(Optional.ofNullable(exceptedEntity));

		UnitOfWork<ExchangeRateEntity> unitOfWork = captUnitOfWork(() -> repository.delete(exchangeRate));

		// When
		unitOfWork.run(sessionMock);

		// Then
		verify(sessionMock).delete(eq(exceptedEntity));
		verify(mapperMock, never()).map(any(ExchangeRateEntity.class));
		verify(mapperMock, never()).map(any(ExchangeRate.class));

	}

	@Test
	public void test_delete_notFound() {
		// Given
		ExchangeRate exchangeRate = new ExchangeRate(LocalDate.MAX, Currency.AMERICAN_DOLAR,
				new BigDecimal("1234.1234"));

		when(queryMock.uniqueResultOptional()).thenReturn(Optional.ofNullable(null));

		UnitOfWork<ExchangeRateEntity> unitOfWork = captUnitOfWork(() -> repository.delete(exchangeRate));

		// When
		Exception e = assertThrows(NotFoundException.class, () -> unitOfWork.run(sessionMock));

		// Then
		assertThat(e).hasMessageContaining("Cannot delete for currency:");
		verify(sessionMock, never()).delete(any());
		verify(mapperMock, never()).map(any(ExchangeRateEntity.class));
		verify(mapperMock, never()).map(any(ExchangeRate.class));

	}

	@Test
	public void test_update() {
		// Given
		ExchangeRateEntity entity = new ExchangeRateEntity(new BigDecimal("123.321"), LocalDate.MAX,
				Currency.AMERICAN_DOLAR);
		BigDecimal expectedRate = new BigDecimal("1111.1111");
		ExchangeRate exchangeRate = new ExchangeRate(LocalDate.MAX, Currency.AMERICAN_DOLAR, expectedRate);

		when(queryMock.uniqueResultOptional()).thenReturn(Optional.of(entity));

		UnitOfWork<ExchangeRateEntity> unitOfWork = captUnitOfWork(() -> repository.update(exchangeRate));

		// When
		unitOfWork.run(sessionMock);

		// Then
		ArgumentCaptor<ExchangeRateEntity> captor = ArgumentCaptor.forClass(ExchangeRateEntity.class);
		verify(sessionMock).update(captor.capture());
		ExchangeRateEntity result = captor.getValue();
		assertThat(result.getRate()).isEqualByComparingTo(expectedRate);
		verify(mapperMock, never()).map(any(ExchangeRateEntity.class));
		verify(mapperMock, never()).map(any(ExchangeRate.class));
	}

	@Test
	public void test_update_notFound() {
		// Given
		ExchangeRate exchangeRate = new ExchangeRate(LocalDate.MAX, Currency.AMERICAN_DOLAR,
				new BigDecimal("1234.1234"));

		when(queryMock.uniqueResultOptional()).thenReturn(Optional.ofNullable(null));

		UnitOfWork<ExchangeRateEntity> unitOfWork = captUnitOfWork(() -> repository.update(exchangeRate));

		// When
		Exception e = assertThrows(BadRequestException.class, () -> unitOfWork.run(sessionMock));

		// Then
		assertThat(e).hasMessageContaining("Cannot update in database, not found data for that currency and date. ");
		verify(sessionMock, never()).update(any());
		verify(mapperMock, never()).map(any(ExchangeRateEntity.class));
		verify(mapperMock, never()).map(any(ExchangeRate.class));
	}

	@Test
	public void test_getMaximumRateInPeriod() {
		// Given
		ExchangeRateEntity exceptedEntity = new ExchangeRateEntity(new BigDecimal("123.321"), LocalDate.MAX,
				Currency.AMERICAN_DOLAR);

		when(queryMock.uniqueResultOptional()).thenReturn(Optional.of(exceptedEntity));
		when(databaseMock.execute(any())).thenReturn(exceptedEntity);

		UnitOfWork<ExchangeRateEntity> unitOfWork = captUnitOfWork(
				() -> repository.getMaximumRateInPeriod(Currency.AMERICAN_DOLAR, LocalDate.MIN, LocalDate.MAX));

		// When
		ExchangeRateEntity result = unitOfWork.run(sessionMock);

		// Then
		assertThat(result).isEqualTo(exceptedEntity);
		verify(mapperMock).map(exceptedEntity);
		verify(mapperMock, never()).map(any(ExchangeRate.class));
	}

	@Test
	public void test_getMaximumRateInPeriod_notFound() {
		// Given
		when(queryMock.uniqueResultOptional()).thenReturn(Optional.ofNullable(null));

		UnitOfWork<ExchangeRateEntity> unitOfWork = captUnitOfWork(
				() -> repository.getMaximumRateInPeriod(Currency.AMERICAN_DOLAR, LocalDate.MIN, LocalDate.MAX));

		// When
		Exception e = assertThrows(NotFoundException.class, () -> unitOfWork.run(sessionMock));

		// Then
		assertThat(e).hasMessageContaining("Cannot find data for currency: ");
		verify(mapperMock, never()).map(any(ExchangeRateEntity.class));
		verify(mapperMock, never()).map(any(ExchangeRate.class));
	}

	@Test
	public void test_getMinimumRateInPeriod() {
		// Given
		ExchangeRateEntity exceptedEntity = new ExchangeRateEntity(new BigDecimal("123.321"), LocalDate.MAX,
				Currency.AMERICAN_DOLAR);

		when(queryMock.uniqueResultOptional()).thenReturn(Optional.of(exceptedEntity));
		when(databaseMock.execute(any())).thenReturn(exceptedEntity);

		UnitOfWork<ExchangeRateEntity> unitOfWork = captUnitOfWork(
				() -> repository.getMinimumRateInPeriod(Currency.AMERICAN_DOLAR, LocalDate.MIN, LocalDate.MAX));

		// When
		ExchangeRateEntity result = unitOfWork.run(sessionMock);

		// Then
		assertThat(result).isEqualTo(exceptedEntity);
		verify(mapperMock).map(exceptedEntity);
		verify(mapperMock, never()).map(any(ExchangeRate.class));
	}

	@Test
	public void test_getMinimumRateInPeriod_notFound() {
		// Given
		when(queryMock.uniqueResultOptional()).thenReturn(Optional.ofNullable(null));

		UnitOfWork<ExchangeRateEntity> unitOfWork = captUnitOfWork(
				() -> repository.getMinimumRateInPeriod(Currency.AMERICAN_DOLAR, LocalDate.MIN, LocalDate.MAX));

		// When
		Exception e = assertThrows(NotFoundException.class, () -> unitOfWork.run(sessionMock));

		// Then
		assertThat(e).hasMessageContaining("Cannot find data for currency: ");
		verify(mapperMock, never()).map(any(ExchangeRateEntity.class));
		verify(mapperMock, never()).map(any(ExchangeRate.class));
	}

	@Test
	public void test_getCurrencyWithHighestExchangeRateDifferenceInPeriod() {
		// Given
		Currency expectedCurrency = Currency.AMERICAN_DOLAR;

		when(queryMock.uniqueResultOptional()).thenReturn(Optional.of(expectedCurrency));

		UnitOfWork<Currency> unitOfWork = captUnitOfWork(
				() -> repository.getCurrencyWithHighestExchangeRateDifferenceInPeriod(LocalDate.MIN, LocalDate.MAX));

		// When
		Currency result = unitOfWork.run(sessionMock);

		// Then
		assertThat(result).isEqualTo(expectedCurrency);
		verify(mapperMock, never()).map(any(ExchangeRateEntity.class));
		verify(mapperMock, never()).map(any(ExchangeRate.class));
	}

	@Test
	public void test_getCurrencyWithHighestExchangeRateDifferenceInPeriod_notFound() {
		// Given
		when(queryMock.uniqueResultOptional()).thenReturn(Optional.ofNullable(null));

		UnitOfWork<Currency> unitOfWork = captUnitOfWork(
				() -> repository.getCurrencyWithHighestExchangeRateDifferenceInPeriod(LocalDate.MIN, LocalDate.MAX));

		// When
		Exception e = assertThrows(NotFoundException.class, () -> unitOfWork.run(sessionMock));

		// Then
		assertThat(e).hasMessageContaining("Cannot find data for period from:");
		verify(mapperMock, never()).map(any(ExchangeRateEntity.class));
		verify(mapperMock, never()).map(any(ExchangeRate.class));
	}

	@Test
	public void test_getFiveMaximumExchangeRate() {
		// Given
		List<ExchangeRateEntity> exceptedResult = new ArrayList<>();
		exceptedResult.add(new ExchangeRateEntity(new BigDecimal(1), LocalDate.MAX, Currency.AMERICAN_DOLAR));
		exceptedResult.add(new ExchangeRateEntity(new BigDecimal(2), LocalDate.MAX, Currency.AMERICAN_DOLAR));
		exceptedResult.add(new ExchangeRateEntity(new BigDecimal(3), LocalDate.MAX, Currency.AMERICAN_DOLAR));
		exceptedResult.add(new ExchangeRateEntity(new BigDecimal(4), LocalDate.MAX, Currency.AMERICAN_DOLAR));
		exceptedResult.add(new ExchangeRateEntity(new BigDecimal(5), LocalDate.MAX, Currency.AMERICAN_DOLAR));

		when(queryMock.getResultList()).thenReturn(exceptedResult);
		when(databaseMock.execute(any())).thenReturn(exceptedResult);

		UnitOfWork<List<ExchangeRateEntity>> unitOfWork = captUnitOfWork(
				() -> repository.getFiveMaximumExchangeRate(Currency.EURO));

		// When
		List<ExchangeRateEntity> result = unitOfWork.run(sessionMock);

		// Then
		assertThat(result).isEqualTo(exceptedResult);
		verify(mapperMock, times(5)).map(any(ExchangeRateEntity.class));
		verify(mapperMock, never()).map(any(ExchangeRate.class));
	}

	@Test
	public void test_getFiveMinimumExchangeRate() {
		// Given
		List<ExchangeRateEntity> exceptedResult = new ArrayList<>();
		exceptedResult.add(new ExchangeRateEntity(new BigDecimal(1), LocalDate.MAX, Currency.AMERICAN_DOLAR));
		exceptedResult.add(new ExchangeRateEntity(new BigDecimal(2), LocalDate.MAX, Currency.AMERICAN_DOLAR));
		exceptedResult.add(new ExchangeRateEntity(new BigDecimal(3), LocalDate.MAX, Currency.AMERICAN_DOLAR));
		exceptedResult.add(new ExchangeRateEntity(new BigDecimal(4), LocalDate.MAX, Currency.AMERICAN_DOLAR));
		exceptedResult.add(new ExchangeRateEntity(new BigDecimal(5), LocalDate.MAX, Currency.AMERICAN_DOLAR));

		when(queryMock.getResultList()).thenReturn(exceptedResult);
		when(databaseMock.execute(any())).thenReturn(exceptedResult);

		UnitOfWork<List<ExchangeRateEntity>> unitOfWork = captUnitOfWork(
				() -> repository.getFiveMinimumExchangeRate(Currency.EURO));

		// When
		List<ExchangeRateEntity> result = unitOfWork.run(sessionMock);

		// Then
		assertThat(result).isEqualTo(exceptedResult);
		verify(mapperMock, times(5)).map(any(ExchangeRateEntity.class));
		verify(mapperMock, never()).map(any(ExchangeRate.class));
	}

	private UnitOfWork captUnitOfWork(Runnable repositoryAction) {
		ArgumentCaptor<UnitOfWork<Void>> unitOfWorkCaptor = ArgumentCaptor.forClass(UnitOfWork.class);

		repositoryAction.run();

		verify(databaseMock).execute(unitOfWorkCaptor.capture());

		return unitOfWorkCaptor.getValue();
	}
}