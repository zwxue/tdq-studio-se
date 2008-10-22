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
package org.talend.dataprofiler.core.ui.views;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.talend.commons.emf.EMFUtil;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.cwm.management.connection.JavaSqlFactory;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.exception.DataprofilerCoreException;
import org.talend.dataprofiler.core.exception.MessageBoxExceptionHandler;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.pattern.CreatePatternAction;
import org.talend.dataprofiler.core.sql.OpenSqlFileAction;
import org.talend.dataprofiler.core.ui.utils.CheckValueUtils;
import org.talend.dataprofiler.core.ui.views.layout.BorderLayout;
import org.talend.dataquality.domain.pattern.ExpressionType;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.domain.pattern.RegularExpression;
import org.talend.dq.dbms.DbmsLanguage;
import org.talend.dq.dbms.DbmsLanguageFactory;
import org.talend.utils.sugars.TypedReturnCode;

/**
 * The View use to test the text whether match the specification regular text.
 * 
 * 
 */

public class PatternTestView extends ViewPart {

    public static final String ID = "org.talend.dataprofiler.core.ui.views.PatternTestView";

    private static Logger log = Logger.getLogger(PatternTestView.class);

    private Combo dbCombo;

    private Text testText, regularText;

    Button sqlButton, testButton;

    Composite butPane;

    List<TdDataProvider> listTdDataProviders = new ArrayList<TdDataProvider>();

    private Button saveButton;

    private Label emoticonLabel;

    private DbmsLanguage createDbmsLanguage;

    private Pattern pattern;

    private Button createPatternButton;

    private RegularExpression regularExpression;

