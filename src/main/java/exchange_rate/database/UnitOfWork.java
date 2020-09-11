package exchange_rate.database;

import org.hibernate.Session;

@FunctionalInterface
public interface UnitOfWork<T> {

	public T run(Session session);
}
