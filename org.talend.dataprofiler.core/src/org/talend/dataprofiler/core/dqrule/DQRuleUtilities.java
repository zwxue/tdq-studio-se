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
package org.talend.dataprofiler.core.dqrule;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.dataprofiler.core.model.TableIndicator;
import org.talend.dataprofiler.core.ui.editor.preview.TableIndicatorUnit;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.sql.SqlFactory;
import org.talend.dataquality.indicators.sql.WhereRuleIndicator;
import org.talend.dataquality.rules.WhereRule;
import org.talend.dq.helper.resourcehelper.DQRuleResourceFileHelper;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;
import org.talend.resource.ResourceManager;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public final class DQRuleUtilities {

	private static Logger log = Logger.getLogger(DQRuleUtilities.class);

	private DQRuleUtilities() {
	}

	/**
	 * DOC xqliu Comment method "createIndicatorUnit".
	 * 
	 * @param fe
	 * @param tableIndicator
	 * @param analysis
	 * @return
	 */
	public static TableIndicatorUnit createIndicatorUnit(IFile fe,
			TableIndicator tableIndicator, Analysis analysis) {
		WhereRule whereRule = DQRuleResourceFileHelper.getInstance()
				.findWhereRule(fe);

		for (Indicator indicator : tableIndicator.getIndicators()) {
			if (whereRule.getName().equals(indicator.getName())) {
				return null;
			}
		}

		WhereRuleIndicator wrIndicator = SqlFactory.eINSTANCE
				.createWhereRuleIndicator();
		wrIndicator.setAnalyzedElement(tableIndicator.getTdTable());
		wrIndicator.setIndicatorDefinition(whereRule);

		IndicatorEnum type = IndicatorEnum.findIndicatorEnum(wrIndicator
				.eClass());
		TableIndicatorUnit addIndicatorUnit = tableIndicator
				.addSpecialIndicator(fe, type, wrIndicator);
		DependenciesHandler.getInstance().setUsageDependencyOn(analysis,
				whereRule);
		return addIndicatorUnit;
	}

	/**
	 * 
	 * DOC xqliu Comment method "isLibraiesSubfolder".
	 * 
	 * @param folder
	 * @param subs
	 * @return
	 */
	public static boolean isLibraiesSubfolder(IFolder folder, String... subs) {
		for (String sub : subs) {
			IPath path = ResourceManager.getLibrariesFolder().getFullPath();
			path = path.append(sub);
			IPath fullPath = folder.getFullPath();
			boolean prefixOf = path.isPrefixOf(fullPath);
			if (prefixOf) {
				return prefixOf;
			}
		}
		return false;
	}
}
