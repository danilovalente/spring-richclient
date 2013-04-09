package org.springframework.richclient.samples.simple.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.binding.format.Formatter;
import org.springframework.binding.format.InvalidFormatException;
import org.springframework.binding.format.support.AbstractFormatter;
import org.springframework.binding.format.support.DateFormatter;
import org.springframework.binding.format.support.SimpleFormatterFactory;
import org.springframework.util.StringUtils;

/**
 * Simple formatter factory that returns a custom date/time formatter.  By default, this 
 * formatter is used to format all date fields in the application.
 *
 * @author Keith Donald
 */
public class SimpleAppFormatterFactory extends SimpleFormatterFactory {

	public Formatter getDateTimeFormatter() {
		return new AppDateFormatter();
	}

	/**
	 * Formatter for date fields in the application.
	 * @author Larry and Geoffrey (by Keith)
	 */
	class AppDateFormatter extends AbstractFormatter {

		/** Default Date format. */
		private final DateFormatter format = new DateFormatter(new SimpleDateFormat("MM-dd-yyyy"));

		/** Pattern to verify date contains full 4 digit year. */
		private final Pattern MDY_PATTERN = Pattern.compile("[0-9]{1,2}-[0-9]{1,2}-[0-9]{4}");

		protected String doFormatValue(Object value) {
			return (value == null) ? "" : format.formatValue(value);
		}

		protected Object doParseValue(String formattedString, Class targetClass) throws InvalidFormatException,
				ParseException {
			String src = (String) formattedString;
			// If the user entered slashes, convert them to dashes
			if (src.indexOf('/') >= 0) {
				src = src.replace('/', '-');
			}
			Object value = null;
			if (StringUtils.hasText(src)) {

				Matcher matcher = MDY_PATTERN.matcher(src);

				if (!matcher.matches()) {
					throw new ParseException("Invalid date format: " + src, 0);
				}

				value = format.parseValue(src, Date.class);
			}
			return value;
		}
	}
}
