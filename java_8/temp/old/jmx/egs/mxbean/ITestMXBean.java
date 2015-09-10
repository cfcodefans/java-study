package cf.study.jmx.egs.mxbean;

import java.util.Set;

import javax.management.MXBean;

@MXBean
public interface ITestMXBean {
	String getString();
	Integer getInteger();
	Double getDouble();
//	Serializable getSerializable();
	Long getLong();
	
	void foo();
	
	Set<String> getStrings();
	Set<Long> getLongs();

	void setStrings(Set<String> strs);
}
