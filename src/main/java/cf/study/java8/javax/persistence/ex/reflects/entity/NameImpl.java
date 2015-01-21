package cf.study.java8.javax.persistence.ex.reflects.entity;

import javax.lang.model.element.Name;

public class NameImpl implements Name {
	public final CharSequence content;

	public NameImpl(CharSequence content) {
		super();
		this.content = content;
	}

	public int length() {
		return content.length();
	}

	public char charAt(int index) {
		return content.charAt(index);
	}

	public CharSequence subSequence(int start, int end) {
		return content.subSequence(start, end);
	}

	public boolean contentEquals(CharSequence cs) {
		return content.equals(cs);
	}
	
	public String toString() {
		return String.valueOf(content);
	}
}