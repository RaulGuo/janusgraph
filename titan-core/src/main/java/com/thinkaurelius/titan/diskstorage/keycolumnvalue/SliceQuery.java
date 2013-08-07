package com.thinkaurelius.titan.diskstorage.keycolumnvalue;

import com.google.common.base.Preconditions;
import com.thinkaurelius.titan.diskstorage.StaticBuffer;
import com.thinkaurelius.titan.diskstorage.util.ByteBufferUtil;
import com.thinkaurelius.titan.graphdb.query.Query;


/**
 * Queries for a slice of data identified by a start point (inclusive) and end point (exclusive).
 * Returns all {@link StaticBuffer}s that lie in this range up to the given limit.
 *
 * If a SliceQuery is marked <i>static</i> it is expected that the result set does not change.
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */

public class SliceQuery {

    public static final boolean DEFAULT_STATIC = false;

    private final StaticBuffer sliceStart;
    private final StaticBuffer sliceEnd;
    private final int limit;
    private final boolean isStatic;

    protected int hashcode;

    public SliceQuery(final StaticBuffer sliceStart, final StaticBuffer sliceEnd, final int limit, boolean isStatic) {
        Preconditions.checkNotNull(sliceStart);
        Preconditions.checkNotNull(sliceEnd);
        Preconditions.checkArgument(limit>0,"Expected positive limit");
        this.sliceStart = sliceStart;
        this.sliceEnd = sliceEnd;
        this.limit = limit;
        this.isStatic = isStatic;
    }

    public SliceQuery(final SliceQuery query) {
        this(query.getSliceStart(),query.getSliceEnd(),query.getLimit(),query.isStatic());
    }

    public SliceQuery(final StaticBuffer sliceStart, final StaticBuffer sliceEnd) {
        this(sliceStart,sliceEnd, Query.NO_LIMIT,DEFAULT_STATIC);
    }

    public SliceQuery(final StaticBuffer sliceStart, final StaticBuffer sliceEnd, final int limit) {
        this(sliceStart,sliceEnd,limit,DEFAULT_STATIC);
    }

    public SliceQuery(final StaticBuffer sliceStart, final StaticBuffer sliceEnd, final boolean isStatic) {
        this(sliceStart,sliceEnd,Query.NO_LIMIT,isStatic);
    }

    /**
     * Whether the result set, if non-empty, is static, i.e. does not
     * change anymore. This allows the query result (if non-empty) to be cached.
     *
     * @return whether query is static
     */
    public boolean isStatic() {
        return isStatic;
    }

    /**
     *
     * @return The maximum number of results to return
     */
    public int getLimit() {
        return limit;
    }

    public boolean hasLimit() {
        return limit!=Query.NO_LIMIT;
    }

    /**
     * The start of the slice is considered to be inclusive
     *
     * @return The StaticBuffer denoting the start of the slice
     */
    public StaticBuffer getSliceStart() {
        return sliceStart;
    }

    /**
     * The end of the slice is considered to be exclusive
     *
     * @return The StaticBuffer denoting the end of the slice
     */
    public StaticBuffer getSliceEnd() {
        return sliceEnd;
    }

    @Override
    public int hashCode() {
        if (hashcode==0) {
            hashcode = sliceStart.hashCode()*2342357 + sliceEnd.hashCode() + 742342357;
            if (hashcode==0) hashcode=1;
        }
        return hashcode;
    }

    @Override
    public boolean equals(Object other) {
        if (this==other) return true;
        else if (other==null) return false;
        else if (!getClass().isInstance(other)) return false;
        SliceQuery oth = (SliceQuery)other;
        return sliceStart.equals(oth.sliceStart) && sliceEnd.equals(oth.sliceEnd);
    }

    public boolean subsumes(SliceQuery oth) {
        Preconditions.checkNotNull(oth);
        if (this==oth) return true;
        else return limit>=oth.limit && sliceStart.compareTo(oth.sliceStart)<=0
                && sliceEnd.compareTo(oth.sliceEnd)>=0;
    }

    public static final StaticBuffer pointRange(StaticBuffer point) {
        return ByteBufferUtil.nextBiggerBuffer(point);
    }

}