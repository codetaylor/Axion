package com.sudoplay.axion.util;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a generic type {@code T}. Java doesn't yet provide a way to represent generic types, so this class does.
 * Forces clients to create a subclass of this class which enables retrieval the type information even at runtime.
 * <p>
 * For example, to create a type literal for {@code List<String>}, you can create an empty anonymous inner class:
 * <p>
 * <p>
 * {@code AxionTypeToken<List<String>> list = new AxionTypeToken<List<String>>() ;}
 * <p>
 * <p>
 * This syntax cannot be used to create type literals that have wildcard parameters, such as {@code Class<?>} or {@code
 * List<? extends CharSequence>}.
 * <p>
 * <p>
 * This class has been derived from the google-gson source <a href="https://code.google.com/p/google-gson/"
 * >https://code.google.com/p/google-gson/</a>.
 * <p>
 * <p>
 * This class contains trivial modifications.
 *
 * @author Bob Lee (original for gson)
 * @author Jesse Wilson (original for gson)
 * @author Sven Mawson (original for gson)
 * @author Jason Taylor (modified for Juple)
 *         <p>
 *         Created by Jason Taylor on 7/16/2015.
 */
public class AxionTypeToken<T> {

  final Class<? super T> rawType;
  final Type type;
  final int hashCode;

  /**
   * Constructs a new type literal. Derives represented class from type parameter.
   * <p>
   * Clients create an empty anonymous subclass. Doing so embeds the type parameter in the anonymous class's type
   * hierarchy so we can reconstitute it at runtime despite erasure.
   */
  @SuppressWarnings("unchecked")
  protected AxionTypeToken() {
    this.type = getSuperclassTypeParameter(getClass());
    this.rawType = (Class<? super T>) AxionType.getRawType(type);
    this.hashCode = type.hashCode();
  }

  /**
   * Unsafe. Constructs a type literal manually.
   */
  @SuppressWarnings("unchecked")
  AxionTypeToken(Type type) {
    this.type = AxionType.canonicalize(AxionContract.assertNotNull(type, "Type can't be null"));
    this.rawType = (Class<? super T>) AxionType.getRawType(this.type);
    this.hashCode = this.type.hashCode();
  }

  /**
   * Returns the type from super class's type parameter in {@link AxionType#canonicalize canonical form}.
   */
  static Type getSuperclassTypeParameter(Class<?> subclass) {
    Type superclass = subclass.getGenericSuperclass();
    if (superclass instanceof Class) {
      throw new RuntimeException("Missing type parameter.");
    }
    ParameterizedType parameterized = (ParameterizedType) superclass;
    return AxionType.canonicalize(parameterized.getActualTypeArguments()[0]);
  }

  /**
   * Returns the raw (non-generic) type for this type.
   */
  public final Class<? super T> getRawType() {
    return rawType;
  }

  /**
   * Gets underlying {@code Type} instance.
   */
  public final Type getType() {
    return type;
  }

  @Override
  public final int hashCode() {
    return this.hashCode;
  }

  @Override
  public final boolean equals(Object o) {
    return o instanceof AxionTypeToken<?>
        && AxionType.equals(type, ((AxionTypeToken<?>) o).type);
  }

  @Override
  public final String toString() {
    return AxionType.typeToString(type);
  }

  /**
   * Gets type literal for the given {@code Type} instance.
   */
  public static AxionTypeToken<?> get(Type type) {
    return new AxionTypeToken<Object>(type);
  }

  /**
   * Gets type literal for the given {@code Class} instance.
   */
  public static <T> AxionTypeToken<T> get(Class<T> type) {
    return new AxionTypeToken<>(type);
  }

  /**
   * Check if this type is assignable from the given class object.
   *
   * @deprecated this implementation may be inconsistent with javac for types with wildcards.
   */
  @Deprecated
  public boolean isAssignableFrom(Class<?> cls) {
    return isAssignableFrom((Type) cls);
  }

  /**
   * Check if this type is assignable from the given Type.
   *
   * @deprecated this implementation may be inconsistent with javac for types with wildcards.
   */
  @Deprecated
  public boolean isAssignableFrom(Type from) {
    if (from == null) {
      return false;
    }

    if (type.equals(from)) {
      return true;
    }

    if (type instanceof Class<?>) {
      return rawType.isAssignableFrom(AxionType.getRawType(from));
    } else if (type instanceof ParameterizedType) {
      return isAssignableFrom(from, (ParameterizedType) type,
          new HashMap<String, Type>());
    } else if (type instanceof GenericArrayType) {
      return rawType.isAssignableFrom(AxionType.getRawType(from))
          && isAssignableFrom(from, (GenericArrayType) type);
    } else {
      throw buildUnexpectedTypeError(type, Class.class,
          ParameterizedType.class, GenericArrayType.class);
    }
  }

