package com.sudoplay.axion.tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.sudoplay.axion.util.TagUtil;

/**
 * @tag.type 9
 * 
 * @tag.name <code>TAG_List</code>
 * 
 * @tag.payload * <code>TAG_Byte</code> tagId<br>
 *              * <code>TAG_Int</code> length<br>
 *              * A sequential list of Tags (not Named Tags), of type
 *              <code>typeId</code>. The length of this array is
 *              <code>length</code> Tags
 * 
 * @tag.note All tags share the same type.
 * 
 * @author Jason Taylor
 * 
 */
public class TagList extends Tag implements Iterable<Tag> {

  public static final byte TAG_ID = (byte) 9;
  public static final String TAG_NAME = "TAG_List";

  private final List<Tag> data;

  /**
   * Store type id for tags in this list; all tags must be of the same type.
   */
  private final byte type;

  public TagList(final Class<? extends Tag> tagClass) {
    this(tagClass, null, null);
  }

  public TagList(final Class<? extends Tag> tagClass, final String newName) {
    this(tagClass, newName, null);
  }

  private TagList(final Class<? extends Tag> tagClass, final String newName, final List<Tag> newList) {
    super(newName);
    if ((type = TagUtil.getId(tagClass)) == TagEnd.TAG_ID) {
      throw new IllegalArgumentException("Can't create " + TagList.TAG_NAME + " from type " + TagEnd.TAG_NAME);
    }
    if (newList == null) {
      data = new ArrayList<Tag>();
    } else {
      data = new ArrayList<Tag>(newList);
      for (Tag tag : data) {
        assertValidTag(tag);
      }
    }
  }

  private void assertValidTag(final Tag tag) {
    if (tag == null) {
      throw new IllegalArgumentException(TagList.TAG_NAME + " can't contain null tags");
    } else if (type != tag.getTagId()) {
      throw new IllegalArgumentException("Can't add tag of type [" + tag.getTagName() + "] to " + TagList.TAG_NAME + " of type " + TagUtil.getName(type));
    } else if (tag.hasParent()) {
      throw new IllegalStateException("Tag can't be added to more than one collection tag");
    }
  }

  /**
   * Add tag to the end of the list. If no tag type has been assigned to the
   * list, assign the type of the tag to be added. If a tag type has been
   * assigned to the list and the tag to be added does not match this type, an
   * exception is thrown. Cannot add <code>TAG_End</code> to the list.
   * 
   * @param tag
   */
  public void add(final Tag tag) {
    assertValidTag(tag);
    tag.setName(null);
    tag.setParent(this);
    data.add(tag);
  }

  public boolean remove(final Tag tag) {
    if (data.remove(tag)) {
      tag.setParent(null);
      return true;
    }
    return false;
  }

  public Tag remove(final int index) {
    return data.remove(index);
  }

  public void addByte(final byte newByte) {
    add(new TagByte(null, newByte));
  }

  public void addByteArray(final byte[] newByteArray) {
    add(new TagByteArray(null, newByteArray));
  }

  public void addCompound(final TagCompound newTagCompound) {
    add(newTagCompound);
  }

  public void addDouble(final double newDouble) {
    add(new TagDouble(null, newDouble));
  }

  public void addFloat(final float newFloat) {
    add(new TagFloat(null, newFloat));
  }

  public void addInt(final int newInt) {
    add(new TagInt(null, newInt));
  }

  public void addIntArray(final int[] newIntArray) {
    add(new TagIntArray(null, newIntArray));
  }

  public void addList(final TagList newTagList) {
    add(newTagList);
  }

  public void addLong(final long newLong) {
    add(new TagLong(null, newLong));
  }

  public void addShort(final short newShort) {
    add(new TagShort(null, newShort));
  }

  public void addString(final String newString) {
    add(new TagString(null, newString));
  }

  public byte getType() {
    return type;
  }

  public List<Tag> getAsList() {
    return Collections.unmodifiableList(data);
  }

  @SuppressWarnings("unchecked")
  public <T extends Tag> T get(final int index) {
    return (T) data.get(index);
  }

  @Override
  public Iterator<Tag> iterator() {
    return Collections.unmodifiableList(data).iterator();
  }

  public int size() {
    return data.size();
  }

  public void clear() {
    data.clear();
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
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((data == null) ? 0 : data.hashCode());
    result = prime * result + type;
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
    TagList other = (TagList) obj;
    if (data == null) {
      if (other.data != null)
        return false;
    } else if (!data.equals(other.data))
      return false;
    if (type != other.type)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return TAG_NAME + super.toString() + ": " + data.size() + " entries of type " + type;
  }

  @Override
  protected void onNameChange(final String oldName, final String newName) {
    if (newName != null && !newName.isEmpty()) {
      throw new IllegalStateException("Tag belongs to a " + TagList.TAG_NAME + " and can not be named");
    }
  }

  @Override
  public TagList clone() {
    if (data.isEmpty()) {
      return new TagList(TagUtil.getTagClass(type), getName());
    } else {
      List<Tag> newList = new ArrayList<Tag>(data.size());
      for (Tag tag : data) {
        newList.add(tag.clone());
      }
      return new TagList(TagUtil.getTagClass(type), getName(), newList);
    }
  }

}
