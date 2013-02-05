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
package org.saiku.olap.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Collection;

import org.olap4j.Axis;
import org.olap4j.OlapException;
import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.Hierarchy;
import org.olap4j.metadata.Level;
import org.olap4j.metadata.Measure;
import org.olap4j.metadata.Member;
import org.olap4j.metadata.NamedList;
import org.olap4j.metadata.Property.StandardMemberProperty;
import org.olap4j.query.QueryAxis;
import org.olap4j.query.QueryDimension;
import org.olap4j.query.Selection;
import org.saiku.olap.dto.SaikuAxis;
import org.saiku.olap.dto.SaikuDimension;
import org.saiku.olap.dto.SaikuDimensionSelection;
import org.saiku.olap.dto.SaikuHierarchy;
import org.saiku.olap.dto.SaikuLevel;
import org.saiku.olap.dto.SaikuMember;
import org.saiku.olap.dto.PropertySaikuMember;
import org.saiku.olap.dto.SaikuProperty;
import org.saiku.olap.dto.SaikuQuery;
import org.saiku.olap.dto.SaikuSelection;
import org.saiku.olap.dto.SaikuSelection.Type;
import org.saiku.olap.query.IQuery;
import org.olap4j.metadata.Property;
import org.saiku.service.util.exception.SaikuServiceException;

public class ObjectUtil {

	private static List<String> standardPropKeys;
	
	static {
		standardPropKeys = new ArrayList<String>();
		StandardMemberProperty sProps[]  = Property.StandardMemberProperty.values();
		for(StandardMemberProperty s : sProps) {
			standardPropKeys.add(s.getName());
		}
		
		// Add extra standard properties not listed by the olap4j API.
		//
		standardPropKeys.add("$name");
		standardPropKeys.add("$scenario");
		standardPropKeys.add("CELL_FORMATTER");
		standardPropKeys.add("CELL_FORMATTER_SCRIPT");
		standardPropKeys.add("CELL_FORMATTER_SCRIPT_LANGUAGE");
		standardPropKeys.add("DISPLAY_FOLDER");
		standardPropKeys.add("FORMAT_EXP");
		standardPropKeys.add("KEY");
	}

	public static SaikuDimension convert(Dimension dim) {
		//SaikuDimension sDim = new SaikuDimension(dim.getName(), dim.getUniqueName(), dim.getCaption(), dim.getDescription(), convertHierarchies(dim.getHierarchies()));
				
		//KB: Add dimension type and description:
		//SaikuDimension sDim = new SaikuDimension(dim.getName(), dim.getUniqueName(), dim.getCaption(), convertHierarchies(dim.getHierarchies()));
		String dimensionType = null;
		String description = dim.getDescription();
		try {
			dimensionType = (dim.getDimensionType() == null ? "" : dim.getDimensionType().toString());
		} catch (OlapException oe) {} //ignore		
		
		SaikuDimension sDim = new SaikuDimension(dim.getName(), dim.getUniqueName(), dim.getCaption(), description, dimensionType, convertHierarchies(dim.getHierarchies()));
		//end KB
		
		return sDim;
	}

	public static SaikuDimension convert(QueryDimension dim) {
		return convert(dim.getDimension());
	}

	public static List<SaikuDimension> convertQueryDimensions(List<QueryDimension> dims) {
		List<SaikuDimension> dimList = new ArrayList<SaikuDimension>();
		for (QueryDimension d : dims) {
			dimList.add(convert(d));
		}
		return dimList;
	}
	
	public static List<SaikuDimension> convertDimensions(List<Dimension> dims) {
		List<SaikuDimension> dimList = new ArrayList<SaikuDimension>();
		for (Dimension d : dims) {
			dimList.add(convert(d));
		}
		return dimList;
	}

	public static List<SaikuHierarchy> convertHierarchies(List<Hierarchy> hierarchies) {
		List<SaikuHierarchy> hierarchyList= new ArrayList<SaikuHierarchy>();
		for (Hierarchy h : hierarchies) {
			hierarchyList.add(convert(h));
		}
		return hierarchyList;

	}

