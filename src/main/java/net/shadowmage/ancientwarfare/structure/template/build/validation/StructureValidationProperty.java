package net.shadowmage.ancientwarfare.structure.template.build.validation;

import net.minecraft.nbt.NBTTagCompound;

public class StructureValidationProperty
{

public static final int DATA_TYPE_UNKNOWN = 0;
public static final int DATA_TYPE_INT = 1;
public static final int DATA_TYPE_BYTE = 2;
public static final int DATA_TYPE_BOOLEAN = 3;
public static final int DATA_TYPE_FLOAT = 4;
public static final int DATA_TYPE_STRING = 5;
//public static final int DATA_TYPE_INT_ARRAY = 6;
//public static final int DATA_TYPE_BYTE_ARRAY = 7;
//public static final int DATA_TYPE_LONG = 8;
//public static final int DATA_TYPE_SHORT = 9;
//public static final int DATA_TYPE_DOUBLE = 10;

String regName;
int dataType;
Object data;

public StructureValidationProperty(String regName, int dataType, Object defaultValue)
  {
  this.regName = regName;
  this.data = defaultValue;
  this.dataType = dataType;
  }

public void setValue(Object value)
  {
  switch(dataType)
  {
  case DATA_TYPE_INT:
    {
    if(Integer.class.isAssignableFrom(value.getClass()) || int.class.isAssignableFrom(value.getClass()))
      {
      data = value;
      }
    }
  break;
  case DATA_TYPE_BYTE:
    {
    if(Byte.class.isAssignableFrom(value.getClass()) || byte.class.isAssignableFrom(value.getClass()))
      {
      data = value;
      }
    }
  break;
  case DATA_TYPE_FLOAT:
    {
    if(Float.class.isAssignableFrom(value.getClass()) || float.class.isAssignableFrom(value.getClass()))
      {
      data = value;
      }
    }
  break;
  case DATA_TYPE_BOOLEAN:
    {
    if(Boolean.class.isAssignableFrom(value.getClass()) || boolean.class.isAssignableFrom(value.getClass()))
      {
      data = value;
      }
    }
  break;
  case DATA_TYPE_STRING:
    {
    if(String.class.isAssignableFrom(value.getClass()))
      {
      data = value;
      }
    }
  break;  
  }
  this.data = value;
  }

public int getDataInt()
  {
  if(dataType==DATA_TYPE_INT)
    {
    return (Integer)data;
    }
  return 0;
  }

public float getDataFloat()
  {
  if(dataType==DATA_TYPE_FLOAT)
    {
    return (Float)data;
    }
  return 0.f;
  }

public String getDataString()
  {
  if(dataType==DATA_TYPE_STRING)
    {
    return (String)data;
    }
  return "";
  }

public boolean getDataBoolean()
  {
  if(dataType==DATA_TYPE_BOOLEAN)
    {
    return (Boolean)data;
    }
  return false;
  }

public void readFromNBT(NBTTagCompound tag)
  {
  switch(dataType)
  {
  case DATA_TYPE_INT:
    {
    data = tag.getInteger(regName);
    }
  break;
  case DATA_TYPE_BYTE:
    {
    data = tag.getByte(regName);
    }
  break;
  case DATA_TYPE_FLOAT:
    {
    data = tag.getFloat(regName);
    }
  break;
  case DATA_TYPE_BOOLEAN:
    {
    data = tag.getBoolean(regName);
    }
  break;
  case DATA_TYPE_STRING:
    {
    data = tag.getString(regName);
    }
  break;
  }
  }

public void writeToNBT(NBTTagCompound tag)
  {
  switch(dataType)
  {
  case DATA_TYPE_INT:
    {
    tag.setInteger(regName, (Integer)data);
    }
  break;
  case DATA_TYPE_BYTE:
    {
    tag.setByte(regName, (Byte)data);
    }
  break;
  case DATA_TYPE_FLOAT:
    {
    tag.setFloat(regName, (Float)data);
    }
  break;
  case DATA_TYPE_BOOLEAN:
    {
    tag.setBoolean(regName, (Boolean)data);
    }
  break;
  case DATA_TYPE_STRING:
    {
    tag.setString(regName, (String)data);
    }
  break;
  }
  }

public StructureValidationProperty copy()
  {
  StructureValidationProperty prop = new StructureValidationProperty(regName, dataType, data);
  return prop;  
  }
}
