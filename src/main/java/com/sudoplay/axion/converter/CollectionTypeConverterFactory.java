package com.sudoplay.axion.converter;

import com.sudoplay.axion.Axion;
import com.sudoplay.axion.AxionWriteException;
import com.sudoplay.axion.registry.TypeConverter;
import com.sudoplay.axion.registry.TypeConverterFactory;
import com.sudoplay.axion.spec.tag.TagList;
import com.sudoplay.axion.system.ObjectConstructor;
import com.sudoplay.axion.tag.Tag;
import com.sudoplay.axion.util.AxionType;
import com.sudoplay.axion.util.AxionTypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Jason Taylor on 7/17/2015.
 */
public class CollectionTypeConverterFactory implements TypeConverterFactory {
  @Override
  public <T extends Tag, V> TypeConverter<T, V> create(
      Axion axion,
      AxionTypeToken<V> typeToken
  ) {
    Type type = typeToken.getType();

    Class<? super V> rawType = typeToken.getRawType();
    if (!Collection.class.isAssignableFrom(rawType)) {
      return null;
    }

    Class<?> rawTypeOfSrc = AxionType.getRawType(type);
    Type elementType = AxionType.getCollectionElementType(type, rawTypeOfSrc);

    TypeConverter<? extends Tag, ?> elementConverter = axion.getConverter(AxionTypeToken.get(elementType));
    ObjectConstructor<V> constructor = axion.getConstructorConstructor(typeToken);
    @SuppressWarnings("unchecked")
    TypeConverter<T, V> result = new Converter(elementConverter, constructor);
    return result;
  }

  @Override
  public TypeConverterFactory newInstance(Axion axion) {
    return new CollectionTypeConverterFactory();
  }

  private class Converter<V> extends TypeConverter<TagList, Collection<V>> {

    private final TypeConverter<Tag, V> elementConverter;
    private final ObjectConstructor<? extends List<V>> constructor;

    private Converter(
        TypeConverter<Tag, V> elementConverter,
        ObjectConstructor<? extends List<V>> constructor
    ) {
      this.elementConverter = elementConverter;
      this.constructor = constructor;
    }

    @Override
    public Collection<V> convert(TagList tagList) {
      Collection<V> collection = constructor.construct();
      tagList.forEach(tag -> {
        V value = elementConverter.convert(tag);
        collection.add(value);
      });
      return collection;
    }

    @SuppressWarnings("unchecked")
    @Override
    public TagList convert(String name, Collection<V> collection) {
      if (collection.isEmpty()) {
        throw new AxionWriteException("Can't write an empty collection");
      }

      V value = collection.iterator().next();

      if (value == null) {
        throw new AxionWriteException("Can't write a collection with a null value");
      }

      Tag valueTag = elementConverter.convert(null, value);

      TagList list = new TagList(valueTag.getClass());

      collection.forEach(v -> {
        if (v == null) {
          throw new AxionWriteException("Can't write a collection with a null value");
        }
        list.add(elementConverter.convert(null, v));
      });

      return list;
    }
  }
}
