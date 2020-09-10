package exchange_rate.nbp_api_client.database;

import org.hibernate.Session;

@FunctionalInterface
public interface UnitOfWork<T> {

	public T run(Session session);
}
