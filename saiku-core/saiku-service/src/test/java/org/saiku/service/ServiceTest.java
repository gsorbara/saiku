package org.saiku.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.saiku.AbstractServiceUtils;
import org.saiku.TConnectionManager;
import org.saiku.datasources.connection.IConnectionManager;
import org.saiku.datasources.datasource.SaikuDatasource;
import org.saiku.olap.discover.OlapMetaExplorer;
import org.saiku.olap.discover.OlapMetaExplorerTest;
import org.saiku.service.datasource.ClassPathResourceDatasourceManager;
import org.saiku.service.datasource.DatasourceService;
import org.saiku.service.datasource.IDatasourceManager;
import org.saiku.service.olap.OlapDiscoverService;
import org.saiku.service.olap.OlapQueryService;

public class ServiceTest {

	private static Logger log = Logger.getLogger(ServiceTest.class);
	
	protected static OlapMetaExplorer olapMetaExplorer;
	
	protected static OlapQueryService olapQueryService;
	
	protected static Properties testProps = new Properties();
	
	protected static final String connectionName = "test";
	
	//FoodMart connection properties
	protected static final String connectionProp = "connection.properties";
	
	//FAOSTAT connection properties
	//protected static final String connectionProp = "faostat-connection.properties";
	
	@BeforeClass
	public static void setup() throws IOException {
		System.out.println("Start setup");
		
		AbstractServiceUtils ast = new AbstractServiceUtils();
		ast.initTestContext();
		
		String returned = computeTestDataRoot(OlapMetaExplorerTest.class);
		File f = new File(System.getProperty("java.io.tmpdir") + "/files/");
		f.mkdir();
		IDatasourceManager ds = new ClassPathResourceDatasourceManager(System.getProperty("java.io.tmpdir") + "/files/");
		InputStream inputStream = OlapMetaExplorerTest.class.getResourceAsStream(connectionProp);
		try {
			testProps.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		setupOlapMetaExplorer(ds);
		setupOlapQueryService(ds);
	}
	
	public static void setupOlapMetaExplorer(IDatasourceManager ds){
		System.out.println("create instance for olapMetaExplorer");
		ds.setDatasource(new SaikuDatasource(connectionName, SaikuDatasource.Type.OLAP, testProps));
		IConnectionManager ic = new TConnectionManager();
		ic.setDataSourceManager(ds);
		olapMetaExplorer = new OlapMetaExplorer(ic);
	}
	
	public static void setupOlapQueryService(IDatasourceManager ds){
		System.out.println("create instance for olapQueryService");
		IConnectionManager ic = new TConnectionManager();
		ic.setDataSourceManager(ds);
		
		DatasourceService dss = new DatasourceService();
		//dss.addDatasource(new SaikuDatasource(connectionName, SaikuDatasource.Type.OLAP, testProps));
		dss.setConnectionManager(ic);
		
		OlapDiscoverService ods = new OlapDiscoverService();
		ods.setDatasourceService(dss);
		
		olapQueryService = new OlapQueryService();
		olapQueryService.setOlapDiscoverService(ods);
	}
	
	public static String computeTestDataRoot(Class anyTestClass) throws IOException {
		System.out.println("start computeTestDataRoot");
		// create a temp file
		File temp = File.createTempFile("temp-file-name", ".tmp");

		System.out.println("Temp file : " + temp.getAbsolutePath());

		// Get tempropary file path
		String absolutePath = temp.getAbsolutePath();
		String tempFilePath = absolutePath.substring(0, absolutePath.lastIndexOf(File.separator));
		return tempFilePath + "/";
	}

}
