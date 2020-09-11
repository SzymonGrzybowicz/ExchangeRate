package exchange_rate.nbp_api_client.database;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hibernate.Session;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import exchange_rate.nbp_api_client.database.util.HibernateUtil;

public class DatabaseTestSuite {

	@Test
	public void test_execute_voidUnitOfWork() throws Exception {
		// Given
		UnitOfWork<Void> unitOfWork = Mockito.mock(UnitOfWork.class);
		when(unitOfWork.run(any())).thenReturn(null);

		Database database = new Database();

		// When
		database.execute(unitOfWork);

		// Then
		verify(unitOfWork).run(any());
	}

	@Test
	public void test_execute_objectUnitOfWork() {
		// Given
		String exceptedString = "exceptedString";
		UnitOfWork<String> unitOfWork = (Session session) -> {
			return exceptedString;
		};

		Database database = new Database();

		// When
		String result = database.execute(unitOfWork);

		// Then
		PowerMockito.verifyStatic(HibernateUtil.class);
		assertThat(result).isEqualTo(exceptedString);
	}
}
