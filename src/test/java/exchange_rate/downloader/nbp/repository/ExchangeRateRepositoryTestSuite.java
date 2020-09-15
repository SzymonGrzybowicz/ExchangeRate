package exchange_rate.downloader.nbp.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.NoResultException;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import database.Database;
import database.UnitOfWork;
import enums.Currency;
import exchange_rate.database.entity.ExchangeRateEntity;
import exchange_rate.database.mapper.ExchangeRateEntityMapper;
import exchange_rate.dto.ExchangeRate;
import exchange_rate.exception.unchecked.BadRequestException;
import exchange_rate.repository.ExchangeRateRepository;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ExchangeRateRepositoryTestSuite {

	@Test
	public void test_save() {
		// Given
		Database databaseMock = Mockito.mock(Database.class);
		Session sessionMock = Mockito.mock(Session.class);
		Query query = mockQuery(sessionMock);
		ExchangeRateEntityMapper mapperMock = Mockito.mock(ExchangeRateEntityMapper.class);
		ExchangeRateEntity expectedEntity = new ExchangeRateEntity(new BigDecimal("123.123"), LocalDate.MAX,
				Currency.AMERICAN_DOLAR);
		when(mapperMock.map(any(ExchangeRate.class))).thenReturn(expectedEntity);

		when(query.getSingleResult()).thenThrow(NoResultException.class);

		ExchangeRateRepository repository = new ExchangeRateRepository(databaseMock);
		repository.setMapper(mapperMock);

		// When
		UnitOfWork unitOfWork = captUnitOfWork(databaseMock, () -> repository
				.save(new ExchangeRate(LocalDate.MAX, Currency.AMERICAN_DOLAR, new BigDecimal("1234.1234"))));

		unitOfWork.run(sessionMock);

		// Then
		verify(sessionMock).save(eq(expectedEntity));
	}

	@Test
	public void test_save_alreadyExists() {
		// Given
		Database databaseMock = Mockito.mock(Database.class);
		Session sessionMock = Mockito.mock(Session.class);
		Query query = mockQuery(sessionMock);
		ExchangeRateEntityMapper mapperMock = Mockito.mock(ExchangeRateEntityMapper.class);
		ExchangeRateEntity entity = new ExchangeRateEntity(new BigDecimal("123.123"), LocalDate.MAX,
				Currency.AMERICAN_DOLAR);
		when(query.getSingleResult()).thenReturn(entity);

		ExchangeRateRepository repository = new ExchangeRateRepository(databaseMock);
		repository.setMapper(mapperMock);

		// When
		UnitOfWork unitOfWork = captUnitOfWork(databaseMock, () -> repository
				.save(new ExchangeRate(LocalDate.MAX, Currency.AMERICAN_DOLAR, new BigDecimal("1234.1234"))));

		Exception e = assertThrows(BadRequestException.class, () -> unitOfWork.run(sessionMock));

		// Then
		assertThat(e).hasMessageContaining("Cannot save into database, found data for that currency and date.");
		verify(mapperMock, never()).map(any(ExchangeRate.class));
		verify(mapperMock, never()).map(any(ExchangeRateEntity.class));
	}

//	@Test
//	public void test_saveRead() {
//		// Given
//		LocalDate date = LocalDate.of(1, 1, 1);
//		Currency currency = Currency.EURO;
//		BigDecimal rate = new BigDecimal("2.14");
//		ExchangeRate exceptedResult = new ExchangeRate(date, currency, rate);
//		repository.save(exceptedResult);
//
//		// When
//		ExchangeRate result = repository.get(currency, date);
//
//		// Then
//		assertThat(exceptedResult).isNotNull();
//		assertThat(exceptedResult).isEqualTo(result);
//
//		// Clean up
//		repository.delete(exceptedResult);
//	}
//
//	@Test
//	public void test_delete() {
//		// Given
//		LocalDate date = LocalDate.of(1, 1, 1);
//		Currency currency = Currency.EURO;
//		BigDecimal rate = new BigDecimal("2.14");
//		ExchangeRate exchangeRate = new ExchangeRate(date, currency, rate);
//		repository.save(exchangeRate);
//
//		// When
//		repository.delete(exchangeRate);
//
//		// Then
//		assertThrows(NotFoundException.class, () -> repository.get(currency, date));
//	}
//
//	@Test
//	public void test_update() {
//		// Given
//		LocalDate date = LocalDate.of(1, 1, 1);
//		Currency currency = Currency.EURO;
//		BigDecimal rate = new BigDecimal("2.14");
//		ExchangeRate exchangeRate = new ExchangeRate(date, currency, rate);
//		repository.save(exchangeRate);
//		BigDecimal updatedRate = rate.multiply(new BigDecimal(100));
//
//		// When
//		exchangeRate.setRate(updatedRate);
//		repository.update(exchangeRate);
//		exchangeRate = repository.get(currency, date);
//
//		// Then
//		assertThat(exchangeRate).isNotNull();
//		assertThat(exchangeRate.getRate().doubleValue()).isEqualTo(updatedRate.doubleValue());
//		assertThat(exchangeRate.getDate()).isEqualTo(date);
//		assertThat(exchangeRate.getCurrency()).isEqualTo(currency);
//
//		// Clean up
//		repository.delete(exchangeRate);
//	}

	private Query mockQuery(Session sessionMock) {
		Query queryMock = Mockito.mock(Query.class);
		when(queryMock.setParameter(Mockito.anyString(), Mockito.any())).thenReturn(queryMock);

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