package com.sudoplay.axion.spec.tag;

import com.sudoplay.axion.tag.Tag;

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
public class TagInt extends Tag {

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
    return super.toString() + ": " + data;
  }

  @Override
  public TagInt clone() {
    return new TagInt(getName(), data);
  }

}
