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
package org.talend.dataprofiler.core.ui.editor.report;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.FileEditorInput;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.helper.AnaResourceFileHelper;
import org.talend.dataprofiler.core.helper.RepResourceFileHelper;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.ui.dialog.filter.TypedViewerFilter;
import org.talend.dataprofiler.core.ui.editor.AbstractMetadataFormPage;
import org.talend.dataprofiler.core.ui.views.provider.ResourceViewContentProvider;
import org.talend.dataprofiler.core.ui.views.provider.ResourceViewLabelProvider;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.reports.TdReport;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC rli class global comment. Detailled comment
 */
public class ReportMasterDetailsPage extends AbstractMetadataFormPage implements PropertyChangeListener {

    private TdReport currentReport;

    private TableViewer tableViewer;

    public ReportMasterDetailsPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
    }

    @Override
    protected ModelElement getCurrentModelElement(FormEditor editor) {
        FileEditorInput input = (FileEditorInput) editor.getEditorInput();
        this.currentReport = RepResourceFileHelper.getInstance().findReport(input.getFile());
        return currentReport;
    }

    protected void createFormContent(IManagedForm managedForm) {
        super.createFormContent(managedForm);
        final ScrolledForm form = managedForm.getForm();

        form.setText("Report Settings");
        metadataSection.setText("Report Metadata");
        metadataSection.setDescription("Set the properties of report.");
        createAnalysesListSection(form, topComp);
    }

    private void createAnalysesListSection(ScrolledForm form, Composite topComp) {
        Section section = createSection(form, topComp, "Analysis List", false, null);
        Composite sectionComp = toolkit.createComposite(section);
        sectionComp.setLayout(new GridLayout());

        Hyperlink clmnBtn = toolkit.createHyperlink(sectionComp, "Select analyses", SWT.NONE);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).applyTo(clmnBtn);
        clmnBtn.addHyperlinkListener(new HyperlinkAdapter() {

            public void linkActivated(HyperlinkEvent e) {
                openAnalysesSelectionDialog();
            }

        });

        tableViewer = new TableViewer(sectionComp, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
        Table analysesTable = tableViewer.getTable();
        analysesTable.setHeaderVisible(true);
        analysesTable.setLinesVisible(true);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(analysesTable);
        TableColumn tableColumn = new TableColumn(analysesTable, SWT.NONE);
        tableColumn.setWidth(150);
        tableColumn.setText("Analysis");
        tableColumn = new TableColumn(analysesTable, SWT.NONE);
        tableColumn.setWidth(150);
        tableColumn.setText("Execution Date");
        tableColumn = new TableColumn(analysesTable, SWT.NONE);
        tableColumn.setWidth(150);
        tableColumn.setText("Refresh");
        AnalysesViewerProvider provider = new AnalysesViewerProvider();
        tableViewer.setLabelProvider(provider);
        tableViewer.setContentProvider(provider);
        section.setClient(sectionComp);
    }

    @SuppressWarnings("unchecked")
    protected void openAnalysesSelectionDialog() {
        CheckedTreeSelectionDialog checkedTreeSelectionDialog = new CheckedTreeSelectionDialog(null,
                new ResourceViewLabelProvider(), new ResourceViewContentProvider());
        checkedTreeSelectionDialog.setContainerMode(true);
        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(DQStructureManager.DATA_PROFILING);
        final Class[] acceptedClasses = new Class[] { IResource.class };
        IResource[] members = null;
        try {
            members = project.members();
        } catch (CoreException e) {
            e.printStackTrace();
            return;
        }
        ArrayList rejectedElements = new ArrayList(members.length);
        for (int i = 0; i < members.length; i++) {
            if (!members[i].equals(ResourcesPlugin.getWorkspace().getRoot().getProject(DQStructureManager.DATA_PROFILING)
                    .getFolder(DQStructureManager.ANALYSIS))) {
                rejectedElements.add(members[i]);
            }
        }
        // rejectedElements.add(ResourcesPlugin.getWorkspace().getRoot().getProject(PluginConstant.METADATA_PROJECTNAME).getFile(
        // ".project"));
        ViewerFilter filter = new TypedViewerFilter(acceptedClasses, rejectedElements.toArray());
        checkedTreeSelectionDialog.addFilter(filter);
        checkedTreeSelectionDialog.setInput(project);
        Object selectedElements = tableViewer.getInput();
        if (selectedElements != null) {
            checkedTreeSelectionDialog.setInitialElementSelections((List) selectedElements);
        }
        if (checkedTreeSelectionDialog.open() == Dialog.OK) {
            Object[] result = checkedTreeSelectionDialog.getResult();
            List<IFile> fileList = new ArrayList<IFile>();
            for (Object obj : result) {
                if (obj instanceof IFile) {
                    IFile file = (IFile) obj;
                    fileList.add(file);
                }
            }
            this.tableViewer.setInput(fileList);
            this.setDirty(true);
        }
    }

    @Override
    public void setDirty(boolean isDirty) {
        if (this.isDirty != isDirty) {
            this.isDirty = isDirty;
            ((ReportEditror) this.getEditor()).firePropertyChange(IEditorPart.PROP_DIRTY);
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (PluginConstant.ISDIRTY_PROPERTY.equals(evt.getPropertyName())) {
            ((ReportEditror) this.getEditor()).firePropertyChange(IEditorPart.PROP_DIRTY);
        }
    }

    /**
     * DOC rli ReportMasterDetailsPage class global comment. Detailled comment
     */
    public class AnalysesViewerProvider extends LabelProvider implements ITableLabelProvider, IStructuredContentProvider {

        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        public String getColumnText(Object element, int columnIndex) {
            String text = PluginConstant.EMPTY_STRING;
            if (element instanceof IFile) {
                Analysis findAnalysis = null;
                IFile file = (IFile) element;
                if (file.getFileExtension().equalsIgnoreCase(FactoriesUtil.ANA)) {
                    findAnalysis = AnaResourceFileHelper.getInstance().findAnalysis(file);
                }
                Date executionDate = findAnalysis.getResults().getResultMetadata().getExecutionDate();
                switch (columnIndex) {
                case 0:
                    text = findAnalysis.getName();
                    break;
                case 1:
                    if (executionDate == null) {
                        break;
                    }
                    text = SimpleDateFormat.getDateTimeInstance().format(executionDate);
                    break;
                case 2:
                    text = Boolean.TRUE.toString();
                    break;
                default:
                    break;
                }
            }

            return text;
        }

        @SuppressWarnings("unchecked")
        public Object[] getElements(Object inputElement) {
            if (inputElement == null) {
                return new Object[0];
            }
            Object[] objs = ((List) inputElement).toArray();
            return objs;
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

    }

}
