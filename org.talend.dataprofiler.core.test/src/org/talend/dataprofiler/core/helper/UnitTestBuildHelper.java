// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
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

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.support.membermodification.MemberMatcher.*;
import static org.powermock.api.support.membermodification.MemberModifier.*;

import java.util.ResourceBundle;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.jfree.util.Log;
import org.powermock.api.mockito.PowerMockito;
import org.talend.commons.bridge.ReponsitoryContextBridge;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.IRepositoryObject;
import org.talend.core.repository.i18n.Messages;
import org.talend.core.repository.model.IRepositoryFactory;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.model.RepositoryFactoryProvider;
import org.talend.cwm.relational.RelationalFactory;
import org.talend.cwm.relational.TdExpression;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisFactory;
import org.talend.dataquality.analysis.AnalysisResult;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.domain.pattern.PatternFactory;
import org.talend.dataquality.domain.pattern.RegularExpression;
import org.talend.dataquality.helpers.AnalysisHelper;
import org.talend.dataquality.helpers.ReportHelper;
import org.talend.dataquality.properties.TDQAnalysisItem;
import org.talend.dataquality.properties.TDQReportItem;
import org.talend.dataquality.properties.impl.PropertiesFactoryImpl;
import org.talend.repository.model.RepositoryNode;
import orgomg.cwmx.analysis.informationreporting.Report;

/**
 * created by yyin on 2012-8-22 Detailled comment: include some structure methods which can be used for any tests who
 * need related object. some are mocked object with the start "mock" on its method name; some are real created object
 * with the start "createReal" on its method name;
 * 
 */
public class UnitTestBuildHelper {

    private static final String REGEXP = "'su.*'"; //$NON-NLS-1$

    private static String[] filterExtensions = { "ana", "rep" };//$NON-NLS-1$//$NON-NLS-2$

    final private static String anaFolderName = "TDQ_Data Profiling/Analyses";//$NON-NLS-1$

    final private static String repFolderName = "TDQ_Data Profiling/Reports";//$NON-NLS-1$

    public static RepositoryNode mockRepositoryNode() {
        RepositoryNode node1 = mock(RepositoryNode.class);
        IRepositoryObject object = mock(IRepositoryObject.class);
        Property prop = mock(Property.class);
        Item item = mock(Item.class);
        ItemState state = mock(ItemState.class);
        when(prop.getItem()).thenReturn(item);
        when(node1.getObject()).thenReturn(object);
        when(object.getProperty()).thenReturn(prop);
        when(item.getState()).thenReturn(state);
        when(state.isDeleted()).thenReturn(false);
        return node1;
    }

    public static CorePlugin mockCorePlugin() {
        CorePlugin corePlugin = mock(CorePlugin.class);
        PowerMockito.mockStatic(CorePlugin.class);
        when(CorePlugin.getDefault()).thenReturn(corePlugin);
        return corePlugin;
    }

    public static ResourceBundle mockResourceBundle() {
        ResourceBundle rb = mock(ResourceBundle.class);
        stub(method(ResourceBundle.class, "getBundle", String.class)).toReturn(rb);
        return rb;
    }

    public static void mockMessages() {
        PowerMockito.mockStatic(Messages.class);
        when(Messages.getString(anyString())).thenReturn("a1");
        PowerMockito.mock(DefaultMessagesImpl.class);
        when(DefaultMessagesImpl.getString(anyString())).thenReturn("a2").thenReturn("a3").thenReturn("a4").thenReturn("a5");
    }

    public static Pattern createRealPattern() {
        Pattern pattern = PatternFactory.eINSTANCE.createPattern();
        pattern.setName("My Pattern"); //$NON-NLS-1$
        RegularExpression regularExpr = PatternFactory.eINSTANCE.createRegularExpression();
        TdExpression expression = createRealExpression();
        regularExpr.setExpression(expression);
        pattern.getComponents().add(regularExpr);
        return pattern;
    }

    public static TdExpression createRealExpression() {
        TdExpression expression = RelationalFactory.eINSTANCE.createTdExpression();
        expression.setBody(REGEXP);
        expression.setLanguage("SQL"); //$NON-NLS-1$
        return expression;
    }

