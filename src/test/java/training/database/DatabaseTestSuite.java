package training.database;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Test;
import org.mockito.Mockito;

import training.database.Database;
import training.database.UnitOfWork;

public class DatabaseTestSuite {

	@Test
	@SuppressWarnings("unchecked")
	public void test_execute_voidUnitOfWork() throws Exception {
		// Given
		SessionFactory sessionFactoryMock = Mockito.mock(SessionFactory.class);
		Session sessionMock = Mockito.mock(Session.class);
		Transaction transactionMock = Mockito.mock(Transaction.class);

		when(sessionFactoryMock.openSession()).thenReturn(sessionMock);
		when(sessionMock.getTransaction()).thenReturn(transactionMock);

		UnitOfWork<Void> unitOfWork = Mockito.mock(UnitOfWork.class);
		when(unitOfWork.run(any())).thenReturn(null);

		Database database = new Database(sessionFactoryMock);

		// When
		database.execute(unitOfWork);

		// Then
		verify(unitOfWork).run(eq(sessionMock));
		verify(sessionFactoryMock).openSession();
		verify(transactionMock).commit();
		verify(sessionMock).close();
	}

	@Test
	public void test_execute_objectUnitOfWork() {
		// Given
		SessionFactory sessionFactoryMock = Mockito.mock(SessionFactory.class);
		Session sessionMock = Mockito.mock(Session.class);
		Transaction transactionMock = Mockito.mock(Transaction.class);

		when(sessionFactoryMock.openSession()).thenReturn(sessionMock);
		when(sessionMock.getTransaction()).thenReturn(transactionMock);

		String exceptedString = "exceptedString";
		UnitOfWork<String> unitOfWork = (Session session) -> {
			return exceptedString;
		};

		Database database = new Database(sessionFactoryMock);

		// When
		String result = database.execute(unitOfWork);

		// Then
		assertThat(result).isEqualTo(exceptedString);

		verify(sessionFactoryMock).openSession();
		verify(transactionMock).commit();
		verify(sessionMock).close();
	}
}
