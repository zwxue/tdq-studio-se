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
package org.talend.dataprofiler.core.pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.dataprofiler.core.helper.PatternResourceFileHelper;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataprofiler.core.model.nodes.indicator.tpye.IndicatorEnum;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.domain.pattern.ExpressionType;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.domain.pattern.RegularExpression;
import org.talend.dataquality.factories.PatternIndicatorFactory;
import org.talend.dataquality.indicators.PatternMatchingIndicator;

/**
 * DOC qzhang class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z nrousseau $
 * 
 */
public class PatternUtilities {

    /**
     * DOC qzhang Comment method "isLibraiesSubfolder".
     * 
     * @param folder
     * @param subs
     * @return
     */
    public static boolean isLibraiesSubfolder(IFolder folder, String... subs) {
        for (String sub : subs) {
            IPath path = new Path(DQStructureManager.LIBRARIES);
            path = path.append(sub);
            IPath fullPath = folder.getFullPath();
            boolean prefixOf = path.isPrefixOf(fullPath);
            if (prefixOf) {
                return prefixOf;
            }
        }
        return false;
    }

    /**
     * DOC qzhang Comment method "createIndicatorUnit".
     * 
     * @param pfile
     * @param columnIndicator
     * @param analysis
     * @return
     */
    public static IndicatorUnit createIndicatorUnit(IFile pfile, ColumnIndicator columnIndicator, Analysis analysis) {
        Pattern pattern = PatternResourceFileHelper.getInstance().findPattern(pfile);
        RegularExpression expression = (RegularExpression) pattern.getComponents().get(0);
        PatternMatchingIndicator patternMatchingIndicator = PatternIndicatorFactory.createRegexpMatchingIndicator(pattern);
        if (ExpressionType.SQL_LIKE.getName().equals(expression.getExpressionType())) {
            patternMatchingIndicator = PatternIndicatorFactory.createSqlPatternMatchingIndicator(pattern);
        }
        IndicatorEnum type = IndicatorEnum.findIndicatorEnum(patternMatchingIndicator.eClass());
        IndicatorUnit addIndicatorUnit = columnIndicator.addSpecialIndicator(type, patternMatchingIndicator);
        DependenciesHandler.getInstance().setUsageDependencyOn(analysis, pattern);
        return addIndicatorUnit;
    }
}
