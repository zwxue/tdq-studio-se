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
package org.talend.dataprofiler.core.model.nodes.foldernode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.talend.cwm.helper.ResourceHelper;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataquality.helpers.IndicatorCategoryHelper;
import org.talend.dataquality.indicators.definition.DefinitionFactory;
import org.talend.dataquality.indicators.definition.IndicatorCategory;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.dq.nodes.foldernode.AbstractFolderNode;
import org.talend.resource.ResourceManager;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class IndicatorFolderNode extends AbstractFolderNode implements IWorkbenchAdapter {
    
    private static final String CATEGORY_OTHER = "Others";
    
    private static IndicatorCategory indicatorCategory;

    public static IndicatorCategory getIndicatorCategory() {
        if (indicatorCategory == null) {
            indicatorCategory = DefinitionFactory.eINSTANCE.createIndicatorCategory();
            indicatorCategory.setLabel(CATEGORY_OTHER);
            indicatorCategory.setName(CATEGORY_OTHER);
        }
        return indicatorCategory;
    }

    private static Map<IndicatorCategory, List<IndicatorDefinition>> categoriesIDMaps;
    
    private static Map<IndicatorCategory, List<IndicatorDefinition>> getCategoriesIDMaps() {
        if (categoriesIDMaps == null) {
            categoriesIDMaps = buildCategories();
        }
        return categoriesIDMaps;
    }

    public static List<IndicatorDefinition> getIndicatorDefinitionList(IndicatorCategory category) {
        if (category != null && getCategoriesIDMaps() != null) {
            return getCategoriesIDMaps().get(category);
        }
        return null;
    }

    private static List<String> shouldNotIncludeIndicatorDefinitionUuidList;
    
    public static List<String> getShouldNotIncludeIndicatorDefinitionUuidList() {
        if (shouldNotIncludeIndicatorDefinitionUuidList == null) {
            shouldNotIncludeIndicatorDefinitionUuidList = new ArrayList<String>();
            // Overview \/
            shouldNotIncludeIndicatorDefinitionUuidList.add("_nZEo8MYSEd27NP4lvE0A4w"); // Connection Overview //$NON-NLS-1$
            shouldNotIncludeIndicatorDefinitionUuidList.add("_QwDiwMYUEd27NP4lvE0A4w"); // Catalog Overview //$NON-NLS-1$
            shouldNotIncludeIndicatorDefinitionUuidList.add("_V4SA0MYUEd27NP4lvE0A4w"); // Schema Overview //$NON-NLS-1$
            shouldNotIncludeIndicatorDefinitionUuidList.add("_hgO7YMYUEd27NP4lvE0A4w"); // Table Overview //$NON-NLS-1$
            shouldNotIncludeIndicatorDefinitionUuidList.add("_lNIE0MbNEd2d_JPxxDRSfQ"); // View Overview //$NON-NLS-1$
            // composite \/
            //            shouldNotIncludeIndicatorDefinitionUuidList.add("_ccHq1BF2Ed2PKb6nEJEvhw"); // Minimal Length //$NON-NLS-1$
            //            shouldNotIncludeIndicatorDefinitionUuidList.add("_ccHq1RF2Ed2PKb6nEJEvhw"); // Maximal Length //$NON-NLS-1$
            //            shouldNotIncludeIndicatorDefinitionUuidList.add("_ccIR4BF2Ed2PKb6nEJEvhw"); // Average Length //$NON-NLS-1$

            shouldNotIncludeIndicatorDefinitionUuidList.add("_ccJgAhF2Ed2PKb6nEJEvhw"); // SUM //$NON-NLS-1$
            shouldNotIncludeIndicatorDefinitionUuidList.add("_vf0k4PkbEd2z55b7dTkWFw"); // Multiple Column Correlation //$NON-NLS-1$
            shouldNotIncludeIndicatorDefinitionUuidList.add("_UUIyoCOMEd6YB57jaCfKaA"); // DQ Rule //$NON-NLS-1$
            shouldNotIncludeIndicatorDefinitionUuidList.add("_10gx4JxcEd2YicTszQEJLA"); // Multiple Column Frequency //$NON-NLS-1$
                                                                                        // Table
            // Summary Statistics \/
            // shouldNotIncludeIndicatorDefinitionUuidList.add("_ccI48RF2Ed2PKb6nEJEvhw"); // Mean
            // shouldNotIncludeIndicatorDefinitionUuidList.add("_ccI48hF2Ed2PKb6nEJEvhw"); // Median
            // shouldNotIncludeIndicatorDefinitionUuidList.add("_ccI48xF2Ed2PKb6nEJEvhw"); // Inter Quartile Range
            // shouldNotIncludeIndicatorDefinitionUuidList.add("_ccI49BF2Ed2PKb6nEJEvhw"); // Lower Quartile
            // shouldNotIncludeIndicatorDefinitionUuidList.add("_ccI49RF2Ed2PKb6nEJEvhw"); // Upper Quartile
            // shouldNotIncludeIndicatorDefinitionUuidList.add("_ccI49hF2Ed2PKb6nEJEvhw"); // Range
            // shouldNotIncludeIndicatorDefinitionUuidList.add("_ccJgABF2Ed2PKb6nEJEvhw"); // Minimum
            // shouldNotIncludeIndicatorDefinitionUuidList.add("_ccJgARF2Ed2PKb6nEJEvhw"); // Maximum
        }
        return shouldNotIncludeIndicatorDefinitionUuidList;
    }

    // private static List<IndicatorDefinition> showIndicatorDefinitions;

    private static List<IndicatorDefinition> getShowIndicatorsDefinitions() {
        // if (showIndicatorDefinitions == null) {
        ArrayList<IndicatorDefinition> showIndicatorDefinitions = new ArrayList<IndicatorDefinition>();

            EList<IndicatorDefinition> indicatorDefinitions = DefinitionHandler.getInstance().getIndicatorsDefinitions()
                    .getIndicatorDefinitions();

            for (IndicatorDefinition indicatorDefinition : indicatorDefinitions) {
                if (!indicatorDefinition.getSqlGenericExpression().isEmpty()
                        && !getShouldNotIncludeIndicatorDefinitionUuidList()
                                .contains(ResourceHelper.getUUID(indicatorDefinition))) {
                    showIndicatorDefinitions.add(indicatorDefinition);
                }
            }

        // }
        return showIndicatorDefinitions;
    }

    /**
     * DOC bZhou IndicatorFolderNode constructor comment.
     * 
     * @param name
     */
    public IndicatorFolderNode(String name) {
        super(name);
        setParent(ResourceManager.getLibrariesFolder().getFolder("Indicators")); //$NON-NLS-1$
        loadChildren();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.nodes.foldernode.AbstractFolderNode#loadChildren()
     */
    @Override
    public void loadChildren() {
        if (getCategoriesIDMaps() != null) {
            setChildren(getCategoriesIDMaps().keySet().toArray(new IndicatorCategory[getCategoriesIDMaps().keySet().size()]));
        }
    }

    /**
     * DOC xqliu Comment method "buildCategories".
     * @return
     */
    private static Map<IndicatorCategory, List<IndicatorDefinition>> buildCategories() {
        Map<IndicatorCategory, List<IndicatorDefinition>> categories = new HashMap<IndicatorCategory, List<IndicatorDefinition>>();

        for (IndicatorDefinition indicatorDefinition : getShowIndicatorsDefinitions()) {
            IndicatorCategory category = IndicatorCategoryHelper.getCategory(indicatorDefinition);
            if (category == null) {
                category = getIndicatorCategory();
            }
            if (category != null) {
                List<IndicatorDefinition> list = categories.get(category);
                if (list == null) {
                    List<IndicatorDefinition> tempList = new ArrayList<IndicatorDefinition>();
                    tempList.add(indicatorDefinition);
                    categories.put(category, tempList);
                } else {
                    list.add(indicatorDefinition);
                }
            }
        }

        return categories;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.model.IWorkbenchAdapter#getChildren(java.lang.Object)
     */
    public Object[] getChildren(Object o) {
        return getChildren();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.model.IWorkbenchAdapter#getImageDescriptor(java.lang.Object)
     */
    public ImageDescriptor getImageDescriptor(Object object) {
        return ImageLib.getImageDescriptor(ImageLib.FOLDERNODE_IMAGE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.model.IWorkbenchAdapter#getLabel(java.lang.Object)
     */
    public String getLabel(Object o) {
        return getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.model.IWorkbenchAdapter#getParent(java.lang.Object)
     */
    public Object getParent(Object o) {
        return getParent();
    }

}
