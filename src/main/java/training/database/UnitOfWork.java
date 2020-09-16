package training.database;

import org.hibernate.Session;

@FunctionalInterface
public interface UnitOfWork<T> {

	public T run(Session session);
}
