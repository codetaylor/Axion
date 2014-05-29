package com.sudoplay.axion;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.sudoplay.axion.AxionConfigurationProtection.ProtectionMode;
import com.sudoplay.axion.adapter.AxionTagRegistrationException;
import com.sudoplay.axion.adapter.TagAdapter;
import com.sudoplay.axion.adapter.TagConverter;
import com.sudoplay.axion.api.AxionWritable;
import com.sudoplay.axion.mapper.AxionMapperRegistrationException;
import com.sudoplay.axion.mapper.NBTObjectMapper;
import com.sudoplay.axion.spec.tag.TagCompound;
import com.sudoplay.axion.stream.AxionInputStream;
import com.sudoplay.axion.stream.AxionOutputStream;
import com.sudoplay.axion.tag.Tag;

/**
 * This is the main class for {@link Axion}, a tool for working with NBT.
 * 
 * @author Jason Taylor
 */
public class Axion {

  private static final String EXT_INSTANCE_NAME = "AXION_EXT";
  private static final String SPEC_INSTANCE_NAME = "AXION_SPEC";
  private static final Axion EXT_INSTANCE = new Axion(AxionConfiguration.EXT_CONFIGURATION);
  private static final Axion SPEC_INSTANCE = new Axion(AxionConfiguration.SPEC_CONFIGURATION);

  @SuppressWarnings("serial")
  private static final Map<String, Axion> INSTANCES = new HashMap<String, Axion>() {
    {
      put(EXT_INSTANCE_NAME, EXT_INSTANCE);
      put(SPEC_INSTANCE_NAME, SPEC_INSTANCE);
    }
  };

  /**
   * Reference to this instances {@link AxionConfiguration}
   */
  private AxionConfiguration configuration;

  /**
   * Creates a new instance of {@link Axion} with an empty, unlocked
   * configuration.
   */
  private Axion() {
    this(new AxionConfiguration(ProtectionMode.Unlocked));
  }

  /**
   * Creates a new instance of {@link Axion} with the {@link AxionConfiguration}
   * given.
   * 
   * @param newConfiguration
   *          the {@link AxionConfiguration} for the new instance
   */
  private Axion(final AxionConfiguration newConfiguration) {
    configuration = newConfiguration;
  }

  /**
   * Create a new configuration with the given name. If a configuration already
   * exists with the name provided, an exception is thrown.
   * <p>
   * This new configuration will not have a base tag or any other tags
   * registered.
   * 
   * @param newName
   *          name of the new configuration
   * @return
   * @throws AxionInstanceCreationException
   */
  public static Axion createInstance(final String newName) throws AxionInstanceCreationException {
    if (INSTANCES.containsKey(newName)) {
      throw new AxionInstanceCreationException(Axion.class.getSimpleName() + " instance alread exists with name: " + newName);
    }
    Axion instance = new Axion();
    INSTANCES.put(newName, instance);
    return instance;
  }

  /**
   * Create a new configuration by duplicating the configuration named with the
   * new name given. If a configuration already exists with the name provided,
   * an exception is thrown.
   * 
   * @param name
   *          the name of the configuration to duplicate
   * @param newName
   *          name of the new configuration
   * @return a new configuration
   * @throws AxionInstanceCreationException
   */
  public static Axion createInstanceFrom(final String name, final String newName) throws AxionInstanceCreationException {
    return createInstanceFrom(Axion.getInstance(name), newName);
  }

  /**
   * Create a new configuration by duplicating the configuration given with the
   * new name given. If a configuration already exists with the name provided,
   * an exception is thrown.
   * 
   * @param axion
   *          the configuration to duplicate
   * @param newName
   *          name of the new configuration
   * @return a new configuration
   * @throws AxionInstanceCreationException
   */
  public static Axion createInstanceFrom(final Axion axion, final String newName) throws AxionInstanceCreationException {
    if (INSTANCES.containsKey(newName)) {
      throw new AxionInstanceCreationException(Axion.class.getSimpleName() + " instance alread exists with name: " + newName);
    }
    Axion instance = new Axion(axion.configuration().clone());
    INSTANCES.put(newName, instance);
    return instance;
  }

