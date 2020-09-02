package exchange_rate.database;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import exchange_rate.database.entity.ExchangeRateEntity;

public class ExchangeRateRepositoryTestSuite {

	private ExchangeRateRepository repository = new ExchangeRateRepository();

	@Test
	public void testSaveInDatabase() {
		ExchangeRateEntity entity = new ExchangeRateEntity(2.14, new Date(), "STH");
		long id = repository.save(entity);
		Assert.assertNotNull(id);
		Assert.assertNotEquals(0, id);
		repository.delete(entity);
	}

	@Test
	public void testReadFromDatabase() {
		ExchangeRateEntity entity = new ExchangeRateEntity(2.14, new Date(1234), "STH");
		long id = repository.save(entity);

		entity = repository.read(id);

		Assert.assertNotNull(entity);
		Assert.assertEquals(2.14, entity.getRate(), 0.1);
		Assert.assertEquals("STH", entity.getCurrencyAlphabeticalCode());
		Assert.assertEquals(1234, entity.getDate().getTime());
	}

	@Test
	public void testDeleteFromDatabase() {
		ExchangeRateEntity entity = new ExchangeRateEntity(2.14, new Date(1234), "STH");
		long id = repository.save(entity);

		repository.delete(entity);

		entity = repository.read(id);

		Assert.assertNull(entity);
	}

	@Test
	public void testUpdateInDatabase() {
		ExchangeRateEntity entity = new ExchangeRateEntity(2.14, new Date(), "STH");
		long firstId = repository.save(entity);

		entity.setRate(0.2);
		entity.setDate(new Date(1234));
		entity.setCurrencyAlphabeticalCode("TEST");

		long secondId = repository.update(entity);

		entity = repository.read(secondId);

		Assert.assertEquals(firstId, entity.getId());
		Assert.assertEquals(0.2, entity.getRate(), 0.1);
		Assert.assertEquals(1234, entity.getDate().getTime());
		Assert.assertEquals("TEST", entity.getCurrencyAlphabeticalCode());

	}
}
