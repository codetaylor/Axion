package com.sudoplay.axion.spec.adapter;

import java.io.IOException;

import com.sudoplay.axion.registry.TagAdapter;
import com.sudoplay.axion.spec.tag.TagFloat;
import com.sudoplay.axion.spec.tag.TagList;
import com.sudoplay.axion.stream.AxionInputStream;
import com.sudoplay.axion.stream.AxionOutputStream;
import com.sudoplay.axion.tag.Tag;

/**
 * The {@link TagAdapter} used to read and write a {@link TagFloat}.
 * <p>
 * Part of the original specification.
 * 
 * @author Jason Taylor
 */
public class TagFloatAdapter extends TagAdapter<TagFloat> {

  @Override
  public void write(final TagFloat tag, final AxionOutputStream out) throws IOException {
    out.writeFloat(tag.get());
  }

  @Override
  public TagFloat read(final Tag parent, final AxionInputStream in) throws IOException {
    return convertToTag((parent instanceof TagList) ? null : in.readString(), in.readFloat());
  }

}