	public static SaikuHierarchy convert(Hierarchy hierarchy) {
		try {			
			String defaultMember = hierarchy.getDefaultMember() != null && !hierarchy.getDefaultMember().isAll() ? 
					hierarchy.getDefaultMember().getUniqueName() : 
					null;
			return new SaikuHierarchy(
					hierarchy.getName(), 
					hierarchy.getUniqueName(), 
					hierarchy.getCaption(), 
					hierarchy.getDescription(), 
					hierarchy.getDimension().getUniqueName(), 
					defaultMember,
					hierarchy.isVisible(),
					convertLevels(hierarchy.getLevels()), 
					convertMembers(hierarchy.getRootMembers(), null, null));
			
		} catch (OlapException e) {
			throw new SaikuServiceException("Cannot get root members",e);
		}
	}

	public static List<SaikuLevel> convertLevels(List<Level> levels) {
		List<SaikuLevel> levelList= new ArrayList<SaikuLevel>();
		for (Level l : levels) {
			levelList.add(convert(l));
		}
		return levelList;

	}

	public static SaikuLevel convert(Level level) {
		try {
			
//			List<SaikuMember> members = convertMembers(level.getMembers());
						
			//KB Adding in level depth and cardinality:
 			return new SaikuLevel(
 					level.getName(), 
 					level.getUniqueName(), 
 					level.getCaption(), 
 					level.getDescription(),
 					level.getDimension().getUniqueName(), 
					level.getHierarchy().getUniqueName(),
					level.getDepth(),
					level.getCardinality(),
					level.isVisible());
			/*return new SaikuLevel(
					level.getName(), 
					level.getUniqueName(), 
					level.getCaption(), 
					level.getDimension().getUniqueName(), 
					level.getHierarchy().getUniqueName(),
					level.isVisible());*/
		}
		catch (Exception e) {
			throw new SaikuServiceException("Cannot convert level: " + level,e);
		}
	}

	public static List<SaikuMember> convertMembers(Collection<Member> members, String properties, Integer[] childMemberCount) {
		
		List<SaikuMember> memberList= new ArrayList<SaikuMember>();

		int i = 0;
		for (Member l : members) {
			
			Integer count = childMemberCount != null ? childMemberCount[i] : null;
			memberList.add(convert(l, properties, count));
		}
		return memberList;
	}
	
	public static List<SaikuSelection> convertSelections(List<Selection> selections) {
		List<SaikuSelection> selectionList= new ArrayList<SaikuSelection>();
		for (Selection sel : selections) {
			selectionList.add(convert(sel));
		}
		return selectionList;
	}

