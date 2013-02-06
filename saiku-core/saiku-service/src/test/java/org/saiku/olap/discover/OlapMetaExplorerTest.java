package org.saiku.olap.discover;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;
import org.olap4j.OlapConnection;
import org.saiku.AbstractServiceUtils;
import org.saiku.TConnectionManager;
import org.saiku.datasources.connection.IConnectionManager;
import org.saiku.datasources.datasource.SaikuDatasource;
import org.saiku.olap.dto.PropertySaikuMember;
import org.saiku.olap.dto.SaikuConnection;
import org.saiku.olap.dto.SaikuCube;
import org.saiku.olap.dto.SaikuDimension;
import org.saiku.olap.dto.SaikuHierarchy;
import org.saiku.olap.dto.SaikuLevel;
import org.saiku.olap.dto.SaikuMember;
import org.saiku.olap.util.exception.SaikuOlapException;
import org.saiku.service.OlapTestParams;
import org.saiku.service.ServiceTest;
import org.saiku.service.datasource.ClassPathResourceDatasourceManager;
import org.saiku.service.datasource.IDatasourceManager;

/**
 * 
 * To use this class for testing there are two steps you have to modify before run test.
 * 
 * <br/> 1. Change the "connectionProp" It is located on ServiceTest.class.
 * <br/> 2. Change the "loadParams".
 */
public class OlapMetaExplorerTest extends ServiceTest{

    private static OlapMetaExplorer olapMetaExplorer;
    private static Properties testProps = new Properties();
	/**
	 * dimension name
	 */
	private final String dimension = "Area";
	
	/**
	 * hierarchy name
	 */
	private final String hierarchy = "Area.Area";
	
	/**
	 * level name
	 */
	private final String level = "Area";
	
	/**
	 * level name
	 */
	private final String allProperty = "ALL";
	
	/**
	 * level name
	 */
	private final String singleProperty = "iso2";
	
	/**
	 * level name
	 */
	private final String multiProperty = "iso3,iso2";
	
	/**
     * Test to retrieve all the properties from a Level member.
     */
	
	@BeforeClass
	public static void loadParams(){
		OlapTestParams.setupParams(OlapTestParams.FOODMART_DATA);
	}
	

    /**
     * Test that you can fetch all available connections.
     * @throws SaikuOlapException
     */
    @Test
    public final void testGetAllConnections() throws SaikuOlapException  {
       List<SaikuConnection> output = olapMetaExplorer.getAllConnections();

       assertNotNull(output);
       
       assertEquals(1, output.size());

       assertEquals(connectionName, output.get(0).getName());       
    }
    
    /**
     * Test that you can get a single connection.
     * @throws SaikuOlapException
     */
    
    @Test
    public final void testGetConnectionSuccess() throws SaikuOlapException{
        SaikuConnection output = olapMetaExplorer.getConnection(connectionName);
        
        assertNotNull(output);
        
        assertEquals(connectionName, output.getName());
    }
    
    /**
     * Test what happens when you call an non existant connection.
     * @throws SaikuOlapException
     */
    @Test
    public final void testGetConnectionFailure() throws SaikuOlapException{
        /*
         * Connection Failure shouldn't throw an NPE it should throw a nicer error.
         */
        SaikuConnection output = null;
        try{
            output = olapMetaExplorer.getConnection("noname");
        }
        catch (Exception e){
        }
        assertNull(output);
    }
    
    
    @Test
    public final void testGetConnections() throws SaikuOlapException{
        List<String> list = new ArrayList<String>();

        list.add(connectionName);

        List<SaikuConnection> connections = olapMetaExplorer.getConnections(list);
        
        assertNotNull(connections);
        
        assertEquals(connectionName, connections.get(0).getName());

    }
    
    @Test
    public final void testGetNativeConnection() throws SaikuOlapException{

        OlapConnection output = olapMetaExplorer.getNativeConnection(connectionName);
        
        assertNotNull(output);
        
    }
    
