package cf.study.web.jetty;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * DateXmlAdapter
 * 
 * @author carsten@thenetcircle.com
 */
public class DateXmlAdapter extends XmlAdapter<String, Date> {
	// MEMBERS
	final DateFormat format;

	// CONSTRUCTION
	public DateXmlAdapter() {
		this(Messages.DATE_FORMAT.get());
	}

	public DateXmlAdapter(final String pattern) {
		this(pattern, TimeZone.getDefault(), Locale.getDefault());
	}

	public DateXmlAdapter(final String pattern, final TimeZone timeZone,
			final Locale locale) {
		super();

		this.format = new SimpleDateFormat(pattern, locale);
		this.format.setTimeZone(timeZone);
	}

	// OVERRIDES/IMPLEMENTS
	@Override
	public String marshal(final Date value) throws Exception {
		return format.format(value);
	}

	@Override
	public Date unmarshal(final String value) throws Exception {
		return format.parse(value);
	}
}
