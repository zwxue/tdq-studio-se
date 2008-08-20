/*
 * Copyright (C) 2007 SQL Explorer Development Team http://sourceforge.net/projects/eclipsesql
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to
 * the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package net.sourceforge.sqlexplorer.plugin.editors;

import net.sourceforge.sqlexplorer.IConstants;
import net.sourceforge.sqlexplorer.dbproduct.Session;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.sessiontree.model.utility.Dictionary;
import net.sourceforge.sqlexplorer.sqleditor.SQLTextViewer;
import net.sourceforge.sqlexplorer.sqleditor.actions.ExecSQLAction;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.views.navigator.ResourceComparator;

/**
 * TextEditor specialisation; encapsulates functionality specific to editing SQL.
 * 
 * Virtually all of this code came from SQLEditor, which used to be derived directly from TextEditor; SQLEditor now
 * combines the text editor (here, SQLTextEditor) and the result and messages panes in a single editor, hence this was
 * separated out for clarity
 * 
 * Note that MouseClickListener was also moved to a top-level, package-private class for readability
 * 
 * @modified John Spackman
 * 
 */
public class SQLTextEditor extends TextEditor {

    static int i = 0;

    private SQLEditor editor;

    private MouseClickListener mcl;

    private IPartListener partListener;

    /* package */SQLTextViewer sqlTextViewer;

    private boolean _enableContentAssist = SQLExplorerPlugin.getDefault().getPluginPreferences()
            .getBoolean(IConstants.SQL_ASSIST);

    private IPreferenceStore store;

    public SQLTextEditor(SQLEditor editor) {
        super();
        this.editor = editor;
        mcl = new MouseClickListener(editor);
        store = SQLExplorerPlugin.getDefault().getPreferenceStore();
        setPreferenceStore(SQLExplorerPlugin.getDefault().getPreferenceStore());
    }

    @Override
    protected boolean isLineNumberRulerVisible() {
        return true;
    }

    @Override
    protected boolean isOverviewRulerVisible() {
        return true;
    }

    @Override
    protected boolean isPrefQuickDiffAlwaysOn() {
        return true;
    }

    @Override
    public boolean isChangeInformationShowing() {
        return true;
    }

