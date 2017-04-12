package cf.study.web.jetty;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

//import com.thenetcircle.javax.xml.bind.XmlList;

/**
 * XmlError
 * 
 * @author carsten@thenetcircle.com
 */
@XmlRootElement(name = "error")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlError implements Serializable {
	// PREDEFINED PROPERTIES
	private static final long serialVersionUID = 1L;

	// MEMBERS
	@XmlElement(name = "message")
	final String message;
	@XmlElement(name = "stacktrace")
	final XmlStackTrace stacktrace;

	// CONSTRUCTION
	public XmlError() {
		this(null, null);
	}

	public XmlError(final String message)
	{
		this(message, null);
	}

	public XmlError(final Throwable throwable)
	{
		this(throwable.getMessage(), throwable);
	}

	public XmlError(final String message, final Throwable throwable)
	{
		this.message = message;
		this.stacktrace = new XmlStackTrace(throwable);
	}

	/**
	 * XmlStackTrace
	 * 
	 * @author carsten@thenetcircle.com
	 */
	@XmlRootElement(name = "stacktrace")
	@XmlAccessorType(XmlAccessType.NONE)
	public static class XmlStackTrace
	{
		// MEMBERS
		@XmlElement(name = "element")
		final List<String> elements = null;//new XmlList<>();

		// CONSTRUCTION
		public XmlStackTrace()
		{
			this(null);
		}

		public XmlStackTrace(final Throwable throwable)
		{
			super();

			if (null != throwable)
			{
				for (final StackTraceElement element : throwable.getStackTrace())
					elements.add(element.toString());
			}
		}
	}
}
