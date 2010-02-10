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
package org.talend.dataprofiler.core.ui.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.definition.IndicatorCategory;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.indicators.sql.JavaUserDefIndicator;
import org.talend.dq.helper.UDIHelper;
import org.talend.dq.helper.resourcehelper.UDIResourceFileHelper;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public final class UDIUtils {

    private UDIUtils() {
    }

    public static IndicatorCategory getUDICategory(IndicatorUnit unit) {
        return UDIHelper.getUDICategory(unit.getIndicator().getIndicatorDefinition());
    }

    public static String getUDICategoryLabel(IndicatorUnit unit) {
        IndicatorCategory ic = getUDICategory(unit);
        if (ic != null) {
            return ic.getLabel();
        }
        return null;
    }

    public static IndicatorUnit[] createIndicatorUnit(IFile pfile, ColumnIndicator columnIndicator, Analysis analysis)
            throws Throwable {
        List<IndicatorUnit> addIndicatorUnits = new ArrayList<IndicatorUnit>();

        IndicatorDefinition udid = UDIResourceFileHelper.getInstance().findUDI(pfile);
        IndicatorCategory ic = UDIHelper.getUDICategory(udid);

        // can't add the same user defined indicator
        for (Indicator indicator : columnIndicator.getIndicators()) {
            if (udid.getName().equals(indicator.getName())) {
                MessageUI.openWarning(DefaultMessagesImpl.getString("UDIUtils.UDISelected") //$NON-NLS-1$
                        + udid.getName());
                return null;
            }
        }

        Indicator udi = UDIFactory.createUserDefIndicator(udid);
        udi.setIndicatorDefinition(udid);
        // MOD mzhao feature 11128, Handle Java User Defined Indicator.
        Indicator judi = UDIHelper.adaptToJavaUDI(udi);
        if (judi != null) {
            ((JavaUserDefIndicator) judi).setExecuteEngine(analysis.getParameters().getExecutionLanguage());
            udi = judi;
        }
        IndicatorEnum indicatorType = IndicatorEnum.findIndicatorEnum(udi.eClass());

        // MOD xqliu 2009-10-09 bug 9304
        // create user defined matching indicator
        // if (DefinitionHandler.getInstance().getUserDefinedMatchIndicatorCategory().equals(ic)) {
        // IFolder libProject = ResourceManager.getLibrariesFolder();
        // CheckedTreeSelectionDialog dialog = PatternUtilities.createPatternCheckedTreeSelectionDialog(libProject);
        // if (dialog.open() == Window.OK) {
        // for (Object obj : dialog.getResult()) {
        // if (obj instanceof IFile) {
        // IFile file = (IFile) obj;
        // IndicatorUnit addIndicatorUnit = PatternUtilities.createIndicatorUnit(file, columnIndicator, analysis,
        // udid);
        // if (addIndicatorUnit == null) {
        // Pattern pattern = PatternResourceFileHelper.getInstance().findPattern(file);
        //                            MessageUI.openError(DefaultMessagesImpl.getString("UDIUtils.PatternSelected") //$NON-NLS-1$
        // + pattern.getName());
        // } else {
        // addIndicatorUnits.add(addIndicatorUnit);
        // }
        // }
        // }
        // }
        // } else {
        addIndicatorUnits.add(columnIndicator.addSpecialIndicator(indicatorType, udi));
        // }
        // ~

        DependenciesHandler.getInstance().setUsageDependencyOn(analysis, udid);
        return addIndicatorUnits.toArray(new IndicatorUnit[addIndicatorUnits.size()]);
    }
}
