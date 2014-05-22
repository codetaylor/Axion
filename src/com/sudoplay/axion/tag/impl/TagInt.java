package com.sudoplay.axion.tag.impl;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sudoplay.axion.Axion;
import com.sudoplay.axion.tag.Abstract_Tag;

/**
 * @tag.type 3
 * 
 * @tag.name <code>TAG_Int</code>
 * 
 * @tag.payload * A signed short (32 bits, big endian).
 * 
 * @author Jason Taylor
 * 
 */
public class TagInt extends Abstract_Tag {
  
  private static final Logger LOG = LoggerFactory.getLogger(TagInt.class);

  public static final byte TAG_ID = (byte) 3;
  public static final String TAG_NAME = "TAG_Int";

  private int data;

  public TagInt(final String newName) {
    super(newName);
  }

  public TagInt(final String newName, final int newInt) {
    super(newName);
    data = newInt;
  }

  public void set(final int newInt) {
    data = newInt;
  }

  public int get() {
    return data;
  }

  @Override
  public byte getTagId() {
    return TAG_ID;
  }

  @Override
  public String getTagName() {
    return TAG_NAME;
  }

  @Override
  public void read(Axion axion, DataInput input) throws IOException {
    data = input.readInt();
    LOG.trace("[{}] read int [{}]", TAG_NAME, data);
  }

  @Override
  public void write(Axion axion, DataOutput output) throws IOException {
    output.writeInt(data);
    LOG.trace("[{}] write int [{}]", TAG_NAME, data);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + data;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    TagInt other = (TagInt) obj;
    if (data != other.data)
      return false;
    return true;
  }
  
  @Override
  public String toString() {
    return TAG_NAME + super.toString() + ": " + data;
  }

}