  /**
   * Removes and returns the configuration named or null if no configuration is
   * found with the given name.
   * 
   * @param name
   *          name of the configuration to remove
   * @return the removed configuration, null if it doesn't exist
   */
  public static Axion deleteInstance(final String name) {
    return INSTANCES.remove(name);
  }

  /**
   * Returns the Axion instance with the given name or null if no instance
   * exists with that name.
   * 
   * @param name
   *          name of the instance to get
   * @return the named Axion instance
   */
  public static Axion getInstance(final String name) {
    return INSTANCES.get(name);
  }

  /**
   * Returns the built-in, extended Axion instance configuration.
   * 
   * @return the extended Axion instance
   */
  public static Axion getExtInstance() {
    return INSTANCES.get(EXT_INSTANCE_NAME);
  }

  /**
   * Returns the built-in, strict specification Axion instance configuration.
   * 
   * @return the strict specification Axion instance
   */
  public static Axion getSpecInstance() {
    return INSTANCES.get(SPEC_INSTANCE_NAME);
  }

  /**
   * Returns an unmodifiable map of all the {@link Axion} instances.
   * 
   * @return an unmodifiable map of all the {@link Axion} instances
   */
  public static Map<String, Axion> getInstances() {
    return Collections.unmodifiableMap(INSTANCES);
  }

  /**
   * Returns the {@link AxionConfiguration} for this {@link Axion} instance.
   * 
   * @return the {@link AxionConfiguration} for this {@link Axion} instance
   */
  public AxionConfiguration configuration() {
    return configuration;
  }

  public String getNameFor(final Tag tag) {
    return tag.getClass().getSimpleName();
  }

  /**
   * Returns the registered id for the {@link Tag} class given.
   * <p>
   * If no id is found, an exception is thrown.
   * 
   * @param tagClass
   *          tag class to get the id for
   * @return the registered id for the {@link Tag} class given
   * @throws AxionTagRegistrationException
   */
  public int getIdFor(final Class<? extends Tag> tagClass) throws AxionTagRegistrationException {
    return configuration.getIdFor(tagClass);
  }

  /**
   * Returns the registered {@link Tag} class for the id given.
   * <p>
   * If no class is found, an exception is thrown.
   * 
   * @param id
   *          id to get the tag class for
   * @return the registered {@link Tag} class for the id given
   * @throws AxionTagRegistrationException
   */
  public Class<? extends Tag> getClassFor(final int id) throws AxionTagRegistrationException {
    return configuration.getClassFor(id);
  }

  /**
   * Returns the registered {@link TagAdapter} for the id given.
   * <p>
   * If no adapter is found, an exception is thrown.
   * 
   * @param id
   *          id to get the adapter for
   * @return the registered {@link TagAdapter} for the id given
   */
  public <T extends Tag> TagAdapter<T> getAdapterFor(final int id) throws AxionTagRegistrationException {
    return configuration.getAdapterFor(id);
  }

  /**
   * Returns the registered {@link TagAdapter} for the tag class given.
   * <p>
   * If no adapter is found, an exception is thrown.
   * 
   * @param tagClass
   *          tag class to get the adapter for
   * @return the registered {@link TagAdapter} for the tag class given
   * @throws AxionTagRegistrationException
   */
  public <T extends Tag> TagAdapter<T> getAdapterFor(final Class<T> tagClass) throws AxionTagRegistrationException {
    return configuration.getAdapterFor(tagClass);
  }

  /**
   * Returns the registered {@link TagConverter} for the tag given.
   * <p>
   * If no converter is found, an exception is thrown.
   * 
   * @param tag
   *          tag to get the tag converter for
   * @return the registered {@link TagConverter} for the tag given
   * @throws AxionTagRegistrationException
   */
  public <T extends Tag, V> TagConverter<T, V> getConverterFor(final T tag) throws AxionTagRegistrationException {
    return configuration.getConverterFor(tag);
  }

