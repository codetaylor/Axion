package com.sudoplay.axion.ext.converter;

import com.sudoplay.axion.ext.tag.TagLongArray;
import com.sudoplay.axion.registry.TypeConverter;

/**
 * The {@link TypeConverter} used to convert to and from a {@link TagLongArray}.
 * <p>
 * Part of the extended, custom specification.
 *
 * @author Jason Taylor
 */
public class TagLongArrayConverter extends TypeConverter<TagLongArray, long[]> {

  @Override
  public long[] convert(final TagLongArray tag) {
    return tag.get();
  }

  @Override
  public TagLongArray convert(final String name, final long[] value) {
    return new TagLongArray(name, value);
  }

}
