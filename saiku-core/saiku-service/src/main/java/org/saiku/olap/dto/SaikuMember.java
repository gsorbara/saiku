/*
 * Copyright (C) 2011 OSBI Ltd
 *
 * This program is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by the Free 
 * Software Foundation; either version 2 of the License, or (at your option) 
 * any later version.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along 
 * with this program; if not, write to the Free Software Foundation, Inc., 
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA 
 *
 */
package org.saiku.olap.dto;

import java.util.List;

public class SaikuMember extends AbstractSaikuObject {
	
	private String caption;
	private String dimensionUniqueName;
	private String description;
	private String levelUniqueName;
	private String hierarchyUniqueName;
	private boolean visible;
	
	//KB: Add MEMBER_KEY property:
	//private List<PropertySaikuMember> properties;
	//Add memberKey:
	private String memberKey;
	//KB Add ChildMemberCount:
	private int childMemberCount;
	
	public SaikuMember() {}

	public SaikuMember(String name, String uniqueName, String caption, String description, String dimensionUniqueName, String hierarchyUniqueName, String levelUniqueName,String memberKey, int childMemberCount) {
		super(uniqueName,name);
		this.caption = caption;
		this.description = description;
		this.dimensionUniqueName = dimensionUniqueName;
		this.levelUniqueName = levelUniqueName;
		this.hierarchyUniqueName = hierarchyUniqueName;
		this.visible = true;
		this.memberKey = memberKey;
		this.childMemberCount = childMemberCount;
	}

	public SaikuMember(String name, String uniqueName, String caption, String description, String dimensionUniqueName, String hierarchyUniqueName, String levelUniqueName, boolean visible, String memberKey, int childMemberCount) {
		super(uniqueName,name);
		this.caption = caption;
		this.description = description;
		this.dimensionUniqueName = dimensionUniqueName;
		this.levelUniqueName = levelUniqueName;
		this.hierarchyUniqueName = hierarchyUniqueName;
		this.visible = visible; 
		this.memberKey = memberKey;
		this.childMemberCount = childMemberCount;
	}

	public SaikuMember(String name, String uniqueName, String caption, String dimensionUniqueName, String levelUniqueName, String memberKey, int childMemberCount) {
		super(uniqueName,name);
		this.caption = caption;
		this.dimensionUniqueName = dimensionUniqueName;
		//this.properties = properties;
		this.memberKey = memberKey;
		this.childMemberCount = childMemberCount;
	}
	
	public String getCaption() {
		return caption;
	}

	public String getDescription() {
		return description;
	}

	public String getLevelUniqueName() {
		return levelUniqueName;
	}
	
	public String getDimensionUniqueName() {
		return dimensionUniqueName;
	}

	public boolean getVisible() {
		return visible;
	}

	public String getHierarchyUniqueName() {
		return hierarchyUniqueName;
	}
	//Getter method for memberKey
	public String getMemberKey() {
		return memberKey;
	}
	//Setter method for memberKey
	public void setMemberKey(String memberKey) {
		this.memberKey = memberKey;
	}
	//Getter method for childMemberCount
	public int getChildMemberCount() {
		return childMemberCount;
	}	
}
