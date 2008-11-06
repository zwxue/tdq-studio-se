// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.editor;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.IDocumentProviderExtension2;
import org.eclipse.ui.texteditor.IElementStateListener;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;

/**
 * DOC rli class global comment. Detailled comment
 */
public abstract class CommonFormEditor extends FormEditor {

    private static Logger log = Logger.getLogger(CommonFormEditor.class);

    protected boolean isDirty = false;

    private IDocumentProvider provider;

    /** The editor's element state listener. */
    private IElementStateListener fElementStateListener = new ElementListener();

    /**
     * DOC rli AnalysisEditor class global comment. Detailled comment
     */
    class ElementListener implements IElementStateListener {

        public void elementContentAboutToBeReplaced(Object element) {
        }

        public void elementContentReplaced(Object element) {
        }

        public void elementDeleted(Object element) {
            close(true);
        }

        public void elementDirtyStateChanged(Object element, boolean isDirty) {
        }

        public void elementMoved(Object originalElement, Object movedElement) {
            close(true);
        }
    }

    protected void setInput(IEditorInput input) {
        super.setInput(input);
        translateInput(input);
        try {
            updateDocumentProvider(input);
        } catch (CoreException e) {
            log.error(DefaultMessagesImpl.getString("CommonFormEditor.install") + input.getName()); //$NON-NLS-1$
        }
    }

    protected void translateInput(IEditorInput input) {
    }

    /**
     * Provide a new DocumentProvider based on the given editor input.
     * 
     * @param input the editor input.
     * @throws CoreException
     */
    protected void updateDocumentProvider(IEditorInput input) throws CoreException {
        IProgressMonitor rememberedProgressMonitor = null;
        provider = new TextFileDocumentProvider();
        provider.addElementStateListener(fElementStateListener);
        if (provider instanceof IDocumentProviderExtension2) {
            IDocumentProviderExtension2 extension = (IDocumentProviderExtension2) provider;
            extension.setProgressMonitor(rememberedProgressMonitor);
        }

        provider.connect(input);
    }

    /**
     * Disposes of the connection with the document provider. Subclasses may extend.
     * 
     * @since 3.0
     */
    protected void disposeDocumentProvider() {
        if (provider != null) {

            IEditorInput input = getEditorInput();
            if (input != null) {
                provider.disconnect(input);
            }

            if (fElementStateListener != null) {
                provider.removeElementStateListener(fElementStateListener);
                fElementStateListener = null;
            }

        }
        provider = null;
    }

    @Override
    public void dispose() {
        this.disposeDocumentProvider();
        super.dispose();
    }

    public void close(final boolean save) {
        Display display = getSite().getShell().getDisplay();
        display.asyncExec(new Runnable() {

            public void run() {
                getSite().getPage().closeEditor(CommonFormEditor.this, save);
            }
        });
    }

    /**
     * DOC qzhang Comment method "refreshDQView".
     */
    protected void refreshDQView() {
        IFile node = ((FileEditorInput) getEditorInput()).getFile();
        IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        IViewPart findView = activePage.findView("org.talend.dataprofiler.core.ui.views.DQRespositoryView"); //$NON-NLS-1$
        DQRespositoryView view = (DQRespositoryView) findView;
        view.getCommonViewer().refresh(node);
    }

    public void doSaveAs() {
        doSave(null);
    }

    public void doSave(IProgressMonitor monitor) {
        refreshDQView();
        this.isDirty = false;
        firePropertyChange(IEditorPart.PROP_DIRTY);
        CorePlugin.getDefault().refreshWorkSpace();
    }

    public boolean isSaveAsAllowed() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.forms.editor.FormEditor#isDirty()
     */
    @Override
    public boolean isDirty() {
        return isDirty || super.isDirty();
    }

    public void setDirty(boolean isDirty) {
        this.isDirty = isDirty;
        firePropertyChange(IEditorPart.PROP_DIRTY);
    }

}