  /**
   * Returns the registered {@link TagConverter} for the value given.
   * <p>
   * If no converter is found, an exception is thrown.
   * 
   * @param value
   *          value to get the tag converter for
   * @return the registered {@link TagConverter} for the value given
   * @throws AxionTagRegistrationException
   */
  public <T extends Tag, V> TagConverter<T, V> getConverterFor(final V value) throws AxionTagRegistrationException {
    return configuration.getConverterFor(value);
  }

  /**
   * Converts the given tag into its value using the {@link TagConverter}
   * registered for the tag's class.
   * <p>
   * If no converter is found, an exception is thrown.
   * 
   * @param tag
   *          tag to convert
   * @return the tag's converted value
   * @throws AxionTagRegistrationException
   */
  @SuppressWarnings("unchecked")
  public <T extends Tag, V> V convertToValue(final T tag) throws AxionTagRegistrationException {
    return (V) configuration.getConverterFor(tag).convert(tag);
  }

  /**
   * Converts the given value into its tag using the converter registered for
   * the value's class.
   * <p>
   * If no converter is found, an exception is thrown.
   * 
   * @param name
   *          name of the new tag
   * @param value
   *          value to convert
   * @return the value's converted tag
   * @throws AxionTagRegistrationException
   */
  @SuppressWarnings("unchecked")
  public <T extends Tag, V> T convertToTag(final String name, final V value) throws AxionTagRegistrationException {
    return (T) configuration.getConverterFor(value).convert(name, value);
  }

  /**
   * Returns the {@link NBTObjectMapper} registered for the class type given.
   * <p>
   * If no mapper is found, an exception is thrown.
   * 
   * @param type
   *          class type to get the {@link NBTObjectMapper} for
   * @return the {@link NBTObjectMapper} registered for the class type given
   * @throws AxionMapperRegistrationException
   */
  public <T extends Tag, O> NBTObjectMapper<T, O> getMapperFor(final Class<O> type) throws AxionMapperRegistrationException {
    return configuration.getMapperFor(type);
  }

  /**
   * Creates an object from a tag using the {@link NBTObjectMapper} registered
   * for the class given as type.
   * <p>
   * If no mapper is found, an exception is thrown.
   * 
   * @param tag
   *          tag to create the object from
   * @param type
   *          class used to lookup registered mapper
   * @return a new object mapped from the tag given
   * @throws AxionMapperRegistrationException
   */
  public <T extends Tag, O> O createObjectFrom(final T tag, final Class<O> type) throws AxionMapperRegistrationException {
    return configuration.getMapperFor(type).createObjectFrom(tag, this);
  }

  /**
   * Creates a tag from an object using the {@link NBTObjectMapper} registered
   * for the object's class.
   * <p>
   * If no mapper is found, an exception is thrown.
   * 
   * @param name
   *          name of the new tag
   * @param object
   *          object to convert
   * @return a new tag mapped from the object given
   * @throws AxionMapperRegistrationException
   */
  public <T extends Tag, O> T createTagFrom(final String name, final O object) throws AxionMapperRegistrationException {
    @SuppressWarnings("unchecked")
    NBTObjectMapper<T, O> mapper = (NBTObjectMapper<T, O>) configuration.getMapperFor(object.getClass());
    return mapper.createTagFrom(name, object, this);
  }

  /**
   * Reads a file into the {@link AxionWritable} given.
   * <p>
   * If no base tag adapter has been registered, an exception is thrown.
   * 
   * @param file
   *          the file to read from
   * @param axionWritable
   *          the {@link AxionWritable} to write to
   * @return the {@link AxionWritable} written to
   * @throws IOException
   */
  public <T extends AxionWritable<TagCompound>> T read(final File file, final T axionWritable) throws IOException, AxionTagRegistrationException {
    axionWritable.read(read(file), this);
    return axionWritable;
  }

  /**
   * Writes an {@link AxionWritable} to the {@link File} given.
   * 
   * @param axionWritable
   *          the {@link AxionWritable} to write to file
   * @param file
   *          file to write to
   * @throws IOException
   */
  public void write(final AxionWritable<TagCompound> axionWritable, final File file) throws IOException {
    write(axionWritable.write(this), file);
  }