    /**
     * 
     * DOC zshen Comment method "initFolder". init the folder which contain ana and rep files
     * 
     * @param folderName
     * @return
     */
    public static IFolder initFolder(String folderName) {
        IRepositoryFactory repository = RepositoryFactoryProvider.getRepositoriyById("local");//$NON-NLS-1$
        ProxyRepositoryFactory proRepInstance = ProxyRepositoryFactory.getInstance();
        if (proRepInstance.getRepositoryFactoryFromProvider() == null) {
            ProxyRepositoryFactory.getInstance().setRepositoryFactoryFromProvider(repository);
        }
        IProject project = ReponsitoryContextBridge.getRootProject();
        if (!project.exists()) {
            try {
                project.create(null);
            } catch (CoreException e) {
                Log.error(e, e);
                e.printStackTrace();
            }
        }
        CorePlugin.getDefault().initProxyRepository();
        if (DQStructureManager.getInstance().isNeedCreateStructure()) {
            DQStructureManager.getInstance().createDQStructure();
        }
        IFolder aa = project.getFolder(folderName);
        if (aa.exists()) {
        } else {
            try {
                aa.create(true, true, null);
            } catch (CoreException e) {
                Log.error(e, e);
                e.printStackTrace();
            }
        }
        // for analyses
        if (anaFolderName.equals(folderName)) {
            IFolder subfolder1 = createRealFolder(aa, "subfolder1");//$NON-NLS-1$
            IFolder subfolder2 = createRealFolder(aa, "subfolder2");//$NON-NLS-1$
            // logic delete one
            createRealAnalysis("a4", null, true);//$NON-NLS-1$
            // ~logic delete one
            createRealAnalysis("a1", null, false);//$NON-NLS-1$
            createRealAnalysis("a3", subfolder2, false);//$NON-NLS-1$
            createRealAnalysis("a2", subfolder1, false);//$NON-NLS-1$

        }
        // for reports
        if (repFolderName.equals(folderName)) {
            IFolder subfolder1 = createRealFolder(aa, "subfolder1");//$NON-NLS-1$
            IFolder subfolder2 = createRealFolder(aa, "subfolder2");//$NON-NLS-1$
            // logic delete one
            createRealReport("a4", null, true);//$NON-NLS-1$
            // ~logic delete one
            createRealReport("a1", null, false);//$NON-NLS-1$
            createRealReport("a3", subfolder2, false);//$NON-NLS-1$
            createRealReport("a2", subfolder1, false);//$NON-NLS-1$
        }
        ;
        return aa;
    }

    public static Report createRealReport(String name, IFolder folder, Boolean isDelete) {
        IPath createPath = Path.EMPTY;
        if (folder != null) {
            createPath = new Path(folder.getFullPath().lastSegment());
        }
        Report report1 = ReportHelper.createReport(name);
        TDQReportItem item1 = PropertiesFactoryImpl.eINSTANCE.createTDQReportItem();
        org.talend.core.model.properties.Property property1 = PropertiesFactory.eINSTANCE.createProperty();
        property1.setId(EcoreUtil.generateUUID());
        property1.setItem(item1);
        property1.setLabel(report1.getName());
        item1.setProperty(property1);
        item1.setReport(report1);
        ItemState itemState = org.talend.core.model.properties.PropertiesFactory.eINSTANCE.createItemState();
        itemState.setDeleted(isDelete);
        item1.setState(itemState);

        try {
            ProxyRepositoryFactory.getInstance().create(item1, createPath, false);
        } catch (PersistenceException e) {
            Log.error(e, e);
            e.printStackTrace();
        }
        return report1;
    }

    public static Analysis createRealAnalysis(String name, IFolder folder, Boolean isDelete) {
        IPath createPath = Path.EMPTY;
        if (folder != null) {
            createPath = new Path(folder.getFullPath().lastSegment());
        }
        Analysis analysis1 = AnalysisHelper.createAnalysis(name);
        TDQAnalysisItem item1 = PropertiesFactoryImpl.eINSTANCE.createTDQAnalysisItem();
        org.talend.core.model.properties.Property property1 = PropertiesFactory.eINSTANCE.createProperty();
        property1.setId(EcoreUtil.generateUUID());
        property1.setItem(item1);
        property1.setLabel(analysis1.getName());
        item1.setProperty(property1);
        item1.setAnalysis(analysis1);
        ItemState itemState = org.talend.core.model.properties.PropertiesFactory.eINSTANCE.createItemState();
        itemState.setDeleted(isDelete);
        item1.setState(itemState);
        AnalysisResult analysisResult1 = AnalysisFactory.eINSTANCE.createAnalysisResult();
        analysis1.setResults(analysisResult1);
        try {
            ProxyRepositoryFactory.getInstance().create(item1, createPath, false);
        } catch (PersistenceException e) {
            Log.error(e, e);
            e.printStackTrace();
        }
        return analysis1;
    }

    /**
     * 
     * DOC zshen Comment method "createFolder". create the subfolder under the parentFolder and named for folderName
     * 
     * @param parentFolder
     * @param folderName
     * @return
     */
    public static IFolder createRealFolder(IFolder parentFolder, String folderName) {
        IFolder currFolder = parentFolder.getFolder(folderName);
        if (!currFolder.exists()) {
            try {
                currFolder.create(true, true, null);
            } catch (CoreException e) {
                Log.error(e, e);
                e.printStackTrace();
            }
        }
        return currFolder;
    }
}
