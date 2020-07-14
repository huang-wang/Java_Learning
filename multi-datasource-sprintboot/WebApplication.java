package csg.ios.app;

import java.util.Calendar;

import javax.annotation.PostConstruct;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@ServletComponentScan
@SpringBootApplication
@EnableScheduling
@Import(DynamicDataSourceRegister.class)
public class WebApplication {
	protected final Logger log = LoggerFactory.getLogger(WebApplication.class);

	public static final String version = "v1.1.200322.01a";

	@Value("${spring.profiles.active:prod}")
	private String profile;

	public static void main(String[] args) {
		SpringApplication.run(WebApplication.class, args);
	}

	@PostConstruct
	private void init() {
	}

	@Bean
	public PaginationInterceptor paginationInterceptor() {
		return new PaginationInterceptor();
	}

	@Scheduled(cron = "5 0 0 * * ?") // (fixedRate = 1000)
	private void onDailyWork() {
		Calendar cale = Calendar.getInstance();
		int _day = cale.get(Calendar.DAY_OF_MONTH);
		int today = cale.get(Calendar.YEAR) * 100 + cale.get(Calendar.MONTH) + 1;
		today = today * 100 + _day;

		if (today > Constants.IDATE) {
			int weekDay = cale.get(Calendar.DAY_OF_WEEK);
			if (cale.getFirstDayOfWeek() == Calendar.SUNDAY) {
				if (weekDay == 1)
					weekDay = 7;
				else
					weekDay = weekDay - 1;
			}
			Constants.IWEEKDAY = weekDay;

			if (Constants.IDATE == Constants.LAST_DAY) {
				cale.add(Calendar.DAY_OF_YEAR, -1);
				int lastday = cale.get(Calendar.YEAR) * 100 + cale.get(Calendar.MONDAY) + 1;
				Constants.IDATE = lastday * 100 + cale.get(Calendar.DAY_OF_MONTH);
			}

			Constants.LAST_DAY = Constants.IDATE;
			Constants.IDATE = today;
		}

		log.info("daily updated, idate={}, last={}, weekday={}", Constants.IDATE, Constants.LAST_DAY,
				Constants.IWEEKDAY);
	}
}
