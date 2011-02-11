// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.ecos.jobs;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.talend.dataprofiler.ecos.model.IEcosCategory;
import org.talend.dataprofiler.ecos.model.IEcosComponent;
import org.talend.dataprofiler.ecos.model.IRevision;
import org.talend.dataprofiler.ecos.model.RevisionInfo;
import org.talend.dataprofiler.ecos.model.impl.EcosComponent;
import org.talend.dataprofiler.ecos.model.impl.Revision;
import org.talend.dataprofiler.ecos.service.EcosystemService;

/**
 * Search for component extensions.
 */
public class ComponentSearcher {

    protected static Logger log = Logger.getLogger(ComponentSearcher.class);

    private static final String RELEASE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"; //$NON-NLS-1$

    private static DateFormat formatter = new SimpleDateFormat(RELEASE_DATE_FORMAT);

    private static List<IEcosCategory> ecosCategories;

    // private static List<IEcosComponent> extensions = new ArrayList<IEcosComponent>();;

    /**
     * DOC bZhou Comment method "getAvailableCategory".
     * 
     * @param version
     * @return empty list if no any category.
     * @throws Exception
     */
    public static List<IEcosCategory> getAvailableCategory(String version) throws Exception {
        if (ecosCategories == null) {
            ecosCategories = EcosystemService.getCategoryList(version);
        }
        return ecosCategories;
    }

    /**
     * Find available components.
     * 
     * @param version The tos version.
     * @param language The project language.
     * @return
     */
    public static List<IEcosComponent> getAvailableComponentExtensions(String version, IEcosCategory categry) {
        return getAvailableComponentExtensions(version, categry, false);
    }

    /**
     * DOC bZhou Comment method "getAvailableComponentExtensions".
     * 
     * @param version
     * @param categry
     * @param reload
     * @return
     */
    public static List<IEcosComponent> getAvailableComponentExtensions(String version, IEcosCategory categry, boolean reload) {

        List<IEcosComponent> extensions = new ArrayList<IEcosComponent>();

        try {
            List<RevisionInfo> revisions = EcosystemService.getRevisionList(categry.getId(), version);

            Map<String, IEcosComponent> extensionsMap = new HashMap<String, IEcosComponent>();

            for (RevisionInfo revision : revisions) {
                IEcosComponent extension = extensionsMap.get(revision.getExtensOnName());
                if (extension == null) {
                    extension = new EcosComponent();
                    extension.setName(revision.getExtensOnName());
                    extension.setAuthor(revision.getAuthorName());
                    extension.setCategry(categry);
                    extension.setDescription(revision.getExtensionDescription());

                    extensionsMap.put(extension.getName(), extension);
                    extensions.add(extension);
                }

                IRevision rev = convertRevision(revision);
                extension.getRevisions().add(rev);
                if (extension.getLatestRevision() == null || extension.getLatestRevision().getDate().before(rev.getDate())) {
                    // assumes that the revision with latest release date is the newest one.
                    extension.setLatestRevision(rev);
                }
            }
        } catch (Exception e) {
            log.error(e, e);
        }

        return extensions;

    }

    /**
     * Convert the web service returned value to our model object.
     * 
     * @param revision The message returned from web service method call.
     * @return
     * @throws ParseException
     */
    private static IRevision convertRevision(RevisionInfo revision) throws ParseException {
        IRevision rev = new Revision();
        rev.setDate(formatter.parse(revision.getRevisionDate()));
        rev.setName(revision.getRevisionName());
        rev.setUrl(revision.getDownloadUrl());
        rev.setDescription(revision.getRevisionDescription());
        rev.setId(revision.getRevisionId());
        rev.setFileName(revision.getFilename());
        return rev;
    }

    /**
     * Find the components that have been installed.
     * 
     * @param components
     * @return
     */
    public static List<IEcosComponent> getInstalledExtensions(List<IEcosComponent> components) {
        List<IEcosComponent> installed = new ArrayList<IEcosComponent>();
        for (IEcosComponent component : components) {
            if (component.getInstalledLocation() != null) {
                installed.add(component);
            }
        }
        return installed;

    }
}
