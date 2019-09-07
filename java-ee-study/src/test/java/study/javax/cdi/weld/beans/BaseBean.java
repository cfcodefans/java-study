package study.javax.cdi.weld.beans;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import study.commons.MiscUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.Objects;

@Dependent
public class BaseBean {
    static final Logger log = LoggerFactory.getLogger(BaseBean.class);
    protected Long createdTime = System.currentTimeMillis();
    @Inject
    protected Long id;

    protected String name;

    public Long getCreatedTime() {
        return createdTime;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseBean)) return false;
        BaseBean baseBean = (BaseBean) o;
        return Objects.equals(getId(), baseBean.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

}
