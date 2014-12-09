package cf.study.java8.javax.cdi.weld.beans;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import misc.MiscUtils;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.log4j.Logger;

public class BasicBean {

	@Inject
	private static Logger log;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getCreatedTime() {
		return createdTime;
	}

	@Inject
	protected Long id;
	protected Long createdTime = System.currentTimeMillis();
	protected String name;
	
	public Long getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof BasicBean))
			return false;
		BasicBean other = (BasicBean) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public BasicBean() {
		log.info(MiscUtils.invocationInfo() + "\n\t" + this);
	}
	
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE); 
	}
	
	@PostConstruct
	public void postConstruct() {
		log.info(MiscUtils.invocationInfo() + "\n\t" + this);
	}
	
	@PreDestroy
	public void preDestroy() {
		log.info(MiscUtils.invocationInfo() + "\n\t" + this);
	}
	
	
}
