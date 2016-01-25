// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.indicators.mapdb;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;

import org.mapdb.SerializerBase;

/**
 * created by talend on Aug 6, 2014 Detailled comment
 * 
 */
public class TalendSerializerBase extends SerializerBase implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 8375426567087046212L;

    static final int TALEND_NULL = 181;

    /*
     * (non-Javadoc)
     * 
     * @see org.mapdb.SerializerBase#serialize(java.io.DataOutput, java.lang.Object)
     */
    @Override
    public void serialize(DataOutput out, Object obj) throws IOException {
        // normal case
        Object newObj = obj;
        if (TupleEmpty.class.isInstance(obj)) {
            out.write(TALEND_NULL);
            return;
        }
        if (Timestamp.class.isInstance(obj)) {
            newObj = obj.toString();
        }
        super.serialize(out, newObj);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mapdb.SerializerBase#serialize(java.io.DataOutput, java.lang.Object,
     * org.mapdb.SerializerBase.FastArrayList)
     */
    @Override
    public void serialize(DataOutput out, Object obj, FastArrayList<Object> objectStack) throws IOException {
        // arrayList case
        Object newObj = obj;
        if (TupleEmpty.class.isInstance(obj)) {
            newObj = null;
        }
        if (Timestamp.class.isInstance(obj)) {
            newObj = obj.toString();
        }
        super.serialize(out, newObj, objectStack);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mapdb.SerializerBase#serializeUnknownObject(java.io.DataOutput, java.lang.Object,
     * org.mapdb.SerializerBase.FastArrayList)
     */
    @Override
    protected void serializeUnknownObject(DataOutput out, Object obj, FastArrayList<Object> objectStack) throws IOException {
        Object newObj = obj.toString();
        super.serialize(out, newObj, objectStack);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mapdb.SerializerBase#deserializeUnknownHeader(java.io.DataInput, int,
     * org.mapdb.SerializerBase.FastArrayList)
     */
    @Override
    protected Object deserializeUnknownHeader(DataInput is, int head, FastArrayList<Object> objectStack) throws IOException {
        if (TALEND_NULL == head) {
            return new TupleEmpty();
        }
        return super.deserializeUnknownHeader(is, head, objectStack);
    }

}
