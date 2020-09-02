package exchange_rate;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import com.squareup.okhttp.OkHttpClient;

import exchange_rate.database.ExchangeRateRepository;
import exchange_rate.database.entity.ExchangeRateEntity;
import exchange_rate.nbp_api_client.Currency;
import exchange_rate.nbp_api_client.NbpClient;
import exchange_rate.utils.StringUtils;

public class Application {

	private ExchangeRateRepository repository = new ExchangeRateRepository();

	public void run() {
		checkExchangeRate();
	}

	private void checkExchangeRate() {

		NbpClient nbpClient = new NbpClient(new OkHttpClient());

		switch (getCurrencyNumber()) {
		case 1:
			nbpClient.requestExchangeRate(Currency.EURO).subscribe(v -> {
				repository.save(new ExchangeRateEntity(v, new Date(), Currency.EURO.getAlphabeticCode()));
				System.out.println("Kurs Euro : " + v);
			});
			break;

		case 2:
			nbpClient.requestExchangeRate(Currency.AMERICAN_DOLAR).subscribe(v -> {
				repository.save(new ExchangeRateEntity(v, new Date(), Currency.AMERICAN_DOLAR.getAlphabeticCode()));
				System.out.println("Kurs Dolara : " + v);
			});
			break;
		case 3:
			nbpClient.requestExchangeRate(Currency.POUND_STERLING).subscribe(v -> {
				repository.save(new ExchangeRateEntity(v, new Date(), Currency.POUND_STERLING.getAlphabeticCode()));
				System.out.println("Kurs Funta Brytyjskiego : " + v);
			});
			break;
		case 8:
			Calendar calendar = Calendar.getInstance();
			calendar.set(2019, 8, 1);
			Date start = calendar.getTime();
			nbpClient.getMinimumExchangeRateInPeroid(Currency.EURO, start, new Date()).subscribe(v -> {
				System.out.println("Najni¿szy kurs Euro: " + v);
			});
			break;
		case 9:
			showAllDatabaseRecords();
			break;
		}

	}

	private int getCurrencyNumber() {
		Scanner scanner = new Scanner(System.in);
		while (true) {
			System.out.println("Wybierz walute ktorej kurs chcesz sprawdzic:");
			System.out.println("1 - Euro");
			System.out.println("2 - Dolar Amerykanski");
			System.out.println("3 - Funt Brytyjski");
			System.out.println("8 - wez w okresie");
			System.out.println("9 - Poka¿ zapisane rekordy w bazie danych");
			System.out.println("0 - Wyjœcie");

			String line = scanner.nextLine();

			if (!StringUtils.isNumber(line) || (Integer.parseInt(line) != 1 && Integer.parseInt(line) != 2
					&& Integer.parseInt(line) != 3 && Integer.parseInt(line) != 8 && Integer.parseInt(line) != 9
					&& Integer.parseInt(line) != 0)) {
				System.out.println("Nie rozpoznano!");
			} else {
				scanner.close();
				return Integer.parseInt(line);
			}
		}
	}

	private void showAllDatabaseRecords() {
		List<ExchangeRateEntity> list = repository.getAllRecords();
		for (ExchangeRateEntity entity : list) {
			System.out.println(entity);
		}
	}

}
