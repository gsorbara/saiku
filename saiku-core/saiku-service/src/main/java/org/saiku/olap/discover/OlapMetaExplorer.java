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
package org.saiku.olap.discover;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.olap4j.OlapConnection;
import org.olap4j.OlapDatabaseMetaData;
import org.olap4j.OlapException;
import org.olap4j.mdx.IdentifierNode;
import org.olap4j.mdx.IdentifierSegment;
import org.olap4j.metadata.Catalog;
import org.olap4j.metadata.Cube;
import org.olap4j.metadata.Database;
import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.Hierarchy;
import org.olap4j.metadata.Level;
import org.olap4j.metadata.Measure;
import org.olap4j.metadata.Member;
import org.olap4j.metadata.Schema;
import org.saiku.datasources.connection.IConnectionManager;
import org.saiku.olap.dto.SaikuCatalog;
import org.saiku.olap.dto.SaikuConnection;
import org.saiku.olap.dto.SaikuCube;
import org.saiku.olap.dto.SaikuDimension;
import org.saiku.olap.dto.SaikuHierarchy;
import org.saiku.olap.dto.SaikuLevel;
import org.saiku.olap.dto.SaikuMember;
import org.saiku.olap.dto.SaikuSchema;
import org.saiku.olap.util.MemberUtil;
import org.saiku.olap.util.ObjectUtil;
import org.saiku.olap.util.SaikuCubeCaptionComparator;
import org.saiku.olap.util.SaikuDimensionCaptionComparator;
import org.saiku.olap.util.SaikuMemberCaptionComparator;
import org.saiku.olap.util.exception.SaikuOlapException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OlapMetaExplorer {
    
    private static final Logger log = LoggerFactory.getLogger(OlapMetaExplorer.class);

	private IConnectionManager connections;

	public OlapMetaExplorer(IConnectionManager ic) {
		connections = ic;
	}

	public SaikuConnection getConnection(String connectionName) throws SaikuOlapException {
		
		OlapConnection olapcon = connections.getOlapConnection(connectionName);
		
		// TODO: In case we have requested a connection in a language that does not exist
		//		 the result is likely to be null.
		//       We need a more robust approach for this use case !!!!
		if (olapcon == null) {
			log.warn("connection " + connectionName + " not found trying default language");
			// FIXME: Currently hard coded to en but we have to use the default language in the catalog 
			connectionName = connectionName.replaceFirst("(-\\ )([\\w]{2})$", "- en");
			olapcon = connections.getOlapConnection(connectionName);
		}
		
		SaikuConnection connection = null;
		
		if (olapcon != null) {
			List<SaikuCatalog> catalogs = new ArrayList<SaikuCatalog>();
			try {
					for (Catalog cat : olapcon.getOlapCatalogs()) {
						List<SaikuSchema> schemas = new ArrayList<SaikuSchema>();
						for (Schema schem : cat.getSchemas()) {
							List<SaikuCube> cubes = new ArrayList<SaikuCube>();
							for (Cube cub : schem.getCubes()) {
								cubes.add(new SaikuCube(connectionName, cub.getUniqueName(), cub.getName(), cub.getCaption(), cat.getName(), schem.getName(), cub.isVisible()));
							}
							Collections.sort(cubes, new SaikuCubeCaptionComparator());
							schemas.add(new SaikuSchema(schem.getName(),cubes));
						}
						if (schemas.size() == 0) {
							OlapDatabaseMetaData olapDbMeta = olapcon.getMetaData();
                            ResultSet cubesResult = olapDbMeta.getCubes(cat.getName(), null, null);

							try {
								List<SaikuCube> cubes = new ArrayList<SaikuCube>();
								while(cubesResult.next()) {

									cubes.add(new SaikuCube(connectionName, cubesResult.getString("CUBE_NAME"),cubesResult.getString("CUBE_NAME"),
											cubesResult.getString("CUBE_NAME"), cubesResult.getString("CATALOG_NAME"),cubesResult.getString("SCHEMA_NAME")));
								}
								Collections.sort(cubes, new SaikuCubeCaptionComparator());
								schemas.add(new SaikuSchema("",cubes));
							} catch (SQLException e) {
								throw new OlapException(e.getMessage(),e);
							} finally {
							    try {
                                    cubesResult.close();
                                } catch (SQLException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
							}

						}
						Collections.sort(schemas);
						catalogs.add(new SaikuCatalog(cat.getName(),schemas));
					}
			} catch (OlapException e) {
				throw new SaikuOlapException("Error getting objects of connection (" + connectionName + ")" ,e);
			}
			Collections.sort(catalogs);
			connection = new SaikuConnection(connectionName,catalogs);
			return connection;
		}
	
		throw new SaikuOlapException("Cannot find connection: (" + connectionName + ")");
	}

	public List<SaikuConnection> getConnections(List<String> connectionNames) throws SaikuOlapException {
		List<SaikuConnection> connectionList = new ArrayList<SaikuConnection>();
		for (String connectionName : connectionNames) {
			connectionList.add(getConnection(connectionName));
		}
		return connectionList;
	}

	public List<SaikuConnection> getAllConnections() throws SaikuOlapException {
		List<SaikuConnection> cubesList = new ArrayList<SaikuConnection>();
		for (String connectionName : connections.getAllOlapConnections().keySet()) {
			cubesList.add(getConnection(connectionName));
		}
		Collections.sort(cubesList);
		return cubesList;
	}


	public List<SaikuCube> getCubes(String connectionName) {
		OlapConnection olapcon = connections.getOlapConnection(connectionName);
		List<SaikuCube> cubes = new ArrayList<SaikuCube>();
		if (olapcon != null) {
			try {
				for (Catalog cat : olapcon.getOlapCatalogs()) {
					for (Schema schem : cat.getSchemas()) {
						for (Cube cub : schem.getCubes()) {
							cubes.add(new SaikuCube(connectionName, cub.getUniqueName(), cub.getName(), cub.getCaption(), cat.getName(), schem.getName(), cub.isVisible()));
						}
					}
				}
			} catch (OlapException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		Collections.sort(cubes, new SaikuCubeCaptionComparator());
		return cubes;

	}

	public List<SaikuCube> getCubes(List<String> connectionNames) {
		List<SaikuCube> cubesList = new ArrayList<SaikuCube>();
		for (String connectionName : connectionNames) {
			cubesList.addAll(getCubes(connectionName));
		}
		Collections.sort(cubesList, new SaikuCubeCaptionComparator());
		return cubesList;
	}

	public List<SaikuCube> getAllCubes() {
		List<SaikuCube> cubes = new ArrayList<SaikuCube>();
		for (String connectionName : connections.getAllOlapConnections().keySet()) {
			cubes.addAll(getCubes(connectionName));
		}

		Collections.sort(cubes, new SaikuCubeCaptionComparator());
		return cubes;
	}

	public Cube getNativeCube(SaikuCube cube) throws SaikuOlapException {
		try {
			OlapConnection con = connections.getOlapConnection(cube.getConnectionName());
			if (con != null ) {
				for (Database db : con.getOlapDatabases()) {
					Catalog cat = db.getCatalogs().get(cube.getCatalogName());
					if (cat != null) {
						for (Schema schema : cat.getSchemas()) {
							if (schema.getName().equals(cube.getSchemaName())) {
								for (Cube cub : schema.getCubes()) {
									if (cub.getName().equals(cube.getName()) || cub.getUniqueName().equals(cube.getUniqueName())) {
										return cub;
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw new SaikuOlapException("Cannot get native cube for ( " + cube+ " )",e);
		}
		throw new SaikuOlapException("Cannot get native cube for ( " + cube+ " )");
	}

	public OlapConnection getNativeConnection(String name) throws SaikuOlapException {
		try {
			OlapConnection con = connections.getOlapConnection(name);
			if (con != null ) {
				return con;
			}
		} catch (Exception e) {
			throw new SaikuOlapException("Cannot get native connection for ( " + name + " )",e);
		}
		return null;
	}

	public List<SaikuDimension> getAllDimensions(SaikuCube cube) throws SaikuOlapException {
		Cube nativeCube = getNativeCube(cube);
		List<SaikuDimension> dimensions = ObjectUtil.convertDimensions(nativeCube.getDimensions());
		for (int i=0; i < dimensions.size();i++) {
			SaikuDimension dim = dimensions.get(i);
			if (dim.getName().equals("Measures") || dim.getUniqueName().equals("[Measures]")) {
				dimensions.remove(i);
				break;
			}
		}

		Collections.sort(dimensions, new SaikuDimensionCaptionComparator());
		return dimensions;
	}

	public SaikuDimension getDimension(SaikuCube cube, String dimensionName) throws SaikuOlapException {
		Cube nativeCube = getNativeCube(cube);
		Dimension dim = nativeCube.getDimensions().get(dimensionName);
		if (dim != null) {
			return ObjectUtil.convert(dim);
		}
		return null;
	}

	public List<SaikuHierarchy> getAllHierarchies(SaikuCube cube) throws SaikuOlapException {
		Cube nativeCube = getNativeCube(cube);
		return ObjectUtil.convertHierarchies(nativeCube.getHierarchies());
	}

	public SaikuHierarchy getHierarchy(SaikuCube cube, String hierarchyName) throws SaikuOlapException {
		Cube nativeCube = getNativeCube(cube);
		Hierarchy h = nativeCube.getHierarchies().get(hierarchyName);
		if (h != null) {
			return ObjectUtil.convert(h);
		}
		return null;
	}

	public List<SaikuMember> getHierarchyRootMembers(SaikuCube cube, String hierarchyName) throws SaikuOlapException {
		Cube nativeCube = getNativeCube(cube);
		List<SaikuMember> members = new ArrayList<SaikuMember>();
		Hierarchy h = nativeCube.getHierarchies().get(hierarchyName);

		if (h == null) {
			for (Hierarchy hlist : nativeCube.getHierarchies()) {
				if (hlist.getUniqueName().equals(hierarchyName) || hlist.getName().equals(hierarchyName)) {
					h = hlist;
				}
			}
		}
		if (h!= null) {
			try {
				members = (ObjectUtil.convertMembers(h.getRootMembers(), null, null));
			} catch (OlapException e) {
				throw new SaikuOlapException("Cannot retrieve root members of hierarchy: " + hierarchyName,e);
			}
		}

		return members;
	}


	public List<SaikuLevel> getAllLevels(SaikuCube cube, String dimension, String hierarchy) throws SaikuOlapException {
		Cube nativeCube = getNativeCube(cube);
		Dimension dim = nativeCube.getDimensions().get(dimension);
		if (dim != null) {
			Hierarchy h = dim.getHierarchies().get(hierarchy);
			if (h == null) {
				for (Hierarchy hlist : dim.getHierarchies()) {
					if (hlist.getUniqueName().equals(hierarchy) || hlist.getName().equals(hierarchy)) {
						h = hlist;
					}
				}
			}

			if (h!= null) {
				List<SaikuLevel> levels = (ObjectUtil.convertLevels(h.getLevels()));
				return levels;
			}
		}
		return new ArrayList<SaikuLevel>();

	}


	public List<SaikuMember> getAllMembers(SaikuCube cube, String dimension, String hierarchy, String level, String properties, boolean children) throws SaikuOlapException {
		
		if(children) {
			
			return getAllMembersWithChildrenCount(cube, dimension, hierarchy, level, properties);
			
		} else {
			
			return getAllMembers(cube, dimension, hierarchy, level, properties);
			
		}
	}

	
	private List<SaikuMember> getAllMembersWithChildrenCount(SaikuCube cube, String dimension, String hierarchy, String level, String properties) 
		throws SaikuOlapException {

		log.debug("Entering getLevelMembersWithChildrenCount method.");
		
		Cube nativeCube = getNativeCube(cube);
		Level upper = MemberUtil.findNativeLevel(nativeCube, dimension, hierarchy, level);
		if(upper == null) {
			log.debug(String.format("No level found for dimension %s, hierarchy %s, level %s", dimension, hierarchy, level));
			return new ArrayList<SaikuMember>();
		}
		
		List<Member> membersUpper = null;
		try {
			membersUpper = upper.getMembers();
		} catch (OlapException e) {
			String msg = "Exception caught while getting all members";
			log.error(msg, e);
			throw new SaikuOlapException(msg, e);
		}

		Integer[] childMemberCount = new Integer[membersUpper.size()];
		Arrays.fill(childMemberCount, new Integer(0));
		
		Level lower = MemberUtil.findNativeLowerLevel(upper);
		if(lower == null) {
			log.debug("No lower level found, no need to count child members.");
			return ObjectUtil.convertMembers(membersUpper, properties, childMemberCount);
		}
		
		// Prepare map to quickly access counters using member.
		//
		Map<Member, Integer> map = new HashMap<Member, Integer>();
		int i = 0;
		for(Member member : membersUpper) {
			map.put(member, new Integer(i++));
		}
		
		// Try to extract the members of the lower level.
		//
		List<Member> membersLower = null;
		try {
			membersLower = lower.getMembers();
		} catch (OlapException e) {
			String msg = "Exception caught while getting members of lower level.";
			log.error(msg, e);
			throw new SaikuOlapException(msg, e);
		}
		
		// Iterate on lower level's members and update children counters.
		//
		for(Member member : membersLower) {
			
			// Find the parent member and increase corresponding counter, if present.
			//
			Integer index = map.get(member.getParentMember());
			if(index != null) {
				childMemberCount[index.intValue()]++;
			}
		}
		
		log.debug("Returning members with populated children count.");
		return ObjectUtil.convertMembers(membersUpper, properties, childMemberCount);
	}
	
	
	private List<SaikuMember> getAllMembers(SaikuCube cube, String dimension, String hierarchy, String level, String properties) 
			throws SaikuOlapException {
		
		try {
			
			Cube nativeCube = getNativeCube(cube);
			Dimension dim = nativeCube.getDimensions().get(dimension);
			if (dim != null) {
				Hierarchy h = dim.getHierarchies().get(hierarchy);
				if (h == null) {
					for (Hierarchy hlist : dim.getHierarchies()) {
						if (hlist.getUniqueName().equals(hierarchy) || hlist.getName().equals(hierarchy)) {
							h = hlist;
						}
					}
				}

				if (h!= null) {
					Level l = h.getLevels().get(level);
					if (l == null) {
						for (Level lvl : h.getLevels()) {
							if (lvl.getUniqueName().equals(level) || lvl.getName().equals(level)) {
								return (ObjectUtil.convertMembers(lvl.getMembers(), properties, null));
							}
						}
					} else {
						return (ObjectUtil.convertMembers(l.getMembers(), properties, null));
					}

				}
			}
		} catch (OlapException e) {
			throw new SaikuOlapException("Cannot get all members",e);
		}

		return new ArrayList<SaikuMember>();
	}


	public List<SaikuMember> getMemberChildren(SaikuCube cube, String uniqueMemberName, String properties, boolean children) throws SaikuOlapException {
		
		List<SaikuMember> members = new ArrayList<SaikuMember>();
		
		try {
			
			Cube nativeCube = getNativeCube(cube);
			
			List<IdentifierSegment> memberList = IdentifierNode.parseIdentifier(uniqueMemberName).getSegmentList();
			Member m = nativeCube.lookupMember(memberList);
			
			if (m != null) {
				
				for (Member c :  m.getChildMembers()) {
					
					Integer childMemberCount = children ? c.getChildMemberCount() : null;
					SaikuMember sm = ObjectUtil.convert(c, properties, childMemberCount);
					members.add(sm);
				}
			}
		} catch (OlapException e) {
			throw new SaikuOlapException("Cannot get child members of member:" + uniqueMemberName,e);
		}

		return members;
	}

	public List<SaikuMember> getAllMeasures(SaikuCube cube) throws SaikuOlapException {
		List<SaikuMember> measures = new ArrayList<SaikuMember>();
		try {
			Cube nativeCube = getNativeCube(cube);
			for (Measure measure : nativeCube.getMeasures()) {
				/* 
				 * comment for skip non visible measure
				if(measure.isVisible()) {
					measures.add(ObjectUtil.convert(measure));
				}
				*/
				measures.add(ObjectUtil.convert(measure, null, null));
			}
			if (measures.size() == 0) {
				Hierarchy hierarchy = nativeCube.getDimensions().get("Measures").getDefaultHierarchy();
				measures = (ObjectUtil.convertMembers(hierarchy.getRootMembers(), null, null));
			}
		} catch (OlapException e) {
			throw new SaikuOlapException("Cannot get measures for cube:"+cube.getName(),e);
		}
		
		Collections.sort(measures, new SaikuMemberCaptionComparator());
		return measures;
	}

	public SaikuMember getMember(SaikuCube cube, String uniqueMemberName, String properties, boolean children) throws SaikuOlapException {
		
		try {
			
			Cube nativeCube = getNativeCube(cube);
			Member m = nativeCube.lookupMember(IdentifierNode.parseIdentifier(uniqueMemberName).getSegmentList());
			if (m != null) {
				
				Integer childMemberCount = children ? m.getChildMemberCount() : null;
				return ObjectUtil.convert(m, properties, childMemberCount);
			}
			
			return null;
			
		} catch (Exception e) {
			throw new SaikuOlapException("Cannot find member: " + uniqueMemberName + " in cube:"+cube.getName(),e);
		}
	}

}
