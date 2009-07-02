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
package org.talend.dataprofiler.core;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;
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
public final class ImageLib {

	protected static Logger log = Logger.getLogger(ImageLib.class);

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

	public static final String ASC_SORT = "asc.gif"; //$NON-NLS-1$

	public static final String DESC_SORT = "desc.gif"; //$NON-NLS-1$

	public static final String SCHEMA = "schema.gif"; //$NON-NLS-1$

	public static final String TABLE = "TdTable.gif"; //$NON-NLS-1$

	public static final String VIEW = "view.gif"; //$NON-NLS-1$

	public static final String DQ_RULE = "dqrule_red.png"; //$NON-NLS-1$

	public static final String ADD_DQ = "add_dqrule.png";//$NON-NLS-1$

	public static final String ADD_PATTERN = "add_pattern.png"; //$NON-NLS-1$

    public static final String SAVE = "save.gif";

    /** PK icon from SQL Explorer. */
	public static final String PK_DECORATE = "pk_decorate.gif"; //$NON-NLS-1$

	public static final String EDIT_COPY = "copy.gif"; //$NON-NLS-1$

	/** index icon from SQL Explorer. */
	public static final String INDEX_VIEW = "index.gif"; //$NON-NLS-1$

	/** Collapse all icon. */
	public static final String COLLAPSE_ALL = "collapseall.gif"; //$NON-NLS-1$

	/** Expand all icon. */
	public static final String EXPAND_ALL = "expandall.gif"; //$NON-NLS-1$

	/** Icon for primary key. */
	public static final String PK_COLUMN = "pkColumn.gif"; //$NON-NLS-1$

	/** Icon for refresh workspace. */
	public static final String REFRESH_SPACE = "refresh.gif"; //$NON-NLS-1$

	/** Icon for Talend Exchange folder. */
	public static final String EXCHANGE = "ecosystem_view.gif";

	/** Icon for Pagination. */
	public static final String ICON_PAGE_LAST_LNK = "bottomb.gif";
	public static final String ICON_PAGE_FIRST_LNK = "topb.gif";
	public static final String ICON_PAGE_PREV_LNK = "prevb.gif";
	public static final String ICON_PAGE_NEXT_LNK = "nextb.gif";
	public static final String ICON_INFO = "info.gif";

	/**
	 * DOC bzhou ImageLib constructor comment.
	 */
	private ImageLib() {

	}

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
			log.error(e, e);
			return null;
		}
	}

	/**
	 * store the image with special name(the name with suffix,such as
	 * "sample.gif").
	 * 
	 * @param iconName
	 */
	public static void addImage(String iconName) {
		try {
			ImageDescriptor descriptor = ImageDescriptor.createFromURL(new URL(
					iconURL, iconName));
			imageRegistry.put(iconName, descriptor);
		} catch (MalformedURLException e) {
			// skip, but try to go on to the next one...
		}
	}

	/**
	 * DOC bzhou ImageLib class global comment. Detailled comment
	 */
	public enum CWMImageEnum {
		Connection(
				DefaultMessagesImpl.getString("ImageLib.connection"), getImage(CONNECTION)), //$NON-NLS-1$
		Catalog(
				DefaultMessagesImpl.getString("ImageLib.catalog"), getImage(CATALOG)), //$NON-NLS-1$
		Schema(
				DefaultMessagesImpl.getString("ImageLib.schema"), getImage(SCHEMA)), //$NON-NLS-1$
		Table(DefaultMessagesImpl.getString("ImageLib.table"), getImage(TABLE)), //$NON-NLS-1$
		View(DefaultMessagesImpl.getString("ImageLib.view"), getImage(VIEW)), //$NON-NLS-1$
		Column(
				DefaultMessagesImpl.getString("ImageLib.column"), getImage(TD_COLUMN)); //$NON-NLS-1$

		private String label;

		private Image img;

		private CWMImageEnum(String label, Image img) {
			this.label = label;
			this.img = img;
		}

		public Image getImg() {
			return img;
		}

		public String getLabel() {
			return label;
		}

		public static Image getImageByLabel(String label) {
			for (CWMImageEnum cwmImage : values()) {
				if (cwmImage.getLabel().equalsIgnoreCase(label)) {
					return cwmImage.getImg();
				}
			}

			return null;
		}
	}

}
