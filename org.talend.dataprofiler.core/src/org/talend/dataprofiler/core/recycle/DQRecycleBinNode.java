// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
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
 * @author Administrator
 *
 */
public class DQRecycleBinNode {
	
	private List<Object> deletedChildren;
	private Object obj;
    

   public DQRecycleBinNode(){
	   
   }


   /*
	 * (non-Javadoc)
	 * 
	 * @see org.talend.dataprofiler.core.recycle.IRecycleBinNode#getObject()
	 */
	public Object getObject() {
		// TODO Auto-generated method stub
		return obj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.talend.dataprofiler.core.recycle.IRecycleBinNode#setObject(java.lang
	 * .Object)
	 */
	public void setObject(Object object) {
		// TODO Auto-generated method stub
		this.obj = object;

	}
	
	public List<Object> getDeletedChildren() {
		// TODO Auto-generated method stub
		return deletedChildren;
	}

	public void setDeletedChildren(List<Object> deletedChildren) {
		// TODO Auto-generated method stub
		this.deletedChildren=deletedChildren;
		
	}

	

}