  /**
   * Reads and returns a {@link TagCompound} from the {@link File} given.
   * <p>
   * If no base tag adapter has been registered, an exception is thrown.
   * 
   * @param file
   *          file to read
   * @return a {@link TagCompound}
   * @throws IOException
   */
  public TagCompound read(final File file) throws IOException, AxionTagRegistrationException {
    FileInputStream fileInputStream = new FileInputStream(file);
    TagCompound result = read(fileInputStream);
    fileInputStream.close();
    return result;
  }

  /**
   * Writes the {@link TagCompound} given to the {@link File} given.
   * 
   * @param tagCompound
   *          the tag to write
   * @param file
   *          the file to write to
   * @throws IOException
   */
  public void write(final TagCompound tagCompound, final File file) throws IOException {
    FileOutputStream fileOutputStream = new FileOutputStream(file);
    write(tagCompound, file);
    fileOutputStream.close();
  }

  /**
   * Reads and returns an {@link AxionWritable} from the {@link InputStream}
   * given.
   * 
   * @param inputStream
   *          stream to read from
   * @param axionWritable
   *          writable to write to
   * @return the {@link AxionWritable} written to
   * @throws IOException
   */
  public <T extends AxionWritable<TagCompound>> T read(final InputStream inputStream, final T axionWritable) throws IOException {
    axionWritable.read(read(inputStream), this);
    return axionWritable;
  }

  /**
   * Writes the {@link AxionWritable} given to the {@link OutputStream} given.
   * <p>
   * If no base tag adapter has been registered, an exception is thrown.
   * 
   * @param axionWritable
   *          {@link AxionWritable} to write
   * @param outputStream
   *          stream to write to
   * @throws IOException
   */
  public void write(final AxionWritable<TagCompound> axionWritable, final OutputStream outputStream) throws IOException, AxionTagRegistrationException {
    write(axionWritable.write(this), outputStream);
  }

  /**
   * Reads and returns a {@link TagCompound} from the {@link InputStream} given.
   * <p>
   * If no base tag adapter has been registered, an exception is thrown.
   * 
   * @param inputStream
   *          the stream to read from
   * @return the {@link TagCompound} read
   * @throws IOException
   */
  public TagCompound read(final InputStream inputStream) throws IOException, AxionTagRegistrationException {
    Tag result = readTag(null, configuration.wrap(inputStream));
    if (!(result instanceof TagCompound)) {
      throw new AxionReadException("Root tag not of type " + TagCompound.class.getSimpleName());
    }
    return (TagCompound) result;
  }

  /**
   * Writes the {@link TagCompound} given to the {@link OutputStream} given.
   * <p>
   * If no base tag adapter has been registered, an exception is thrown.
   * 
   * @param tagCompound
   *          the tag to write
   * @param outputStream
   *          the stream to write to
   * @throws IOException
   */
  public void write(final TagCompound tagCompound, final OutputStream outputStream) throws IOException, AxionTagRegistrationException {
    writeTag(tagCompound, configuration.wrap(outputStream));
  }

  /**
   * Reads and returns a {@link Tag} from the {@link AxionInputStream} given.
   * <p>
   * If no base tag adapter has been registered, an exception is thrown.
   * 
   * @param parent
   *          the tag requesting the read
   * @param in
   *          the stream to read from
   * @return the {@link Tag} read
   * @throws IOException
   */
  protected Tag readTag(final Tag parent, final AxionInputStream in) throws IOException, AxionTagRegistrationException {
    return configuration.getBaseTagAdapter().read(parent, in);
  }

  /**
   * Writes the {@link Tag} given to the {@link AxionOutputStream} given.
   * <p>
   * If no base tag adapter has been registered, an exception is thrown.
   * 
   * @param tag
   *          the tag to write
   * @param out
   *          the stream to write to
   * @throws IOException
   */
  protected void writeTag(final Tag tag, final AxionOutputStream out) throws IOException, AxionTagRegistrationException {
    configuration.getBaseTagAdapter().write(tag, out);
  }

}
