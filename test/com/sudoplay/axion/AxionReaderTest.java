package com.sudoplay.axion;

import com.sudoplay.axion.api.AxionReader;
import com.sudoplay.axion.api.AxionWritable;
import com.sudoplay.axion.api.AxionWriter;
import com.sudoplay.axion.api.impl.DefaultAxionReader;
import com.sudoplay.axion.ext.tag.TagBoolean;
import com.sudoplay.axion.mapper.NBTObjectMapper;
import com.sudoplay.axion.spec.tag.TagCompound;
import com.sudoplay.axion.spec.tag.TagInt;
import com.sudoplay.axion.spec.tag.TagList;
import com.sudoplay.axion.spec.tag.TagString;
import com.sudoplay.axion.tag.Tag;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.function.Function;

import static org.junit.Assert.*;

public class AxionReaderTest {

  private static Axion axion;

  @BeforeClass
  public static void before() {
    axion = Axion.createInstanceFrom(Axion.getExtInstance(), "test");
    axion.registerNBTObjectMapper(Vector.class, new VectorMapper());
  }

  private Vector getTestVector() {
    Vector vector = new Vector();
    vector.x = 3;
    vector.y = 1;
    vector.z = 4;
    return vector;
  }

  private TagList getTestTagList() {
    TagList t = new TagList(TagInt.class);
    Random random = new Random(42);
    for (int i = 0; i < 100; ++i) {
      t.add(new TagInt(random.nextInt(100) + 1));
    }
    return t;
  }

  private WritableVector getTestWritableVector() {
    WritableVector v = new WritableVector();
    v.x = 3;
    v.y = 1;
    v.z = 4;
    return v;
  }

  private TagCompound getTestTagCompound() {
    TagCompound t = new TagCompound();
    t.put("boolean", new TagBoolean(false));
    t.put("string", new TagString("someString"));
    t.put("int", new TagInt(42));
    t.put("vector", axion.createTagFrom(getTestVector()));
    t.put("list", getTestTagList());
    t.put("compound", getNestedTestTagCompound());

    TagCompound out = new TagCompound();
    getTestWritableVector().write(axion.defaultWriter(out));
    t.put("writableVector", out);
    return t;
  }

  private TagCompound getNestedTestTagCompound() {
    TagCompound t = new TagCompound();
    t.put("string", new TagString("someString"));
    t.put("int", new TagInt(42));
    t.put("vector", axion.createTagFrom(getTestVector()));
    t.put("list", getTestTagList());
    return t;
  }

  private AxionReader getTestReader() {
    return new DefaultAxionReader(getTestTagCompound(), axion);
  }

