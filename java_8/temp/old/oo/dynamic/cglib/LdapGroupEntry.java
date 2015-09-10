package cf.study.oo.dynamic.cglib;

import org.apache.commons.lang.builder.ToStringBuilder;


public class LdapGroupEntry {

	//TODO need refactory, need optimization
	
	//FIELD the names of fields must consistent with the Attribute Descriptions in ldap server
	//see the com.thenetcircle.services.ldap.api.LdapConnector.map(SearchResultEntry, Class<T>)
	private String cn;

	public String getCn() {
		return cn;
	}

	public void setCn(String cn) {
		this.cn = cn;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
