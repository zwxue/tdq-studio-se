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
package org.talend.dq.analysis;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.talend.cwm.db.connection.MdmConnection;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.ResourceHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.management.i18n.Messages;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.cwm.softwaredeployment.TdProviderConnection;
import org.talend.cwm.xml.TdXMLElement;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisContext;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.foundation.softwaredeployment.ProviderConnection;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC xqliu class global comment. TODO 10238
 */
public class MdmAnalysisExecutor extends AnalysisExecutor {

    protected static final String SLASH = "/";

    protected static final String DOUBLE_SLASH = "//";

    private TdDataProvider dataprovider;

    private static Logger log = Logger.getLogger(MdmAnalysisExecutor.class);

    protected boolean isAccessWith(TdDataProvider dp) {
        if (dataprovider == null) {
            dataprovider = dp;
            return true;
        }
        // else compare
        if (ResourceHelper.areSame(dataprovider, dp)) {
            return true;
        }
        // else
        return false;
    }

    protected boolean runAnalysis(Analysis analysis, String sqlStatement) {
        return true;
    }

    /**
     * DOC xqliu Comment method "belongToSameDocument".
     * 
     * @param xmlElemet
     * @return
     */
    protected boolean belongToSameDocument(final TdXMLElement xmlElemet) {
        return true;
    }

    @Override
    protected String createSqlStatement(Analysis analysis) {
        // TODO 10238
        return "";
    }

    @Override
    protected boolean check(final Analysis analysis) {
        if (analysis == null) {
            this.errorMessage = Messages.getString("ColumnAnalysisExecutor.AnalysisIsNull"); //$NON-NLS-1$
            return false;
        }
        if (!super.check(analysis)) {
            // error message already set in super method.
            return false;
        }

        // --- check existence of context
        AnalysisContext context = analysis.getContext();
        if (context == null) {
            this.errorMessage = Messages.getString("ColumnAnalysisExecutor.NoContextSet", analysis.getName()); //$NON-NLS-1$
            return false;
        }

        // --- check that there exists at least on element to analyze
        if (context.getAnalysedElements().size() == 0) {
            this.errorMessage = Messages.getString("ColumnAnalysisExecutor.AnalysisHaveAtLeastOneColumn"); //$NON-NLS-1$
            return false;
        }

        // --- check that the connection has been set
        if (context.getConnection() == null) {
            this.errorMessage = Messages.getString("ColumnAnalysisExecutor.NoConnectionSet"); //$NON-NLS-1$
            return false;
        }

        return checkAnalyzedElements(analysis, context);
    }

    /**
     * DOC xqliu Comment method "checkAnalyzedElements".
     * 
     * @param analysis
     * @param context
     * @return
     */
    protected boolean checkAnalyzedElements(final Analysis analysis, AnalysisContext context) {
        ModelElementAnalysisHandler analysisHandler = new ModelElementAnalysisHandler();
        analysisHandler.setAnalysis(analysis);

        for (ModelElement node : context.getAnalysedElements()) {
            TdXMLElement xmlElement = SwitchHelpers.XMLELEMENT_SWITCH.doSwitch(node);

            // --- Check that each analyzed element has at least one indicator
            if (analysisHandler.getIndicators(xmlElement).size() == 0) {
                this.errorMessage = Messages.getString("ColumnAnalysisExecutor.EachColumnHaveOneIndicator"); //$NON-NLS-1$
                return false;
            }

            // --- get the data provider
            TdDataProvider dp = DataProviderHelper.getTdDataProvider(xmlElement);
            if (!isAccessWith(dp)) {
                this.errorMessage = Messages.getString("ColumnAnalysisExecutor.AllColumnsBelongSameConnection", //$NON-NLS-1$
                        xmlElement.getName(), dataprovider.getName());
                return false;
            }
        }
        return true;
    }

    /**
     * DOC xqliu Comment method "getMdmConnection".
     * 
     * @param analysis
     * @return
     */
    protected TypedReturnCode<MdmConnection> getMdmConnection(Analysis analysis) {
        TypedReturnCode<MdmConnection> rc = new TypedReturnCode<MdmConnection>(false);
        TdDataProvider dataProvider = (TdDataProvider) analysis.getContext().getConnection();
        EList<ProviderConnection> resourceConnections = dataProvider.getResourceConnection();
        if (resourceConnections != null && resourceConnections.size() > 0) {
            TdProviderConnection providerConnection = (TdProviderConnection) resourceConnections.get(0);
            String url = providerConnection.getConnectionString();
            Properties props = new Properties();
            props.setProperty(TaggedValueHelper.USER, DataProviderHelper.getUser(providerConnection));
            props.setProperty(TaggedValueHelper.PASSWORD, DataProviderHelper.getClearTextPassword(providerConnection));
            props.setProperty(TaggedValueHelper.UNIVERSE, DataProviderHelper.getUniverse(providerConnection));
            MdmConnection mdmConnection = new MdmConnection(url, props);
            rc.setObject(mdmConnection);
            rc.setOk(mdmConnection.checkDatabaseConnection().isOk());
            rc.setMessage(url);
        }
        return rc;
    }
}