    @Override
    public IProgressMonitor getProgressMonitor() {
        return super.getProgressMonitor();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#performSaveAs(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    protected void performSaveAs(IProgressMonitor progressMonitor) {
        // PTODO qzhang correct save the sql file. for bug 3860.
        Shell shell = getSite().getShell();
        final IEditorInput input = getEditorInput();
        IDocumentProvider provider = getDocumentProvider();
        final IEditorInput newInput;
        if (input instanceof IURIEditorInput && !(input instanceof IFileEditorInput)) {
            super.performSaveAs(progressMonitor);
            return;
        }

        // PTODO qzhang fixed bug 3907
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        ILabelProvider lp = new WorkbenchLabelProvider();
        ITreeContentProvider cp = new WorkbenchContentProvider();
        FolderSelectionDialog dialog = new FolderSelectionDialog(shell, lp, cp);
        // dialog.setValidator(validator);
        dialog.setTitle("Select folder");
        dialog.setMessage("Select the folder in which the item will be created");
        dialog.setInput(root);
        dialog.addFilter(new ViewerFilter() {

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object,
             * java.lang.Object)
             */
            @Override
            public boolean select(Viewer viewer, Object parentElement, Object element) {
                if (IProject.class.isInstance(element)) {
                    return "Libraries".equals(((IProject) element).getName());
                } else if (IFolder.class.isInstance(element)) {
                    IPath path = new Path("Source Files");
                    IPath projectRelativePath = ((IFolder) element).getProjectRelativePath();
                    return path.isPrefixOf(projectRelativePath);
                }
                return false;
            }
        });
        dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));
        // SaveAsDialog dialog = new SaveAsDialog(shell);
        // IFile original = (input instanceof IFileEditorInput) ? ((IFileEditorInput) input).getFile() : null;
        // if (original != null)
        // dialog.setOriginalFile(original);

        // dialog.create();

        // if (provider.isDeleted(input) && original != null) {
        // String message = "The file is already deleted.";
        // dialog.setErrorMessage(null);
        // dialog.setMessage(message, IMessageProvider.WARNING);
        // }
        if (dialog.open() == Window.CANCEL) {
            if (progressMonitor != null)
                progressMonitor.setCanceled(true);
            return;
        }

        Object elements = dialog.getResult()[0];
        IResource elem = (IResource) elements;
        if (elem instanceof IFolder) {
            IPath filePath = ((IFolder) elem).getFullPath();
            filePath = filePath.append(getTitle());
            if (filePath == null) {
                if (progressMonitor != null)
                    progressMonitor.setCanceled(true);
                return;
            }
            IWorkspace workspace = ResourcesPlugin.getWorkspace();
            IFile file = workspace.getRoot().getFile(filePath);
            // PTODO qzhang 4753: Ask for a new name when saving a file with an already existing name
            if (file.exists() && SQLExplorerPlugin.isEditorSerialName(filePath.lastSegment())) {
                InputDialog inputDialog = new InputDialog(getSite().getShell(), "New File Name",
                        "this file exists already, please input new file name: ", filePath.lastSegment(), null);
                if (inputDialog.open() == InputDialog.CANCEL) {
                    return;
                } else {
                    IPath lseg = filePath.removeLastSegments(1);
                    IPath append = lseg.append(inputDialog.getValue());
                    file = workspace.getRoot().getFile(append);
                }
            }
            newInput = new FileEditorInput(file);
            if (provider == null) {
                // editor has programmatically been closed while the dialog was open
                return;
            }
            boolean success = false;
            try {

                provider.aboutToChange(newInput);
                createIFile(progressMonitor, file, getViewer().getDocument().get());
                success = true;

            } catch (CoreException x) {
                final IStatus status = x.getStatus();
                if (status == null || status.getSeverity() != IStatus.CANCEL) {
                    String title = "The file save failure.";
                    String msg = "The file save failure.";
                    MessageDialog.openError(shell, title, msg);
                }
            } finally {
                provider.changed(newInput);
                if (success)
                    setInput(newInput);
            }

            if (progressMonitor != null)
                progressMonitor.setCanceled(!success);
        }
    }

    /**
     * DOC qzhang Comment method "createIFile".
     * 
     * @param monitor
     * @param file
     * @param content
     * @throws CoreException
     */
    private void createIFile(IProgressMonitor monitor, IFile file, String content) throws CoreException {
        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }
        try {
            monitor.beginTask("save file...", 2000);
            ITextFileBufferManager manager = FileBuffers.getTextFileBufferManager();
            manager.connect(file.getFullPath(), LocationKind.IFILE, monitor);
            ITextFileBuffer buffer = ITextFileBufferManager.DEFAULT.getTextFileBuffer(file.getFullPath(), LocationKind.IFILE);
            buffer.getDocument().set(content);
            buffer.commit(monitor, true);
            manager.disconnect(file.getFullPath(), LocationKind.IFILE, monitor);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            monitor.done();
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.texteditor.AbstractTextEditor#createActions()
     */
    protected void createActions() {

        super.createActions();

        if (!_enableContentAssist) {
            return;
        }

        Action action = new Action("Auto-Completion") {

            public void run() {
                sqlTextViewer.showAssistance();
            }
        };

        // This action definition is associated with the accelerator Ctrl+Space
        action.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
        setAction("ContentAssistProposal", action);

    }

    public void createPartControl(Composite parent) {

        super.createPartControl(parent);

        PlatformUI.getWorkbench().getHelpSystem().setHelp(getSourceViewer().getTextWidget(),
                SQLExplorerPlugin.PLUGIN_ID + ".SQLEditor");

        Object adapter = getAdapter(org.eclipse.swt.widgets.Control.class);
        if (adapter instanceof StyledText) {
            StyledText text = (StyledText) adapter;
            text.setWordWrap(SQLExplorerPlugin.getDefault().getPluginPreferences().getBoolean(IConstants.WORD_WRAP));

            FontData[] fData = PreferenceConverter.getFontDataArray(store, IConstants.FONT);
            if (fData.length > 0) {
                JFaceResources.getFontRegistry().put(fData[0].toString(), fData);
                text.setFont(JFaceResources.getFontRegistry().get(fData[0].toString()));
            }
        }
    }

    protected ISourceViewer createSourceViewer(final Composite parent, IVerticalRuler ruler, int style) {

        parent.setLayout(new FillLayout());
        final Composite myParent = new Composite(parent, SWT.NONE);

        GridLayout layout = new GridLayout();
        layout.marginHeight = layout.marginWidth = layout.horizontalSpacing = layout.verticalSpacing = 0;
        myParent.setLayout(layout);

        // create divider line

        Composite div1 = new Composite(myParent, SWT.NONE);
        GridData lgid = new GridData();
        lgid.grabExcessHorizontalSpace = true;
        lgid.horizontalAlignment = GridData.FILL;
        lgid.heightHint = 1;
        lgid.verticalIndent = 1;
        div1.setLayoutData(lgid);
        div1.setBackground(editor.getSite().getShell().getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));

        // create text viewer

        GridData gid = new GridData();
        gid.grabExcessHorizontalSpace = gid.grabExcessVerticalSpace = true;
        gid.horizontalAlignment = gid.verticalAlignment = GridData.FILL;

        Dictionary dictionary = null;
        if (editor.getSession() != null && _enableContentAssist) {
            dictionary = editor.getSession().getUser().getMetaDataSession().getDictionary();
        }
        sqlTextViewer = new SQLTextViewer(myParent, style, store, dictionary, ruler);
        sqlTextViewer.getControl().setLayoutData(gid);

        // create bottom divider line

        Composite div2 = new Composite(myParent, SWT.NONE);
        lgid = new GridData();
        lgid.grabExcessHorizontalSpace = true;
        lgid.horizontalAlignment = GridData.FILL;
        lgid.heightHint = 1;
        lgid.verticalIndent = 0;
        div2.setLayoutData(lgid);
        div2.setBackground(editor.getSite().getShell().getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));

        final SQLEditor thisEditor = editor;
        sqlTextViewer.getTextWidget().addVerifyKeyListener(new VerifyKeyListener() {

            private ExecSQLAction _execSQLAction = new ExecSQLAction(thisEditor);

            public void verifyKey(VerifyEvent event) {

                if (event.stateMask == SWT.CTRL && event.keyCode == 13) {
                    event.doit = false;
                    _execSQLAction.run();
                }
            }
        });

        sqlTextViewer.getTextWidget().addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {

                SQLTextEditor.this.editor.getEditorSite().getPage().activate(SQLTextEditor.this.editor.getEditorSite().getPart());
            }
        });

        myParent.layout();

        IDocument dc = new Document();
        sqlTextViewer.setDocument(dc);

        mcl.install(sqlTextViewer);

        return sqlTextViewer;
    }

    public void setNewDictionary(final Dictionary dictionary) {
        if (editor.getSite() != null && editor.getSite().getShell() != null && editor.getSite().getShell().getDisplay() != null)
            editor.getSite().getShell().getDisplay().asyncExec(new Runnable() {

                public void run() {

                    if (sqlTextViewer != null) {
                        sqlTextViewer.setNewDictionary(dictionary);
                        // if (editor.getSession() != null) {
                        // sqlTextViewer.refresh();
                        // }
                    }

                }
            });
    }

    public void onEditorSessionChanged(Session session) {
        if (session != null && _enableContentAssist) {
            setNewDictionary(editor.getSession().getUser().getMetaDataSession().getDictionary());
        } else {
            setNewDictionary(null);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IWorkbenchPart#dispose()
     */
    public void dispose() {
        if (partListener != null)
            editor.getEditorSite().getPage().removePartListener(partListener);
        mcl.uninstall();
        super.dispose();
    }

    ISourceViewer getViewer() {
        return getSourceViewer();
    }
}
