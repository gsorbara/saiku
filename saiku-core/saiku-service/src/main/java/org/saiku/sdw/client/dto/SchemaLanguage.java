package org.saiku.sdw.client.dto;

import java.util.Date;

public class SchemaLanguage {

	private String language;
	private String xml;
	private String fileName;
	private Date creationTimestamp;
	private Date lastXmlUpdate;
	private Date lastPublished;
	private Boolean enabled;

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Date getCreationTimestamp() {
		return creationTimestamp;
	}

	public void setCreationTimestamp(Date creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

	public Date getLastXmlUpdate() {
		return lastXmlUpdate;
	}

	public void setLastXmlUpdate(Date lastXmlUpdate) {
		this.lastXmlUpdate = lastXmlUpdate;
	}

	public Date getLastPublished() {
		return lastPublished;
	}

	public void setLastPublished(Date lastPublished) {
		this.lastPublished = lastPublished;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	
}
