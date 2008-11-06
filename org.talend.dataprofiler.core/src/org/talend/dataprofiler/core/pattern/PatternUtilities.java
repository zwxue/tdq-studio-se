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

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.domain.pattern.ExpressionType;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.domain.pattern.PatternComponent;
import org.talend.dataquality.domain.pattern.RegularExpression;
import org.talend.dataquality.domain.pattern.impl.RegularExpressionImpl;
import org.talend.dataquality.factories.PatternIndicatorFactory;
import org.talend.dataquality.indicators.PatternMatchingIndicator;
import org.talend.dq.helper.resourcehelper.PatternResourceFileHelper;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;

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
            valid = ((body != null) && body.matches("'.*'")); //$NON-NLS-1$
            if (!valid) {
                break;
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
            log.error(DefaultMessagesImpl.getString("PatternUtilities.couldnotSetDef") + patternMatchingIndicator.getName()); //$NON-NLS-1$
        }
        IndicatorEnum type = IndicatorEnum.findIndicatorEnum(patternMatchingIndicator.eClass());
        IndicatorUnit addIndicatorUnit = columnIndicator.addSpecialIndicator(type, patternMatchingIndicator);
        DependenciesHandler.getInstance().setUsageDependencyOn(analysis, pattern);
        return addIndicatorUnit;
    }

    public static Set<String> getAllPatternNames(IFolder folder) {

        Set<String> list = new HashSet<String>();
        return getNestFolderPatternNames(list, folder);
    }

    /**
     * DOC zqin Comment method "getNestFolderPatternNames".
     * 
     * @param folder
     * @return
     */
    private static Set<String> getNestFolderPatternNames(Set<String> list, IFolder folder) {
        try {
            for (IResource resource : folder.members()) {
                if (resource instanceof IFile) {
                    Pattern fr = PatternResourceFileHelper.getInstance().findPattern((IFile) resource);
                    if (fr != null) {
                        list.add(fr.getName());
                    }
                } else {
                    getNestFolderPatternNames(list, (IFolder) resource);
                }
            }
        } catch (CoreException e) {
            e.printStackTrace();
        }
        return list;
    }
}
