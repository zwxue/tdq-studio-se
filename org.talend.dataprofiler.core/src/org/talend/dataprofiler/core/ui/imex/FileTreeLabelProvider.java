// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.imex;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.talend.commons.emf.EMFUtil;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.ui.imex.model.ItemRecord;
import org.talend.resource.EResourceConstant;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class FileTreeLabelProvider extends LabelProvider {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
     */
    @Override
    public Image getImage(Object element) {
        Image image = null;
        if (element instanceof ItemRecord) {
            ItemRecord record = (ItemRecord) element;
            File file = record.getFile();
            String fileName = file.getName();
            if (file.isDirectory()) {
                image = ImageLib.getImage(ImageLib.FOLDERNODE_IMAGE);

                EResourceConstant constant = resolveResourceConstant(fileName);
                if (constant != null) {
                    switch (constant) {
                    case DATA_PROFILING:
                        image = ImageLib.getImage(ImageLib.DATA_PROFILING);
                        break;
                    case METADATA:
                        image = ImageLib.getImage(ImageLib.METADATA);
                        break;
                    case LIBRARIES:
                        image = ImageLib.getImage(ImageLib.LIBRARIES);
                        break;
                    case ANALYSIS:
                        break;
                    case REPORTS:
                        break;
                    case EXCHANGE:
                        image = ImageLib.getImage(ImageLib.EXCHANGE);
                        break;
                    case DB_CONNECTIONS:
                    case MDM_CONNECTIONS:
                        image = ImageLib.getImage(ImageLib.CONNECTION);
                        break;

                    default:
                        break;
                    }
                }
            } else {
                if (fileName.endsWith(FactoriesUtil.ANA)) {
                    image = ImageLib.getImage(ImageLib.ANALYSIS_OBJECT);
                } else if (fileName.endsWith(FactoriesUtil.REP)) {
                    image = ImageLib.getImage(ImageLib.REPORT_OBJECT);
                } else if (fileName.endsWith(FactoriesUtil.PATTERN)) {
                    image = ImageLib.getImage(ImageLib.PATTERN_REG);
                } else if (fileName.endsWith(FactoriesUtil.DQRULE)) {
                    image = ImageLib.getImage(ImageLib.DQ_RULE);
                } else if (fileName.endsWith(FactoriesUtil.PROV)) {
                    image = ImageLib.getImage(ImageLib.TD_DATAPROVIDER);
                } else if (fileName.endsWith(FactoriesUtil.UDI)) {
                    image = ImageLib.getImage(ImageLib.IND_DEFINITION);
                }
            }
        }

        return image != null ? image : super.getImage(element);
    }

    /**
     * DOC bZhou Comment method "resolveResourceConstant".
     * 
     * @param fileName
     * @return
     */
    private EResourceConstant resolveResourceConstant(String fileName) {
        for (EResourceConstant constant : EResourceConstant.values()) {
            if (StringUtils.equals(constant.getName(), fileName)) {
                return constant;
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
     */
    @Override
    public String getText(Object element) {
        if (element instanceof ItemRecord) {
            ItemRecord recored = (ItemRecord) element;
            File file = recored.getFile();
            String fileExtension = new Path(file.getName()).getFileExtension();
            if (file.isFile() && FactoriesUtil.isEmfFile(fileExtension)) {
                EMFUtil emfUtil = new EMFUtil();
                URI uri = URI.createFileURI(file.getAbsolutePath());
                Resource resource = emfUtil.getResourceSet().getResource(uri, true);
                EList<EObject> contents = resource.getContents();
                if (contents != null && !contents.isEmpty()) {
                    EObject eObject = contents.get(0);
                    if (eObject instanceof ModelElement) {
                        return ((ModelElement) eObject).getName();
                    }
                }
            }

            return file.getName();

        }

        return super.getText(element);
    }

}
