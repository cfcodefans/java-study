package cf.study.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TestPojo {
	private long numAttr = System.currentTimeMillis();
	private String strAttr = TestPojo.class.getSimpleName();
	private Date dateAttr = new Date();
	private Collection<TestPojo> colAttr = new ArrayList<TestPojo>();
	private Map<String, Object> mapAttr = new HashMap<String, Object>();

	public long getNumAttr() {
		return numAttr;
	}

	public void setNumAttr(long numAttr) {
		this.numAttr = numAttr;
	}

	public String getStrAttr() {
		return strAttr;
	}

	public void setStrAttr(String strAttr) {
		this.strAttr = strAttr;
	}

	public Date getDateAttr() {
		return dateAttr;
	}

	public void setDateAttr(Date dateAttr) {
		this.dateAttr = dateAttr;
	}

	public Collection<TestPojo> getColAttr() {
		return colAttr;
	}

	public void setColAttr(Collection<TestPojo> colAttr) {
		this.colAttr = colAttr;
	}

	public Map<String, Object> getMapAttr() {
		return mapAttr;
	}

	public void setMapAttr(Map<String, Object> mapAttr) {
		this.mapAttr = mapAttr;
	}

}