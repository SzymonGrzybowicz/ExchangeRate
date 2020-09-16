package training.exchange_rate;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.squareup.okhttp.OkHttpClient;

import training.exchange_rate.cache.Cache;
import training.exchange_rate.cache.database.DatabaseCache;
import training.exchange_rate.repository.ExchangeRateRepository;

@Configuration
public class BeansConfig {

	private ExchangeRateRepository exchangeRateRepository;

	public BeansConfig(ExchangeRateRepository exchangeRateRepository) {
		this.exchangeRateRepository = exchangeRateRepository;
	}

	@Bean
	public Cache cache() {
		return new DatabaseCache(exchangeRateRepository);
	}

	@Bean
	public OkHttpClient okHttpClient() {
		return new OkHttpClient();
	}
}
