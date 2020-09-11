package exampleHQL;

import javax.persistence.NoResultException;

import org.hibernate.Session;
import org.hibernate.query.Query;

import exchange_rate.country.database.entity.CountryEntity;
import exchange_rate.database.Database;
import exchange_rate.database.UnitOfWork;
import exchange_rate.downloader.nbp.exception.checked.NotFoundException;
import exchange_rate.enums.CountryName;

public class ExampleCountryRepo {

	private final Database database = Database.getInstance();

	public void getWithoutNPlus1(CountryName countryName) {

		System.out.println("\n\n\n\n\n\n read without n+1");

		UnitOfWork<CountryEntity> unitOfWork = (Session session) -> {
			try {
				Query<CountryEntity> query = session
						.createQuery("FROM CountryEntity e INNER JOIN FETCH e.currencies c WHERE e.name = :name",
								CountryEntity.class)
						.setParameter("name", countryName);

				CountryEntity entity = query.getSingleResult();
				System.out.println(entity.toString());

				return entity;
			} catch (NoResultException e) {
				throw new NotFoundException("Cannot find data for country name: " + countryName + " in database");
			}
		};
		database.execute(unitOfWork);
	}

	public void getWithNPlus1(CountryName countryName) {

		System.out.println("\n\n\n\n\n\n read with n+1");

		UnitOfWork<CountryEntity> unitOfWork = (Session session) -> {
			Query<CountryEntity> query = session
					.createQuery("FROM CountryEntity WHERE name = :name", CountryEntity.class)
					.setParameter("name", countryName);

			CountryEntity entity = query.getSingleResult();
			System.out.println(entity.toString());

			return entity;
		};
		CountryEntity entity = database.execute(unitOfWork);
	}

}
