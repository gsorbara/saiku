package org.saiku.sdw.datasource;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.saiku.datasources.connection.ISaikuConnection;
import org.saiku.datasources.datasource.SaikuDatasource;
import org.saiku.sdw.client.SDWMetadataClient;
import org.saiku.sdw.client.dto.Catalog;
import org.saiku.sdw.client.dto.Catalogs;
import org.saiku.sdw.client.dto.Connection;
import org.saiku.sdw.client.dto.Schema;
import org.saiku.sdw.client.dto.SchemaLanguage;
import org.saiku.sdw.client.dto.SchemaLanguages;
import org.saiku.sdw.client.dto.Schemas;
import org.saiku.sdw.client.dto.Workspace;
import org.saiku.sdw.client.dto.Workspaces;
import org.saiku.service.datasource.IDatasourceManager;
import org.saiku.service.util.exception.SaikuServiceException;

public class SDWDatasourceManager implements IDatasourceManager{
	
	private static Logger log = Logger.getLogger(SDWDatasourceManager.class);
	
	private Map<String,SaikuDatasource> datasources = Collections.synchronizedMap(new HashMap<String,SaikuDatasource>());
	private SDWMetadataClient sdwMetadataClient;
	
	public SDWDatasourceManager(SDWMetadataClient sdwMetadataClient){
		this.sdwMetadataClient = sdwMetadataClient;
		load();
	}
	
	public void setSdwMetadataClient(SDWMetadataClient sdwMetadataClient) {
		this.sdwMetadataClient = sdwMetadataClient;
		load();
	}

	protected void createDatasource(Workspace workspace) throws Exception {
		String workspaceName = workspace.getName();
		log.debug("create datasource for workspaceName="+workspaceName);
		
		Catalogs catalogs = sdwMetadataClient.retrieveCatalogs(workspaceName);
		List<Catalog> listCatalog = catalogs.getCatalog();
		
		log.debug(workspaceName + " size = " + listCatalog.size());
		
		if(!listCatalog.isEmpty()){
			for (Catalog catalog : listCatalog) {
				
				if(!catalog.isVisible()) {
					log.debug("Skipping non-visible catalog: " + catalog.getName());
					continue;
				}
				
				String catalogName = catalog.getName();
				if(catalogName != null){
					Schemas schemas = sdwMetadataClient.retrieveSchemas(workspaceName, catalogName);
					List<Schema> listSchema = schemas.getSchema();
					if(listSchema != null) {
						for (Schema schema : listSchema) {
							if(!schema.isVisible()) {
								log.debug("Skipping non-visible schema: " + schema.getName());
								continue;
							}
							
							String schemaName = schema.getName();
							String connectionname = schema.getConnectionName();
							if(schemaName != null && connectionname != null){
								Connection connection = sdwMetadataClient.retrieveConnection(workspaceName,connectionname );
								
								SchemaLanguages schemaLanguages = sdwMetadataClient.retrieveSchemaLanguages(workspaceName, catalogName, schemaName);
								if(schemaLanguages != null){
									for(SchemaLanguage schemaLanguage : schemaLanguages.getSchemaLanguages()) {
										
										String mondrianSchemaXML = schemaLanguage.getXml();
										
										if(mondrianSchemaXML != null && connection != null){
											
											 log.debug("create datasource for workspaceName="+workspaceName + ", catalogName=" + catalogName + ", schemaName=" + schemaName + ", language=" + schemaLanguage.getLanguage());
											
											 // TODO: should be managed better than this!
											 //
											 String datasourceName = null;
											 if(schema.getName().endsWith("- " + schemaLanguage.getLanguage()))
												 datasourceName = schema.getName();
											 else
												 datasourceName = schema.getName() + " - " + schemaLanguage.getLanguage();
											 
											 //Add the database connection info.
											 Properties props = new Properties();
											 props.setProperty(ISaikuConnection.NAME_KEY, datasourceName);
											 props.setProperty("type", ISaikuConnection.OLAP_DATASOURCE);
											 props.setProperty(ISaikuConnection.USERNAME_KEY, connection.getUsername());
											 props.setProperty(ISaikuConnection.PASSWORD_KEY, connection.getPassword());
											 props.setProperty(ISaikuConnection.DRIVER_KEY, "mondrian.olap4j.MondrianOlap4jDriver");
											 
											 //Add the database URL		
											 StringBuffer buffer = new StringBuffer();
											 buffer.append("jdbc:mondrian:Jdbc=");
											 buffer.append(connection.getUrl());
											 
											 //Add mondrian schema for each connection
											 buffer.append(";CatalogContent=\"" + mondrianSchemaXML.replaceAll("\"", "\"\""));
											 buffer.append("\";JdbcDrivers=");
											 buffer.append(connection.getDriver());
												
											 props.setProperty(ISaikuConnection.URL_KEY, buffer.toString());
		
											 //SDW-206
											 props.setProperty("workspaceName", workspaceName);
											 props.setProperty("connectionname", connectionname);
											 props.setProperty("catalogName", catalogName);
											 props.setProperty("schemaName", schemaName);
											 props.setProperty("schemaLanguage", schemaLanguage.getLanguage());
											 
											 
											 //String datasourceName = workspaceName + " | " + catalogName + " | " + schemaName + " | " + connectionname;
											 SaikuDatasource ds = new SaikuDatasource(datasourceName,SaikuDatasource.Type.OLAP,props);
											 datasources.put(datasourceName, ds);
											 
										}
										
									}
								}								
							}else{
								log.error("Schema || connectionname not found for workspaceName="+workspaceName + ", catalogName="+catalogName);
							}
						}
					}
				}else{
					log.error("Catalog not found for workspaceName="+workspaceName);
				}
			}
		}else{
			log.error("Catalog not found for workspaceName="+workspaceName);
		}
	}
	
