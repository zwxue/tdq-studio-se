// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.cwm.compare.ui.editor;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.ICompareInputLabelProvider;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.compare.diff.metamodel.ComparisonResourceSnapshot;
import org.eclipse.emf.compare.diff.metamodel.DiffModel;
import org.eclipse.emf.compare.ui.ModelCompareInput;
import org.eclipse.emf.compare.util.ModelUtils;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorLauncher;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.cwm.compare.DQStructureComparer;
import org.talend.cwm.compare.i18n.Messages;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.dq.nodes.foldernode.IFolderNode;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.resource.relational.Catalog;

/**
 * DOC mzhao class global comment. Detailled comment
 */
public class ModelElementCompareEditorLauncher implements IEditorLauncher {

    private String connectionName = ""; //$NON-NLS-1$

    private Object selectedObject = null;

    private boolean compareEachOther;

    private static Logger log = Logger.getLogger(ModelElementCompareEditorLauncher.class);

    public ModelElementCompareEditorLauncher(String connName, Object selObj, boolean ce) {
        connectionName = connName;
        selectedObject = selObj;
        compareEachOther = ce;
    }

    public void open(IPath file) {
        try {
            final EObject snapshot = ModelUtils.load(file.toFile(), new ResourceSetImpl());
            if (snapshot instanceof ComparisonResourceSnapshot) {
                CompareConfiguration comapreConfiguration = new CompareConfiguration();
                comapreConfiguration.setDefaultLabelProvider(new ICompareInputLabelProvider() {

                    public Image getAncestorImage(Object input) {
                        return null;
                    }

                    public String getAncestorLabel(Object input) {
                        return ""; //$NON-NLS-1$
                    }

                    public Image getLeftImage(Object input) {
                        return null;
                    }

                    public String getLeftLabel(Object input) {
                        String showLabel = Messages.getString("ModelElementCompareEditorLauncher.LocalStructure", connectionName); //$NON-NLS-1$
                        if (compareEachOther) {
                            showLabel = Messages.getString("ModelElementCompareEditorLauncher.FirstElement"); //$NON-NLS-1$
                        }
                        return showLabel;
                    }

                    public Image getRightImage(Object input) {
                        return null;
                    }

                    public String getRightLabel(Object input) {
                        String showLabel = Messages.getString("ModelElementCompareEditorLauncher.DistantStructure"); //$NON-NLS-1$
                        if (compareEachOther) {
                            showLabel = Messages.getString("ModelElementCompareEditorLauncher.SecondElement"); //$NON-NLS-1$
                        }
                        return showLabel;
                    }

                    public Image getImage(Object element) {
                        return null;
                    }

                    public String getText(Object element) {
                        return Messages.getString("ModelElementCompareEditorLauncher.TextLabel"); //$NON-NLS-1$
                    }

                    public void addListener(ILabelProviderListener listener) {
                        // needn't to do anything ???
                    }

                    public void dispose() {
                        // needn't to do anything ???
                    }

                    public boolean isLabelProperty(Object element, String property) {
                        return false;
                    }

                    public void removeListener(ILabelProviderListener listener) {
                        // needn't to do anything ???
                    }

                });
                ModelElementCompareEditorInput compEditorInput = new ModelElementCompareEditorInput(
                        (ComparisonResourceSnapshot) snapshot, comapreConfiguration, selectedObject);
                // MOD mzhao bug 8581 Add the specific title for comparison
                // editor.
                String editorTitle = Messages.getString("ModelElementCompareEditorLauncher.Compare"); //$NON-NLS-1$
                if (selectedObject instanceof IFile) {
                    editorTitle = PrvResourceFileHelper.getInstance().findProvider((IFile) selectedObject).getName();
                } else if (selectedObject instanceof IFolderNode) {
                    editorTitle = ((ModelElement) ((IFolderNode) selectedObject).getParent()).getName();
                } else if (selectedObject instanceof Catalog) {
                    editorTitle = ((Catalog) selectedObject).getName();
                } else if (selectedObject instanceof IRepositoryViewObject) {
                    editorTitle = ((ConnectionItem) ((IRepositoryViewObject) selectedObject).getProperty().getItem())
                            .getConnection().getName();
                } else if (selectedObject instanceof Connection) {
                    editorTitle = ((Connection) selectedObject).getName();
                }
                compEditorInput.setTitle(editorTitle);
                CompareUI.openCompareEditor(compEditorInput);
                addCloseListener(compEditorInput);
                compEditorInput.hookLeftPanelContextMenu(compareEachOther);
                compEditorInput.hookToolBar(compareEachOther);
            }
        } catch (IOException e) {
            // Fichier non lisible
            System.out.println(e);
            assert false;
        }
    }

    /**
     * remove the temp file when compare any thing
     * 
     * @param compEditorInput
     */
    private void addCloseListener(ModelElementCompareEditorInput compEditorInput) {
        CorePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().addPartListener(new IPartListener() {

            public void partActivated(IWorkbenchPart part) {
                // not implement

            }

            public void partBroughtToTop(IWorkbenchPart part) {
                // not implement

            }

            public void partClosed(IWorkbenchPart part) {
                if (part instanceof EditorPart) {
                    IEditorInput editorInput = ((EditorPart) part).getEditorInput();
                    if (editorInput instanceof ModelElementCompareEditorInput) {
                        removeTempFiles((ModelElementCompareEditorInput) editorInput);
                    }
                }
                removeListener();

            }

            /**
             * Remove this listener when not any CompareEditor is opening
             */
            private void removeListener() {
                IEditorReference[] editorReferences = CorePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow()
                        .getActivePage().getEditorReferences();
                for (IEditorReference editorPart : editorReferences) {
                    try {
                        IEditorInput editorInput = editorPart.getEditorInput();
                        if (editorInput instanceof ModelElementCompareEditorInput) {
                            return;
                        }
                    } catch (PartInitException e) {
                        log.debug("listener can not be remove for delete temp file when generater by open compare editor", e); //$NON-NLS-1$
                    }
                }
                CorePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().removePartListener(this);

            }

            private boolean removeTempFiles(ModelElementCompareEditorInput editorInput) {
                boolean returnCode = true;
                if (editorInput == null) {
                    return false;
                }
                Object compareResult = editorInput.getCompareResult();
                if (compareResult != null && compareResult instanceof ModelCompareInput) {
                    returnCode &= removeLeftResource((ModelCompareInput) compareResult);
                    returnCode &= removeRightResource((ModelCompareInput) compareResult);
                    returnCode &= removeCurrResource((ModelCompareInput) compareResult);
                }
                return returnCode;
            }

            private boolean removeLeftResource(ModelCompareInput compareResult) {
                Resource leftResource = compareResult.getLeftResource();
                return DQStructureComparer.removeResourceFromWorkspace(leftResource);
            }

            private boolean removeRightResource(ModelCompareInput compareResult) {
                Resource rightResource = compareResult.getRightResource();
                return DQStructureComparer.removeResourceFromWorkspace(rightResource);
            }

            private boolean removeCurrResource(ModelCompareInput compareResult) {
                Object diff = compareResult.getDiff();
                Resource currResource = null;
                if (diff instanceof DiffModel) {
                    currResource = ((DiffModel) diff).eResource();
                }
                return DQStructureComparer.removeResourceFromWorkspace(currResource);
            }

            public void partDeactivated(IWorkbenchPart part) {
                // not implement

            }

            public void partOpened(IWorkbenchPart part) {
                // not implement

            }

        });
    }
}
