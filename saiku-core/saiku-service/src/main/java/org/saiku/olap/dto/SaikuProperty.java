package org.saiku.olap.dto;

public class SaikuProperty{

	private String name;
	private String caption;
	private String value;
	
	public SaikuProperty(String name, String caption, String value){
		this.name = name;
		this.caption = caption;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCaption() {
		return caption;
	}
	public void setCaption(String caption) {
		this.caption = caption;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
}
