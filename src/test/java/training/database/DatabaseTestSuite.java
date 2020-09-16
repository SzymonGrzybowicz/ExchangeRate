package training.database;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import training.exchange_rate.exception.unchecked.BadRequestException;

public class DatabaseTestSuite {

	@Mock
	private SessionFactory sessionFactoryMock;

	@Mock
	private Session sessionMock;

	@Mock
	private Transaction transactionMock;

	private Database database;

	@Before
	public void init() {
		MockitoAnnotations.openMocks(this);
		when(sessionFactoryMock.openSession()).thenReturn(sessionMock);
		when(sessionMock.getTransaction()).thenReturn(transactionMock);
		database = new Database(sessionFactoryMock);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void test_execute_voidUnitOfWork() throws Exception {
		// Given
		UnitOfWork<Void> unitOfWork = Mockito.mock(UnitOfWork.class);
		when(unitOfWork.run(any())).thenReturn(null);

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
		when(sessionFactoryMock.openSession()).thenReturn(sessionMock);
		when(sessionMock.getTransaction()).thenReturn(transactionMock);

		String exceptedString = "exceptedString";
		UnitOfWork<String> unitOfWork = (Session session) -> {
			return exceptedString;
		};

		// When
		String result = database.execute(unitOfWork);

		// Then
		assertThat(result).isEqualTo(exceptedString);

		verify(sessionFactoryMock).openSession();
		verify(transactionMock).commit();
		verify(sessionMock).close();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void test_execute_throwsException() {
		// Given
		UnitOfWork<Void> unitOfWork = Mockito.mock(UnitOfWork.class);
		String exceptedMessage = "testMESSAGE";
		when(unitOfWork.run(any())).thenThrow(new BadRequestException(exceptedMessage));

		// Then
		Exception e = assertThrows(BadRequestException.class, () -> database.execute(unitOfWork));

		// Then
		assertThat(e).hasMessage(exceptedMessage);
		verify(sessionFactoryMock).openSession();
		verify(transactionMock).rollback();
		verify(sessionMock).close();
	}
}
