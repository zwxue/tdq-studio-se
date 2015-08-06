// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.recycle;


import java.util.List;

/**
 * @author qiongli
 * As a subNode under Recycle bin
 */
public class DQRecycleBinNode {
	
	private List<Object> deletedChildren;
	private Object obj;
    

   public DQRecycleBinNode(){
	   
   }

    /**
     * store file or folder.
     * 
     * @return
     */
	public Object getObject() {
		return obj;
	}

    /**
     * 
     * @param object
     */
	public void setObject(Object object) {
		this.obj = object;

	}

    /**
     * get its children.
     * 
     * @return
     */
	public List<Object> getDeletedChildren() {
		return deletedChildren;
	}

	public void setDeletedChildren(List<Object> deletedChildren) {
		this.deletedChildren=deletedChildren;
		
	}

	

}
