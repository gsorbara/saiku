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
package org.saiku.olap.dto.resultset;

import java.io.Serializable;
import java.util.List;


public class MemberCell extends AbstractBaseCell implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean lastRow = false;

    private boolean expanded = false;

    private String parentDimension = null;

    private String parentMember = null;

    private MemberCell rightOf = null;

    private String uniqueName;

    private int childMemberCount;

    private String rightOfDimension;

    private List<String> memberPath;
    
    private String hierarchy;
    
    private String level;

    //private HashMap<String, String> properties = new HashMap<String, String>();
    /**
     * 
     * Blank Constructor for Serializable niceness, don't use it.
     * 
     */
    public MemberCell() {
        super();
    }

    /**
     * 
     * Creates a member cell.
     * 
     * @param b
     * @param c
     */
    public MemberCell(final boolean right, final boolean sameAsPrev) {
        this();
        this.right = right;
        this.sameAsPrev = sameAsPrev;
    }

    /**
     * Returns true if this is the bottom row of the column headers(supposedly).
     * 
     * @return the lastRow
     */
    public boolean isLastRow() {
        return lastRow;
    }

    /**
     * 
     * Set true if this is the bottom row of the column headers.
     * 
     * @param lastRow
     *            the lastRow to set
     */
    public void setLastRow(final boolean lastRow) {
        this.lastRow = lastRow;
    }

    public void setParentDimension(final String parDim) {
        parentDimension = parDim;
    }

    public String getParentDimension() {
        return parentDimension;
    }

    public String getHierarchy() {
		return hierarchy;
	}

	public void setHierarchy(String hierarchy) {
		this.hierarchy = hierarchy;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	/**
     *Is the member expanded?.
     * 
     * @return the expanded
     */
    public boolean isExpanded() {
        return expanded;
    }

    /**
     * 
     *Set Expanded Flag.
     * 
     * @param expanded
     *            the expanded to set
     */
    public void setExpanded(final boolean expanded) {
        this.expanded = expanded;
    }

    /**
     *Set the Parent Member Variable.
     * 
     * @param parentMember
     */
    public void setParentMember(final String parentMember) {

        this.parentMember = parentMember;

    }

    public String getParentMember() {
        return parentMember;
    }

    /**
     * Set the cell's unique name. 
     * 
     * @param uniqueName
     */
    public void setUniquename(final String uniqueName) {

        this.uniqueName = uniqueName;

    }

    public String getUniqueName() {
        return uniqueName;
    }

    /**
     * List the amount of children.
     * 
     * @param childMemberCount
     */
    public void setChildMemberCount(final int childMemberCount) {
        this.childMemberCount = childMemberCount;
    }

    public int getChildMemberCount() {
        return childMemberCount;
    }

    /**
     *TODO JAVADOC
     * 
     * @param memberCell
     */
    public void setRightOf(final MemberCell memberCell) {
        this.rightOf = memberCell;

    }

    public MemberCell getRightOf() {
        return rightOf;
    }

    /**
     *TODO JAVADOC
     * 
     * @param name
     */
    public void setRightOfDimension(String name) {

        this.rightOfDimension = name;

    }

    public String getRightOfDimension() {
        return this.rightOfDimension;
    }

    public void setMemberPath(List<String> memberPath) {
        this.memberPath = memberPath;

    }

    public List<String> getMemberPath() {
        return memberPath;
    }

    /*public void setProperty(String name, String value){
        properties.put(name, value);
    }

    public HashMap<String, String> getProperties(){
        return properties;
    }

    public String getProperty(String name){
        return properties.get(name);
    }*/

}
