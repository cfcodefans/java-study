package cf.study.java.javax.cdi.weld.beans;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import misc.MiscUtils;

@Dependent
public class BasicBean {

	private static Logger log = LoggerFactory.getLogger(BasicBean.class);
	
	protected Long createdTime = System.currentTimeMillis();

	@Inject
	protected Long id;

	protected String name;

	public BasicBean() {
		log.info(MiscUtils.invocationInfo());
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
	public Long getCreatedTime() {
		return createdTime;
	}
	
	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@PostConstruct
	public void postConstruct() {
		log.info(MiscUtils.invocationInfo() + "\n\t" + this);
	}
	
	@PreDestroy
	public void preDestroy() {
		log.info(MiscUtils.invocationInfo() + "\n\t" + this);
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE); 
	}
}