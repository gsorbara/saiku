/*
 * Copyright (C) 2011 OSBI Ltd
 *
 * This program is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by the Free 
 * Software Foundation; either version 2 of the License, or (at your option) 
 * any later version.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along 
 * with this program; if not, write to the Free Software Foundation, Inc., 
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA 
 *
 */
package org.saiku.olap.dto.resultset;

import java.io.Serializable;
import java.util.HashMap;


public class FaoMemberCell extends MemberCell implements Serializable {
	
    private static final long serialVersionUID = 1L;

    private HashMap<String,String> faoProperties = new HashMap<String, String>();

    public FaoMemberCell() {
        super();
    }

    public FaoMemberCell(final boolean right, final boolean sameAsPrev) {
    	
    	super(right, sameAsPrev);
    }

	public HashMap<String, String> getFaoProperties() {
		return faoProperties;
	}

	public void setFaoProperties(HashMap<String, String> faoProperties) {
		this.faoProperties = faoProperties;
	}

    public void setFaoProperty(String name, String value){
    	faoProperties.put(name, value);
    }
    
    public String getFaoProperty(String name){
        return faoProperties.get(name);
    }

}
