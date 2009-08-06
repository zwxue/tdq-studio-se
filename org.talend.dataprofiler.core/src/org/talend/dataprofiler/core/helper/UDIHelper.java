// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;
import org.talend.cwm.constants.DevelopmentStatus;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataprofiler.core.pattern.PatternUtilities;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dataprofiler.core.ui.utils.MessageUI;
import org.talend.dataprofiler.core.ui.utils.UDIFactory;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.domain.pattern.ExpressionType;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.helpers.DomainHelper;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.definition.DefinitionFactory;
import org.talend.dataquality.indicators.definition.IndicatorCategory;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.helper.resourcehelper.PatternResourceFileHelper;
import org.talend.dq.helper.resourcehelper.UDIResourceFileHelper;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;
import org.talend.resource.ResourceManager;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public final class UDIHelper {

    private static Logger log = Logger.getLogger(UDIHelper.class);

    public static IndicatorCategory getUDICategory(IndicatorDefinition indicatorDefinition) {
        EList<IndicatorCategory> categories = indicatorDefinition.getCategories();
        if (categories != null && categories.size() > 0) {
            return categories.get(0);
        }
        return null;
    }

    public static void setUDICategory(IndicatorDefinition indicatorDefinition, String categoryLabel) {
        if (indicatorDefinition != null) {
            IndicatorCategory indicatorCategory = DefinitionHandler.getInstance().getIndicatorCategoryByLabel(categoryLabel);
            indicatorCategory = indicatorCategory == null ? DefinitionHandler.getInstance()
                    .getUserDefinedCountIndicatorCategory() : indicatorCategory;
            if (!indicatorDefinition.getCategories().contains(indicatorCategory)) {
                indicatorDefinition.getCategories().add(indicatorCategory);
            }
        }
    }
    
    public static IndicatorDefinition createUDI(String name, String author, String description, String purpose, String status,
            String category) {
        IndicatorDefinition id = DefinitionFactory.eINSTANCE.createIndicatorDefinition();
        id.setName(name);
        MetadataHelper.setAuthor(id, author == null ? "" : author); //$NON-NLS-1$
        MetadataHelper.setDescription(description == null ? "" : description, id); //$NON-NLS-1$
        MetadataHelper.setPurpose(purpose == null ? "" : purpose, id); //$NON-NLS-1$
        MetadataHelper.setDevStatus(id, DevelopmentStatus.get(status == null ? "" : status)); //$NON-NLS-1$
        setUDICategory(id, category);
        return id;
    }

    public static IndicatorUnit[] createIndicatorUnit(IFile pfile, ColumnIndicator columnIndicator, Analysis analysis) {
        List<IndicatorUnit> addIndicatorUnits = new ArrayList<IndicatorUnit>();

        IndicatorDefinition udid = UDIResourceFileHelper.getInstance().findUDI(pfile);

        // can't add the same user defined indicator
        for (Indicator indicator : columnIndicator.getIndicators()) {
            if (udid.getName().equals(indicator.getName())) {
                MessageUI.openWarning("User Defined Indicator: " + udid.getName()
                        + "\n\nThis indicator is already selected for this column.");
                return null;
            }
        }

        Indicator udi = UDIFactory.createIndicator(udid);
        udi.setIndicatorDefinition(udid);

        IndicatorEnum indicatorType = IndicatorEnum.findIndicatorEnum(udi.eClass());
        // can't add the same category user defined indicator, expect user defined matching indicator
        if (!IndicatorEnum.RegexpMatchingIndicatorEnum.equals(indicatorType)) {
            IndicatorUnit[] indicatorUnits = columnIndicator.getIndicatorUnits();
            for (IndicatorUnit unit : indicatorUnits) {
                if (unit.getType().equals(indicatorType)) {
                    MessageUI.openWarning("User Defined Indicator: " + udid.getName()
                            + "\n\nThe indicator with same category of it is already selected for this column.");
                    return null;
                }
            }
        }

        // create user defined matching indicator
        if (IndicatorEnum.RegexpMatchingIndicatorEnum.equals(indicatorType)) {
            IFolder libProject = ResourceManager.getLibrariesFolder();
            CheckedTreeSelectionDialog dialog = PatternUtilities.createPatternCheckedTreeSelectionDialog(libProject);
            if (dialog.open() == Window.OK) {
                for (Object obj : dialog.getResult()) {
                    if (obj instanceof IFile) {
                        IFile file = (IFile) obj;
                        Pattern pattern = PatternResourceFileHelper.getInstance().findPattern(file);
                        String expressionType = DomainHelper.getExpressionType(pattern);
                        boolean isSQLPattern = (ExpressionType.SQL_LIKE.getLiteral().equals(expressionType));
                        if (isSQLPattern) {
                            MessageUI.openError("Pattern: " + pattern.getName()
                                    + "\n\nThis indicator is not regular expression matching.");
                        } else {
                            IndicatorUnit addIndicatorUnit = PatternUtilities.createIndicatorUnit(file, columnIndicator,
                                    analysis, udid);
                            if (addIndicatorUnit == null) {
                                MessageUI.openError("Pattern: " + pattern.getName()
                                        + "\n\nThis indicator is already selected for this column.");
                            } else {
                                addIndicatorUnits.add(addIndicatorUnit);
                            }
                        }
                    }
                }
            }
        } else {
            addIndicatorUnits.add(columnIndicator.addSpecialIndicator(indicatorType, udi));
        }

        DependenciesHandler.getInstance().setUsageDependencyOn(analysis, udid);
        return addIndicatorUnits.toArray(new IndicatorUnit[addIndicatorUnits.size()]);
    }

    public static Set<String> getAllIndicatorNames(IFolder folder) {
        Set<String> list = new HashSet<String>();
        return getNestFolderIndicatorNames(list, folder);
    }

    private static Set<String> getNestFolderIndicatorNames(Set<String> list, IFolder folder) {
        try {
            for (IResource resource : folder.members()) {
                if (resource instanceof IFile) {
                    IndicatorDefinition id = UDIResourceFileHelper.getInstance().findUDI((IFile) resource);
                    if (id != null) {
                        list.add(id.getName());
                    }
                } else {
                    getNestFolderIndicatorNames(list, (IFolder) resource);
                }
            }
        } catch (CoreException e) {
            log.error(e, e);
        }
        return list;
    }
}
