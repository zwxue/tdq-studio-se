package org.talend.commons.MapDB.utils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.util.Comparator;

import org.mapdb.BTreeKeySerializer;
import org.mapdb.BTreeMap;
import org.mapdb.Fun;
import org.mapdb.Serializer;

public final class TupleArrayKeySerializer extends BTreeKeySerializer<TupleEmpty> implements Serializable {

    private static final long serialVersionUID = 2183804367032891772L;

    protected final Comparator<Integer> aComparator;

    protected final Comparator<String> bComparator;

    protected final Serializer<Integer> aSerializer;

    protected final Serializer<String> bSerializer;

    /**
     * Construct new Tuple2 Key Serializer. You may pass null for some value, In that case 'default' value will be used,
     * Comparable comparator and Default Serializer from DB.
     * 
     * @param aComparator comparator used for first tuple value
     * @param aSerializer serializer used for first tuple value
     * @param bSerializer serializer used for second tuple value
     */
    public TupleArrayKeySerializer(Comparator<Integer> aComparator, Comparator<String> bComparator,
            Serializer<Integer> aSerializer, Serializer<String> bSerializer) {
        this.aComparator = aComparator;
        this.bComparator = bComparator;
        this.aSerializer = aSerializer;
        this.bSerializer = bSerializer;
    }

    @Override
    public void serialize(DataOutput out, int start, int end, Object[] keys) throws IOException {

        for (int i = start; i < end; i++) {
            TupleArray t = (TupleArray) keys[i];
            // write new A
            aSerializer.serialize(out, t.arrayLength);

            for (String element : t.keyArray) {
                bSerializer.serialize(out, element);
                //
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mapdb.BTreeKeySerializer#deserialize(java.io.DataInput, int, int, int)
     */
    @Override
    public Object[] deserialize(DataInput in, int start, int end, int size) throws IOException {

        Object[] ret = new Object[size];
        try {
            Integer a = null;
            String[] keyArray = null;
            for (int i = start; i < end; i++) {
                // read new A
                a = Integer.valueOf(aSerializer.deserialize(in, -1));

                // init bCount array
                keyArray = new String[a];

                for (int index = 0; index < a.intValue(); index++) {
                    String value = bSerializer.deserialize(in, -1);
                    keyArray[index] = value;
                }
                ret[i] = new TupleArray(a, keyArray);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    // @Override
    // public void serialize(DataOutput out, int start, int end, Object[] keys) throws IOException {
    // int acount = 0;
    // Integer[] bCount = null;
    // if (keys != null && keys[0] != null && keys.length > 0) {
    // TupleArray firstTuple = (TupleArray) keys[0];
    // bCount = new Integer[firstTuple.arrayLength];
    //
    // for (int i = start; i < end; i++) {
    // TupleArray t = (TupleArray) keys[i];
    // if (acount == 0) {
    // // write new A
    // aSerializer.serialize(out, t.arrayLength);
    // // count how many A are following
    // acount = 1;
    // while (i + acount < end
    // && aComparator.compare(t.arrayLength, ((TupleArray) keys[i + acount]).arrayLength) == 0) {
    // acount++;
    // }
    // DataOutput2.packInt(out, acount);
    //
    // }
    // for (int index = 0; index < t.keyArray.length; index++) {
    // if (index == t.keyArray.length) {
    // bSerializer.serialize(out, t.keyArray[index].toString());
    // continue;
    // }
    // if (bCount[index] == null || bCount[index] == 0) {
    // bSerializer.serialize(out, t.keyArray[index].toString());
    // //
    // bCount[index] = 1;
    // while (i + bCount[index] < end
    // && bComparator.compare(t.keyArray[index], ((TupleArray) keys[i + bCount[index]]).keyArray[index]) == 0) {
    // bCount[index]++;
    // }
    // DataOutput2.packInt(out, bCount[index].intValue());
    // }
    // bCount[index]--;
    // }
    // acount--;
    // }
    // }
    // }
    //
    // /*
    // * (non-Javadoc)
    // *
    // * @see org.mapdb.BTreeKeySerializer#deserialize(java.io.DataInput, int, int, int)
    // */
    // @Override
    // public Object[] deserialize(DataInput in, int start, int end, int size) throws IOException {
    //
    // Object[] ret = new Object[size];
    // try {
    // Integer a = null;
    // String[] keyArray = null;
    // int acount = 0;
    // Integer[] bCount = null;
    // for (int i = start; i < end; i++) {
    // if (acount == 0) {
    // // read new A
    // a = aSerializer.deserialize(in, -1);
    // acount = DataInput2.unpackInt(in);
    //
    // // init bCount array
    // if (bCount == null) {
    // keyArray = new String[a];
    // bCount = new Integer[a];
    // }
    // }
    //
    // for (int index = 0; index < a.intValue(); index++) {
    // if (index == a.intValue() - 1) {
    // String value = bSerializer.deserialize(in, -1);
    // continue;
    // }
    // if (bCount[index] == null || bCount[index] == 0) {
    // String value = bSerializer.deserialize(in, -1);
    // bCount[index] = DataInput2.unpackInt(in);
    // // if (bCount[index] > 2) {
    // // System.out.println("fail");
    // // }
    // keyArray[index] = value;
    // }
    // bCount[index]--;
    // }
    // ret[i] = new TupleArray(a, keyArray);
    // acount--;
    // }
    // assert (acount == 0);
    //
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // return ret;
    // }

    @Override
    public Comparator<TupleEmpty> getComparator() {
        return BTreeMap.COMPARABLE_COMPARATOR;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TupleArrayKeySerializer t = (TupleArrayKeySerializer) o;

        return Fun.eq(aComparator, t.aComparator) && Fun.eq(bComparator, t.bComparator) && Fun.eq(aSerializer, t.aSerializer)
                && Fun.eq(bSerializer, t.bSerializer);
    }

    @Override
    public int hashCode() {
        int result = aComparator != null ? aComparator.hashCode() : 0;
        result = 31 * result + (aSerializer != null ? aSerializer.hashCode() : 0);
        result = 31 * result + (aSerializer != null ? aSerializer.hashCode() : 0);
        result = 31 * result + (bSerializer != null ? bSerializer.hashCode() : 0);
        return result;
    }
}
