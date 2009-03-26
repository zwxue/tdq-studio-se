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
package org.talend.dataprofiler.ecos.model.impl;

import java.util.Date;

import org.talend.dataprofiler.ecos.model.IEcosComponent;
import org.talend.dataprofiler.ecos.model.IRevision;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class Revision implements IRevision {

    private IEcosComponent component;

    private Date date;

    private String description;

    private String fileName;

    private int id;

    private String name;

    private String url;

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.ecos.model.IRevision#getComponent()
     */
    @Override
    public IEcosComponent getComponent() {
        return component;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.ecos.model.IRevision#getDate()
     */
    @Override
    public Date getDate() {
        return date;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.ecos.model.IRevision#getDescription()
     */
    @Override
    public String getDescription() {
        return description;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.ecos.model.IRevision#getFileName()
     */
    @Override
    public String getFileName() {
        return fileName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.ecos.model.IRevision#getId()
     */
    @Override
    public int getId() {
        return id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.ecos.model.IRevision#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.ecos.model.IRevision#getUrl()
     */
    @Override
    public String getUrl() {
        return url;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.ecos.model.IRevision#setComponent(org.talend.dataprofiler.ecos.model.IEcosComponent)
     */
    @Override
    public void setComponent(IEcosComponent value) {
        this.component = value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.ecos.model.IRevision#setDate(java.util.Date)
     */
    @Override
    public void setDate(Date value) {
        this.date = value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.ecos.model.IRevision#setDescription(java.lang.String)
     */
    @Override
    public void setDescription(String value) {
        this.description = value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.ecos.model.IRevision#setFileName(java.lang.String)
     */
    @Override
    public void setFileName(String value) {
        this.fileName = value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.ecos.model.IRevision#setId(int)
     */
    @Override
    public void setId(int value) {
        this.id = value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.ecos.model.IRevision#setName(java.lang.String)
     */
    @Override
    public void setName(String value) {
        this.name = value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.ecos.model.IRevision#setUrl(java.lang.String)
     */
    @Override
    public void setUrl(String value) {
        this.url = value;
    }

}
