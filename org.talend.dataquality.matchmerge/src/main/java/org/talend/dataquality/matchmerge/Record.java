/*
 * Copyright (C) 2006-2013 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package org.talend.dataquality.matchmerge;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Record {

    private final List<Attribute> attributes;

    private final String id;

    private String groupId;

    private Set<String> relatedIds = new HashSet<String>();

    public Record(String id) {
        this.id = id;
        this.attributes = new ArrayList<Attribute>();
    }

    public Record(List<Attribute> attributes, String id) {
        this.attributes = attributes;
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getId() {
        return id;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public Set<String> getRelatedIds() {
        return relatedIds;
    }

    public void setRelatedIds(Set<String> relatedIds) {
        this.relatedIds = relatedIds;
    }

    public void clear() {
        attributes.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Record)) return false;

        Record record = (Record) o;

        if (attributes != null ? !attributes.equals(record.attributes) : record.attributes != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return attributes != null ? attributes.hashCode() : 0;
    }
}
