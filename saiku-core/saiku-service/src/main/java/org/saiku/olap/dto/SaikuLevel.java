/*  
 *   Copyright 2012 OSBI Ltd
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.saiku.olap.dto;

public class SaikuLevel extends AbstractSaikuObject {
	
	private String caption;
	private String hierarchyUniqueName;
	private String dimensionUniqueName;
//	private transient List<SaikuMember> members;
	private boolean visible;
	private String description;
	//KB Adding in level depth and cardinality:
	private int depth;
	private int cardinality;
	
	public SaikuLevel() {
		super(null,null);
		throw new RuntimeException("Unsupported Constructor. Serialization only");
	}

	//Add new variable to constructor 
	public SaikuLevel(
			String name, 
			String uniqueName, 
			String caption, 
			String description,
			String dimensionUniqueName, 
			String hierarchyUniqueName,
			int depth,
			int cardinality,
			boolean visible) 
	{
		super(uniqueName,name);
		this.caption = caption;
		this.hierarchyUniqueName = hierarchyUniqueName;
		this.dimensionUniqueName = dimensionUniqueName;
		this.visible = visible;
		this.description = description;
//		this.members = members;
		this.depth = depth;
		this.cardinality = cardinality;
	}

	public String getCaption() {
		return caption;
	}
	
	public String getHierarchyUniqueName() {
		return hierarchyUniqueName;
	}
	
	public String getDimensionUniqueName() {
		return dimensionUniqueName;
	}
	
	//Added getter method for depth and cardinality
	public int getDepth() {
		return depth;
	}
	
	public int getCardinality() {
		return cardinality;
	}

	public boolean isVisible() {
		return visible;

	}	

	public String getDescription() {
		return description;
	}
	
//	public List<SaikuMember> getMembers() {
//		return members;
//	}
}
