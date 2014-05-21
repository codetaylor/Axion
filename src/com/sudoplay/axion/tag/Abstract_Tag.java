package com.sudoplay.axion.tag;

public abstract class Abstract_Tag implements Interface_Tag {

  private String name;

  public Abstract_Tag(final String newName) {
    setName(newName);
  }

  public void setName(final String newName) {
    name = (newName == null) ? "" : newName;
  }

  public String getName() {
    return (name == null) ? "" : name;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Abstract_Tag other = (Abstract_Tag) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }

}
