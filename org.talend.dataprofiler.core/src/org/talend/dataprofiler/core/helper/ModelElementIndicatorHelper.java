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
package org.talend.dataprofiler.core.helper;

import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.cwm.helper.ModelElementHelper;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.xml.TdXMLElement;
import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataprofiler.core.model.ModelElementIndicator;
import org.talend.dataprofiler.core.model.XmlElementIndicator;
import org.talend.dataprofiler.core.model.impl.ColumnIndicatorImpl;
import org.talend.dataprofiler.core.model.impl.XmlElementIndicatorImpl;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import orgomg.cwm.objectmodel.core.ModelElement;


/**
 * DOC xqliu  class global comment. Detailled comment
 */
public final class ModelElementIndicatorHelper {

    private ModelElementIndicatorHelper() {
    }

    public static final ModelElementIndicator createModelElementIndicator(ModelElement modelElement) {
        if (modelElement instanceof TdColumn) {
            return createColumnIndicator((TdColumn) modelElement);
        } else if (modelElement instanceof TdXMLElement) {
            return createXmlElementIndicator((TdXMLElement) modelElement);
        }
        return null;
    }

    public static final ColumnIndicator createColumnIndicator(TdColumn tdColumn) {
        return new ColumnIndicatorImpl(tdColumn);
    }

    public static final XmlElementIndicator createXmlElementIndicator(TdXMLElement tdXMLElement) {
        return new XmlElementIndicatorImpl(tdXMLElement);
    }

    public static final ColumnIndicator switchColumnIndicator(IndicatorUnit indicatorUnit) {
        if (indicatorUnit.isColumn()) {
            return (ColumnIndicator) indicatorUnit.getModelElementIndicator();
        }
        return null;
    }

    public static final ColumnIndicator switchColumnIndicator(ModelElementIndicator indicator) {
        if (indicator instanceof ColumnIndicator) {
            return (ColumnIndicator) indicator;
        }
        return null;
    }

    public static final XmlElementIndicator switchXmlElementIndicator(IndicatorUnit indicatorUnit) {
        if (indicatorUnit.isXmlElement()) {
            return (XmlElementIndicator) indicatorUnit.getModelElementIndicator();
        }
        return null;
    }

    public static final XmlElementIndicator switchXmlElementIndicator(ModelElementIndicator indicator) {
        if (indicator instanceof XmlElementIndicator) {
            return (XmlElementIndicator) indicator;
        }
        return null;
    }

    public static final Connection getTdDataProvider(ModelElementIndicator indicator) {
        return ModelElementHelper.getTdDataProvider(indicator.getModelElement());
    }

}