  /**
   * Check if this type is assignable from the given type token.
   *
   * @deprecated this implementation may be inconsistent with javac for types with wildcards.
   */
  @Deprecated
  public boolean isAssignableFrom(AxionTypeToken<?> token) {
    return isAssignableFrom(token.getType());
  }

  /**
   * Private helper function that performs some assignability checks for the provided GenericArrayType.
   */
  private static boolean isAssignableFrom(Type from, GenericArrayType to) {
    Type toGenericComponentType = to.getGenericComponentType();
    if (toGenericComponentType instanceof ParameterizedType) {
      Type t = from;
      if (from instanceof GenericArrayType) {
        t = ((GenericArrayType) from).getGenericComponentType();
      } else if (from instanceof Class<?>) {
        Class<?> classType = (Class<?>) from;
        while (classType.isArray()) {
          classType = classType.getComponentType();
        }
        t = classType;
      }
      return isAssignableFrom(t, (ParameterizedType) toGenericComponentType,
          new HashMap<String, Type>());
    }
    // No generic defined on "to"; therefore, return true and let other
    // checks determine assignability
    return true;
  }

  /**
   * Private recursive helper function to actually do the type-safe checking of assignability.
   */
  private static boolean isAssignableFrom(Type from, ParameterizedType to,
                                          Map<String, Type> typeVarMap) {

    if (from == null) {
      return false;
    }

    if (to.equals(from)) {
      return true;
    }

    // First figure out the class and any type information.
    Class<?> clazz = AxionType.getRawType(from);
    ParameterizedType ptype = null;
    if (from instanceof ParameterizedType) {
      ptype = (ParameterizedType) from;
    }

    // Load up parameterized variable info if it was parameterized.
    if (ptype != null) {
      Type[] tArgs = ptype.getActualTypeArguments();
      TypeVariable<?>[] tParams = clazz.getTypeParameters();
      for (int i = 0; i < tArgs.length; i++) {
        Type arg = tArgs[i];
        TypeVariable<?> var = tParams[i];
        while (arg instanceof TypeVariable<?>) {
          TypeVariable<?> v = (TypeVariable<?>) arg;
          arg = typeVarMap.get(v.getName());
        }
        typeVarMap.put(var.getName(), arg);
      }

      // check if they are equivalent under our current mapping.
      if (typeEquals(ptype, to, typeVarMap)) {
        return true;
      }
    }

    for (Type itype : clazz.getGenericInterfaces()) {
      if (isAssignableFrom(itype, to, new HashMap<String, Type>(typeVarMap))) {
        return true;
      }
    }

    // Interfaces didn't work, try the superclass.
    Type sType = clazz.getGenericSuperclass();
    return isAssignableFrom(sType, to, new HashMap<String, Type>(typeVarMap));
  }

  private static AssertionError buildUnexpectedTypeError(Type token,
                                                         Class<?>... expected) {

    // Build exception message
    StringBuilder exceptionMessage = new StringBuilder(
        "Unexpected type. Expected one of: ");
    for (Class<?> clazz : expected) {
      exceptionMessage.append(clazz.getName()).append(", ");
    }
    exceptionMessage.append("but got: ").append(token.getClass().getName())
        .append(", for type token: ").append(token.toString()).append('.');

    return new AssertionError(exceptionMessage.toString());
  }

  /**
   * Checks if two parameterized types are exactly equal, under the variable replacement described in the typeVarMap.
   */
  private static boolean typeEquals(ParameterizedType from,
                                    ParameterizedType to, Map<String, Type> typeVarMap) {
    if (from.getRawType().equals(to.getRawType())) {
      Type[] fromArgs = from.getActualTypeArguments();
      Type[] toArgs = to.getActualTypeArguments();
      for (int i = 0; i < fromArgs.length; i++) {
        if (!matches(fromArgs[i], toArgs[i], typeVarMap)) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  /**
   * Checks if two types are the same or are equivalent under a variable mapping given in the type map that was
   * provided.
   */
  private static boolean matches(Type from, Type to, Map<String, Type> typeMap) {
    return to.equals(from)
        || (from instanceof TypeVariable && to.equals(typeMap
        .get(((TypeVariable<?>) from).getName())));

  }

}