    /**
     * Test to prove that non existant connection currently throws NPE.
     * @throws Exception
     */
    @Test(expected = NullPointerException.class)
    public void testForExpectedExceptionWithAnnotation()
            throws Exception {
        olapMetaExplorer.getConnection("noname");
    }
    
    
    public final void testGetMultipleConnections(){
        
    }
    /**
     * Make sure you can grab a cube from a specified connection.
     */
    @Test
    public final void testGetCubesSingleConnection(){
    	
        List<SaikuCube> output = olapMetaExplorer.getCubes(connectionName);

        assertNotNull(output);

        assertEquals(OlapTestParams.cubeName, output.get(OlapTestParams.cubeNameIndex).getName());

    }
    
    /**
     * Make sure you can grab a cube from a specified connection.
     */
    @Test
    public final void testGetCubesMultipleConnections(){
        List<String> cubes = new ArrayList<String>();

        cubes.add(connectionName);

        List<SaikuCube> output = olapMetaExplorer.getCubes(cubes);

        assertNotNull(output);

        assertEquals(OlapTestParams.cubeName, output.get(OlapTestParams.cubeNameIndex).getName());
    }
    
    public final void testGetCubesMultipleConnectionsConnection(){
    }
    
    /**
     * Test to make sure you can retrieve all the cubes from a schema.
     */
    @Test
    public final void testGetAllCubes(){
        List<SaikuCube> output = olapMetaExplorer.getAllCubes();
        
        assertNotNull(output);
        
        assertEquals(OlapTestParams.totalNumberOfCubes, output.size());
        
        for (int i = 0; i < output.size(); i++){
        	assertEquals(OlapTestParams.catalogName, output.get(i).getCatalogName());
        	output.get(i).getName();
        	assertEquals(connectionName, output.get(i).getConnectionName());
        	output.get(i).getCubeName();
        	assertEquals(OlapTestParams.schemaName, output.get(i).getSchemaName());

        	output.get(i).getUniqueName();
        }
        
    }
    
    /**
     * Test to make sure that the cubes are returned in the same order.
     */    
    @Test
    public final void testCubeReturnOrder(){
      List<SaikuCube> output = olapMetaExplorer.getAllCubes();
      
         assertNotNull(output);

         String[] names = { "HR","Sales", "Sales 2","Sales Ragged","Sales Scenario","Store","Warehouse","Warehouse and Sales"};

         for (int i = 0; i < output.size(); i++){
          assertEquals(names[i], output.get(i).getName());
          assertEquals("["+names[i]+"]",output.get(i).getCubeName());
         }
    } 
    
    public final void testGetNativeCube(){
        
        
    }
    
    /**
     * Test to make sure you can get all the dimensions in a cube.
     * @throws SaikuOlapException
     */
    @Test
    public final void testGetAllDimensions() throws SaikuOlapException{
    	List<SaikuCube> cubes = olapMetaExplorer.getAllCubes();
    	
    	for (SaikuCube saikuCube : cubes) {
    		if(OlapTestParams.cubeName.equals(saikuCube.getName())){
    			List<SaikuDimension> dims = olapMetaExplorer.getAllDimensions(saikuCube);
    			assertNotNull(dims);
    	        assertEquals(OlapTestParams.numberOfDimension, dims.size());
    	        break;
    		}
		}

    }
    
    /**
     * Test to make sure you can get a single dimension in a cube.
     * @throws SaikuOlapException
     */
    @Test
    public final void testGetDimension() throws SaikuOlapException{
        List<SaikuCube> cubes = olapMetaExplorer.getAllCubes();
    	
        for (SaikuCube saikuCube : cubes) {
    		if(OlapTestParams.cubeName.equals(saikuCube.getName())){
    			
    			SaikuDimension dim = olapMetaExplorer.getDimension(saikuCube, OlapTestParams.dimName);
    			assertNotNull(dim);
    			assertEquals(OlapTestParams.dimName, dim.getName());
    	        break;
    		}
		}

    }
    
    /**
     * Test to make sure you can get a single dimension in a cube.
     * @throws SaikuOlapException
     */
    @Test
    public final void testGetDimensionNull() throws SaikuOlapException{
        List<SaikuCube> cubes = olapMetaExplorer.getAllCubes();
        

        for (SaikuCube saikuCube : cubes) {
    		if(OlapTestParams.cubeName.equals(saikuCube.getName())){
    			
    			SaikuDimension dim = olapMetaExplorer.getDimension(cubes.get(0), "No dimension");
    		    assertNull(dim);    		    
    	        break;
    		}
		}    
    }

    
    @Test
    public final void testGetAllHierarchies() throws SaikuOlapException{        

    	List<SaikuCube> cubes = olapMetaExplorer.getAllCubes();
    	
        for (SaikuCube saikuCube : cubes) {
    		if(OlapTestParams.cubeName.equals(saikuCube.getName())){
    			
    			List<SaikuHierarchy> hier = olapMetaExplorer.getAllHierarchies(saikuCube);
    		    
    			assertNotNull(hier);
    	    	
    	    	assertEquals(OlapTestParams.numberOfHierarchy, hier.size());
    			
    	        break;
    		}
		}    
    	

    }
    
