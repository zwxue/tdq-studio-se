package org.talend.datascience.common.inference.type;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TypeInferenceUtilsTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TypeInferenceUtilsTest.class);

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testIsBoolean() throws Exception {
		List<String> values = loadData("org/talend/datascience/common/inference/testBoolean.csv");
		int countOfBooleans = 0;
		String timeStart = getCurrentTimeStamp();
		LOGGER.debug("Detect boolean start at: " + timeStart);
		// Assert total count.
		Assert.assertEquals(10000, values.size());
		for (String value : values) {
			if (TypeInferenceUtils.isBoolean(value)) {
				countOfBooleans++;
			}
		}
		String timeEnd = getCurrentTimeStamp();
		LOGGER.debug("Detect boolean end at: " + timeEnd);
		// Assert count of matches.
		Assert.assertEquals(2000, countOfBooleans);
		double difference = getTimeDifference(timeStart, timeEnd);

		LOGGER.debug("Detect boolean time diff: " + difference + " s.");
		Assert.assertTrue(difference < 0.005);
	}

	@Test
	public void testIsEmpty() throws Exception {
		List<String> values = loadData("org/talend/datascience/common/inference/testString.csv");
		int countOfEmpties = 0;
		String timeStart = getCurrentTimeStamp();
		LOGGER.debug("Detect empty start at: " + timeStart);
		// Assert total count.
		Assert.assertEquals(10000, values.size());
		for (String value : values) {
			if (TypeInferenceUtils.isEmpty(value)) {
				countOfEmpties++;
			}
		}
		String timeEnd = getCurrentTimeStamp();
		LOGGER.debug("Detect empty end at: " + timeEnd);
		// Assert count of matches.
		Assert.assertEquals(2000, countOfEmpties);
		double difference = getTimeDifference(timeStart, timeEnd);

		LOGGER.debug("Detect empty time diff: " + difference + " s.");
		Assert.assertTrue(difference < 0.006);
	}

	@Test
	public void testIsChar() throws Exception {
		List<String> values = loadData("org/talend/datascience/common/inference/testChar.csv");
		int countOfChars = 0;
		String timeStart = getCurrentTimeStamp();
		LOGGER.debug("Detect char start at: " + timeStart);
		// Assert total count.
		Assert.assertEquals(10000, values.size());
		for (String value : values) {
			if (TypeInferenceUtils.isChar(value)) {
				countOfChars++;
			}
		}
		String timeEnd = getCurrentTimeStamp();
		LOGGER.debug("Detect char end at: " + timeEnd);
		// Assert count of matches.
		Assert.assertEquals(5000, countOfChars);
		double difference = getTimeDifference(timeStart, timeEnd);

		LOGGER.debug("Detect char time diff: " + difference + " s.");
		Assert.assertTrue(difference < 0.05);
	}

	@Test
	public void testIsInteger() throws Exception {
		List<String> values = loadData("org/talend/datascience/common/inference/testInteger.csv");
		int countOfIntegers = 0;
		String timeStart = getCurrentTimeStamp();
		LOGGER.debug("Detect integer start at: " + timeStart);
		// Assert total count.
		Assert.assertEquals(10000, values.size());
		for (String value : values) {
			if (TypeInferenceUtils.isInteger(value)) {
				countOfIntegers++;
			}
		}
		String timeEnd = getCurrentTimeStamp();
		LOGGER.debug("Detect integer end at: " + timeEnd);
		// Assert count of matches.
		Assert.assertEquals(3000, countOfIntegers);
		// Assert time span.
		double difference = getTimeDifference(timeStart, timeEnd);

		LOGGER.debug("Detect integer time diff: " + difference + " s.");
		Assert.assertTrue(difference < 0.08);
	}

	@Test
	public void testIsDouble() throws Exception {
		List<String> values = loadData("org/talend/datascience/common/inference/testDouble.csv");
		int countOfDoubles = 0;
		String timeStart = getCurrentTimeStamp();
		LOGGER.debug("Detect double start at: " + timeStart);
		// Assert total count.
		Assert.assertEquals(10000, values.size());
		for (String value : values) {
			if (TypeInferenceUtils.isDouble(value)) {
				countOfDoubles++;
			}
		}
		String timeEnd = getCurrentTimeStamp();
		LOGGER.debug("Detect double end at: " + timeEnd);
		// Assert count of matches.
		Assert.assertEquals(6000, countOfDoubles);
		// Assert time span.
		double difference = getTimeDifference(timeStart, timeEnd);

		LOGGER.debug("Detect double time diff: " + difference + " s.");
		Assert.assertTrue(difference < 0.09);
	}

	@Test
	public void testIsString() throws Exception {
		List<String> values = loadData("org/talend/datascience/common/inference/testString.csv");
		int countOfStrings = 0;
		String timeStart = getCurrentTimeStamp();
		LOGGER.debug("Detect string start at: " + timeStart);
		// Assert total count.
		Assert.assertEquals(10000, values.size());
		for (String value : values) {
			if (TypeInferenceUtils.isAlphString(value)) {
				countOfStrings++;
			}
		}
		String timeEnd = getCurrentTimeStamp();
		LOGGER.debug("Detect string end at: " + timeEnd);
		// Assert count of matches.
		Assert.assertEquals(2000, countOfStrings);
		// Assert time span.
		double difference = getTimeDifference(timeStart, timeEnd);

		LOGGER.debug("Detect string time diff: " + difference + " s.");
		Assert.assertTrue(difference < 0.11);
	}

	@Test
	public void testIsDate() throws Exception {
		List<String> values = loadData("org/talend/datascience/common/inference/testDate.csv");
		int countOfDates = 0;
		String timeStart = getCurrentTimeStamp();
		LOGGER.debug("Detect date start at: " + timeStart);
		// Assert total count.
		Assert.assertEquals(10000, values.size());
		for (String value : values) {
			if (TypeInferenceUtils.isDate(value)) {
				countOfDates++;
			}
		}
		String timeEnd = getCurrentTimeStamp();
		LOGGER.debug("Detect date end at: " + timeEnd);
		// Assert count of matches.
		Assert.assertEquals(6001, countOfDates);
		double difference = getTimeDifference(timeStart, timeEnd);

		LOGGER.debug("Detect date time diff: " + difference + " s.");
		Assert.assertTrue(difference < 0.43);
	}

	private List<String> loadData(String path) throws IOException {
		List<String> values = IOUtils.readLines(this.getClass()
				.getClassLoader().getResourceAsStream(path));
		return values;
	}

	public static double getTimeDifference(String timeStart, String timeEnd) {
		String startMin = StringUtils.substringAfterLast(
				StringUtils.substringBeforeLast(timeStart, ":"), ":");
		String endMin = StringUtils.substringAfterLast(
				StringUtils.substringBeforeLast(timeEnd, ":"), ":");

		String lEnd = StringUtils.substringAfterLast(timeEnd, ".");
		String lStart = StringUtils.substringAfterLast(timeStart, ".");
		double endTimeInSecond = Double.valueOf(StringUtils.substringAfterLast(
				StringUtils.substringBeforeLast(timeEnd, "."), ":")
				+ "."
				+ lEnd);
		double startTimeInSecond = Double.valueOf(StringUtils
				.substringAfterLast(
						StringUtils.substringBeforeLast(timeStart, "."), ":")
				+ "." + lStart);
		double difference = 0;
		difference = endTimeInSecond - startTimeInSecond;
		return difference
				+ (Integer.valueOf(endMin) - Integer.valueOf(startMin)) * 60;
	}

	public static String getCurrentTimeStamp() {
		SimpleDateFormat sdfDate = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss.SSS");
		Date now = new Date();
		String strDate = sdfDate.format(now);
		return strDate;
	}

}
