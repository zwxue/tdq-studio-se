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
package org.talend.dataprofiler.core;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;

/**
 * Store and lazy load Imaged. <br/>
 * 
 * $Id: ImageLib.java,v 1.5 2007/04/05 05:33:07 pub Exp $
 * 
 */
public class ImageLib {

    private static ImageRegistry imageRegistry;

    private static URL iconURL;

    // public static final String REFRESH_IMAGE = "refresh.gif"; //$NON-NLS-1$
    public static final String EXPLORE_IMAGE = "magnifier.png"; //$NON-NLS-1$

    public static final String REFRESH_IMAGE = "run_exc.gif"; //$NON-NLS-1$

    public static final String FOLDERNODE_IMAGE = "folder.gif"; //$NON-NLS-1$

    public static final String FOLDER_WIZ_IMAGE = "folder_wiz.gif"; //$NON-NLS-1$

    public static final String FOLDER_NEW_IMAGE = "folder_new.gif"; //$NON-NLS-1$

    public static final String PROJECT_ACTIVE = "prject_active.gif"; //$NON-NLS-1$

    public static final String REPORT_HISTORIZE = "report_go.png"; //$NON-NLS-1$

    public static final String NEW_CONNECTION = "new_alias.gif"; //$NON-NLS-1$

    public static final String DELETE_ACTION = "cross.png"; //$NON-NLS-1$

    public static final String ADD_ACTION = "add.gif"; //$NON-NLS-1$

    public static final String SECTION_PREVIEW = "eye.png"; //$NON-NLS-1$

    public static final String ACTION_NEW_REPORT = "report_add.png"; //$NON-NLS-1$

    public static final String ACTION_NEW_ANALYSIS = "chart_bar_add.png"; //$NON-NLS-1$

    public static final String INDICATOR_OPTION = "page_white_gear.png"; //$NON-NLS-1$

    public static final String ANALYSIS_OBJECT = "chart_bar.png"; //$NON-NLS-1$

    public static final String REPORT_OBJECT = "report.png"; //$NON-NLS-1$

    public static final String TD_COLUMN = "TdColumn.gif"; //$NON-NLS-1$

    public static final String TD_DATAPROVIDER = "TdDataProvider.gif"; //$NON-NLS-1$

    public static final String EDITCONNECTION = "sample.gif"; //$NON-NLS-1$

    public static final String CREATE_SQL_ACTION = "new_sqlEditor.gif"; //$NON-NLS-1$

    public static final String LICENSE_WIZ = "license_wiz.png"; //$NON-NLS-1$

    public static final String PATTERN_REG = "pattern.png"; //$NON-NLS-1$

    public static final String REGISTER_WIZ = "register_wiz.png"; //$NON-NLS-1$

    public static final String CONNECTION = "connection.gif"; //$NON-NLS-1$

    public static final String METADATA = "metadata.png"; //$NON-NLS-1$

    public static final String OPTION = "option.png"; //$NON-NLS-1$

    public static final String LIBRARIES = "libraries.png"; //$NON-NLS-1$

    public static final String DATA_PROFILING = "server_chart.png"; //$NON-NLS-1$

    public static final String EXPORT_REPORT = "export_rep.gif"; //$NON-NLS-1$

    public static final String LEVEL_WARNING = "level_warning.gif"; //$NON-NLS-1$

    public static final String EMOTICON_SMILE = "emoticon_smile.png"; //$NON-NLS-1$

    public static final String EXCLAMATION = "exclamation.png"; //$NON-NLS-1$

    public static final String CATALOG = "catalog.jpg"; //$NON-NLS-1$

    /**
     * get <code>ImageDescriptor</code> with special imageName.
     * 
     * @param imageName
     * @return
     */
    public static ImageDescriptor getImageDescriptor(String imageName) {
        if (imageRegistry == null) {
            initialize();
        }
        ImageDescriptor imageDesc = imageRegistry.getDescriptor(imageName);
        if (imageDesc == null) {
            addImage(imageName);
            return imageRegistry.getDescriptor(imageName);
        }
        return imageDesc;
    }

    /**
     * get <code>Image</code> with special imageName.
     * 
     * @param imageName
     * @return
     */
    public static Image getImage(String imageName) {
        if (imageRegistry == null) {
            initialize();
        }
        if (imageRegistry == null) {
            return null;
        }
        Image image = imageRegistry.get(imageName);
        if (image == null) {
            addImage(imageName);
            return imageRegistry.get(imageName);
        }
        return image;
    }

    /**
     * initialize the fieds.
     */
    static void initialize() {
        CorePlugin amcPlugin = CorePlugin.getDefault();
        if (amcPlugin != null) {
            imageRegistry = amcPlugin.getImageRegistry();
            iconURL = getIconLocation();
        }
    }

    /**
     * get current icons URL.
     * 
     * @return
     */
    private static URL getIconLocation() {
        URL installURL = CorePlugin.getDefault().getBundle().getEntry("/"); //$NON-NLS-1$
        try {
            return new URL(installURL, "icons/"); //$NON-NLS-1$
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * store the image with special name(the name with suffix,such as "sample.gif").
     * 
     * @param iconName
     */
    public static void addImage(String iconName) {
        try {
            ImageDescriptor descriptor = ImageDescriptor.createFromURL(new URL(iconURL, iconName));
            imageRegistry.put(iconName, descriptor);
        } catch (MalformedURLException e) {
            // skip, but try to go on to the next one...
        }
    }

}
