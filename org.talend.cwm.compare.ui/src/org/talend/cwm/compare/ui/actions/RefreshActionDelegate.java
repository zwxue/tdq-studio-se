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
package org.talend.cwm.compare.ui.actions;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.diff.metamodel.DiffElement;
import org.eclipse.emf.compare.diff.metamodel.DiffFactory;
import org.eclipse.emf.compare.diff.metamodel.DiffModel;
import org.eclipse.emf.compare.diff.metamodel.ModelInputSnapshot;
import org.eclipse.emf.compare.diff.service.DiffService;
import org.eclipse.emf.compare.match.api.MatchOptions;
import org.eclipse.emf.compare.match.metamodel.MatchModel;
import org.eclipse.emf.compare.match.metamodel.UnMatchElement;
import org.eclipse.emf.compare.match.service.MatchService;
import org.eclipse.emf.compare.util.ModelUtils;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.talend.commons.emf.EMFUtil;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.SchemaHelper;
import org.talend.cwm.management.api.ConnectionService;
import org.talend.cwm.management.api.FolderProvider;
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.relational.TdSchema;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.relational.TdView;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.cwm.softwaredeployment.TdProviderConnection;
import org.talend.dataquality.PluginConstant;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dq.analysis.parameters.DBConnectionParameter;
import org.talend.dq.writer.EMFSharedResources;
import org.talend.resource.ResourceManager;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.foundation.softwaredeployment.ProviderConnection;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.TaggedValue;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class RefreshActionDelegate implements IObjectActionDelegate {

    protected static Logger log = Logger.getLogger(RefreshActionDelegate.class);

    protected static final EMFUtil EMFUTIL = EMFSharedResources.getSharedEmfUtil();

    protected IWorkbenchPart workbenchpart;

    protected Object selectedObject;

    protected IFile selectedFileObject;

    protected TdCatalog selectedCatalog;

    protected TdSchema selectedSchema;

    protected TdDataProvider selectedDataProvider;

    private static final boolean CAT_WITH_PRV = true;

    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        this.workbenchpart = targetPart;
    }

    public void run(IAction action) {
        // DQStructureComparer.copyCurrentResourceFile(m_SelectedFileObject);

        if (selectedObject instanceof TdDataProvider) {
            // TODO:
        } else if (selectedObject instanceof TdSchema) {
            // TODO:
        } else if (selectedObject instanceof TdTable) {
            // TODO:
        } else if (selectedObject instanceof TdView) {
            // TODO:
        }

        synchronize();
        // DQStructureComparer.deleteCopiedResourceFile();
    }

    @SuppressWarnings("restriction")
    private void synchronizeDirect() {
        EMFUtil util = EMFSharedResources.getSharedEmfUtil();
        ResourceSet resourceSet = util.getResourceSet();
        URI uri = URI.createPlatformResourceURI(selectedFileObject.getFullPath().toString(), false);
        final Resource resource = resourceSet.createResource(uri);
        boolean ok;

        Collection<? extends ModelElement> schemata = DataProviderHelper.getTdSchema(selectedDataProvider);
        if (CAT_WITH_PRV) {
            ok = resource.getContents().addAll(schemata);
        } else {
            // ok = addElementsToOwnResources(schemata,
            // folderProvider.getFolderResource(), util);
            // util.save();
        }
    }

    private void synchronize() {
        // Load Model from Prv File
        // Creates the resourceSet where we'll load the models
        final ResourceSet resourceSet = new ResourceSetImpl();
        try {

            final EObject alreadySavedModel = ModelUtils.load(selectedFileObject.toString(), resourceSet);
            TypedReturnCode<TdDataProvider> rc = null;

            EList<ProviderConnection> connections = ((TdDataProvider) alreadySavedModel).getResourceConnection();
            ListIterator<ProviderConnection> li = connections.listIterator();
            if (li.hasNext()) {
                TdProviderConnection pc = (TdProviderConnection) li.next();
                String dbUrl = pc.getConnectionString();
                String driverClassName = pc.getDriverClassName();
                EList<EObject> pcObjects = pc.eContents();
                ListIterator<EObject> liEObject = pcObjects.listIterator();

                Properties parameters = new Properties();

                while (liEObject.hasNext()) {
                    EObject eo = liEObject.next();
                    if (eo instanceof TaggedValue) {
                        String tag = ((TaggedValue) eo).getTag();
                        System.out.print(tag);
                        String value = ((TaggedValue) eo).getValue();
                        System.out.println(" " + value); //$NON-NLS-1$
                        parameters.put(tag, value);
                    }

                }

                System.out.println(dbUrl);
                System.out.println(driverClassName);
                DBConnectionParameter dbcp = new DBConnectionParameter();

                dbcp.setName(pc.getName());
                dbcp.setAuthor(MetadataHelper.getAuthor(pc));
                dbcp.setDescription(MetadataHelper.getDescription(pc));
                dbcp.setPurpose(MetadataHelper.getPurpose(pc));
                dbcp.setStatus(MetadataHelper.getDevStatus(pc).getLiteral());

                dbcp.setDriverClassName(driverClassName);
                dbcp.setJdbcUrl(dbUrl);
                dbcp.setParameters(parameters);
                rc = ConnectionService.createConnection(dbcp);
            }
            System.out.println(alreadySavedModel.toString());
            IFolder folder = ResourceManager.getMetadataFolder().getFolder(PluginConstant.DB_CONNECTIONS);
            FolderProvider fp = new FolderProvider();
            fp.setFolderResource(folder);
            IFile file2 = loadDataProviderAndStructureInMemory(rc.getObject(), fp);

            final EObject model2 = ModelUtils.load(file2.toString(), resourceSet);

            // MODSCA 2008-03-31 add option for ignoring some elements
            Map<String, Object> options = new HashMap<String, Object>();
            options.put(MatchOptions.OPTION_IGNORE_XMI_ID, true);
            options.put(MatchOptions.OPTION_SEARCH_WINDOW, 500);

            // Creates the match then the diff model for those two models
            final MatchModel match = MatchService.doMatch(alreadySavedModel, model2, options);
            final DiffModel diff = DiffService.doDiff(match, false);

            // MODSCA 2008-03-31 play around with the elements
            // EList matchedElements = match.getMatchedElements();
            // for (Object m : matchedElements) {
            // MatchElement elt = (MatchElement) m;
            //
            // }
            EList<UnMatchElement> unMatchedElements = match.getUnMatchedElements();
            for (Object object : unMatchedElements) {
                UnMatchElement unMatched = (UnMatchElement) object;
                ModelElement modelElt = (ModelElement) unMatched.getElement();
                System.out.println("Unmatched elt= " + modelElt.getName()); //$NON-NLS-1$
            }
            System.out.println("LEFT MODEL=" + match.getLeftModel()); //$NON-NLS-1$
            EList<DiffElement> ownedElements = diff.getOwnedElements();
            // for (DiffElement oe : ownedElements) {
            // // System.out.println(oe.g);
            // }
            // if (true) {
            // return;
            // }

            // Prints the results
            try {
                System.out.println(ModelUtils.serialize(match));
                System.out.println(ModelUtils.serialize(diff));
            } catch (IOException e) {
                log.error(e, e);
            }

            // Serializes the result as "result.emfdiff" in the directory this
            // class has been called from.
            String outputFile = "out/result.emfdiff"; //$NON-NLS-1$
            System.out.println("saving emfdiff as \"" + outputFile + "\""); //$NON-NLS-1$ //$NON-NLS-2$
            final ModelInputSnapshot snapshot = DiffFactory.eINSTANCE.createModelInputSnapshot();
            snapshot.setDate(Calendar.getInstance().getTime());
            snapshot.setMatch(match);
            snapshot.setDiff(diff);
            ModelUtils.save(snapshot, outputFile); //$NON-NLS-1$

        } catch (IOException e) {
            System.out.print(e.getMessage());
            // log.error(e, e);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            System.out.print(e.getMessage());
            log.error(e, e);
        }
    }

    private IFile loadDataProviderAndStructureInMemory(TdDataProvider dataProvider, FolderProvider folderProvider) {

        IPath folderPath = ((folderProvider != null) && folderProvider.getFolder() != null) ? folderProvider.getFolderResource()
                .getFullPath() : null;
        if (folderPath == null) { // do not serialize data
            System.out.println("Data provider not serialized: no folder given."); //$NON-NLS-1$
            return null;
        }

        // --- add resources in resource set
        EMFUtil util = EMFSharedResources.getSharedEmfUtil();
        ResourceSet resourceSet = util.getResourceSet();

        String fileName = ".refresh.prv"; //$NON-NLS-1$
        IFile file = folderProvider.getFolderResource().getFile(fileName);
        // File file = new File(dataproviderFilename);
        if (file.exists()) {
            try {
                file.delete(true, null);
            } catch (CoreException e) {
                log.error(e, e);
            }
        }
        URI uri = URI.createPlatformResourceURI(file.getFullPath().toString(), false);
        final Resource resource = resourceSet.createResource(uri);
        boolean ok = resource.getContents().add(dataProvider);

        // save each catalog is its own file
        Collection<? extends ModelElement> catalogs = DataProviderHelper.getTdCatalogs(dataProvider);
        if (CAT_WITH_PRV) {
            ok = resource.getContents().addAll(catalogs);
        } else {
            // ok = addElementsToOwnResources(catalogs,
            // folderProvider.getFolderResource(), util);
        }

        // save each schema is its own file
        Collection<? extends ModelElement> schemata = DataProviderHelper.getTdSchema(dataProvider);
        if (CAT_WITH_PRV) {
            ok = resource.getContents().addAll(schemata);
        } else {
            // ok = addElementsToOwnResources(schemata,
            // folderProvider.getFolderResource(), util);
            // util.save();
        }
        Iterator<? extends ModelElement> it = schemata.iterator();
        while (it.hasNext()) {
            TdSchema tdschema = (TdSchema) it.next();
            Collection<? extends ModelElement> tables = SchemaHelper.getTables(tdschema);
            ok = resource.getContents().addAll(tables);
            Collection<? extends ModelElement> views = SchemaHelper.getViews(tdschema);
            ok = resource.getContents().addAll(views);

        }

        EMFUtil.saveSingleResource(resource);

        return file;
    }

    public void selectionChanged(IAction action, ISelection selection) {
        if (selection != null && selection instanceof TreeSelection) {
            TreeSelection ts = (TreeSelection) selection;
            TreePath[] treepaths = ts.getPaths();
            if (treepaths.length >= 1) {
                int count = treepaths[0].getSegmentCount();
                for (int i = 0; i < count; i++) {
                    Object o = treepaths[0].getSegment(i);
                    if (o instanceof IFile) {
                        if (((IFile) o).getFileExtension().toLowerCase().equals("prv")) { //$NON-NLS-1$
                            selectedFileObject = (IFile) o;
                        }
                    } else if (o instanceof TdSchema) {
                        selectedSchema = (TdSchema) o;
                    } else if (o instanceof TdCatalog) {
                        selectedCatalog = (TdCatalog) o;
                    } else if (o instanceof TdDataProvider) {
                        selectedDataProvider = (TdDataProvider) o;
                    }
                }
            }
            selectedObject = ts.getFirstElement();
        }
    }
}
