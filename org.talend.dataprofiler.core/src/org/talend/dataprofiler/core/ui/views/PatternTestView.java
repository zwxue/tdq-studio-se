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

import net.sourceforge.sqlexplorer.plugin.editors.SQLEditor;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.handlers.IHandlerService;
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
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.pattern.CreatePatternAction;
import org.talend.dataprofiler.core.sql.OpenSqlFileAction;
import org.talend.dataprofiler.core.ui.editor.pattern.PatternMasterDetailsPage;
import org.talend.dataprofiler.core.ui.utils.CheckValueUtils;
import org.talend.dataprofiler.core.ui.views.layout.BorderLayout;
import org.talend.dataquality.domain.pattern.ExpressionType;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.domain.pattern.PatternComponent;
import org.talend.dataquality.domain.pattern.RegularExpression;
import org.talend.dataquality.domain.pattern.impl.RegularExpressionImpl;
import org.talend.dataquality.helpers.BooleanExpressionHelper;
import org.talend.dq.dbms.DbmsLanguage;
import org.talend.dq.dbms.DbmsLanguageFactory;
import org.talend.utils.sugars.TypedReturnCode;

/**
 * The View use to test the text whether match the specification regular text.
 * 
 * 
 */

public class PatternTestView extends ViewPart {

    private static final String NO_DATABASE_SELECTEDED = DefaultMessagesImpl.getString("PatternTestView.database"); //$NON-NLS-1$

    public static final String ID = "org.talend.dataprofiler.core.ui.views.PatternTestView"; //$NON-NLS-1$

    public static final String VIEW_CONTEXT_ID = "org.talend.dataprofiler.core.ui.views.PatternTestView.viewScope"; //$NON-NLS-1$

    private static Logger log = Logger.getLogger(PatternTestView.class);

    private CCombo dbCombo;

    private Text testText, regularText;

    Button sqlButton, testButton;

    Composite butPane;

    List<TdDataProvider> listTdDataProviders = new ArrayList<TdDataProvider>();

    private Button saveButton;

    private Label emoticonLabel;

    private Pattern pattern;

    private Button createPatternButton;

    private RegularExpression regularExpression;

    private PatternMasterDetailsPage editorPage;

