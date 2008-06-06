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
package org.talend.dataprofiler.core.ui.utils;

import java.util.Comparator;

import org.eclipse.core.resources.IFile;
import org.talend.dataprofiler.core.factory.ModelElementFileFactory;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * The factory to build comparator.
 */
public final class ComparatorsFactory {

    private ComparatorsFactory() {
    }

    public static final int FILEMODEL_COMPARATOR_ID = 0;

    public static final int MODELELEMENT_COMPARATOR_ID = 1;

    @SuppressWarnings("unchecked")
    public static Comparator buildComparator(int comparatorId) {
        switch (comparatorId) {
        case FILEMODEL_COMPARATOR_ID:
            return new FileModelComparator();
        case MODELELEMENT_COMPARATOR_ID:
            return new ModelElementComparator();
        default:
            return new ModelElementComparator();
        }
    }

    /**
     * Implements Comparator to implement custom sorting of {@link IFile} which contains {@link ModelElement} elements.
     */
    static class FileModelComparator implements Comparator<IFile> {

        public int compare(IFile arg0, IFile arg1) {

            if (arg0 == null || arg1 == null) {
                return 0;
            }
            ModelElement modelElement0 = ModelElementFileFactory.getModelElement(arg0);
            ModelElement modelElement1 = ModelElementFileFactory.getModelElement(arg1);
            if (modelElement0 == null || modelElement1 == null) {
                return 0;
            }
            String name0 = modelElement0.getName();
            String name1 = modelElement1.getName();

            if (name0 == null || name1 == null) {
                return 0;
            }

            return name0.compareTo(name1);
        }

    }

    /**
     * Implements Comparator to implement custom sorting of {@link ModelElement}.
     */
    static class ModelElementComparator implements Comparator<Object> {

        public int compare(Object arg0, Object arg1) {

            if (arg0 == null || arg1 == null) {
                return 0;
            }
            String name0 = ((ModelElement) arg0).getName();
            String name1 = ((ModelElement) arg1).getName();

            if (name0 == null || name1 == null) {
                return 0;
            }

            return name0.compareTo(name1);
        }
    }
}
