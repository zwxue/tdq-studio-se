// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.views.provider;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ResourceBundle;

import org.apache.poi.hpsf.Property;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.jfree.util.Log;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.talend.commons.bridge.ReponsitoryContextBridge;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.metadata.MetadataFillFactory;
import org.talend.core.model.metadata.builder.database.ExtractMetaDataUtils;
import org.talend.core.model.metadata.builder.util.MetadataConnectionUtils;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.repository.model.IRepositoryFactory;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.model.RepositoryFactoryProvider;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.PackageHelper;
import org.talend.cwm.helper.SchemaHelper;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.ui.dialog.message.DeleteModelElementConfirmDialog;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisFactory;
import org.talend.dataquality.analysis.AnalysisResult;
import org.talend.dataquality.helpers.AnalysisHelper;
import org.talend.dataquality.helpers.ReportHelper;
import org.talend.dataquality.properties.TDQAnalysisItem;
import org.talend.dataquality.properties.TDQReportItem;
import org.talend.dataquality.properties.impl.PropertiesFactoryImpl;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.dq.writer.impl.AnalysisWriter;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.resource.EResourceConstant;
import org.talend.resource.ResourceManager;
import org.talend.utils.sql.ConnectionUtils;
import orgomg.cwmx.analysis.informationreporting.Report;

import com.sun.mail.imap.protocol.Item;

/**
 * DOC zshen class global comment. Test the method
 * org.talend.dataprofiler.core.ui.views.provider.ResourceViewLabelProvider#getFileCount
 */

public class ResourceViewLabelProviderTest {

    private static String[] filterExtensions = { "ana", "rep" };//$NON-NLS-1$//$NON-NLS-2$

    final private String anaFolderName = "TDQ_Data Profiling/Analyses";//$NON-NLS-1$

    final private String repFolderName = "TDQ_Data Profiling/Reports";//$NON-NLS-1$

    /**
     * Test method for
     * {@link org.talend.dataprofiler.core.ui.views.provider.ResourceViewLabelProvider#getFileCount(org.eclipse.core.resources.IFolder, java.lang.String[])}
     * 
     */
    @Test
    public void testGetFileCount() {
    	
        IFolder anaFolder = initFolder(EResourceConstant.ANALYSIS.getPath());
        IFolder repFolder = initFolder(EResourceConstant.REPORTS.getPath());
        ResourceViewLabelProvider reViewLabelProvider = new ResourceViewLabelProvider();
        int AnalysisNum = reViewLabelProvider.getFileCount(anaFolder, filterExtensions);
        int reportNum = reViewLabelProvider.getFileCount(repFolder, filterExtensions);
        assertEquals(AnalysisNum, 3);
        assertEquals(reportNum, 3);
    }

    private void initElements() {
		AnaResourceFileHelper.getAll();
		
	}

	/**
     * 
     * DOC zshen Comment method "initFolder". init the folder which contain ana and rep files
     * 
     * @param folderName
     * @return
     */
    public IFolder initFolder(String folderName) {
    	IRepositoryFactory repository = RepositoryFactoryProvider.getRepositoriyById("local");//$NON-NLS-1$
    	ProxyRepositoryFactory proRepInstance = ProxyRepositoryFactory.getInstance();
    	if(proRepInstance.getRepositoryFactoryFromProvider()==null){
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
        if(DQStructureManager.getInstance().isNeedCreateStructure()){
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
        	IFolder subfolder1 = createFolder(aa, "subfolder1");//$NON-NLS-1$
            IFolder subfolder2 = createFolder(aa, "subfolder2");//$NON-NLS-1$
          //logic delete one
            createAnalysis("a4",null,true);//$NON-NLS-1$
          //~logic delete one
            createAnalysis("a1",null,false);//$NON-NLS-1$
            createAnalysis("a3",subfolder2,false);//$NON-NLS-1$
            createAnalysis("a2",subfolder1,false);//$NON-NLS-1$

        }
        // for reports
        if (repFolderName.equals(folderName)) {
            IFolder subfolder1 = createFolder(aa, "subfolder1");//$NON-NLS-1$
            IFolder subfolder2 = createFolder(aa, "subfolder2");//$NON-NLS-1$
          //logic delete one
            createReport("a4",null,true);//$NON-NLS-1$
          //~logic delete one
            createReport("a1",null,false);//$NON-NLS-1$
            createReport("a3",subfolder2,false);//$NON-NLS-1$
            createReport("a2",subfolder1,false);//$NON-NLS-1$
        };
        return aa;
    }

    private void createReport(String name, IFolder folder,Boolean isDelete) {
    	IPath createPath=Path.EMPTY;
    	if(folder!=null){
    		createPath=new Path(folder.getFullPath().lastSegment());
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
			ProxyRepositoryFactory.getInstance().create(item1,createPath , false);
		} catch (PersistenceException e) {
Log.error(e, e);
			e.printStackTrace();
		}
		
	}

	private void createAnalysis(String name, IFolder folder,Boolean isDelete) {
		IPath createPath=Path.EMPTY;
    	if(folder!=null){
    		createPath=new Path(folder.getFullPath().lastSegment());
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
	}

	/**
     * 
     * DOC zshen Comment method "createFolder". create the subfolder under the parentFolder and named for folderName
     * 
     * @param parentFolder
     * @param folderName
     * @return
     */
    public IFolder createFolder(IFolder parentFolder, String folderName) {
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

    /**
     * 
     * DOC zshen Comment method "createFile". create the file under the parentFolder and named for filName
     * 
     * @param parentFolder
     * @param filName
     * @return
     */
    public IFile createFile(IFolder parentFolder, String filName) {
        IFile file = parentFolder.getFile(filName);
        IPath parentPath = parentFolder.getFullPath().removeFirstSegments(1);
        if (!file.exists()) {
            try {
            	InputStream resourceAsStream = ResourceViewLabelProviderTest.class.getClassLoader().getResourceAsStream("/temp/"+parentPath.toOSString()+"/"+filName);//$NON-NLS-1$//$NON-NLS-2$
                file.create(resourceAsStream, true, null);
            } catch (CoreException e) {
               Log.error(e, e);
                e.printStackTrace();
            }
        }
        return file;
    }
}
