package training.exchange_rate;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.squareup.okhttp.OkHttpClient;

@Configuration
public class BeansConfig {

	@Bean
	public OkHttpClient okHttpClient() {
		return new OkHttpClient();
	}
}
