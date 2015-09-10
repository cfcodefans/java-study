package cf.study.oo.dynamic.cglib;

import org.apache.commons.lang.builder.ToStringBuilder;


public class LdapUserEntry {

	//TODO need refactory, need optimization
	
	//FIELD the names of fields must consistent with the Attribute Descriptions in ldap server
	//see the com.thenetcircle.services.ldap.api.LdapConnector.map(SearchResultEntry, Class<T>)
	private String cn;
	private String sn;
	private String userPassword;

	public String getCn() {
		return cn;
	}

	public void setCn(String cn) {
		this.cn = cn;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public String getAttr(String attrName) {
		if ("cn".equals(attrName)) {
			return cn;
		}
		if ("sn".equals(attrName)) {
			return sn;
		}
		if ("userPassword".equals(attrName)) {
			return userPassword;
		}
		return null;
	}
}
