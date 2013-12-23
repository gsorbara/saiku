package org.saiku.sdw.client;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.saiku.sdw.client.dto.Catalog;
import org.saiku.sdw.client.dto.Catalogs;
import org.saiku.sdw.client.dto.Connection;
import org.saiku.sdw.client.dto.Connections;
import org.saiku.sdw.client.dto.Schema;
import org.saiku.sdw.client.dto.SchemaLanguage;
import org.saiku.sdw.client.dto.SchemaLanguages;
import org.saiku.sdw.client.dto.Schemas;
import org.saiku.sdw.client.dto.Workspace;
import org.saiku.sdw.client.dto.Workspaces;
import org.saiku.sdw.client.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SDWMetadataClientManager extends SDWClientAbstract{
	
	private static Logger log = Logger.getLogger(SDWMetadataClientManager.class);
	
	/**
	 * All the REST service URI has been configured in spring configuration, and injected to SDWMetadataConfiguration object.
	 */
	private SDWMetadataConfiguration sdwMetadataConfiguration;
	
	public SDWMetadataClientManager(SDWMetadataConfiguration sdwMetadataConfiguration){
		this.sdwMetadataConfiguration = sdwMetadataConfiguration;
	}
	
	public SDWMetadataConfiguration getSdwMetadataConfiguration() {
		return sdwMetadataConfiguration;
	}

	public void setSdwMetadataConfiguration(
			SDWMetadataConfiguration sdwMetadataConfiguration) {
		this.sdwMetadataConfiguration = sdwMetadataConfiguration;
	}
	
	/**
	 * Retrieves the workspaces from SDW metadata service.
	 * <br/><br/><b>Service URI:</b> /techcdr-sdw/services/resources/workspaces
	 * @return Workspaces info
	 */
	public Workspaces retrieveWorkspaces(){
		String workspaceUrlStr = sdwMetadataConfiguration.getHost()+sdwMetadataConfiguration.getWorkspaceUri();
		log.debug(workspaceUrlStr);
		
		try {
			InputStream is = deriveInputStreamFrom(new URL(sdwMetadataConfiguration.getHost()+sdwMetadataConfiguration.getWorkspaceUri()));
			if(is != null){
				Workspaces workspaces = new Workspaces();
				List<Workspace> list = new ArrayList<Workspace>();
				workspaces.setWorkspace(list);
				
				Document doc = XMLUtil.buildDocument(is, false);
				NodeList nodeList = XMLUtil.query(doc, "//Workspace");
				int size = nodeList.getLength();
				for (int i = 1; i <=size; i++) {
					
					Node uuid = XMLUtil.queryNode(doc, "(//Workspace/uuid)["+i+"]");
					Node name = XMLUtil.queryNode(doc, "(//Workspace/name)["+i+"]");
					Node caption = XMLUtil.queryNode(doc, "(//Workspace/caption)["+i+"]");
					Node description = XMLUtil.queryNode(doc, "(//Workspace/description)["+i+"]");
					Node visible = XMLUtil.queryNode(doc, "(//Workspace/visible)["+i+"]");
					Node published = XMLUtil.queryNode(doc, "(//Workspace/published)["+i+"]");
					Node status = XMLUtil.queryNode(doc, "(//Workspace/status)["+i+"]");
					
					Workspace workspace = new Workspace();
					if(caption != null)workspace.setCaption(caption.getTextContent());
					if(description != null)workspace.setDescription(description.getTextContent());
					if(name != null)workspace.setName(name.getTextContent());
					if(published != null)workspace.setPublished(Boolean.parseBoolean(published.getTextContent()));
					if(status != null)workspace.setStatus(status.getTextContent());
					if(uuid != null)workspace.setUuid(uuid.getTextContent());
					if(visible != null)workspace.setVisible(Boolean.parseBoolean(visible.getTextContent()));
					list.add(workspace);
				}
				return workspaces;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Retrieves the Catalogs from SDW metadata service.
	 * <br/><br/><b>Service URI:</b> /techcdr-sdw/services/resources/catalogs/{workspaceName}
	 * @param workspaceName
	 * @return Catalogs info
	 */
	public Catalogs retrieveCatalogs(String workspaceName){
		String catalogUrlStr = sdwMetadataConfiguration.getHost()+sdwMetadataConfiguration.getCatalogUri();
		catalogUrlStr = catalogUrlStr.replaceAll("\\{workspaceName\\}", workspaceName);
		catalogUrlStr = catalogUrlStr.replaceAll("\\s", "%20");
		log.debug(catalogUrlStr);
		try {
			InputStream is = deriveInputStreamFrom(new URL(catalogUrlStr));
			if(is != null){
				Catalogs catalogs = new Catalogs();
				List<Catalog> list = new ArrayList<Catalog>();
				catalogs.setCatalog(list);
				
				Document doc = XMLUtil.buildDocument(is, false);
				NodeList nodeList = XMLUtil.query(doc, "//Catalog");
				int size = nodeList.getLength();
				for (int i = 1; i <=size; i++) {
					Node uuid = XMLUtil.queryNode(doc, "(//Catalog/uuid)["+i+"]");
					Node name = XMLUtil.queryNode(doc, "(//Catalog/name)["+i+"]");
					Node visible = XMLUtil.queryNode(doc, "(//Catalog/visible)["+i+"]");
					Node published = XMLUtil.queryNode(doc, "(//Catalog/published)["+i+"]");
					Node status = XMLUtil.queryNode(doc, "(//Catalog/status)["+i+"]");
					
					Catalog catalog = new Catalog();
					if(name != null)catalog.setName(name.getTextContent());
					if(published != null)catalog.setPublished(Boolean.parseBoolean(published.getTextContent()));
					if(status != null)catalog.setStatus(status.getTextContent());
					if(uuid != null)catalog.setUuid(uuid.getTextContent());
					if(visible != null)catalog.setVisible(Boolean.parseBoolean(visible.getTextContent()));
					list.add(catalog);
				}
				return catalogs;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Retrieves the Connection from SDW metadata service.
	 * <br/><br/><b>Service URI:</b> /techcdr-sdw/services/resources/connections/{workspaceName}/{connectionName}
	 * @param workspaceName
	 * @return Connection info
	 */
	public Connection retrieveConnection(String workspaceName,String connectionName){
		String connectionUrlStr = sdwMetadataConfiguration.getHost()+sdwMetadataConfiguration.getConnectionUri();
		connectionUrlStr = connectionUrlStr.replaceAll("\\{workspaceName\\}", workspaceName);
		connectionUrlStr = connectionUrlStr.replaceAll("\\{connectionName\\}", connectionName);
		connectionUrlStr = connectionUrlStr.replaceAll("\\s", "%20");
		log.debug(connectionUrlStr);
		try {
			
			InputStream is = deriveInputStreamFrom(new URL(connectionUrlStr));
			if(is != null){
				
				Connection connection = new Connection();
				Document doc = XMLUtil.buildDocument(is, false);
				Node uuid = XMLUtil.queryNode(doc, "//uuid");
				Node name = XMLUtil.queryNode(doc, "//name");
				Node caption = XMLUtil.queryNode(doc, "//caption");
				Node description = XMLUtil.queryNode(doc, "//description");
				Node driver = XMLUtil.queryNode(doc, "//driver");
				Node url = XMLUtil.queryNode(doc, "//url");
				Node username = XMLUtil.queryNode(doc, "//username");
				Node password = XMLUtil.queryNode(doc, "//password");
				
				if(caption != null)connection.setCaption(caption.getTextContent());
				if(description != null)connection.setDescription(description.getTextContent());
				if(driver != null)connection.setDriver(driver.getTextContent());
				if(name != null)connection.setName(name.getTextContent());
				if(password != null)connection.setPassword(password.getTextContent());
				if(url != null)connection.setUrl(url.getTextContent());
				if(username != null)connection.setUsername(username.getTextContent());
				if(uuid != null)connection.setUuid(uuid.getTextContent());
				
				return connection;
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Retrieves the list of Connection from SDW metadata service.
	 * <br/><br/><b>Service URI:</b> /techcdr-sdw/services/resources/connections/{workspaceName}
	 * @param workspaceName
	 * @return List of available connections
	 */
	public Connections retrieveConnections(String workspaceName) {
		String connectionsUrlStr = sdwMetadataConfiguration.getHost()+sdwMetadataConfiguration.getConnectionsUri();
		connectionsUrlStr = connectionsUrlStr.replaceAll("\\{workspaceName\\}", workspaceName);
		connectionsUrlStr = connectionsUrlStr.replaceAll("\\s", "%20");
		log.debug(connectionsUrlStr);
		try{
			InputStream is = deriveInputStreamFrom(new URL(connectionsUrlStr));
			if(is != null){
				Connections connections = new Connections();
				List<Connection> list = new ArrayList<Connection>();
				connections.setConnections(list);
				Document doc = XMLUtil.buildDocument(is, false);
				NodeList nodeList = XMLUtil.query(doc, "//Connection");
				int size = nodeList.getLength();
				for (int i = 1; i <=size; i++) {
					Node uuid = XMLUtil.queryNode(doc, "(//Connection/uuid)["+i+"]");
					Node name = XMLUtil.queryNode(doc, "(//Connection/name)["+i+"]");
					Node caption = XMLUtil.queryNode(doc, "(//Connection/caption)["+i+"]");
					Node description = XMLUtil.queryNode(doc, "(//Connection/description)["+i+"]");
					Node driver = XMLUtil.queryNode(doc, "(//Connection/driver)["+i+"]");
					Node url = XMLUtil.queryNode(doc, "(//Connection/url)["+i+"]");
					Node username = XMLUtil.queryNode(doc, "(//Connection/username)["+i+"]");
					Node password = XMLUtil.queryNode(doc, "(//Connection/password)["+i+"]");
					
					Connection connection = new Connection();
					if(caption != null)connection.setCaption(caption.getTextContent());
					if(description != null)connection.setDescription(description.getTextContent());
					if(driver != null)connection.setDriver(driver.getTextContent());
					if(name != null)connection.setName(name.getTextContent());
					if(password != null)connection.setPassword(password.getTextContent());
					if(url != null)connection.setUrl(url.getTextContent());
					if(username != null)connection.setUsername(username.getTextContent());
					if(uuid != null)connection.setUuid(uuid.getTextContent());
					list.add(connection);
				}
				return connections;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Retrieves the Schemas from SDW metadata service.
	 * <br/><br/><b>Service URI:</b> /techcdr-sdw/services/resources/schemas/{workspaceName}?catalogName={catalogName}
	 * @param workspaceName
	 * @param catalogName
	 * @return Schemas info
	 */
	public Schemas retrieveSchemas(String workspaceName,String catalogName){
		String schemasUrlStr = sdwMetadataConfiguration.getHost()+sdwMetadataConfiguration.getSchemaUri();
		schemasUrlStr = schemasUrlStr.replaceAll("\\{workspaceName\\}", workspaceName);
		schemasUrlStr = schemasUrlStr.replaceAll("\\{catalogName\\}", catalogName);
		schemasUrlStr = schemasUrlStr.replaceAll("\\s", "%20");
		log.debug(schemasUrlStr);
		try {
			InputStream is = deriveInputStreamFrom(new URL(schemasUrlStr));
			if(is != null){
				Schemas schemas = new Schemas();
				List<Schema> list = new ArrayList<Schema>();
				schemas.setSchema(list);
				
				Document doc = XMLUtil.buildDocument(is, false);
				NodeList nodeList = XMLUtil.query(doc, "//Schema");
				int size = nodeList.getLength();
				for (int i = 1; i <=size; i++) {
					Node uuid = XMLUtil.queryNode(doc, "(//Schema/uuid)["+i+"]");
					Node name = XMLUtil.queryNode(doc, "(//Schema/name)["+i+"]");
					Node defaultDescription = XMLUtil.queryNode(doc, "(//Schema/defaultDescription)["+i+"]");
					Node defaultLanguage = XMLUtil.queryNode(doc, "(//Schema/defaultLanguage)["+i+"]");
					Node version = XMLUtil.queryNode(doc, "(//Schema/version)["+i+"]");
					Node visible = XMLUtil.queryNode(doc, "(//Schema/visible)["+i+"]");
					Node published = XMLUtil.queryNode(doc, "(//Schema/published)["+i+"]");
					Node fileName = XMLUtil.queryNode(doc, "(//Schema/fileName)["+i+"]");
					Node status = XMLUtil.queryNode(doc, "(//Schema/status)["+i+"]");
					Node connectionName = XMLUtil.queryNode(doc, "(//Schema/connectionName)["+i+"]");
					
					Schema schema = new Schema();
					if(connectionName != null)schema.setConnectionName(connectionName.getTextContent());
					if(defaultDescription != null)schema.setDefaultDescription(defaultDescription.getTextContent());
					if(defaultLanguage != null)schema.setDefaultLanguage(defaultLanguage.getTextContent());
					if(fileName != null)schema.setFileName(fileName.getTextContent());
					if(name != null)schema.setName(name.getTextContent());
					if(published != null)schema.setPublished(Boolean.parseBoolean(published.getTextContent()));
					if(status != null)schema.setStatus(status.getTextContent());
					if(uuid != null)schema.setUuid(uuid.getTextContent());
					if(version != null)schema.setVersion(Integer.parseInt(version.getTextContent()));
					if(visible != null)schema.setVisible(Boolean.parseBoolean(visible.getTextContent()));
					list.add(schema);
				}
				return schemas;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Retrieves the mondrian schemas xml from SDW metadata service.
	 * <br/><br/><b>Service URI:</b> /techcdr-sdw/services/resources/schemas/{workspaceName}/{catalogName}.{schemaName}
	 * @param workspaceName
	 * @param catalogName
	 * @param schemaName
	 * @return Mondrian Schema
	 */
	public String retrieveMondrianSchemasXML(String workspaceName,String catalogName,String schemaName){
		String schemaXML = sdwMetadataConfiguration.getHost()+sdwMetadataConfiguration.getMondrianSchemaUri();
		schemaXML = schemaXML.replaceAll("\\{workspaceName\\}", workspaceName);
		schemaXML = schemaXML.replaceAll("\\{catalogName\\}", catalogName);
		schemaXML = schemaXML.replaceAll("\\{schemaName\\}", schemaName);
		schemaXML = schemaXML.replaceAll("\\s", "%20");
		log.debug(schemaXML);
		try {
			InputStream is = deriveInputStreamFrom(new URL(schemaXML));
			if(is != null)
			return IOUtils.toString(is, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	
	/**
	 * Retrieves the SchemaLanguagess from SDW metadata service.
	 * <br/><br/><b>Service URI:</b> /techcdr-sdw/services/resources/schemas/{workspaceName}/{catalogName}.{schemaName}/language
	 * @param workspaceName
	 * @param catalogName
	 * @param schemaName
	 * @return Schema languages info
	 */
	public SchemaLanguages retrieveSchemaLanguages(String workspaceName, String catalogName, String schemaName){
		String schemaLanguagesUrlStr = sdwMetadataConfiguration.getHost()+sdwMetadataConfiguration.getSchemaLanguagesUri();
		schemaLanguagesUrlStr = schemaLanguagesUrlStr.replaceAll("\\{workspaceName\\}", workspaceName);
		schemaLanguagesUrlStr = schemaLanguagesUrlStr.replaceAll("\\{catalogName\\}", catalogName);
		schemaLanguagesUrlStr = schemaLanguagesUrlStr.replaceAll("\\{schemaName\\}", schemaName);
		schemaLanguagesUrlStr = schemaLanguagesUrlStr.replaceAll("\\s", "%20");
		log.debug(schemaLanguagesUrlStr);
		try {
			InputStream is = deriveInputStreamFrom(new URL(schemaLanguagesUrlStr));
			if(is != null){
				SchemaLanguages schemaLanguages = new SchemaLanguages();
				List<SchemaLanguage> list = new ArrayList<SchemaLanguage>();
				schemaLanguages.setSchemaLanguages(list);
				
				Document doc = XMLUtil.buildDocument(is, false);
				NodeList nodeList = XMLUtil.query(doc, "//SchemaLanguage");
				int size = nodeList.getLength();
				for (int i = 1; i <=size; i++) {
					Node language = XMLUtil.queryNode(doc, "(//SchemaLanguage/language)["+i+"]");
					Node xml = XMLUtil.queryNode(doc, "(//SchemaLanguage/xml)["+i+"]");
					Node fileName = XMLUtil.queryNode(doc, "(//SchemaLanguage/fileName)["+i+"]");
					Node enabled = XMLUtil.queryNode(doc, "(//SchemaLanguage/enabled)["+i+"]");
					
					SchemaLanguage schemaLanguage = new SchemaLanguage();
					if(language != null)
						schemaLanguage.setLanguage(language.getTextContent());
					if(xml != null)
						schemaLanguage.setXml(xml.getTextContent());
					if(fileName != null)
						schemaLanguage.setFileName(fileName.getTextContent());
					
					if(enabled != null && Boolean.parseBoolean(enabled.getTextContent()))					
						list.add(schemaLanguage);
				}
				return schemaLanguages;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	
}
