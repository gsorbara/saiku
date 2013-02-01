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
package org.saiku.web.rest.resources;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.saiku.olap.dto.SaikuConnection;
import org.saiku.service.olap.OlapDiscoverService;
import org.saiku.service.util.exception.SaikuServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Path("/saiku/sanitycheck")
public class SanityCheckResource {

	private OlapDiscoverService olapDiscoverService;

	private static final Logger log = LoggerFactory
			.getLogger(OlapDiscoverResource.class);

	public void setOlapDiscoverService(OlapDiscoverService olapds) {
		olapDiscoverService = olapds;
	}

	/**
	 * Get service Sanity Check.
	 * 
	 * @return A HTTP status
	 */
	@GET
	@Path("")
	@Produces({ "application/json" })
	public Status checkSanityCheck() {
		try {
			List<SaikuConnection> result = olapDiscoverService
					.getAllConnections();
			if (result == null)
				return Status.INTERNAL_SERVER_ERROR;

			return Status.OK;
		} catch (SaikuServiceException e) {
			log.error(this.getClass().getName(), e);
			return Status.INTERNAL_SERVER_ERROR;
		}
	}

}
