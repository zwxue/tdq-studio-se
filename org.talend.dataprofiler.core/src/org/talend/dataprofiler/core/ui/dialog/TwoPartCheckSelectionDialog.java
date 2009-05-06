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
package org.talend.dataprofiler.core.ui.dialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TreeAdapter;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.dialogs.SelectionStatusDialog;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.model.nodes.foldernode.ColumnFolderNode;
import org.talend.dataprofiler.core.model.nodes.foldernode.TableFolderNode;
import org.talend.dataprofiler.core.model.nodes.foldernode.ViewFolderNode;
import org.talend.dataprofiler.core.ui.editor.analysis.AbstractAnalysisMetadataPage;
import org.talend.dataprofiler.core.ui.views.filters.AbstractViewerFilter;

/**
 * A class to select elements out of a tree structure.
 * 
 * @since 2.0
 */
public abstract class TwoPartCheckSelectionDialog extends SelectionStatusDialog
		implements ISelectionChangedListener {
	private CheckboxTreeViewer fViewer;

	protected ILabelProvider fLabelProvider;

	protected ITreeContentProvider fContentProvider;

	private CheckboxTableViewer sTableViewer;

	protected ILabelProvider sLabelProvider;

	protected IStructuredContentProvider sContentProvider;

	private ISelectionStatusValidator fValidator = null;

	private ViewerComparator fComparator;

	private String fEmptyListMessage = DefaultMessagesImpl
			.getString("TwoPartCheckSelectionDialog.noEntriesAvailable"); //$NON-NLS-1$

	private IStatus fCurrStatus = new Status(IStatus.OK, PlatformUI.PLUGIN_ID,
			0, "", null); //$NON-NLS-1$

	private List<ViewerFilter> fFilters;

	private Object fInput;

	private boolean fIsEmpty;

	private int fWidth = 60;

	private int fHeight = 18;

	protected boolean fContainerMode;

	private Object[] fExpandedElements;
	private AbstractAnalysisMetadataPage metadataFormPage;
	protected int dialogType;

	public int getDialogType() {
		return dialogType;
	}

	public void setDialogType(int dialogType) {
		this.dialogType = dialogType;
	}

	public static final int DIALOG_TYPE_COLUMN = 1;

	public static final int DIALOG_TYPE_TABLE = 2;

	/**
	 * Constructs an instance of <code>ElementTreeSelectionDialog</code>.
	 * 
	 * @param parent
	 *            The shell to parent from.
	 * @param labelProvider
	 *            the label provider to render the entries
	 * @param contentProvider
	 *            the content provider to evaluate the tree structure
	 */
	@SuppressWarnings("unchecked")
	public TwoPartCheckSelectionDialog(
			AbstractAnalysisMetadataPage metadataFormPage, Shell parent,
			String message) {
		super(parent);
		setResult(new ArrayList(0));
		setStatusLineAboveButtons(true);
		fContainerMode = true;
		fExpandedElements = null;
		this.metadataFormPage = metadataFormPage;
		int shellStyle = getShellStyle();
		setShellStyle(shellStyle | SWT.MAX | SWT.RESIZE);
		setMessage(message);
		initProvider();
		// MOD mzhao 2009-05-05, bug 6587.
		addConnFilterListener();
	}

	/**
	 * Init the LabelProvider or ContentProvider for treeViewer and tableViewer.
	 */
	protected abstract void initProvider();

	/**
	 * If set, the checked /gray state of containers (inner nodes) is derived
	 * from the checked state of its leaf nodes.
	 * 
	 * @param containerMode
	 *            The containerMode to set
	 */
	public void setContainerMode(boolean containerMode) {
		fContainerMode = containerMode;
	}

	/**
	 * Sets the message to be displayed if the list is empty.
	 * 
	 * @param message
	 *            the message to be displayed.
	 */
	public void setEmptyListMessage(String message) {
		fEmptyListMessage = message;
	}

	/**
	 * Sets the sorter used by the tree viewer.
	 * 
	 * @param sorter
	 * @deprecated since 3.3, use
	 *             {@link TwoPartCheckSelectionDialog#setComparator(ViewerComparator)}
	 *             instead
	 */
	public void setSorter(ViewerSorter sorter) {
		fComparator = sorter;
	}

	/**
	 * Sets the comparator used by the tree viewer.
	 * 
	 * @param comparator
	 * @since 3.3
	 */
	public void setComparator(ViewerComparator comparator) {
		fComparator = comparator;
	}

	/**
	 * Adds a filter to the tree viewer.
	 * 
	 * @param filter
	 *            a filter.
	 */
	public void addFilter(ViewerFilter filter) {
		if (fFilters == null) {
			fFilters = new ArrayList<ViewerFilter>(4);
		}
		fFilters.add(filter);
	}

	/**
	 * Sets an optional validator to check if the selection is valid. The
	 * validator is invoked whenever the selection changes.
	 * 
	 * @param validator
	 *            the validator to validate the selection.
	 */
	public void setValidator(ISelectionStatusValidator validator) {
		fValidator = validator;
	}

	/**
	 * Sets the tree input.
	 * 
	 * @param input
	 *            the tree input.
	 */
	public void setInput(Object input) {
		fInput = input;
	}

	public void setOutput(Object output) {
		sTableViewer.setInput(output);
	}

	/**
	 * Expands elements in the tree.
	 * 
	 * @param elements
	 *            The elements that will be expanded.
	 */
	public void setExpandedElements(Object[] elements) {
		fExpandedElements = elements;
	}

	/**
	 * Sets the size of the tree in unit of characters.
	 * 
	 * @param width
	 *            the width of the tree.
	 * @param height
	 *            the height of the tree.
	 */
	public void setSize(int width, int height) {
		fWidth = width;
		fHeight = height;
	}

	/**
	 * Validate the receiver and update the status with the result.
	 * 
	 */
	protected void updateOKStatus() {
		if (!fIsEmpty) {
			if (fValidator != null) {
				fCurrStatus = fValidator.validate(fViewer.getCheckedElements());
				updateStatus(fCurrStatus);
			} else if (!fCurrStatus.isOK()) {
				fCurrStatus = new Status(IStatus.OK, PlatformUI.PLUGIN_ID,
						IStatus.OK, "", //$NON-NLS-1$
						null);
			}
		} else {
			fCurrStatus = new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID,
					IStatus.OK, fEmptyListMessage, null);
		}
		updateStatus(fCurrStatus);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.window.Window#open()
	 */
	public int open() {
		fIsEmpty = evaluateIfTreeEmpty(fInput);
		super.open();
		return getReturnCode();
	}

	/**
	 * Handles cancel button pressed event.
	 */
	protected void cancelPressed() {
		setResult(null);
		super.cancelPressed();
	}

	/*
	 * @see SelectionStatusDialog#computeResult()
	 */
	protected void computeResult() {
		setResult(Arrays.asList(fViewer.getCheckedElements()));
	}

	private void parentCreate() {
		super.create();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.window.Window#create()
	 */
	public void create() {
		BusyIndicator.showWhile(null, new Runnable() {

			public void run() {
				parentCreate();
				fViewer.setCheckedElements(getInitialElementSelections()
						.toArray());
				fViewer.getTree().addTreeListener(new TreeAdapter() {

					@Override
					public void treeExpanded(TreeEvent e) {
						TreeItem item = (TreeItem) e.item;
						if (!item.getText().endsWith(")")) { //$NON-NLS-1$
							Object obj = item.getData();

							if (obj instanceof TableFolderNode
									|| obj instanceof ViewFolderNode
									|| obj instanceof ColumnFolderNode) {
								item.setText(item.getText()
										+ "(" + item.getItemCount() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
								fViewer.getTree().layout();
							}
						}
						super.treeExpanded(e);
					}

				});

				updateOKStatus();
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets
	 * .Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		Label messageLabel = createMessageArea(composite);

		Composite twoPartComp = new Composite(composite, 0);
		GridLayout layout = new GridLayout(2, true);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		twoPartComp.setLayout(layout);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true,
				true).applyTo(twoPartComp);

		CheckboxTreeViewer treeViewer = createFirstPart(twoPartComp);
		sTableViewer = createSecondPart(twoPartComp);

		Control buttonComposite = createSelectionButtons(composite);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.widthHint = convertWidthInCharsToPixels(fWidth);
		data.heightHint = convertHeightInCharsToPixels(fHeight);
		Tree treeWidget = treeViewer.getTree();
		treeWidget.setLayoutData(data);
		treeWidget.setFont(parent.getFont());
		if (fIsEmpty) {
			messageLabel.setEnabled(false);
			treeWidget.setEnabled(false);
			buttonComposite.setEnabled(false);
		}
		addCheckedListener();
		return composite;
	}

	/**
	 * Add the checked listener for treeviewer or tableviewer.
	 */
	protected void addCheckedListener() {

		// When user checks a checkbox in the tree, check all its children
		// fViewer.addCheckStateListener(new ICheckStateListener() {
		//
		// public void checkStateChanged(CheckStateChangedEvent event) {
		// // If the item is checked . . .
		// if (event.getChecked()) {
		// // . . . check all its children
		// fViewer.setSubtreeChecked(event.getElement(), true);
		// } else {
		// fViewer.setSubtreeChecked(event.getElement(), false);
		// }
		// }
		// });
	}

	/**
	 * DOC mzhao 2009-05-05 bug: 6587, Add connection metadata filter.
	 */

	private void addConnFilterListener() {
		AbstractViewerFilter connFiler = new AbstractViewerFilter() {
			@Override
			public int getId() {
				return 0;
			}

			@Override
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {
				if (element instanceof IFile) {
					IFile file = (IFile) element;
					Integer selectIndex = metadataFormPage.getConnCombo()
							.getSelectionIndex();
					Integer connectionIndex = null;
					Object value = metadataFormPage.getConnCombo().getData(
							file.getName());
					if (value != null && value instanceof Integer) {
						connectionIndex = (Integer) value;
					}
					if (connectionIndex != null
							&& selectIndex.intValue() == connectionIndex
									.intValue()) {
						return true;
					} else {
						return false;
					}
				}
				return true;
			}

		};
		addFilter(connFiler);
	}

	/**
	 * Creates the tree viewer.
	 * 
	 * @param parent
	 *            the parent composite
	 * @return the tree viewer
	 */
	protected CheckboxTreeViewer createFirstPart(Composite parent) {
		if (fContainerMode) {
			if (DIALOG_TYPE_TABLE == this.getDialogType()) {
				fViewer = new TableSelectionViewer(parent, SWT.BORDER);
			} else {
				fViewer = new ColumnSelectionViewer(parent, SWT.BORDER);
			}
			// {
			//
			// protected void handleTreeExpand(TreeEvent event) {
			// super.handleTreeExpand(event);
			// checkElementChecked();
			// }
			// };
		} else {
			fViewer = new CheckboxTreeViewer(parent, SWT.BORDER) {

				protected void handleTreeExpand(TreeEvent event) {
					super.handleTreeExpand(event);
					// checkElementChecked();
				}

			};
		}

		fViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		applyDialogFont(fViewer.getTree());

		fViewer.setContentProvider(fContentProvider);
		fViewer.setLabelProvider(fLabelProvider);
		fViewer.addCheckStateListener(new ICheckStateListener() {

			public void checkStateChanged(CheckStateChangedEvent event) {
				updateOKStatus();
			}
		});

		fViewer.setComparator(fComparator);
		if (fFilters != null) {
			for (int i = 0; i != fFilters.size(); i++) {
				fViewer.addFilter((ViewerFilter) fFilters.get(i));
			}
		}
		fViewer.setInput(fInput);
		// fViewer.
		fViewer.addSelectionChangedListener(this);
		return fViewer;
	}

	// protected void checkElementChecked() {
	// }

	protected CheckboxTableViewer createSecondPart(Composite parent) {
		CheckboxTableViewer viewer = CheckboxTableViewer.newCheckList(parent,
				SWT.MULTI | SWT.BORDER);
		viewer.setLabelProvider(this.sLabelProvider);
		viewer.setContentProvider(this.sContentProvider);
		viewer.addSelectionChangedListener(this);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true,
				true).applyTo(viewer.getTable());
		return viewer;
	}

	/**
	 * Returns the tree viewer.
	 * 
	 * @return the tree viewer
	 */
	protected CheckboxTreeViewer getTreeViewer() {
		return fViewer;
	}

	protected CheckboxTableViewer getTableViewer() {
		return sTableViewer;
	}

	/**
	 * Adds the selection and deselection buttons to the dialog.
	 * 
	 * @param composite
	 *            the parent composite
	 * @return Composite the composite the buttons were created in.
	 */
	protected Composite createSelectionButtons(Composite composite) {
		Composite buttonComposite = new Composite(composite, SWT.RIGHT);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		buttonComposite.setLayout(layout);
		buttonComposite.setFont(composite.getFont());
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_END
				| GridData.GRAB_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		composite.setData(data);
		Button selectButton = createButton(buttonComposite,
				IDialogConstants.SELECT_ALL_ID, "Select &All", false); //$NON-NLS-1$

		Button deselectButton = createButton(buttonComposite,
				IDialogConstants.DESELECT_ALL_ID, "&Deselect All", false); //$NON-NLS-1$

		addSelectionButtonListener(selectButton, deselectButton);
		return buttonComposite;
	}

	/**
	 * Add the listeners for all select(deselect) button.
	 * 
	 * @param selectButton
	 * @param deselectButton
	 */
	protected void addSelectionButtonListener(Button selectButton,
			Button deselectButton) {
		SelectionListener listener = new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				Object[] viewerElements = fContentProvider.getElements(fInput);
				if (fContainerMode) {
					fViewer.setCheckedElements(viewerElements);
				} else {
					for (int i = 0; i < viewerElements.length; i++) {
						fViewer.setSubtreeChecked(viewerElements[i], true);
					}
				}
				updateOKStatus();
			}
		};
		selectButton.addSelectionListener(listener);

		listener = new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				fViewer.setCheckedElements(new Object[0]);
				updateOKStatus();
			}
		};
		deselectButton.addSelectionListener(listener);
	}

	private boolean evaluateIfTreeEmpty(Object input) {
		Object[] elements = fContentProvider.getElements(input);
		if (elements.length > 0) {
			if (fFilters != null) {
				for (int i = 0; i < fFilters.size(); i++) {
					ViewerFilter curr = (ViewerFilter) fFilters.get(i);
					elements = curr.filter(fViewer, input, elements);
				}
			}
		}
		return elements.length == 0;
	}
}
