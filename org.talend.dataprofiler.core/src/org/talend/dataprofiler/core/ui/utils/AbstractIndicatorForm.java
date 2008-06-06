// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.AbstractIndicatorParameter;


/**
 * DOC zqin  class global comment. Detailled comment
 */
public abstract class AbstractIndicatorForm extends AbstractForm {
    
    private static List<AbstractIndicatorParameter> parameters = new ArrayList<AbstractIndicatorParameter>();
    
    public static final String TEXT_LENGTH_FORM = "Text Length";
    
    public static final String BINS_DESIGNER_FORM = "Bins Designer";
    
    public static final String DATA_THRESHOLDS_FORM = "Data Thresholds";
    
    public static final String TEXT_PARAMETERS_FORM = "Text Parameter";
    
    public static final String TIME_SLICES_FROM = "Time Slices";
    
    /**
     * DOC zqin AbstractIndicatorForm constructor comment.
     * @param parent
     * @param style
     */
    public AbstractIndicatorForm(Composite parent, int style) {
        
        super(parent, style);
    }
    
    public void injectTheParameter(AbstractIndicatorParameter parameter) {
        
        if (parameter != null) {
            
            setParameter(parameter);
        }
        
        AbstractIndicatorForm.parameters.add(getParameter()); 

    }
    
    public static List<AbstractIndicatorParameter> getTheParameter() {
        
        return AbstractIndicatorForm.parameters;
    }
    
    public static void emptyParameterList() {
        
        parameters.clear();
    }
    
    public abstract String getFormName();
    
    public abstract AbstractIndicatorParameter getParameter();
    
    public abstract void setParameter(AbstractIndicatorParameter parameter);
}