    @Test
    public final void testGetHierarchy() throws SaikuOlapException{
    	
    	List<SaikuCube> cubes = olapMetaExplorer.getAllCubes();
    	for (SaikuCube saikuCube : cubes) {
    		if(OlapTestParams.cubeName.equals(saikuCube.getName())){
    			
    			SaikuHierarchy hier = olapMetaExplorer.getHierarchy(saikuCube, OlapTestParams.hierarchyName);
    			assertNotNull(hier);
    		        
    		    assertEquals(OlapTestParams.hierarchyName, hier.getName());
    		    
    	        break;
    		}
		}    
    	

    }

//    @Test
    public final void testGetHierarchyRootMembers() throws SaikuOlapException{
//    	olapMetaExplorer.getHierarchyRootMembers(olapMetaExplorer.getAllCubes().get(0), null);
    }
    
    @Test
    public final void testGetAllLevels() throws SaikuOlapException{

    	List<SaikuCube> cubes = olapMetaExplorer.getAllCubes();
    	for (SaikuCube saikuCube : cubes) {
    		if(OlapTestParams.cubeName.equals(saikuCube.getName())){
    			
    			List<SaikuLevel> levels = olapMetaExplorer.getAllLevels(saikuCube, OlapTestParams.dimName, OlapTestParams.hierarchyName);
    			
    			assertNotNull(levels);
    	    	
    	    	assertEquals(OlapTestParams.level, levels.size());
    			
    	        break;
    		}
		}    
    	
    }
    
    @Test
    public final void testGetAllLevelsUniqueNameHierarchy() throws SaikuOlapException{

    	List<SaikuCube> cubes = olapMetaExplorer.getAllCubes();
    	for (SaikuCube saikuCube : cubes) {
    		if(OlapTestParams.cubeName.equals(saikuCube.getName())){
    			
    			List<SaikuLevel> levels = olapMetaExplorer.getAllLevels(saikuCube, OlapTestParams.dimName, OlapTestParams.uniqueHierarchyName);
    			
    			assertNotNull(levels);
    	    	
    	    	assertEquals(OlapTestParams.level, levels.size());
    			
    	        break;
    		}
		}   
    	
    }
    
    @Test
    public final void testGetAllMembers() throws SaikuOlapException{
    	
    	List<SaikuCube> cubes = olapMetaExplorer.getAllCubes();
    	for (SaikuCube saikuCube : cubes) {
    		if(OlapTestParams.cubeName.equals(saikuCube.getName())){
    			
    			List<SaikuMember> members = olapMetaExplorer.getAllMembers(
    					saikuCube, 
    					OlapTestParams.dimName, 
    					OlapTestParams.hierarchyName, 
    					OlapTestParams.levelName,
    					null,
    					false);
    			assertNotNull(members);
    			assertEquals(OlapTestParams.totalMember, members.size());
    			
    	        break;
    		}
		}  
    	
    }
    
    
    @Test
    public final void testGetMemeberChildren() throws SaikuOlapException{

    	List<SaikuCube> cubes = olapMetaExplorer.getAllCubes();
    	for (SaikuCube saikuCube : cubes) {
    		if(OlapTestParams.cubeName.equals(saikuCube.getName())){
    			
    			List<SaikuMember> members = olapMetaExplorer.getMemberChildren(saikuCube, OlapTestParams.uniqueMemberName, null, false);
    	    	
    	    	assertNotNull(members);
    	    	
    	    	assertEquals(OlapTestParams.totalNumberMemeberChildren, members.size());
    			
    	        break;
    		}
		}  
    	
    }
  
