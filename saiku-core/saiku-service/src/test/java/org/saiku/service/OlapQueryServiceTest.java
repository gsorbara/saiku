package org.saiku.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.saiku.olap.dto.SaikuConnection;
import org.saiku.olap.dto.SaikuCube;
import org.saiku.olap.dto.SaikuMember;
import org.saiku.olap.dto.SaikuQuery;
import org.saiku.olap.dto.resultset.AbstractBaseCell;
import org.saiku.olap.dto.resultset.CellDataSet;
import org.saiku.olap.util.exception.SaikuOlapException;

/**
 * 
 * To use this class for testing there are two steps you have to modify before run test.
 * 
 * <br/> 1. Change the "connectionProp" It is located on ServiceTest.class.
 * <br/> 2. Change the "loadParams".
 */
public class OlapQueryServiceTest extends ServiceTest{
	
	
	private static Logger log = Logger.getLogger(OlapQueryServiceTest.class);
	
	
	@BeforeClass
	public static void loadParams(){
		
		OlapTestParams.setupParams(OlapTestParams.FOODMART_DATA);
	}
	
	/**
	 * Get Cube by name.
	 * @param name
	 * @return
	 */
	private SaikuCube getExistingCubeByName(String name){
		List<SaikuCube> cubes = olapMetaExplorer.getCubes(connectionName); 
		for (SaikuCube saikuCube : cubes) {
			if(name.equals(saikuCube.getName())){
				return saikuCube;
			}
		}
		return null;
	}
	
	/**
	 * Get default Cube. 
	 * @return
	 */
	public SaikuCube getCube(){
		System.out.println("Start getCube for " + OlapTestParams.cubeName);
		return getExistingCubeByName(OlapTestParams.cubeName);
	}
	
	/**
	 * Get Cube by name.
	 * @param name
	 * @return
	 */
	public SaikuCube getCubeByName(String name){
		return getExistingCubeByName(name);
	}
	
	/**
	 * This method attempt to check current connection.
	 * @throws SaikuOlapException
	 */
	@Test
	public final void testConnection() throws SaikuOlapException{
		SaikuConnection output = olapMetaExplorer.getConnection(connectionName);
		assertNotNull(output);
	}
	
	/**
	 * This method attempt to check number of cube existing in the system.
	 */
	@Test
	public final void testCubeExisting(){
		List<SaikuCube> cubes = olapMetaExplorer.getCubes(connectionName);
		
		//Just list all the existing cube 
		for (SaikuCube saikuCube : cubes) {
			System.out.println("Cube name: " + saikuCube.getName());
			
		}
		//Expect number of existing cube more than zero.
		boolean condition = cubes.size() > 0;
		Assert.assertTrue(condition);
	}
	
	/**
	 * This method attempt to create a new Olap query.
	 */
	@Test
	public final void testCreateNewQuery(){
		System.out.println("Start testing create new query");
		
		//Create new olap query for testing
		SaikuQuery query = olapQueryService.createNewOlapQuery(OlapTestParams.queryName, getCube());
		
		System.out.println("Query "+query + " was created");
		
		assertNotNull(query);
		assertEquals(OlapTestParams.queryName, query.getName());
		System.out.println("Query "+query.getName() + " was created");
	}
	
	/**
	 * This method attempt to cancel a new Olap query.
	 */
	@Test
	public final void testCancelQuery(){
		System.out.println("Start testing cancel new query");
		
		//Create new olap query for testing
		SaikuQuery query = olapQueryService.createNewOlapQuery(OlapTestParams.queryName, getCube());
		assertNotNull(query);
		System.out.println("Query "+query + " was created");		
		try{
			olapQueryService.cancel(OlapTestParams.queryName);
			System.out.println("Query "+query.getName() + " was canceled");
		}catch (Exception e) {
			Assert.fail();
			System.out.println("Query "+query.getName() + " was failed to cancel");
		}
		
	}
	
	
	
	/**
	 * This method attempt execute the MDX query and get the result from resultSet.
	 * @throws Exception
	 */
	@Test
	public final void testMDXQuery() throws Exception{
		log.info("Start testing mds query");
		
		//Execute the MDX query
		CellDataSet cellDataSet = olapQueryService.executeMdx(OlapTestParams.queryName, OlapTestParams.mdxQuery);
		
		assertNotNull(cellDataSet);
		
		//Get the result
		AbstractBaseCell[][] abs = cellDataSet.getCellSetBody();
		
		assertNotNull(abs);
	}
	
	/**
	 * This method attempt to get the non-numerical values for Measure in ResultSet.
	 * <br/>
	 * <br/>
	 * Note: This test case base on JIRA SDW-38
	 */
	@Test
	public final void testNonNumericalValues(){
		System.out.println("Start testing testNonNumericalValues");
		
		//Execute the MDX query
		CellDataSet cellDataSet = olapQueryService.executeMdx(OlapTestParams.queryName, OlapTestParams.mdxQuery);
		
		assertNotNull(cellDataSet);
		
		//Get the result
		AbstractBaseCell[][] abs = cellDataSet.getCellSetBody();
		
		assertNotNull(abs);
		
		int h = cellDataSet.getHeight();
		int w = cellDataSet.getWidth();
		
		System.out.println("Result Height "+h + " | Result Width "+w);
		
		String value = abs[1][1].getFormattedValue();
		
		System.out.println("Result value = " + value);
		
		assertNotNull(value);
		
	}
	
	/**
	 * This method attempt to check the attribute visible on measures.
	 * <br/>
	 * <br/>
	 * Note: This test case related to JIRA SDW-39
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testAttributeVisible() throws Exception{
		System.out.println("Start testing testNonNumericalValues");
		
		List<SaikuMember> members = olapMetaExplorer.getAllMeasures(getCubeByName(OlapTestParams.cubeName_2));
		
		assertNotNull(members);
		
		//Print all member
		for (SaikuMember saikuMember : members) {	
			System.out.println("Member " + saikuMember.getName() + " : Visible=" + saikuMember.getVisible());
		}
		
		
	}
	
	/**
	 * This method attempt to check the attribute visible on measures, and assert a true value.
	 * <br/>
	 * <br/>
	 * Note: This test case related to JIRA SDW-39
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testAttributeVisibleTrue() throws Exception{
		System.out.println("start testAttributeVisibleTrue");
		
		List<SaikuMember> members = olapMetaExplorer.getAllMeasures(getCubeByName(OlapTestParams.cubeName_2));
		
		//Expect the result is true for this member index.
		Assert.assertTrue(members.get(OlapTestParams.memberIndex_1).getVisible());
	}
	
	/**
	 * This method attempt to check the attribute visible on measures, and assert a false value.
	 * <br/>
	 * <br/>
	 * Note: This test case related to JIRA SDW-39
	 * 
	 * @throws Exception 
	 */
	/*@Test
	public final void testAttributeVisibleFalse() throws Exception{
		System.out.println("start testAttributeVisibleFalse");
		
		List<SaikuMember> members = olapMetaExplorer.getAllMeasures(getCubeByName(OlapTestParams.cubeName_2));
		
		//Expect the result is false for this member index.
		Assert.assertFalse(members.get(OlapTestParams.memberIndex_2).getVisible());
	}*/
}
