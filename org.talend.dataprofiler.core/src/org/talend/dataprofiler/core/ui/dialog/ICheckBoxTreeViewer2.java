package org.talend.dataprofiler.core.ui.dialog;

public interface ICheckBoxTreeViewer2 {

	 public boolean setChecked(Object element, boolean state, boolean isCheckRelation);
	 
	 public void setCheckedElements(Object[] elements,boolean isCheckRelation);
}