    @Override
    public void createPartControl(final Composite parent) {
        BorderLayout blay = new BorderLayout();
        parent.setLayout(blay);
        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(BorderLayout.NORTH);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.numColumns = 1;
        composite.setLayout(layout);
        Composite coboCom = new Composite(composite, SWT.NONE);
        layout = new GridLayout();
        layout.numColumns = 2;
        coboCom.setLayout(layout);
        GridData data = new GridData();
        data.horizontalAlignment = GridData.CENTER;
        coboCom.setLayoutData(data);
        Label dbLabel = new Label(coboCom, SWT.NONE);
        dbLabel.setText("DB Connections");
        dbCombo = new Combo(coboCom, SWT.DROP_DOWN);
        data = new GridData();
        data.widthHint = 80;
        data.heightHint = 100;
        dbCombo.setLayoutData(data);
        IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getProject(DQStructureManager.METADATA).getFolder(
                DQStructureManager.DB_CONNECTIONS);
        listTdDataProviders = DqRepositoryViewService.listTdDataProviders(folder, true);
        List<String> items = new ArrayList<String>();
        for (TdDataProvider tdDataProvider : listTdDataProviders) {
            items.add(tdDataProvider.getName());
        }
        if (!items.isEmpty()) {
            dbCombo.setItems(items.toArray(new String[0]));
            if (dbCombo.getText().equals("")) {
                dbCombo.setText(items.get(0));
            }
        }
        Composite imgCom = new Composite(composite, SWT.NONE);
        imgCom.setLayout(layout);
        data = new GridData();
        data.horizontalAlignment = GridData.END;
        imgCom.setLayoutData(data);

        emoticonLabel = new Label(imgCom, SWT.NONE);
        GridData gd = new GridData();
        gd.heightHint = 18;
        emoticonLabel.setLayoutData(gd);
        emoticonLabel.setText(PluginConstant.SPACE_STRING + PluginConstant.SPACE_STRING + PluginConstant.SPACE_STRING);

        Label textAreaLabel = new Label(composite, SWT.NONE);
        textAreaLabel.setText("Test Area");
        testText = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        data = new GridData(GridData.FILL_HORIZONTAL);
        data.heightHint = 40;
        testText.setLayoutData(data);

        Label regularLabel = new Label(composite, SWT.NONE);
        regularLabel.setText("Regular Expression");
        this.regularText = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        this.regularText.setLayoutData(data);
        regularText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                boolean enabled = (!regularText.getText().equals(PluginConstant.EMPTY_STRING))
                        && (CheckValueUtils.isPatternValue(regularText.getText()));
                if (pattern != null) {
                    saveButton.setEnabled(enabled);
                }
                createPatternButton.setEnabled(enabled);
            }

        });

        BorderLayout blayout = new BorderLayout();
        Composite bottom = new Composite(parent, SWT.NONE);
        bottom.setLayout(blayout);
        bottom.setLayoutData(BorderLayout.CENTER);
        Composite centerPane = new Composite(bottom, SWT.NONE);
        centerPane.setLayoutData(BorderLayout.CENTER);
        final Composite rightPane = new Composite(bottom, SWT.NONE);
        rightPane.setLayoutData(BorderLayout.EAST);

        GridLayout llayout = new GridLayout();
        llayout.numColumns = 3;
        centerPane.setLayout(llayout);
        GridLayout rlayout = new GridLayout();
        rlayout.numColumns = 1;
        rightPane.setLayout(rlayout);
        data = new GridData();
        data.heightHint = 25;
        data.widthHint = 92;
        sqlButton = new Button(centerPane, SWT.PUSH);
        sqlButton.setText("SQL");
        sqlButton.setLayoutData(data);
        sqlButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                IFolder sourceFolder = ResourcesPlugin.getWorkspace().getRoot().getProject(DQStructureManager.LIBRARIES)
                        .getFolder(DQStructureManager.SOURCE_FILES);
                IFile sqlFile = sourceFolder.getFile("SQL Editor.sql");
                int i = 0;
                while (sqlFile.exists()) {
                    sqlFile = sourceFolder.getFile("SQL Editor (" + i + ").sql");
                    i++;
                }
                List<IFile> arrayList = new ArrayList<IFile>(2);
                arrayList.add(sqlFile);
                new OpenSqlFileAction(arrayList).run();
            }
        });
        createPatternButton = new Button(centerPane, SWT.PUSH);
        createPatternButton.setText("Create Pattern");
        data = new GridData();
        data.heightHint = 25;
        data.widthHint = 92;
        createPatternButton.setLayoutData(data);
        createPatternButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {

                new CreatePatternAction(ResourcesPlugin.getWorkspace().getRoot().getProject(DQStructureManager.LIBRARIES)
                        .getFolder(DQStructureManager.PATTERNS), ExpressionType.REGEXP, regularText.getText(), createDbmsLanguage
                        .getDbmsName()).run();
            }
        });
        createPatternButton.setEnabled(false);
        saveButton = new Button(centerPane, SWT.PUSH);
        saveButton.setText("Save");
        saveButton.setEnabled(false);
        data = new GridData();
        data.heightHint = 25;
        data.widthHint = 92;
        saveButton.setLayoutData(data);
        saveButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                if (pattern != null) {
                    regularExpression.getExpression().setBody(regularText.getText());
                    EMFUtil.saveSingleResource(pattern.eResource());
                    MessageDialog.openInformation(new Shell(), "Success", "Success to save the pattern '" + pattern.getName()
                            + "'");
                }
                saveButton.setEnabled(false);
            }
        });
        testButton = new Button(rightPane, SWT.PUSH);
        testButton.setText("Test");
        testButton.setLayoutData(data);
        testButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                testRegularText();
            }

        });
    }

    /**
     * Test the text by the regular text of regularText.
     */
    private void testRegularText() {
        for (TdDataProvider tddataprovider : listTdDataProviders) {
            if (tddataprovider.getName().equals(dbCombo.getText())) {
                createDbmsLanguage = DbmsLanguageFactory.createDbmsLanguage(tddataprovider);
                String selectRegexpTestString = createDbmsLanguage.getSelectRegexpTestString(testText.getText(), regularText
                        .getText());
                TypedReturnCode<Connection> rcConn = JavaSqlFactory.createConnection(tddataprovider);
                try {
                    if (!rcConn.isOk()) {
                        throw new DataprofilerCoreException(rcConn.getMessage());
                    }
                    Connection connection = rcConn.getObject();
                    Statement createStatement = connection.createStatement();
                    ResultSet resultSet = createStatement.executeQuery(selectRegexpTestString);
                    while (resultSet.next()) {
                        String okString = resultSet.getString(1);
                        if ("1".equalsIgnoreCase(okString)) {
                            emoticonLabel.setImage(ImageLib.getImage(ImageLib.EMOTICON_SMILE));
                            return;
                        }
                    }
                    emoticonLabel.setImage(ImageLib.getImage(ImageLib.EXCLAMATION));
                    return;
                } catch (Exception exception) {
                    log.error(exception, exception);
                    MessageBoxExceptionHandler.process(exception, new Shell());
                    emoticonLabel.setImage(null);
                    return;
                } finally {
                    ConnectionUtils.closeConnection(rcConn.getObject());
                }
            }
        }
        MessageDialog.openWarning(new Shell(), "", "No database is selected!");
    }

    /**
     * 
     * DOC autumn PatternTestView class global comment. Detailled comment
     */
    // class TreeSelectDialog extends SelectionDialog {
    //
    // TreeViewer treeViewer;
    //
    // List<Object> tdCatalogItemList = new ArrayList();
    //
    // String title;
    //
    // protected TreeSelectDialog(Shell parentShell) {
    //
    // super(parentShell);
    // }
    //
    // public TreeSelectDialog(TdDataProvider tdDataProvider, Shell parentShell) {
    // super(parentShell);
    // title = tdDataProvider.getName();
    // this.setTitle(title);
    // tdCatalogItemList.addAll(CatalogHelper.getTdCatalogs(tdDataProvider.getDataPackage()));
    // }
    //
    // private void createTreeViewer(Composite parent) {
    // // treeViewer = new CheckboxTreeViewer(parent);
    // treeViewer = new TreeViewer(parent, SWT.BORDER);
    // treeViewer.setContentProvider(new TableViewContentProvider());
    // treeViewer.setLabelProvider(new TableTreeLabelProvider());
    // treeViewer.setInput(tdCatalogItemList);
    // treeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
    // }
    //
    // @Override
    // protected Control createDialogArea(Composite parent) {
    // Composite composite = (Composite) super.createDialogArea(parent);
    // GridLayout layout = new GridLayout();
    // layout.numColumns = 1;
    // composite.setLayout(layout);
    // composite.setLayoutData(new GridData(GridData.FILL_BOTH));
    // createTreeViewer(composite);
    // return composite;
    // }
    //
    // /**
    // *
    // */
    // protected void initializeBounds() {
    // this.getShell().setSize(400, 300);
    // }
    //
    // /**
    // * @since 3.4
    // */
    // protected boolean isResizable() {
    // return false;
    // }
    // }
    /**
     * 
     * qwei SelectRepositoryContextDialog class global comment. Detailled comment
     */
    // class TableViewContentProvider implements ITreeContentProvider {
    //
    // public Object[] getChildren(Object parentElement) {
    // if (parentElement instanceof TdCatalog) {
    // // TdCatalog item = (TdCatalog) parentElement;
    //
    // }
    // return null;
    // }
    //
    // public Object getParent(Object element) {
    //
    // return element;
    // }
    //
    // public boolean hasChildren(Object element) {
    // return false;
    // }
    //
    // @SuppressWarnings("unchecked")
    // public Object[] getElements(Object inputElement) {
    // return ((List) inputElement).toArray();
    // }
    //
    // public void dispose() {
    //
    // }
    //
    // public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    //
    // }
    //
    // }
    /**
     * 
     * qwei TableSelectionDialog class global comment. Detailled comment
     */
    // class TableTreeLabelProvider implements ILabelProvider {
    //
    // public final Image getImage(Object element) {
    // // obtain the base image by querying the element
    //
    // if (element instanceof TdCatalog) {
    // return ImageLib.getImage(ImageLib.CATALOG);
    // }
    // return null;
    // }
    //
    // public final String getText(Object element) {
    // // query the element for its label
    // String label = "";
    // if (element instanceof TdCatalog) {
    // TdCatalog tdCatalog = (TdCatalog) element;
    // label = tdCatalog.getName();
    // }
    // return label;
    // }
    //
    // public void addListener(ILabelProviderListener listener) {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // public void dispose() {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // public boolean isLabelProperty(Object element, String property) {
    // // TODO Auto-generated method stub
    // return false;
    // }
    //
    // public void removeListener(ILabelProviderListener listener) {
    // // TODO Auto-generated method stub
    //
    // }
    // }
    /**
     * add DBCombo selectionListener DOC autumn Comment method "initDBComboLister".
     */
    // private void initDBComboListener() {
    // dbCombo.addSelectionListener(new SelectionAdapter() {
    //
    // public void widgetSelected(SelectionEvent e) {
    // // TODO Auto-generated method stub
    // String strDBName = dbCombo.getText();
    // }
    //
    // });
    // }
    /**
     * Set the pattern and regularExpression value to the corresponding field, it can be called after the control
     * created.
     * 
     * @param pattern
     * @param regularExpression
     */
    public void setPatternExpression(Pattern pattern, RegularExpression regularExpression) {
        this.pattern = pattern;
        this.regularExpression = regularExpression;
        if (PluginConstant.EMPTY_STRING.equals(regularText)) {
            return;
        }
        this.regularText.setText(regularExpression.getExpression().getBody());
        this.saveButton.setEnabled(true);
        this.createPatternButton.setEnabled(true);
    }

    public String getRegularText() {
        return this.regularText.getText();
    }

    @Override
    public void setFocus() {

    }
}
