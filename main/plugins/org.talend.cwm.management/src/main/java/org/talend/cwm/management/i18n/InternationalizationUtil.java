// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.cwm.management.i18n;

import org.apache.commons.lang.StringUtils;
import org.talend.core.model.properties.Property;
import org.talend.dataquality.PluginConstant;

/**
 * internationalization System indicator definition
 * 
 */
public class InternationalizationUtil {

    /**
     * 
     * get Internationalization Label of indicatorDefinition
     * 
     * @param originalName the value of parameter should be lable of property
     * @return
     */
    public static String getDefinitionInternationalizationLabel(String originalName) {
        String getInternationalizationLabel = originalName == null ? StringUtils.EMPTY : Messages.getString(originalName.replace(
                PluginConstant.UNDER_LINE, PluginConstant.DOT_STRING));
        return getInternationalizationLabel;
    }

    /**
     * 
     * get Internationalization Label of indicatorDefinition
     * 
     * @param originalName the value of parameter should be lable of property
     * @return
     */
    public static String getCategoryInternationalizationLabel(String originalName) {
        String getInternationalizationLabel = originalName == null ? StringUtils.EMPTY : Messages.getString(originalName.replace(
                PluginConstant.SPACE_STRING, PluginConstant.DOT_STRING));
        return getInternationalizationLabel;
    }

    /**
     * 
     * get Internationalization Label of indicatorDefinition
     * 
     * @param originalName
     * @return
     */
    public static String getDefinitionInternationalizationLabel(Property property) {
        if (property == null) {
            return StringUtils.EMPTY;
        }
        String originalName = property.getLabel();
        return getDefinitionInternationalizationLabel(originalName);
    }
}
