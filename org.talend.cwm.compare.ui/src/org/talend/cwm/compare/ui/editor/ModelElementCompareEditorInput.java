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
package org.talend.cwm.compare.ui.editor;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareViewerPane;
import org.eclipse.compare.Splitter;
import org.eclipse.compare.structuremergeviewer.ICompareInput;
import org.eclipse.compare.structuremergeviewer.ICompareInputChangeListener;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.compare.diff.metamodel.DiffModel;
import org.eclipse.emf.compare.diff.metamodel.ModelInputSnapshot;
import org.eclipse.emf.compare.match.metamodel.MatchModel;
import org.eclipse.emf.compare.ui.ModelCompareInput;
import org.eclipse.emf.compare.ui.viewer.structure.ModelStructureMergeViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.talend.cwm.compare.ui.actions.provider.CompareModelStructureLabelProvider;
import org.talend.cwm.compare.ui.views.CompareModelContentMergeViewer;

/**
 * 
 * DOC mzhao class global comment. Detailled comment
 */
@SuppressWarnings("restriction")
public class ModelElementCompareEditorInput extends CompareEditorInput {

    private Object selectedObject = null;

    /**
     * Content merge viewer of this {@link CompareViewerPane}. It represents the bottom splitted part of the view.
     */
    protected CompareModelContentMergeViewer contentMergeViewer;

    /**
     * Structure merge viewer of this {@link CompareViewerPane}. It represents the top {@link TreeViewer} of the view.
     */
    protected ModelStructureMergeViewer structureMergeViewer;

    /** {@link DiffModel} result of the underlying comparison. */
    private final DiffModel diff;

    /**
     * This listener will be in charge of updating the {@link CompareModelContentMergeViewer} and
     * {@link ModelStructureMergeViewer}'s input.
     */
    private final ICompareInputChangeListener inputListener;

    /** {@link ModelInputSnapshot} result of the underlying comparison. */
    private final ModelInputSnapshot inputSnapshot;

    /** {@link MatchModel} result of the underlying comparison. */
    private final MatchModel match;

    /**
     * This constructor takes a {@link ModelInputSnapshot} as input.
     * 
     * @param snapshot The {@link ModelInputSnapshot} loaded from an emfdiff.
     */
    public ModelElementCompareEditorInput(ModelInputSnapshot snapshot, CompareConfiguration comConf, Object selObj) {
        super(comConf);
        diff = snapshot.getDiff();
        match = snapshot.getMatch();
        inputSnapshot = snapshot;
        selectedObject = selObj;
        inputListener = new ICompareInputChangeListener() {

            public void compareInputChanged(ICompareInput source) {
                structureMergeViewer.setInput(source);
                contentMergeViewer.setInput(source);
            }
        };
    }
    public void hookLeftPanelContextMenu() {
    	contentMergeViewer.hookContextMenu();
	}

    /**
     * {@inheritDoc}
     * 
     * @see CompareEditorInput#createContents(Composite)
     */
    @Override
    public Control createContents(Composite parent) {
        final Splitter fComposite = new Splitter(parent, SWT.VERTICAL);

        createOutlineContents(fComposite, SWT.HORIZONTAL);

        final CompareViewerPane pane = new CompareViewerPane(fComposite, SWT.NONE);

        contentMergeViewer = new CompareModelContentMergeViewer(pane,
				getCompareConfiguration(), selectedObject);
        pane.setContent(contentMergeViewer.getControl());

        contentMergeViewer.setInput(inputSnapshot);

        final int structureWeight = 30;
        final int contentWeight = 70;
        fComposite.setWeights(new int[] { structureWeight, contentWeight });

        return fComposite;
    }

    /**
     * {@inheritDoc}
     * 
     * @see CompareEditorInput#createOutlineContents(Composite, int)
     */
    @Override
    public Control createOutlineContents(Composite parent, int direction) {
        final Splitter splitter = new Splitter(parent, direction);

        final CompareViewerPane pane = new CompareViewerPane(splitter, SWT.NONE);

        structureMergeViewer = new ModelStructureMergeViewer(pane, getCompareConfiguration());
        structureMergeViewer.setLabelProvider(new CompareModelStructureLabelProvider());
        pane.setContent(structureMergeViewer.getTree());

        structureMergeViewer.setInput(inputSnapshot);

        return splitter;
    }

    /**
     * {@inheritDoc}
     * 
     * @see CompareEditorInput#prepareInput(IProgressMonitor)
     */
    @Override
    protected Object prepareInput(IProgressMonitor monitor) {
        final ModelCompareInput input = new ModelCompareInput(match, diff);
        input.addCompareInputChangeListener(inputListener);
        return input;
    }

}
