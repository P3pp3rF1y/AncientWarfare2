package net.shadowmage.ancientwarfare.core.util;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.nbt.NBTBase;
import net.shadowmage.ancientwarfare.core.config.AWLog;

public class Json
{

/**
 * Return a tag-type-id string for the given NBT base tag, for use in JsonTagReader and JsonTagWriter methods
 * @param tag
 * @return
 */
public static String getTagType(NBTBase tag)
  {
  byte id = tag.getId();
  switch(id)
  {
  case 1://byte
  return "pb";
  case 2://short
  return "ps";
  case 3://int
  return "pi";
  case 4://long
  return "pl";
  case 5://float
  return "pf";
  case 6://double
  return "pd";  
  case 7://array byte
  return "ab";
  case 8://string
  return "ss";
  case 9://list
  return "ls";
  case 10://compound
  return "ct";
  case 11://array int
  return "ai";
  }
  return null;
  }

/**
 * Return a string representing the input JsonObject.  This string should be suitable for reading back through the parseJson method (and/or most other JSON parsers)
 * @param json
 * @return
 */
public static String getJsonData(JsonObject json){return "JSON:{"+json.getJsonString()+"}";}

/**
 * Parse a single-line string representing a json formatted object prefixed with JSON: and wrapped in brackets {}<br>
 * Returns a JsonObject representing the contents of the input string
 * @param data
 * @return
 */
public static JsonObject parseJson(String data)
  {
  if(data.startsWith("JSON:{") && data.endsWith("}"))
    {
    JsonParser parser = new JsonParser(data.substring(6, data.length()-1));
    try
      {
      return parser.process();
      }
    catch (IOException e)
      {
      e.printStackTrace();
      }     
    }
  return null;
  }

public static JsonObject parseJson(Reader reader)
  {
  JsonParser parser = new JsonParser(reader);
  try
    {
    return parser.process();
    } 
  catch (IOException e)
    {
    e.printStackTrace();
    }
  return null;
  }

/**
 * Internal helper for parsing of Json raw data, basically  to allow use of instance variables from a static method.
 * @author Shadowmage
 */
private static class JsonParser
{

Reader reader;

int rawChar;
int charIndex = -1;

boolean readEnd = false;
boolean atEnd = false;
int bufferStart = 0;
int bufferEnd = 0;
char[] readBuffer = new char[1024];
char currentChar;

protected JsonParser(String data)
  {
  this.reader = new StringReader(data);
  }

protected JsonParser(Reader reader){this.reader = reader;}

protected JsonObject process() throws IOException
  {
  readRawChar();
  return readObject();
  }

private void readRawChar() throws IOException
  {
  if(atEnd){return;}
  charIndex++;//will be 0 at first iteration
  if(charIndex >= bufferEnd)//refresh buffer, will be 0 at first iteration, causing initial buffer load
    {
    if(readEnd)
      {
      atEnd = true;
      return;
      }
    bufferStart = charIndex;
    bufferEnd = bufferStart + readBuffer.length;
    int read = reader.read(readBuffer);
    if(bufferStart+read<bufferEnd)
      {
      bufferEnd=bufferStart+read;
      readEnd=true;
      }   
    }
  int index = charIndex - bufferStart;
  if(index >= bufferEnd)
    {
    charIndex = -1;
    rawChar = -1;
    currentChar = ' ';
    return;
    }
  rawChar = readBuffer[index];
  currentChar = readBuffer[index];
  }

private void skipBlanks() throws IOException
  {
  while(rawChar==' ' || rawChar=='\r' || rawChar== '\n')
    {
    readRawChar();
    }
  }

protected JsonAbstract readAbstract() throws IOException
  {
  if(rawChar=='{')
    {
    return readObject();
    }
  else if(rawChar=='[')
    {
    return readArray();
    }
  return readValue();
  }

/**
 * rawChar should == '{' at the start of this call
 * @return
 * @throws IOException
 */
protected JsonObject readObject() throws IOException
  { 
  if(rawChar!='{'){throw throwUnexpectedException("expected object start {");}
  JsonObject object = new JsonObject();
  readRawChar();
  skipBlanks();
  if(rawChar=='}')
    {
    readRawChar();//read to next char
    skipBlanks();//advance to next valid character
    return object;
    }//end was detected with nothing intervening
  
  String name;
  JsonAbstract value;
  
  while(rawChar!='}')
    {
    skipBlanks();
    name = readName();
    skipBlanks();
    if(rawChar!=':'){throw throwUnexpectedException("Did not find name separator : while parsing object");}
    readRawChar();//pull the next valid char, should be starter for array or object, quote for value start, or a digit for a value
    skipBlanks();
    value = readAbstract();//parse the next object in
    object.writeAbstract(name, value);//add it to object map
    skipBlanks();//advance to next valid character
    if(rawChar==',')
      {
      readRawChar();
      skipBlanks();
      }    
    }
  readRawChar();//read to next char
  skipBlanks();//advance to next valid character
  return object;
  }

/**
 * raw char should == '[' at the start of this call
 * @return
 * @throws IOException
 */
protected JsonArray readArray() throws IOException
  {
  if(rawChar!='['){throw throwUnexpectedException("expected array start [");}
  JsonArray array = new JsonArray();
  readRawChar();
  skipBlanks();//advance to next valid char
  if(rawChar==']')
    {
    readRawChar();//read to next char
    skipBlanks();//advance to next valid character
    return array;
    }//end was detected with nothing intervening  
  JsonAbstract value;  
  while(rawChar!=']')
    {
    skipBlanks();
    value = readAbstract();//parse the next object in dataset
    array.add(value);
    skipBlanks();//advance to next valid character    
    if(rawChar==',')
      {
      readRawChar();//advance past comma
      skipBlanks();
      }    
    }
  readRawChar();//read to next char
  skipBlanks();//advance to next valid character
  return array;
  }

/**
 * raw char should == '"' at the start of this call
 * @return
 * @throws IOException
 */
protected JsonValue readValue() throws IOException
  {
  String value = readString();
  JsonValue jsonValue = new JsonValue(value);
  return jsonValue; 
  }

private String readName() throws IOException
  {
  if(rawChar!='"'){throw throwUnexpectedException("Did not find name start while parsing object");}
  return readString();
  }

private String readString() throws IOException
  {
  if(rawChar!='"'){throw throwUnexpectedException("Did not find string entry while parsing value");}
  StringBuilder builder = new StringBuilder();
  readRawChar();
  while(rawChar!='"')
    {
    builder.append(currentChar);
    readRawChar();
    }
  readRawChar();
  return builder.toString();
  }

private JsonParsingException throwUnexpectedException(String message)
  {
  return new JsonParsingException(message+"\n"+"At char index: "+charIndex+" char: " + currentChar);
  }

@SuppressWarnings("serial")
public static class JsonParsingException extends RuntimeException
  {
  public JsonParsingException(String message)
    {
    super(message);
    }  
  }

}

/**
 * Abstract Json base class for all json data objects.  Should not be extended or reused outside existing uses (JsonObject, JsonArray, JsonValue)
 * @author Shadowmage
 */
public abstract static class JsonAbstract
{
protected abstract String getJsonString();
}

/**
 * Denotes a complex Json Object with named fields.  Fields may be retrieved by type or as abstract objects.  Essentially a string-value map of names to JsonAbstract objects.
 * @author Shadowmage
 *
 */
public static final class JsonObject extends JsonAbstract
{

private HashMap<String, JsonAbstract> fields = new HashMap<String, JsonAbstract>();

public JsonArray getArray(String name)
  {
  JsonAbstract a = fields.get(name);
  return a instanceof JsonArray ? (JsonArray)a : null;
  }

public JsonValue getValue(String name)
  {
  JsonAbstract a = fields.get(name);
  return a instanceof JsonValue ? (JsonValue)a : null;
  }

public JsonObject getObject(String name)
  {
  JsonAbstract a = fields.get(name);
  return a instanceof JsonObject ? (JsonObject)a : null;
  }

public JsonAbstract getAbstract(String name){return fields.get(name);}

public Set<String> keySet(){return fields.keySet();}

public void writeObject(String name, JsonObject object){fields.put(name, object);}//TODO null object handling

public void writeArray(String name, JsonArray array){fields.put(name, array);}//TODO null array handling

public void writeValue(String name, JsonValue value){fields.put(name, value);}//TODO null value handling

public void writeAbstract(String name, JsonAbstract value){fields.put(name, value);}

@Override
protected String getJsonString()
  {
  String data = "{";
  Iterator<String> it = fields.keySet().iterator();
  String key;
  while(it.hasNext() && (key=it.next())!=null)
    {
    data = data + "\"" + key + "\":" + fields.get(key).getJsonString();
    if(it.hasNext()){data=data+",";}
    }
  data = data + "}";
  return data;
  }
}

/**
 * Denotes an array of JsonAbstract objects, essentially an unchecked list.  Ordering should be consistent.  No remove operations are given as this is a data read/write format, not storage.<br>
 * These objects may be JsonObject, JsonArray, or JsonValue types, no consistency checking is enforced by the class
 * @author Shadowmage
 */
public static final class JsonArray extends JsonAbstract
{

private List<JsonAbstract> values = new ArrayList<JsonAbstract>();

public void add(JsonAbstract value){values.add(value);}

public int size(){return values.size();}

public JsonAbstract getAbstract(int index){return values.get(index);}

public JsonArray getArray(int index)
  {
  JsonAbstract a = values.get(index);
  return a instanceof JsonArray ? (JsonArray)a : null;
  }

public JsonValue getValue(int index)
  {
  JsonAbstract a = values.get(index);
  return a instanceof JsonValue ? (JsonValue)a : null;
  }

public JsonObject getObject(int index)
  {
  JsonAbstract a = values.get(index);
  return a instanceof JsonObject ? (JsonObject)a : null;
  }

@Override
protected String getJsonString()
  {
  String data = "[";
  Iterator<JsonAbstract> it = values.iterator();
  JsonAbstract value;
  while(it.hasNext() && (value=it.next())!=null)
    {
    data = data + value.getJsonString();
    if(it.hasNext()){data=data+",";}
    }
  data = data+"]";
  return data;
  }

}

/**
 * Denotes a single primitive value (string, boolean, byte, short, int, long, float, double)<br>
 * @author Shadowmage
 */
public static final class JsonValue extends JsonAbstract
{

String value;

public JsonValue(String value){this.value=value;}

public String getStringValue(){return value;}

public boolean getBooleanValue(){return Boolean.parseBoolean(value);}

public long getIntegerValue()
  {
  try{return Long.parseLong(value);}
  catch(NumberFormatException e){return 0;}
  }

public double getFloatValue()
  {
  try{return Double.parseDouble(value);}
  catch(NumberFormatException e){return 0;}
  }

@Override
public String toString()
  {
  return value;
  }

@Override
protected String getJsonString(){return "\""+value+"\"";}

}

}
