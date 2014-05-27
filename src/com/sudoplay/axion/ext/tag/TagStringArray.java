package com.sudoplay.axion.ext.tag;

import java.util.Arrays;

import com.sudoplay.axion.tag.Tag;

/**
 * @tag.type 85
 * 
 * @tag.name <code>TAG_String_Array</code>
 * 
 * @tag.payload * <code>TAG_Int</code> length<br>
 *              * An array of Strings. The length of this array is
 *              <code>length</code> Strings.
 * 
 * @author Jason Taylor
 * 
 */
public class TagStringArray extends Tag {

  private String[] data;

  public TagStringArray(final String newName) {
    this(newName, new String[0]);
  }

  public TagStringArray(final String newName, final String[] newStringArray) {
    super(newName);
    set(newStringArray);
  }

  public void set(final String[] newStringArray) {
    if (newStringArray == null) {
      throw new IllegalArgumentException(this.toString() + " doesn't support null payload");
    }
    data = newStringArray.clone();
  }

  public String[] get() {
    return data.clone();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + Arrays.hashCode(data);
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
    TagStringArray other = (TagStringArray) obj;
    if (!Arrays.equals(data, other.data))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return super.toString() + ": [" + data.length + " strings]";
  }

  @Override
  public Tag clone() {
    return new TagStringArray(getName(), data);
  }

}
