package org.talend.commons.MapDB.utils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

import org.mapdb.Fun;

public final class TupleArray implements Comparable<TupleArray>, Serializable {

    private static final long serialVersionUID = -8816277286657643283L;

    final public Integer arrayLength;

    public String[] keyArray;

    public TupleArray(Integer length, String[] array) {
        this.arrayLength = length;
        this.keyArray = array;
    }

    public static final Comparator<TupleArray> TUPLE_COMPARATOR = new TupleComparator(null, null);

    @Override
    public int compareTo(TupleArray o) {
        return TUPLE_COMPARATOR.compare(this, o);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final TupleArray t = (TupleArray) o;

        return eq(keyArray, t.keyArray);
    }

    /** returns true if all elements are equal, works with nulls */
    static public boolean eq(String[] a, String[] b) {
        return Arrays.equals(a, b);
        // if (a == b) {
        // return true;
        // } else if (a != null && a.equals(b)) {
        // return true;
        // } else if (a != null && b != null && a.length == b.length) {
        // for (int index = 0; index < a.length; index++) {
        // if (a[index] != null && !a[index].equals(b[index])) {
        // return false;
        // }
        // }
        // return true;
        // }
        // return false;
    }

    @Override
    public int hashCode() {
        int result = 0;
        if (keyArray != null && keyArray.length > 0) {

            for (String element : keyArray) {
                result = 31 * result + (element != null ? element.hashCode() : 0);
            }
        }
        return result;
    }

    @Override
    public String toString() {

        return "Tuple[" + arrayLength + ", " + keyArray[0] + "]";

    }

    public static final class TupleComparator implements Comparator<TupleArray>, Serializable {

        private static final long serialVersionUID = 1156568632023474010L;

        protected final Comparator<Integer> a;

        protected final Comparator<String> b;

        public TupleComparator(Comparator<Integer> a, Comparator<String> b) {
            this.a = a == null ? Fun.COMPARATOR : a;
            this.b = b == null ? Fun.COMPARATOR : b;
        }

        @Override
        public int compare(final TupleArray o1, final TupleArray o2) {
            int i = a.compare(o1.arrayLength, o2.arrayLength);
            if (i != 0) {
                return i;
            }
            for (int index = 0; index < o1.keyArray.length; index++) {
                i = b.compare(o1.keyArray[index], o2.keyArray[index]);
                if (i != 0) {
                    return i;
                }
            }
            return i;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            TupleComparator that = (TupleComparator) o;

            return a.equals(that.a) && b.equals(that.b);
        }

        @Override
        public int hashCode() {
            int result = a.hashCode();
            result = 31 * result + b.hashCode();
            return result;
        }
    }
}
