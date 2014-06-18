package net.shadowmage.ancientwarfare.structure.template.build.validation;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.util.StringTools;

public class StructureValidationProperty
{

public static final int DATA_TYPE_UNKNOWN = 0;
public static final int DATA_TYPE_INT = 1;
public static final int DATA_TYPE_BYTE = 2;
public static final int DATA_TYPE_BOOLEAN = 3;
public static final int DATA_TYPE_FLOAT = 4;
public static final int DATA_TYPE_STRING = 5;
public static final int DATA_TYPE_INT_ARRAY = 6;
public static final int DATA_TYPE_STRING_SET = 7;

String regName;
int dataType;
Object data;

public StructureValidationProperty(String regName, int dataType, Object defaultValue)
  {
  this.regName = regName;
  this.data = defaultValue;
  this.dataType = dataType;
  }

public int getDataType()
  {
  return dataType;
  }

public String getRegName()
  {
  return regName;
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
  case DATA_TYPE_STRING_SET:
    {
    if(Set.class.isAssignableFrom(value.getClass()))
      {
      data = value;
      }
    }
  break; 
  case DATA_TYPE_INT_ARRAY:
    {
    if(int[].class.isAssignableFrom(value.getClass()))
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

public int[] getDataIntArray()
  {
  if(dataType==DATA_TYPE_INT_ARRAY)
    {
    return (int[])data;
    }
  return new int[0];
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

@SuppressWarnings("unchecked")
public Set<String> getDataStringSet()
  {
  return (Set<String>)data;
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
  case DATA_TYPE_INT_ARRAY:
    {
    data = tag.getIntArray(regName);
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
  case DATA_TYPE_STRING_SET:
    {
    Set<String> data = new HashSet<String>();
    NBTTagList names = tag.getTagList(regName, Constants.NBT.TAG_STRING);
    for(int i = 0; i < names.tagCount(); i++)
      {
      data.add(names.getStringTagAt(i));
      }
    this.data = data;
    }
  break;
  }
  }

@SuppressWarnings("unchecked")
public void writeToNBT(NBTTagCompound tag)
  {
  switch(dataType)
  {
  case DATA_TYPE_INT:
    {
    tag.setInteger(regName, (Integer)data);
    }
  break;
  case DATA_TYPE_INT_ARRAY:
    {
    tag.setIntArray(regName, (int[])data);
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
  case DATA_TYPE_STRING_SET:
    {
    NBTTagList names = new NBTTagList();
    Set<String> data = (Set<String>)this.data;
    for(String name : data)
      {
      names.appendTag(new NBTTagString(name));
      }
    tag.setTag(regName, names);
    }    
  break;  
  }
  }

public StructureValidationProperty copy()
  {
  /**
   * TODO need to do a switch on data type, return a new object representing data from current object, so there
   * are no reference collisions when altering the returned property
   */
  return new StructureValidationProperty(regName, dataType, data);
  }
}
