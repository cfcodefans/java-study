package cf.study.oo.dynamic.cglib;


public class EntityMetadata {
	private String idFieldName;
	private String[] attrNames;
	
	public String getIdFieldName() {
		return idFieldName;
	}
	public EntityMetadata(String idFieldName, String...attrNames) {
		super();
		this.idFieldName = idFieldName;
		this.attrNames = attrNames;
	}
	public void setIdFieldName(String idFieldName) {
		this.idFieldName = idFieldName;
	}
	public String[] getAttrNames() {
		return attrNames;
	}
	public void setAttrNames(String[] attrNames) {
		this.attrNames = attrNames;
	}
	
	
}	
