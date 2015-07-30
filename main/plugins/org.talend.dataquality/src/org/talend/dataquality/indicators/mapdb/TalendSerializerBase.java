// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;

import org.apache.log4j.Logger;
import org.mapdb.SerializerBase;
import org.talend.dataquality.indicators.mapdb.helper.IObjectConvertArray;

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
    
    private static Logger log=Logger.getLogger(TalendSerializerBase.class);

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
        
        if(obj instanceof IObjectConvertArray){
            newObj=((IObjectConvertArray) obj).getArrays();
            out.write(Header.POJO);
            this.serializeClass(out, obj.getClass());
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
        
        if(Header.POJO==head){
            Class<?> deserializeClass = this.deserializeClass(is);
            Object deserialize = this.deserialize(is, -1);
            try {
                Object newInstance = deserializeClass.newInstance();
                if(IObjectConvertArray.class.isInstance(newInstance)){
                    ((IObjectConvertArray)newInstance).restoreObjectByArrays((Object[])deserialize);
                    return newInstance;
                }
            } catch (SecurityException e) {
                log.error(e, e);
            } catch (InstantiationException e) {
                log.error(e, e);
            } catch (IllegalAccessException e) {
                log.error(e, e);
            } catch (IllegalArgumentException e) {
                log.error(e, e);
            }
        }
        return super.deserializeUnknownHeader(is, head, objectStack);
    }

}