    @Test
    public final void testGetAllMeasures() throws SaikuOlapException{
    	
    	List<SaikuCube> cubes = olapMetaExplorer.getAllCubes();
    	for (SaikuCube saikuCube : cubes) {
    		if(OlapTestParams.cubeName.equals(saikuCube.getName())){
    			
    			List<SaikuMember> members = olapMetaExplorer.getAllMeasures(saikuCube);
    			
    			assertNotNull(members);
    	    	
    	    	assertEquals(OlapTestParams.totalNumberMeasure, members.size());
    			
    	        break;
    		}
		}
    	
    }
    
    @Test
    public final void testGetMemeber() throws SaikuOlapException{
    	
    	List<SaikuCube> cubes = olapMetaExplorer.getAllCubes();
    	for (SaikuCube saikuCube : cubes) {
    		if(OlapTestParams.cubeName.equals(saikuCube.getName())){
    			
    			SaikuMember member = olapMetaExplorer.getMember(saikuCube, OlapTestParams.uniqueMemberName, null, false);
    	    	
    	    	assertNotNull(member);
    	    	assertEquals(OlapTestParams.uniqueMemberName, member.getUniqueName());
    			
    	        break;
    		}
    	}
    	
    }
    
    
    @Test
    public final void testGetAllMembersUniqueNameHierarchy() throws SaikuOlapException{
    	
    	List<SaikuCube> cubes = olapMetaExplorer.getAllCubes();
    	for (SaikuCube saikuCube : cubes) {
    		if(OlapTestParams.cubeName.equals(saikuCube.getName())){
    			
    			List<SaikuMember> members = olapMetaExplorer.getAllMembers(
    					saikuCube, 
    					OlapTestParams.dimName, 
    					OlapTestParams.uniqueHierarchyName, 
    					OlapTestParams.levelName,
    					null,
    					false);
    			assertNotNull(members);
    			assertEquals(OlapTestParams.totalMember, members.size());
    	        break;
    		}
    	}    	
    }
    
    @Test
    public final void testGetAllMembersUniqueNameLevel() throws SaikuOlapException{

    	List<SaikuCube> cubes = olapMetaExplorer.getAllCubes();
    	for (SaikuCube saikuCube : cubes) {
    		if(OlapTestParams.cubeName.equals(saikuCube.getName())){
    			
    			List<SaikuMember> members = olapMetaExplorer.getAllMembers(
    					saikuCube, 
    					OlapTestParams.dimName, 
    					OlapTestParams.hierarchyName, 
    					OlapTestParams.uniqueLevelName,
    					null,
    					false);
    			
    			assertNotNull(members);
    			
    			assertEquals(OlapTestParams.totalMember, members.size());
    			
    	        break;
    		}
    	}
    	
    }
    
    @Test
    public void testGetMemberAllProperty() throws SaikuOlapException{
    	
		List<SaikuCube> cubes = olapMetaExplorer.getAllCubes();
		for (SaikuCube saikuCube : cubes) {
			if(OlapTestParams.cubeName.equals(saikuCube.getName())){	    			
    			List<SaikuMember> members = olapMetaExplorer.getAllMembers(saikuCube, dimension, 
    					hierarchy, level, allProperty, false);
    			
    			assertNotNull(members);
    			for(SaikuMember m : members){
    				assertNotNull(m);
    				assertTrue(m instanceof PropertySaikuMember);
    				
    				PropertySaikuMember psm = (PropertySaikuMember)m;
    				assertTrue(psm.getProperties().size() > 1);    				
    				break;
    			}
    			
    	        break;
    		}
		}    	
    }
    
    /**
     * Test to retrieve single property from a Level member.
     */
    @Test
    public void testGetMemberSingleProperty() throws SaikuOlapException{
    	
		List<SaikuCube> cubes = olapMetaExplorer.getAllCubes();
		for (SaikuCube saikuCube : cubes) {
			if(OlapTestParams.cubeName.equals(saikuCube.getName())){	    			
    			List<SaikuMember> members = olapMetaExplorer.getAllMembers(
    					saikuCube, 
    					dimension, 
    					hierarchy, 
    					level, 
    					singleProperty,
    					false);
    			
    			assertNotNull(members);
    			for(SaikuMember m : members){
    				assertNotNull(m);
    				assertTrue(m instanceof PropertySaikuMember);
    				
    				PropertySaikuMember psm = (PropertySaikuMember)m;
    				assertTrue(psm.getProperties().size() == 1);    				
    				break;
    			}
    			
    	        break;
    		}
		}    	
    }
    