	private static SaikuSelection convert(Selection sel) {
		Type type;
		String hierarchyUniqueName;
		String levelUniqueName;
		if (Level.class.isAssignableFrom(sel.getRootElement().getClass())) {
			type = SaikuSelection.Type.LEVEL;
			hierarchyUniqueName = ((Level) sel.getRootElement()).getHierarchy().getUniqueName();
			levelUniqueName = sel.getUniqueName();
		} else {
			type = SaikuSelection.Type.MEMBER;
			hierarchyUniqueName = ((Member) sel.getRootElement()).getHierarchy().getUniqueName();
			levelUniqueName = ((Member) sel.getRootElement()).getLevel().getUniqueName();
		}
		return new SaikuSelection(
				sel.getRootElement().getName(),
				sel.getUniqueName(),
				sel.getRootElement().getCaption(),
				sel.getRootElement().getDescription(),
				sel.getDimension().getName(),
				hierarchyUniqueName,
				levelUniqueName,
				type);

	}

	
	public static SaikuMember convert(Member m, String properties, Integer childMemberCount) {
		
		String memberKey = "";
		try {
			Object memberKeyObj = m.getPropertyValue(Property.StandardMemberProperty.MEMBER_KEY);
			memberKey = (memberKeyObj != null ? memberKeyObj.toString() : null);
		} catch (Exception e) {
		//ignore
		}
		
		if (m instanceof Measure) {
			
			return new SaikuMember(
					m.getName(), 
					m.getUniqueName(), 
					m.getCaption(), 
					m.getDescription(),
					m.getDimension().getUniqueName(),
					m.getHierarchy().getUniqueName(),
					m.getLevel().getUniqueName(),
					m.isVisible(),
					memberKey,
					childMemberCount);
		} else {

			if(properties != null) {

				List<SaikuProperty> prop = extractProperties(m, properties);
				return new PropertySaikuMember(
						m.getName(), 
						m.getUniqueName(), 
						m.getCaption(), 
						m.getDescription(),
						m.getDimension().getUniqueName(),
						m.getHierarchy().getUniqueName(),
						m.getLevel().getUniqueName(),
						memberKey,
						childMemberCount,
						prop);
				
			} else {

				return new SaikuMember(
						m.getName(), 
						m.getUniqueName(), 
						m.getCaption(), 
						m.getDescription(),
						m.getDimension().getUniqueName(),
						m.getHierarchy().getUniqueName(),
						m.getLevel().getUniqueName(),
						memberKey,
						childMemberCount);
			}
		}
	}
	
	
	private static List<SaikuProperty> extractProperties(Member m, String properties) {
		
		List<SaikuProperty> prop = new ArrayList<SaikuProperty>();	
		try {

			// Iterate through member properties.
			//
			NamedList<Property> memberProperties = m.getProperties();								
			if(memberProperties != null) {

				if(!"all".equalsIgnoreCase(properties)) {

					String propertiesSelectList[] = properties.split(",");
					HashSet<String> propHash = new HashSet<String>();
					propHash.addAll(Arrays.asList(propertiesSelectList));
					ArrayList<String> propKeys = new ArrayList<String>();
					propKeys.addAll(propHash);
					
					for(String propertiesSelect : propHash) {

						Property p = memberProperties.get(propertiesSelect);
						
						if(p != null) {
							if(!standardPropKeys.contains(p.getName())) {
								if(p.getUniqueName().equals(propertiesSelect) || p.getName().equals(propertiesSelect)) {

									prop.add(new SaikuProperty(
													p.getName() , 
													p.getCaption(), 
													m.getPropertyFormattedValue(p)));
								}
							}
						}		
					}
					
				} else {

					for(Property p : memberProperties) {

						if(!standardPropKeys.contains(p.getName())) {
							
							prop.add(new SaikuProperty(
											p.getName() , 
											p.getCaption(), 
											m.getPropertyFormattedValue(p)));
						}
					}
				}
			}					
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}

		return prop;
	}
	
	public static SaikuDimensionSelection convertDimensionSelection(QueryDimension dim) {
		List<SaikuSelection> selections = ObjectUtil.convertSelections(dim.getInclusions());
		return new SaikuDimensionSelection(
				dim.getName(),
				dim.getDimension().getUniqueName(),
				dim.getDimension().getCaption(),
				dim.getDimension().getDescription(),
				selections);
	}
	
	public static List<SaikuDimensionSelection> convertDimensionSelections(List<QueryDimension> dimensions) {
		List<SaikuDimensionSelection> dims = new ArrayList<SaikuDimensionSelection>();
		for (QueryDimension dim : dimensions) {
			dims.add(convertDimensionSelection(dim));
		}
		return dims;
	}
	
	public static SaikuAxis convertQueryAxis(QueryAxis axis) {
		List<SaikuDimensionSelection> dims = ObjectUtil.convertDimensionSelections(axis.getDimensions());
		Axis location = axis.getLocation();
		String so = axis.getSortOrder() == null? null : axis.getSortOrder().name();
		return new SaikuAxis(
				location.name(),
				location.axisOrdinal(),
				axis.getName(),
				dims,
				so,
				axis.getSortIdentifierNodeName());
	}
	
	public static SaikuQuery convert(IQuery q) {
		List<SaikuAxis> axes = new ArrayList<SaikuAxis>();
		if (q.getType().equals(IQuery.QueryType.QM)) {
			for (Axis axis : q.getAxes().keySet()) {
				if (axis != null) {
					axes.add(convertQueryAxis(q.getAxis(axis)));
				}
			}
		}
		return new SaikuQuery(q.getName(), q.getSaikuCube(), axes, q.getMdx(), q.getType().toString());
		
	}

}