	public void load() {
		
		log.debug("load datasource");
		datasources.clear();
		try{
			
			Workspaces ws = sdwMetadataClient.retrieveWorkspaces();
			
			List<Workspace> workspaces = ws.getWorkspace();
			for (Workspace workspace : workspaces) {
				log.debug(workspace.getName());
				
				if(workspace.isVisible())
					createDatasource(workspace);
				else
					log.debug("Skipping non visible workspace: " + workspace);
			}
			
		}catch(Exception e){
			e.printStackTrace();
			throw new SaikuServiceException(e.getMessage(),e);
		}
	}
	
	public void loadByName(String name){

		log.debug("load datasource by name");
		
		SaikuDatasource ds = datasources.get(name);
		if(ds != null){			
			String workspaceName = ds.getProperties() == null ? "" : (String) ds.getProperties().get("workspaceName");
			String connectionname = ds.getProperties() == null ? "" : (String) ds.getProperties().get("connectionname");
			String catalogName = ds.getProperties() == null ? "" : (String) ds.getProperties().get("catalogName");
			String schemaName = ds.getProperties() == null ? "" : (String) ds.getProperties().get("schemaName");
			String datasourceName = ds.getProperties() == null ? "" : (String) ds.getProperties().get(ISaikuConnection.NAME_KEY);  
			String sLanguage =  ds.getProperties() == null ? "" : (String) ds.getProperties().get("schemaLanguage");
			datasources.remove(datasourceName);
			
			Connection connection = sdwMetadataClient.retrieveConnection(workspaceName,connectionname );		
			
			SchemaLanguages schemaLanguages = sdwMetadataClient.retrieveSchemaLanguages(workspaceName, catalogName, schemaName);
			for(SchemaLanguage schemaLanguage : schemaLanguages.getSchemaLanguages()) {
				if(!sLanguage.equals(schemaLanguage.getLanguage()))
					continue;
				
				String mondrianSchemaXML = schemaLanguage.getXml();
				if(mondrianSchemaXML != null && connection != null){				
					
					 log.debug("reload datasource for workspaceName="+workspaceName + ", catalogName=" + catalogName + ", schemaName=" + schemaName);
									 
					 //Add the database connection info.
					 Properties props = new Properties();
					 props.setProperty(ISaikuConnection.NAME_KEY, datasourceName);
					 props.setProperty("type", ISaikuConnection.OLAP_DATASOURCE);
					 props.setProperty(ISaikuConnection.USERNAME_KEY, connection.getUsername());
					 props.setProperty(ISaikuConnection.PASSWORD_KEY, connection.getPassword());
					 props.setProperty(ISaikuConnection.DRIVER_KEY, "mondrian.olap4j.MondrianOlap4jDriver");
					 
					 //Add the database URL		
					 StringBuffer buffer = new StringBuffer();
					 buffer.append("jdbc:mondrian:Jdbc=");
					 buffer.append(connection.getUrl());
					 
					 //Add mondrian schema for each connection
					 buffer.append(";CatalogContent=\"" + mondrianSchemaXML.replaceAll("\"", "\"\""));
					 buffer.append("\";JdbcDrivers=");
					 buffer.append(connection.getDriver());
						
					 props.setProperty(ISaikuConnection.URL_KEY, buffer.toString());

					 props.setProperty("workspaceName", workspaceName);
					 props.setProperty("connectionname", connectionname);
					 props.setProperty("catalogName", catalogName);
					 props.setProperty("schemaName", schemaName);
					 props.setProperty("schemaLanguage", sLanguage);
					 
					 //String datasourceName = workspaceName + " | " + catalogName + " | " + schemaName + " | " + connectionname;
					 ds = new SaikuDatasource(datasourceName,SaikuDatasource.Type.OLAP,props);
					 datasources.put(datasourceName, ds);
					 
				}
			}
		}
	}

	public SaikuDatasource addDatasource(SaikuDatasource datasource) {
		log.debug("add datasource | datasource="+datasource);
		datasources.put(datasource.getName(), datasource);
		return datasource;
	}

	public SaikuDatasource setDatasource(SaikuDatasource datasource) {
		log.debug("set datasource | datasource="+datasource);
		return addDatasource(datasource);
	}

	public List<SaikuDatasource> addDatasources(List<SaikuDatasource> datasources) {
		log.debug("add list of datasource | datasources="+datasources);
		for (SaikuDatasource ds : datasources) {
			addDatasource(ds);
		}
		return datasources;
	}

	public boolean removeDatasource(String datasourceName) {
		log.debug("remove datasource");
		datasources.remove(datasourceName);
		return true;
	}

	public Map<String, SaikuDatasource> getDatasources() {
		log.debug("get list of datasource");
		return datasources;
	}

	public SaikuDatasource getDatasource(String datasourceName) {
		log.debug("get datasource | datasourceName="+datasourceName);
		return datasources.get(datasourceName);
	}
	
}
