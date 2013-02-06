package org.saiku.sdw;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.saiku.TConnectionManager;
import org.saiku.datasources.connection.IConnectionManager;
import org.saiku.datasources.datasource.SaikuDatasource;
import org.saiku.olap.discover.OlapMetaExplorer;
import org.saiku.olap.dto.SaikuConnection;
import org.saiku.sdw.client.SDWMetadataClient;
import org.saiku.sdw.client.SDWMetadataClientManager;
import org.saiku.sdw.client.SDWMetadataConfiguration;
import org.saiku.sdw.datasource.SDWDatasourceManager;
import org.saiku.service.datasource.IDatasourceManager;

public class SDWDatasourceManagerTest {
	
	public static IDatasourceManager ds;
	
	
	@BeforeClass
	public static void setup() throws IOException {
		SDWMetadataConfiguration config = new SDWMetadataConfiguration();
		System.out.println("start up");
		InputStream is = SDWDatasourceManagerTest.class.getResourceAsStream("sdw-config.properties");
		try{
			Properties prop = new Properties();
			prop.load(is);
			
			config.setHost((String) prop.get("host"));
			config.setCatalogUri((String) prop.get("catalogUri"));
			config.setConnectionUri((String) prop.get("connectionUri"));
			config.setConnectionsUri((String) prop.get("connectionsUri"));
			config.setMondrianSchemaUri((String) prop.get("mondrianSchemaUri"));
			config.setSchemaUri((String) prop.get("schemaUri"));
			config.setWorkspaceUri((String) prop.get("workspaceUri"));
			config.setSchemaLanguagesUri((String) prop.get("schemaLanguagesUri"));
			
		}catch(Exception e){
			//e.printStackTrace();
			System.out.println("file not found : sdw-config.properties");
		}
		
		SDWMetadataClient client = new SDWMetadataClientManager(config);
		ds = new SDWDatasourceManager(client);
		
	}
	
	/**
	 * This method attempts to test load all the connection from SDW metadata service and store in saiku datasources 
	 */
	
	@Test
	public void testLoadDatasource(){
		System.out.println("testing loading datasource from sdw metadata service");
		
		ds.load();
		Map<String,SaikuDatasource> datasources = ds.getDatasources();
		Assert.assertTrue(!datasources.isEmpty());
		Iterator<String> iterator = datasources.keySet().iterator();
		
		System.out.println(datasources.size() + " has been load");
		
		while (iterator.hasNext()) {
			String string = (String) iterator.next();
			SaikuDatasource sds = datasources.get(string);
			System.out.println(sds.getName());
		}
	}
	
	/**
	 * This method attempts to test get the existing datasource.
	 */

	@Test
	public void testGetDatasource(){
		System.out.println("testing get datasource");
		Map<String,SaikuDatasource> datasources = ds.getDatasources();
		Iterator<String> keys = datasources.keySet().iterator();
		while (keys.hasNext()) {
			String string = (String) keys.next();
			SaikuDatasource saikuDatasource = datasources.get(string);
			if(saikuDatasource != null){
				Assert.assertTrue(true);
			}else{
				Assert.assertTrue(false);
			}
			break;
		}
	}
	
	/**
	 * This method attempts to test get all datasources.
	 */

	@Test
	public void testGetDatasources(){
		System.out.println("testing get datasources");
		Map<String,SaikuDatasource> datasources = ds.getDatasources();
		
		Assert.assertFalse(datasources.isEmpty());
		System.out.println("return " + datasources.size() + " items");
	}
	
	/**
	 * This method attempts to test add new datasources.
	 */

	@Test
	public void testAddDatasource(){
		System.out.println("testing add new datasources");
		SaikuDatasource datasource = new SaikuDatasource("test",SaikuDatasource.Type.OLAP,new Properties());
		ds.addDatasource(datasource);
		
		datasource = ds.getDatasource("test");
		Assert.assertNotNull(datasource);
	}
	
	/**
	 * This method attempts to test remove add new datasources.
	 */

	@Test
	public void testRemoveDatasource(){
		System.out.println("testing remove datasources");
		SaikuDatasource datasource = new SaikuDatasource("test",SaikuDatasource.Type.OLAP,new Properties());
		ds.addDatasource(datasource);
		
		datasource = ds.getDatasource("test");
		Assert.assertNotNull(datasource);
		
		ds.removeDatasource("test");
		datasource = ds.getDatasource("test");
		Assert.assertNull(datasource);
	}
	
	/**
	 * This method attempts to test build OlapMetaExplorer
	 */

	@Test
	public void testBuildOlapMetaExplorer(){
		System.out.println("testing OlapMetaExplorer");
		try{
			IConnectionManager ic = new TConnectionManager();
			ic.setDataSourceManager(ds);
			OlapMetaExplorer olapMetaExplorer = new OlapMetaExplorer(ic);
			List<SaikuConnection> connections = olapMetaExplorer.getAllConnections();
			
			Assert.assertNotNull(connections);
			
			for (SaikuConnection saikuConnection : connections) {
				System.out.println(saikuConnection.getUniqueName());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	/**
	 * This method attempts to test remove add new datasources.
	 */
	@Test
	 public void testReloadDatasource(){
	  System.out.println("testing reload datasources");  
	  ds.removeDatasource("hungermap - en");
	  
	  ds.loadByName("hungermap - en");  
	  SaikuDatasource datasource = ds.getDatasource("hungermap - en");  
	  Assert.assertNotNull(datasource);
	  //System.out.println(ds.getDatasources());
	 }
	
}