    @Override
    public void createPartControl(final Composite parent) {
        ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL);
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);
        Composite mainComposite = new Composite(scrolledComposite, SWT.NONE);
        scrolledComposite.setContent(mainComposite);
        BorderLayout blay = new BorderLayout();
        mainComposite.setLayout(blay);
        final Composite composite = new Composite(mainComposite, SWT.NONE);
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
        dbLabel.setText(DefaultMessagesImpl.getString("PatternTestView.Connections")); //$NON-NLS-1$
        dbLabel.setToolTipText(DefaultMessagesImpl.getString("PatternTestView.databaseExecuted")); //$NON-NLS-1$
        dbCombo = new CCombo(coboCom, SWT.DROP_DOWN | SWT.BORDER);
        dbCombo.setEditable(false);
        data = new GridData();
        data.widthHint = 100;
        // data.heightHint = 100;
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
            if (dbCombo.getText().equals("")) { //$NON-NLS-1$
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
        gd.widthHint = 18;
        emoticonLabel.setLayoutData(gd);
        emoticonLabel.setText(PluginConstant.SPACE_STRING + PluginConstant.SPACE_STRING + PluginConstant.SPACE_STRING);

        Label textAreaLabel = new Label(composite, SWT.NONE);
        textAreaLabel.setText(DefaultMessagesImpl.getString("PatternTestView.area")); //$NON-NLS-1$
        testText = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        data = new GridData(GridData.FILL_HORIZONTAL);
        data.heightHint = 40;
        testText.setLayoutData(data);
        testText.setToolTipText(DefaultMessagesImpl.getString("PatternTestView.enterHere")); //$NON-NLS-1$

        Label regularLabel = new Label(composite, SWT.NONE);
        regularLabel.setText(DefaultMessagesImpl.getString("PatternTestView.regularExpression")); //$NON-NLS-1$
        this.regularText = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        this.regularText.setLayoutData(data);
        regularText.setToolTipText(DefaultMessagesImpl.getString("PatternTestView.enterHereAgainst")); //$NON-NLS-1$
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
        Composite bottom = new Composite(mainComposite, SWT.NONE);
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
        sqlButton.setText(DefaultMessagesImpl.getString("PatternTestView.SQL")); //$NON-NLS-1$
        sqlButton.setToolTipText(DefaultMessagesImpl.getString("PatternTestView.generatedSQLStatement")); //$NON-NLS-1$
        sqlButton.setLayoutData(data);
        sqlButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                openSQLEditor();
            }
        });
        createPatternButton = new Button(centerPane, SWT.PUSH);
        createPatternButton.setText(DefaultMessagesImpl.getString("PatternTestView.createPattern")); //$NON-NLS-1$
        createPatternButton.setToolTipText(DefaultMessagesImpl.getString("PatternTestView.CreateNewPattern")); //$NON-NLS-1$
        data = new GridData();
        data.heightHint = 25;
        data.widthHint = 92;
        createPatternButton.setLayoutData(data);
        createPatternButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                String language = null;
                if (regularExpression != null) {
                    language = regularExpression.getExpression().getLanguage();
                } else {
                    DbmsLanguage dbmsLanguage = getDbmsLanguage();
                    if (dbmsLanguage != null) {
                        language = dbmsLanguage.getDbmsName();
                    }
                }
                new CreatePatternAction(ResourcesPlugin.getWorkspace().getRoot().getProject(DQStructureManager.LIBRARIES)
                        .getFolder(DQStructureManager.PATTERNS), ExpressionType.REGEXP, regularText.getText(), language).run();
            }
        });
        createPatternButton.setEnabled(false);
        saveButton = new Button(centerPane, SWT.PUSH);
        saveButton.setText(DefaultMessagesImpl.getString("PatternTestView.Save")); //$NON-NLS-1$
        saveButton.setEnabled(false);
        saveButton.setToolTipText(DefaultMessagesImpl.getString("PatternTestView.SaveRegularExpression")); //$NON-NLS-1$
        data = new GridData();
        data.heightHint = 25;
        data.widthHint = 92;
        saveButton.setLayoutData(data);
        saveButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                savePattern();
            }
        });
        testButton = new Button(rightPane, SWT.PUSH);
        testButton.setText(DefaultMessagesImpl.getString("PatternTestView.test")); //$NON-NLS-1$
        testButton.setToolTipText(DefaultMessagesImpl.getString("PatternTestView.ValidateEnteredString")); //$NON-NLS-1$
        testButton.setLayoutData(data);
        testButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                testRegularText();
            }

        });

        scrolledComposite.setMinSize(mainComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        mainComposite.layout();
        activateContext();
    }

    /**
     * Activate a context that this view uses. It will be tied to this view activation events and will be removed when
     * the view is disposed.
     */
    private void activateContext() {
        IContextService contextService = (IContextService) getSite().getService(IContextService.class);
        contextService.activateContext(VIEW_CONTEXT_ID);

        TestRegularAction testRegularAction = new TestRegularAction();
        IHandlerService service = (IHandlerService) getViewSite().getService(IHandlerService.class);
        service.activateHandler(testRegularAction.getActionDefinitionId(), new ActionHandler(testRegularAction));
    }

    /**
     * DOC rli PatternTestView class global comment. Detailled comment
     */
    public class TestRegularAction extends Action {

        public TestRegularAction() {
            this.setActionDefinitionId("org.talend.dataprofiler.core.testRegular"); //$NON-NLS-1$
        }

        @Override
        public void run() {
            testRegularText();
        }
    }

    /**
     * Test the text by the regular text of regularText.
     */
    private void testRegularText() {
        for (TdDataProvider tddataprovider : listTdDataProviders) {
            if (tddataprovider.getName().equals(dbCombo.getText())) {
                DbmsLanguage createDbmsLanguage = DbmsLanguageFactory.createDbmsLanguage(tddataprovider);
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
                        if ("1".equalsIgnoreCase(okString)) { //$NON-NLS-1$
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
        MessageDialog.openWarning(new Shell(), "", NO_DATABASE_SELECTEDED); //$NON-NLS-1$
    }

    /**
     * Set the pattern and regularExpression value to the corresponding field, it can be called after the control
     * created.
     * 
     * @param pattern
     * @param regularExpression
     */
    public void setPatternExpression(PatternMasterDetailsPage editorPage, Pattern editorPattern,
            RegularExpression regularExpression) {
        this.editorPage = editorPage;
        pattern = editorPattern;
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

    /**
     * DOC rli Comment method "openSQLEditor".
     */
    private void openSQLEditor() {
        IFolder sourceFolder = ResourcesPlugin.getWorkspace().getRoot().getProject(DQStructureManager.LIBRARIES).getFolder(
                DQStructureManager.SOURCE_FILES);
        IFile sqlFile = sourceFolder.getFile("SQL Editor.sql"); //$NON-NLS-1$
        int i = 0;
        while (sqlFile.exists()) {
            sqlFile = sourceFolder.getFile("SQL Editor (" + i + ").sql"); //$NON-NLS-1$ //$NON-NLS-2$
            i++;
        }
        List<IFile> arrayList = new ArrayList<IFile>(2);
        arrayList.add(sqlFile);
        OpenSqlFileAction openSqlFileAction = new OpenSqlFileAction(arrayList);
        openSqlFileAction.run();
        IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        IWorkbenchPage page = activeWorkbenchWindow.getActivePage();
        IEditorPart findEditor = page.findEditor(openSqlFileAction.getEditorInput());

        DbmsLanguage dbmsLanguage = this.getDbmsLanguage();
        if (dbmsLanguage != null) {
            String selectRegexpTestString = dbmsLanguage.getSelectRegexpTestString(testText.getText(), regularText.getText());

            ((SQLEditor) findEditor).setText(selectRegexpTestString);
        } else {
            MessageDialog.openWarning(new Shell(), "", NO_DATABASE_SELECTEDED); //$NON-NLS-1$
        }

    }

    /**
     * If the pattern is not null, will save it and update the corresponding pattern editor content.
     */
    private void savePattern() {
        // If the pattern is not null, will update the pattern editor content.
        if (pattern != null) {
            String expressionLanguage = this.regularExpression.getExpression().getLanguage();
            DbmsLanguage dbmsLanguage = this.getDbmsLanguage();
            if (dbmsLanguage != null && (!dbmsLanguage.getDbmsName().equalsIgnoreCase(expressionLanguage))) {
                String messageInfo = DefaultMessagesImpl
                        .getString(
                                "PatternTestView.modifiedTheRegularExpression", expressionLanguage, dbmsLanguage.getDbmsName(), expressionLanguage, expressionLanguage, dbmsLanguage.getDbmsName()); //$NON-NLS-1$
                MessageDialog messageDialog = new MessageDialog(new Shell(), DefaultMessagesImpl
                        .getString("PatternTestView.warning"), null, messageInfo, MessageDialog.WARNING, //$NON-NLS-1$
                        new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL }, 0);
                int result = messageDialog.open();
                if (result == MessageDialog.OK) {
                    regularExpression.getExpression().setBody(regularText.getText());
                } else {
                    EList<PatternComponent> components = this.pattern.getComponents();
                    boolean isContainLanguage = false;
                    for (int i = 0; i < components.size(); i++) {
                        RegularExpressionImpl regularExpress = (RegularExpressionImpl) components.get(i);
                        // set the regular text to the corresponding regular expression
                        if (dbmsLanguage.getDbmsName().equalsIgnoreCase(regularExpress.getExpression().getLanguage())) {
                            regularExpress.getExpression().setBody(regularText.getText());
                            isContainLanguage = true;
                            break;
                        }
                    }

                    // Not find the corresponding regular expression, will new a expression.
                    if (!isContainLanguage) {
                        RegularExpression newRegularExpress = BooleanExpressionHelper.createRegularExpression(dbmsLanguage
                                .getDbmsName(), regularText.getText());
                        this.pattern.getComponents().add(newRegularExpress);
                    }
                }
            } else {
                regularExpression.getExpression().setBody(regularText.getText());
            }

            EMFUtil.saveSingleResource(pattern.eResource());
            editorPage.updatePatternDefinitonSection();
            // MessageDialog.openInformation(new Shell(), "Success", "Success to save the pattern '" +
            // pattern.getName()
            // + "'");
            saveButton.setEnabled(false);
        }
    }

    /**
     * DOC rli Comment method "getDbmsLanguage".
     * 
     * @param language
     * @return
     */
    private DbmsLanguage getDbmsLanguage() {
        for (TdDataProvider tddataprovider : listTdDataProviders) {
            if (tddataprovider.getName().equals(dbCombo.getText())) {
                return DbmsLanguageFactory.createDbmsLanguage(tddataprovider);
            }
        }
        return null;
    }
}
