package com.sudoplay.axion.spec.adapter;

import java.io.IOException;

import com.sudoplay.axion.Axion;
import com.sudoplay.axion.adapter.TagAdapter;
import com.sudoplay.axion.spec.tag.TagIntArray;
import com.sudoplay.axion.spec.tag.TagList;
import com.sudoplay.axion.stream.AxionInputStream;
import com.sudoplay.axion.stream.AxionOutputStream;
import com.sudoplay.axion.tag.Tag;

public class TagIntArrayAdapter implements TagAdapter<TagIntArray> {

  @Override
  public void write(final TagIntArray tag, final AxionOutputStream out, final Axion axion) throws IOException {
    int[] data = (tag.get());
    int len = data.length;
    out.writeInt(len);
    for (int i = 0; i < len; i++) {
      out.writeInt(data[i]);
    }
  }

  @Override
  public TagIntArray read(final Tag parent, final AxionInputStream in, final Axion axion) throws IOException {
    String name = (parent instanceof TagList) ? null : in.readString();
    int len = in.readInt();
    int[] data = new int[len];
    for (int i = 0; i < len; i++) {
      data[i] = in.readInt();
    }
    return axion.convertToTag(name, data);
  }

}
