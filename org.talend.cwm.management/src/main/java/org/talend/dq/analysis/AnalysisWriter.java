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
package org.talend.dq.analysis;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.commons.emf.EMFSharedResources;
import org.talend.commons.emf.EMFUtil;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.helpers.AnalysisHelper;
import org.talend.dataquality.helpers.DomainHelper;
import org.talend.i18n.Messages;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;

/**
 * @author scorreia
 * 
 * This class saves the analysis.
 */
public class AnalysisWriter {

    private static Logger log = Logger.getLogger(AnalysisWriter.class);

    public static final String VALID_EXTENSION = FactoriesUtil.ANA;

    /**
     * Method "save".
     * 
     * @param analysis the analysis to save
     * @return true if no problem, false with a message if a problem occured.
     */
    public ReturnCode save(Analysis analysis) {
        assert analysis != null : "No analysis to save (null)";
        ReturnCode rc = new ReturnCode();
        Resource resource = analysis.eResource();
        if (resource == null) {
            rc.setReturnCode(Messages.getString("AnalysisWriter.Error",//$NON-NLS-1$
                    analysis.getName()), false);
            return rc;
        }
        // --- store descriptions (description and purpose) in the same resource
        EList<EObject> resourceContents = resource.getContents();
        // resourceContents.addAll(analysis.getDescription());
        // --- store the data filter in the same resource
        EList<Domain> dataFilter = AnalysisHelper.getDataFilter(analysis);

        // remove any existing domain (and replace by the new domain)
        List<Domain> domains = DomainHelper.getDomains(resourceContents);
        resourceContents.removeAll(domains);

        if (dataFilter != null) {
            // scorreia save them in their own file? -> no, it's ok to save them in the analysis file.
            for (Domain domain : dataFilter) {
                if (!resourceContents.contains(domain)) {
                    resourceContents.add(domain);
                }
            }
        }

        // save the resource and related resources (when needed, for example when we change the data mining type of a
        // column)
        boolean saved = EMFUtil.saveResource(resource);

        if (!saved) {
            rc.setReturnCode(Messages.getString("AnalysisWriter.ProblemSavingAnalysis", analysis.getName()), saved); //$NON-NLS-1$
        }
        return rc;

    }

    /**
     * Method "save" writes the analysis in the given file. If the file already exists, it is overridden. This method
     * should not be for saving an analysis already saved. In this case, use save(Analysis).
     * 
     * @param analysis the analysis to save
     * @param file the file in which the analysis will be save
     * @return whether everything is ok
     */
    protected ReturnCode save(Analysis analysis, IFile file) {
        assert file != null : "Cannot save analysis: No file name given.";
        assert analysis != null : "No analysis to save (null)";

        ReturnCode rc = new ReturnCode();
        if (!checkFileExtension(file)) {
            rc.setReturnCode(Messages.getString("AnalysisWriter.BadFileExtension", file.getFullPath(), VALID_EXTENSION), false); //$NON-NLS-1$
            return rc;
        }
        EMFSharedResources util = EMFSharedResources.getInstance();

        boolean added = util.addEObjectToResourceSet(file.getFullPath().toString(), analysis);

        if (!added) {
            rc.setReturnCode(Messages.getString("AnalysisWriter.AnalysisNotSave", util.getLastErrorMessage()), added); //$NON-NLS-1$
            return rc;
        }

        return save(analysis);
    }

    /**
     * Method "createAnalysisFile" creates a new file (or overwrite the existing file) with the analysis data. This
     * method must not be used to update an existing analysis. The file is created by
     * {@link DqRepositoryViewService#createFilename(String, String, String)}.
     * 
     * @param analysis the analysis to save
     * @param folder the folder where to save the analysis
     * @return the return code and the created file.
     */
    public TypedReturnCode<IFile> createAnalysisFile(Analysis analysis, IFolder folder) {
        assert analysis != null;
        TypedReturnCode<IFile> rc = new TypedReturnCode<IFile>();
        String filename = DqRepositoryViewService.createFilename(analysis.getName(), FactoriesUtil.ANA);
        IFile file = folder.getFile(filename);
        // File file = new File(filename);
        if (file.exists()) {
            rc.setReturnCode(Messages.getString("AnalysisWriter.CannotSaveAnalysis", analysis.getName(), filename), false); //$NON-NLS-1$
            return rc;
        }
        ReturnCode saved = save(analysis, file);
        if (saved.isOk()) {
            if (log.isDebugEnabled()) {
                log.debug("Saved in  " + file.getFullPath().toString());
            }
            rc.setObject(file);
            analysis.setFileName(file.getFullPath().toString());
        } else {
            rc.setReturnCode(Messages.getString("AnalysisWriter.FailedToSaveAnalysis", analysis.getName(), filename), false); //$NON-NLS-1$
        }
        return rc;
    }

    private boolean checkFileExtension(IFile file) {
        return file.getFileExtension().equalsIgnoreCase(VALID_EXTENSION);
    }
}
