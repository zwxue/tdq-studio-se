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

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.dataprofiler.core.helper.resourcehelper.PatternResourceFileHelper;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataprofiler.core.model.nodes.indicator.tpye.IndicatorEnum;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.domain.pattern.ExpressionType;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.domain.pattern.PatternComponent;
import org.talend.dataquality.domain.pattern.RegularExpression;
import org.talend.dataquality.domain.pattern.impl.RegularExpressionImpl;
import org.talend.dataquality.factories.PatternIndicatorFactory;
import org.talend.dataquality.indicators.PatternMatchingIndicator;
import org.talend.dq.indicators.definitions.DefinitionHandler;

/**
 * DOC qzhang class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z nrousseau $
 * 
 */
public final class PatternUtilities {

    private static Logger log = Logger.getLogger(PatternUtilities.class);

    private PatternUtilities() {
    }

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
     * DOC qzhang Comment method "isPatternValid".
     * 
     * @param pattern
     * @return
     */
    public static boolean isPatternValid(Pattern pattern) {
        boolean valid = true;
        EList<PatternComponent> components = pattern.getComponents();
        for (int i = 0; i < components.size(); i++) {
            RegularExpressionImpl regularExpress = (RegularExpressionImpl) components.get(i);
            String body = regularExpress.getExpression().getBody();
            valid = ((body != null) && (body.matches("^.*'")));
            if (!valid) {
                break;
            } else {
                // if (body.charAt(0) == '^') {
                // body = "'" + body.substring(1);
                // } else {
                body = "'" + body;
                // }
                regularExpress.getExpression().setBody(body);
            }
        }
        return valid;
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
        PatternMatchingIndicator patternMatchingIndicator = (ExpressionType.SQL_LIKE.getName().equals(expression
                .getExpressionType())) ? PatternIndicatorFactory.createSqlPatternMatchingIndicator(pattern)
                : PatternIndicatorFactory.createRegexpMatchingIndicator(pattern);

        // MOD scorreia 2008-09-18: bug 5131 fixed: set indicator's definition when the indicator is created.
        if (!DefinitionHandler.getInstance().setDefaultIndicatorDefinition(patternMatchingIndicator)) {
            log.error("Could not set the definition of the given indicator : " + patternMatchingIndicator.getName());
        }
        IndicatorEnum type = IndicatorEnum.findIndicatorEnum(patternMatchingIndicator.eClass());
        IndicatorUnit addIndicatorUnit = columnIndicator.addSpecialIndicator(type, patternMatchingIndicator);
        DependenciesHandler.getInstance().setUsageDependencyOn(analysis, pattern);
        return addIndicatorUnit;
    }
}
