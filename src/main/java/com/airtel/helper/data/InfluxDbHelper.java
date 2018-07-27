package com.airtel.helper.data;

import java.util.concurrent.TimeUnit;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;

import com.airtel.helper.constants.Constants;

public class InfluxDbHelper implements Constants {

	private static InfluxDB createConnectionInflux() {
		InfluxDB influxDB = InfluxDBFactory.connect(INFLUX_URL, INFLUX_USER, INFLUX_PASS);
		influxDB.setDatabase(INFLUX_DB_NAME);
		return influxDB;
	}

	public static void populateAutomationStats(long testDuration, String testName, int passCount, int failCount) {
		InfluxDB influxDB = createConnectionInflux();
		Point testResult = Point.measurement(INFLUX_MEASUREMENT).time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
				.addField("suite_name", testName).addField("run_duration", testDuration)
				.addField("total_test", passCount + failCount).addField("pass_test", passCount)
				.addField("fail_test", failCount).addField("success_pct", passCount * 100 / (passCount + failCount))
				.build();
		influxDB.write(testResult);
		influxDB.close();
	}

}
