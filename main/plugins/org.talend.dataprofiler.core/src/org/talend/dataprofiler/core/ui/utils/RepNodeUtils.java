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
package org.talend.dataprofiler.core.ui.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ui.editor.AbstractItemEditorInput;
import org.talend.dataprofiler.core.ui.views.resources.IRepositoryObjectCRUD;
import org.talend.dataprofiler.core.ui.views.resources.LocalRepositoryObjectCRUD;
import org.talend.dataprofiler.core.ui.views.resources.RemoteRepositoryObjectCRUD;
import org.talend.dq.helper.ProxyRepositoryManager;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.repository.model.IRepositoryNode;

/**
 * RepositoryNode's UI utils.
 */
public final class RepNodeUtils {

    private static Logger log = Logger.getLogger(RepNodeUtils.class);

    private RepNodeUtils() {
    }

    /**
     * close file node's editor.
     * 
     * @param files
     * @param save
     */
    public static void closeFileEditor(List<IFile> files, boolean save) {
        List<IEditorReference> need2CloseEditorRefs = new ArrayList<IEditorReference>();
        IEditorReference[] editorReferences = CorePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage()
                .getEditorReferences();

        try {
            for (IEditorReference editorRef : editorReferences) {
                if (editorRef != null) {
                    IEditorInput editorInput = editorRef.getEditorInput();
                    if (editorInput != null) {
                        if (editorInput instanceof FileEditorInput) {
                            IFile file = ((FileEditorInput) editorInput).getFile();
                            if (file != null) {
                                for (IFile ifile : files) {
                                    if (ifile != null) {
                                        String osString = ifile.getRawLocation().toOSString();
                                        String osString2 = file.getRawLocation().toOSString();
                                        if (osString.equals(osString2)) {
                                            need2CloseEditorRefs.add(editorRef);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (PartInitException e) {
            log.warn(e, e);
        }

        if (need2CloseEditorRefs.size() > 0) {
            CorePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage()
                    .closeEditors(need2CloseEditorRefs.toArray(new IEditorReference[need2CloseEditorRefs.size()]), save);

        }
    }

    /**
     * close ModelElement node's editor.
     * 
     * @param nodes
     */
    public static void closeModelElementEditor(List<? extends IRepositoryNode> nodes, boolean save) {
        List<String> uuids = RepositoryNodeHelper.getUuids(nodes);

        List<IEditorReference> need2CloseEditorRefs = new ArrayList<IEditorReference>();
        IEditorReference[] editorReferences = CorePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage()
                .getEditorReferences();

        try {
            for (IEditorReference editorRef : editorReferences) {
                if (editorRef != null) {
                    IEditorInput editorInput = editorRef.getEditorInput();
                    if (editorInput != null) {
                        if (editorInput instanceof AbstractItemEditorInput) {
                            String modelElementUuid = ((AbstractItemEditorInput) editorInput).getModelElementUuid();
                            if (modelElementUuid != null && uuids.contains(modelElementUuid)) {
                                need2CloseEditorRefs.add(editorRef);
                            }
                        }
                    }
                }
            }
        } catch (PartInitException e) {
            log.warn(e, e);
        }

        if (need2CloseEditorRefs.size() > 0) {
            CorePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage()
                    .closeEditors(need2CloseEditorRefs.toArray(new IEditorReference[need2CloseEditorRefs.size()]), save);

        }
    }

    /**
     * 
     * Get repostiroy object CRUD class according to project type.
     * 
     * @return
     */
    public static IRepositoryObjectCRUD getRepositoryObjectCRUD() {
        if (ProxyRepositoryManager.getInstance().isLocalProject()) {
            return new LocalRepositoryObjectCRUD();
        } else {
            return new RemoteRepositoryObjectCRUD();
        }
    }
}
