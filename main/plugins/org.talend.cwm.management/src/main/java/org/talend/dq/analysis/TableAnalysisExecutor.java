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
package org.talend.dq.analysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.ResourceHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.management.i18n.Messages;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dq.indicators.IndicatorEvaluator;
import org.talend.dq.sql.converters.CwmZQuery;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.ColumnSet;
import orgomg.cwm.resource.relational.NamedColumnSet;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class TableAnalysisExecutor extends AnalysisExecutor {

    private Connection dataprovider;

    private static Logger log = Logger.getLogger(TableAnalysisExecutor.class);

    protected Map<ModelElement, Package> schemata = new HashMap<ModelElement, Package>();

    protected boolean isAccessWith(Connection dp) {
        if (dataprovider == null) {
            dataprovider = dp;
            return true;
        }
        // else compare
        return ResourceHelper.areSame(dataprovider, dp);
    }

    @Override
    protected String createSqlStatement(Analysis analysis) {
        this.cachedAnalysis = analysis;
        CwmZQuery query = new CwmZQuery();
        EList<ModelElement> analysedElements = analysis.getContext().getAnalysedElements();
        if (analysedElements.isEmpty()) {
            setError(Messages.getString("ColumnAnalysisExecutor.CannotCreateSQLStatement",//$NON-NLS-1$
                    analysis.getName()));
            return null;
        }
        Set<ColumnSet> fromPart = new HashSet<ColumnSet>();
        // Set<ColumnSet> wherePart = new HashSet<ColumnSet>();
        for (ModelElement modelElement : analysedElements) {
            NamedColumnSet set = SwitchHelpers.NAMED_COLUMN_SET_SWITCH.doSwitch(modelElement);
            if (set == null) {
                setError(Messages.getString("TableAnalysisExecutor.NoContainerFound", modelElement.getName()));//$NON-NLS-1$
                return null;
            }
            // else add into select
            if (!query.addSelect(set)) {
                setError(Messages.getString("TableAnalysisExecutor.Problem"));//$NON-NLS-1$
                return null;
            }
            // add from
            fromPart.add(set);
            // TODO add where part
        }

        if (!query.addFrom(fromPart)) {
            setError(Messages.getString("TableAnalysisExecutor.ProblemAddFromPart"));//$NON-NLS-1$
            return null;
        }
        return query.generateStatement();
    }

    @Override
    protected ReturnCode evaluate(Analysis analysis, java.sql.Connection connection, String sqlStatement) {
        IndicatorEvaluator eval = new IndicatorEvaluator(analysis);
        eval.setMonitor(getMonitor());
        // --- add indicators
        EList<Indicator> indicators = analysis.getResults().getIndicators();
        for (Indicator indicator : indicators) {
            assert indicator != null;
            NamedColumnSet set = SwitchHelpers.NAMED_COLUMN_SET_SWITCH.doSwitch(indicator.getAnalyzedElement());
            if (set == null) {
                continue;
            }
            // --- get the schema owner
            if (!belongToSameSchemata(set)) {
                setError(Messages.getString("TableAnalysisExecutor.GivenTable", set.getName()));//$NON-NLS-1$
                return new ReturnCode(getErrorMessage(), Boolean.FALSE);
            }
            String setName = dbms().getQueryColumnSetWithPrefix(set);

            eval.storeIndicator(setName, indicator);
        }
        // set it into the evaluator
        eval.setConnection(connection);
        // use pooled connection
        eval.setPooledConnection(POOLED_CONNECTION);

        // when to close connection
        boolean closeAtTheEnd = true;
        Package catalog = schemata.values().iterator().next();
        if (!eval.selectCatalog(catalog.getName())) {
            log.warn("Failed to select catalog " + catalog.getName() + " for connection.");//$NON-NLS-1$//$NON-NLS-2$
        }
        return eval.evaluateIndicators(sqlStatement, closeAtTheEnd);
    }

    protected boolean belongToSameSchemata(final NamedColumnSet set) {
        assert set != null;
        if (schemata.get(set) != null) {
            return true;
        }
        // get catalog or schema
        Package schema = ColumnSetHelper.getParentCatalogOrSchema(set);
        if (schema == null) {
            setError(Messages.getString("TableAnalysisExecutor.NoSchemaOrCatalogFound", set.getName())); //$NON-NLS-1$
            return false;
        }
        schemata.put(set, schema);
        return true;
    }
}
