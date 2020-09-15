package exchange_rate.database;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import database.Database;
import database.UnitOfWork;
import database.util.HibernateUtil;

@RunWith(PowerMockRunner.class)
public class DatabaseTestSuite {

	@Test
	@PrepareForTest(HibernateUtil.class)
	@SuppressWarnings("unchecked")
	public void test_execute_voidUnitOfWork() throws Exception {
		// Given
		PowerMockito.mockStatic(HibernateUtil.class);
		SessionFactory sessionFactoryMock = Mockito.mock(SessionFactory.class);
		Session sessionMock = Mockito.mock(Session.class);
		Transaction transactionMock = Mockito.mock(Transaction.class);

		when(HibernateUtil.getSessionFactory()).thenReturn(sessionFactoryMock);
		when(sessionFactoryMock.openSession()).thenReturn(sessionMock);
		when(sessionMock.getTransaction()).thenReturn(transactionMock);

		UnitOfWork<Void> unitOfWork = Mockito.mock(UnitOfWork.class);
		when(unitOfWork.run(any())).thenReturn(null);

		Database database = Database.getInstance();

		// When
		database.execute(unitOfWork);

		// Then
		verify(unitOfWork).run(eq(sessionMock));
		verify(sessionFactoryMock).openSession();
		verify(transactionMock).commit();
		verify(sessionMock).close();
	}

	@Test
	@PrepareForTest(HibernateUtil.class)
	public void test_execute_objectUnitOfWork() {
		// Given
		PowerMockito.mockStatic(HibernateUtil.class);
		SessionFactory sessionFactoryMock = Mockito.mock(SessionFactory.class);
		Session sessionMock = Mockito.mock(Session.class);
		Transaction transactionMock = Mockito.mock(Transaction.class);

		when(HibernateUtil.getSessionFactory()).thenReturn(sessionFactoryMock);
		when(sessionFactoryMock.openSession()).thenReturn(sessionMock);
		when(sessionMock.getTransaction()).thenReturn(transactionMock);

		String exceptedString = "exceptedString";
		UnitOfWork<String> unitOfWork = (Session session) -> {
			return exceptedString;
		};

		Database database = Database.getInstance();

		// When
		String result = database.execute(unitOfWork);

		// Then
		assertThat(result).isEqualTo(exceptedString);

		verify(sessionFactoryMock).openSession();
		verify(transactionMock).commit();
		verify(sessionMock).close();
	}
}