  @Test
  public void test_has() {
    AxionReader r = getTestReader();

    // should return true when the tag exists
    assertTrue(r.has("int"));

    // should return false when the tag does not exist
    assertFalse(r.has("omg"));

    // should throw IllegalArgumentException on null name parameter
    try {
      r.has(null);
      fail();
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void test_read_name() {
    AxionReader in = getTestReader();

    // support direct assignment of registered tags without cast
    int read = in.read("int");
    assertEquals(42, read);

    // reading the vector should return an ArrayList not a Vector nor a TagInt
    assertEquals(ArrayList.class, in.read("vector").getClass());
    assertNotEquals(Vector.class, in.read("vector").getClass());
    assertNotEquals(TagInt.class, in.read("vector").getClass());

    // reading a compound should return a HashMap, not a TagCompound
    assertEquals(HashMap.class, in.read("compound").getClass());
    assertNotEquals(TagCompound.class, in.read("compound").getClass());

    // should throw IllegalArgumentException on null name parameter
    try {
      in.read((String) null);
      fail();
    } catch (IllegalArgumentException e) {
      // expected
    }

    // should return null when trying to read a tag that doesn't exist
    assertNull(in.read("omg"));
  }

  @Test
  public void test_read_name_defaultValue() {
    AxionReader in = getTestReader();

    // should return the tag's value if the tag is found
    assertEquals(42, (int) in.read("int", 73));

    // should return supplied default value if the tag is not found
    assertEquals(73, (int) in.read("who", 73));

    // should return null if the supplied fallback default value is null
    assertNull(in.read("who", (Object) null));

    // should throw IllegalArgumentException on null name parameter
    try {
      in.read((String) null, 42);
      fail();
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void test_read_name_function() {
    AxionReader in = getTestReader();

    // should return value after function application
    assertTrue(in.read("boolean", value -> !value));

    // should throw IllegalArgumentException on null name parameter
    try {
      boolean b = in.read((String) null, value -> !value);
      fail();
    } catch (IllegalArgumentException e) {
      // expected
    }

    // should throw IllegalArgumentException on null function parameter
    try {
      boolean b = (boolean) in.read("boolean", (Function) null);
      fail();
    } catch (IllegalArgumentException e) {
      // expected
    }

    // should throw IllegalArgumentException when tag does not exist
    try {
      boolean b = in.read("loobean", value -> !value);
      fail();
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void test_read_tag() {
    AxionReader in = getTestReader();

    // should return the tag's value
    assertEquals(42, (int) in.read(new TagInt(42)));

    // should throw IllegalArgumentException on null tag parameter
    try {
      in.read((Tag) null);
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void test_read_tag_defaultValue() {
    AxionReader in = getTestReader();

    // should return tag's value if not null
    assertEquals(42, (int) in.read(new TagInt(42), 73));

    // should return default value on null tag parameter
    assertEquals(42, (int) in.read((Tag) null, 42));

    // should accept null as a default value
    assertNull(in.read((Tag) null, (Integer) null));
  }

  @Test
  public void test_read_tag_function() {
    AxionReader in = getTestReader();

    // should apply the function before returning the value
    int actual = in.read(new TagInt(40), value -> value + 2);
    assertEquals(42, actual);

    // should throw IllegalArgumentException on null tag parameter
    try {
      int i = in.read((Tag) null, value -> value + 2);
      fail();
    } catch (IllegalArgumentException e) {
      // expected
    }

    // should throw IllegalArgumentException on null function parameter
    try {
      int i = in.read(new TagInt(42), (Function<Integer, Integer>) null);
      fail();
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void test_read_name_class() {
    AxionReader in = getTestReader();

    // should return null when the tag doesn't exist
    assertNull(in.read("yay", Vector.class));

    // should get tag by name and convert AxionWritable instances
    Vector v = in.read("writableVector", WritableVector.class);
    assertEquals(1, v.y);

    // should get tag by name and convert mappable instances
    Vector v2 = in.read("vector", Vector.class);
    assertEquals(1, v2.y);

    // should throw IllegalArgumentException on null name parameter
    try {
      in.read((String) null, Vector.class);
      fail();
    } catch (IllegalArgumentException e) {
      // expected
    }

    // should throw IllegalArgumentException on null class parameter
    try {
      in.read("vector", (Class) null);
      fail();
    } catch (IllegalArgumentException e) {
      // expected
    }

  }

  @Test
  public void test_read_name_class_defaultValue() {
    AxionReader in = getTestReader();

    // should return default value when tag doesn't exist
    Vector v = new Vector();
    assertEquals(v, in.read("novec", Vector.class, v));

    // should allow null as a default parameter
    assertNull(in.read("zomg", Vector.class, (Vector) null));

    // should throw IllegalArgumentException on null name parameter
    try {
      in.read((String) null, Vector.class, new Vector());
      fail();
    } catch (IllegalArgumentException e) {
      // expected
    }

    // should throw IllegalArgumentException on null class parameter
    try {
      in.read("vector", null, new Vector());
      fail();
    } catch (IllegalArgumentException e) {
      // expected
    }

  }

  @Test
  public void test_read_name_class_function() {
    AxionReader in = getTestReader();

    // should apply given function before returning value
    Vector v = in.read("vector", Vector.class, (Function<Vector, Vector>) vector -> {
      vector.y = 42;
      return vector;
    });
    assertEquals(42, v.y);

    // should throw IllegalArgumentException on null class parameter
    try {
      in.read("vector", Vector.class, (Function<Vector, Vector>) null);
      fail();
    } catch (IllegalArgumentException e) {
      // expected
    }

    // should throw IllegalArgumentException on null name parameter
    try {
      in.read((String) null, Vector.class, (Function<Vector, Vector>) vector -> vector);
      fail();
    } catch (IllegalArgumentException e) {
      // expected
    }

    // should throw IllegalArgumentException on null class parameter
    try {
      in.read("vector", (Class<Vector>) null, (Function<Vector, Vector>) vector -> vector);
      fail();
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void test_read_tag_class() {
    AxionReader in = getTestReader();

    // should throw IllegalArgumentException on null tag parameter
    try {
      in.read((Tag) null, Vector.class);
      fail();
    } catch (IllegalArgumentException e) {
      // expected
    }

    // should throw IllegalArgumentException on null class parameter
    try {
      in.read(new TagCompound(), (Class<Vector>) null);
      fail();
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  public static class VectorMapper implements NBTObjectMapper<TagList, Vector> {
    @Override
    public TagList createTagFrom(String name, Vector object, Axion axion) {
      TagList out = new TagList(TagInt.class);
      out.add(new TagInt(object.x));
      out.add(new TagInt(object.y));
      out.add(new TagInt(object.z));
      return out;
    }

    @Override
    public Vector createObjectFrom(TagList tag, Axion axion) {
      Vector object = new Vector();
      object.x = ((TagInt) tag.get(0)).get();
      object.y = ((TagInt) tag.get(1)).get();
      object.z = ((TagInt) tag.get(2)).get();
      return object;
    }
  }

  public static class Vector {
    public int x, y, z;
  }

  public static class WritableVector extends Vector implements AxionWritable {

    @Override
    public void write(AxionWriter out) {
      out.write("x", x).write("y", y).write("z", z);
    }

    @Override
    public void read(AxionReader in) {
      x = in.read("x");
      y = in.read("y");
      z = in.read("z");
    }
  }

}
