package com.qlu.cup.session;

/**
 * 分页用，记录限制
 * 使用默认的就好，也为扩展带来方便
 */
public class RowBounds {

  public static final int NO_ROW_OFFSET = 0;
  public static final int NO_ROW_LIMIT = Integer.MAX_VALUE;
  public static final RowBounds DEFAULT = new RowBounds();

  //offset,limit就等于一般分页的start,limit,
  private int offset;
  private int limit;

  //默认是一页Integer.MAX_VALUE条
  public RowBounds() {
    this.offset = NO_ROW_OFFSET;
    this.limit = NO_ROW_LIMIT;
  }

  public RowBounds(int offset, int limit) {
    this.offset = offset;
    this.limit = limit;
  }

  public int getOffset() {
    return offset;
  }

  public int getLimit() {
    return limit;
  }

}
