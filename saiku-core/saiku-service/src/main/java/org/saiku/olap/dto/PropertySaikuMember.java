package org.saiku.olap.dto;

import java.util.List;

public class PropertySaikuMember extends SaikuMember{

	private List<SaikuProperty> properties;
	
	public PropertySaikuMember(String name, String uniqueName, String caption, String description, String dimensionUniqueName, String hierarchyUniqueName, String levelUniqueName,String memberKey, Integer childMemberCount, List<SaikuProperty> properties) {
		super(name, uniqueName, caption, description, dimensionUniqueName, hierarchyUniqueName, levelUniqueName, memberKey, childMemberCount);
		this.properties = properties;
	}

	public List<SaikuProperty> getProperties() {
		return properties;
	}
	public void setProperties(List<SaikuProperty> properties) {
		this.properties = properties;
	}
	
}