    /**
     * Test to retrieve multi property from a Level member.
     */
    @Test
    public void testGetMemberMultiProperty() throws SaikuOlapException{
    	
		List<SaikuCube> cubes = olapMetaExplorer.getAllCubes();
		for (SaikuCube saikuCube : cubes) {
			if(OlapTestParams.cubeName.equals(saikuCube.getName())){	    			
    			List<SaikuMember> members = olapMetaExplorer.getAllMembers(
    					saikuCube, 
    					dimension, 
    					hierarchy, 
    					level, 
    					multiProperty,
    					false);
    			
    			assertNotNull(members);
    			for(SaikuMember m : members){
    				assertNotNull(m);
    				assertTrue(m instanceof PropertySaikuMember);
    				
    				PropertySaikuMember psm = (PropertySaikuMember)m;
    				assertTrue(psm.getProperties().size() > 1);    				
    				break;
    			}
    			
    	        break;
    		}
		}    	
    }
    
    /**
     * Test to retrieve by property name that not exist in Level and should return property size is 0.
     */
    @Test
    public void testGetMemberProperty() throws SaikuOlapException{
    	
		List<SaikuCube> cubes = olapMetaExplorer.getAllCubes();
		for (SaikuCube saikuCube : cubes) {
			if(OlapTestParams.cubeName.equals(saikuCube.getName())){	    			
    			List<SaikuMember> members = olapMetaExplorer.getAllMembers(
    					saikuCube, 
    					dimension, 
    					hierarchy, 
    					level, 
    					"test1234",
    					false);
    			
    			assertNotNull(members);
    			for(SaikuMember m : members){
    				assertNotNull(m);
    				assertTrue(m instanceof PropertySaikuMember);
    				
    				PropertySaikuMember psm = (PropertySaikuMember)m;
    				assertTrue(psm.getProperties().size() == 0);    	
    				
    				break;
    			}
    			
    	        break;
    		}
		}    	
    }    
    
    public static void main(String[] args) {
    	AbstractServiceUtils ast = new AbstractServiceUtils();
	    ast.initTestContext();
	    IConnectionManager ic = new TConnectionManager();
	   
	    File f = new File(System.getProperty("java.io.tmpdir")+"/files/");
	    f.mkdir();
	    IDatasourceManager ds = new ClassPathResourceDatasourceManager(System.getProperty("java.io.tmpdir")+"/files/");
	    InputStream inputStream= OlapMetaExplorerTest.class.getResourceAsStream("connection.properties");
	    try {
	        testProps.load(inputStream);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    ds.setDatasource(new SaikuDatasource("test", SaikuDatasource.Type.OLAP, testProps));
	    ic.setDataSourceManager(ds);
	    olapMetaExplorer = new OlapMetaExplorer(ic);
	    
	}
    

    
    
    @BeforeClass
    public static void setup() throws IOException{
	    AbstractServiceUtils ast = new AbstractServiceUtils();
	    ast.initTestContext();
	    IConnectionManager ic = new TConnectionManager();
	    computeTestDataRoot(OlapMetaExplorerTest.class);
	    File f = new File(System.getProperty("java.io.tmpdir")+"/files/");
	    f.mkdir();
	    IDatasourceManager ds = new ClassPathResourceDatasourceManager(System.getProperty("java.io.tmpdir")+"/files/");
	    InputStream inputStream= OlapMetaExplorerTest.class.getResourceAsStream("connection.properties");
	    try {
	        testProps.load(inputStream);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    ds.setDatasource(new SaikuDatasource("test", SaikuDatasource.Type.OLAP, testProps));
	    ic.setDataSourceManager(ds);
	    olapMetaExplorer = new OlapMetaExplorer(ic);
	
	}
    
    public static String computeTestDataRoot(Class<?> anyTestClass) throws IOException {
        
        //create a temp file
        File temp = File.createTempFile("temp-file-name", ".tmp"); 

        System.out.println("Temp file : " + temp.getAbsolutePath());

    //Get tempropary file path
        String absolutePath = temp.getAbsolutePath();
        String tempFilePath = absolutePath.
            substring(0,absolutePath.lastIndexOf(File.separator));
        return tempFilePath+"/";
      }

}
