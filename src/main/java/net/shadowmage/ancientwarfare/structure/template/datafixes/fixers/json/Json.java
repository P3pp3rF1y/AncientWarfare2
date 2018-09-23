package net.shadowmage.ancientwarfare.structure.template.datafixes.fixers.json;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Json {
	private Json() {}

	/*
	 * Return a string representing the input JsonObject.  This string should be suitable for reading back through the parseJson method (and/or most other JSON parsers)
	 */
	public static String getJsonData(JsonObject json) {
		return "JSON:{" + json.getJsonString() + "}";
	}

	/*
	 * Parse a single-line string representing a json formatted object prefixed with JSON: and wrapped in brackets {}<br>
	 * Returns a JsonObject representing the contents of the input string
	 */
	public static Optional<JsonObject> parseJson(String data) {
		if (isSerializedJSON(data)) {
			JsonParser parser = new JsonParser(data.substring(6, data.length() - 1));
			try {
				return Optional.of(parser.process());
			}
			catch (IOException e) {
				//noop
			}
		}
		return Optional.empty();
	}

	static boolean isSerializedJSON(String data) {
		return data.startsWith("JSON:{") && data.endsWith("}");
	}

	/*
	 * Internal helper for parsing of Json raw data, basically  to allow use of instance variables from a static method.
	 *
	 * @author Shadowmage
	 */
	private static class JsonParser {
		private Reader reader;
		private int rawChar;
		private int charIndex = -1;

		private boolean readEnd = false;
		private boolean atEnd = false;
		private int bufferStart = 0;
		private int bufferEnd = 0;
		private char[] readBuffer = new char[1024];
		private char currentChar;

		protected JsonParser(String data) {
			this.reader = new StringReader(data);
		}

		protected JsonParser(Reader reader) {
			this.reader = reader;
		}

		protected JsonObject process() throws IOException {
			readRawChar();
			return readObject();
		}

		private void readRawChar() throws IOException {
			if (atEnd) {
				return;
			}
			charIndex++;//will be 0 at first iteration
			if (charIndex >= bufferEnd)//refresh buffer, will be 0 at first iteration, causing initial buffer load
			{
				if (readEnd) {
					atEnd = true;
					return;
				}
				bufferStart = charIndex;
				bufferEnd = bufferStart + readBuffer.length;
				int read = reader.read(readBuffer);
				if (bufferStart + read < bufferEnd) {
					bufferEnd = bufferStart + read;
					readEnd = true;
				}
			}
			int index = charIndex - bufferStart;
			if (index >= bufferEnd) {
				charIndex = -1;
				rawChar = -1;
				currentChar = ' ';
				return;
			}

			rawChar = readBuffer[index];
			currentChar = readBuffer[index];

			//test if char at index == '\', if so, examine the NEXT char, if it was an escaped char
		}

		private void skipBlanks() throws IOException {
			while (rawChar == ' ' || rawChar == '\r' || rawChar == '\n') {
				readRawChar();
			}
		}

		private JsonAbstract readAbstract() throws IOException {
			if (rawChar == '{') {
				return readObject();
			} else if (rawChar == '[') {
				return readArray();
			}
			return readValue();
		}

		/*
		 * rawChar should == '{' at the start of this call
		 */
		private JsonObject readObject() throws IOException {
			if (rawChar != '{') {
				throw throwUnexpectedException("expected object start {");
			}
			JsonObject object = new JsonObject();
			readRawChar();
			skipBlanks();
			if (rawChar == '}') {
				readRawChar();//read to next char
				skipBlanks();//advance to next valid character
				return object;
			}//end was detected with nothing intervening

			String name;
			JsonAbstract value;

			while (rawChar != '}') {
				skipBlanks();
				name = readName();
				skipBlanks();
				if (rawChar != ':') {
					throw throwUnexpectedException("Did not find name separator : while parsing object");
				}
				readRawChar();//pull the next valid char, should be starter for array or object, quote for value start, or a digit for a value
				skipBlanks();
				value = readAbstract();//parse the next object in
				object.writeAbstract(name, value);//add it to object map
				skipBlanks();//advance to next valid character
				if (rawChar == ',') {
					readRawChar();
					skipBlanks();
				}
			}
			readRawChar();//read to next char
			skipBlanks();//advance to next valid character
			return object;
		}

		/*
		 * raw char should == '[' at the start of this call
		 */
		private JsonArray readArray() throws IOException {
			if (rawChar != '[') {
				throw throwUnexpectedException("expected array start [");
			}
			JsonArray array = new JsonArray();
			readRawChar();
			skipBlanks();//advance to next valid char
			if (rawChar == ']') {
				readRawChar();//read to next char
				skipBlanks();//advance to next valid character
				return array;
			}//end was detected with nothing intervening
			JsonAbstract value;
			while (rawChar != ']') {
				skipBlanks();
				value = readAbstract();//parse the next object in dataset
				array.add(value);
				skipBlanks();//advance to next valid character
				if (rawChar == ',') {
					readRawChar();//advance past comma
					skipBlanks();
				}
			}
			readRawChar();//read to next char
			skipBlanks();//advance to next valid character
			return array;
		}

		/*
		 * raw char should == '"' at the start of this call
		 */
		private JsonValue readValue() throws IOException {
			return new JsonValue(readString());
		}

		private String readName() throws IOException {
			if (rawChar != '"') {
				throw throwUnexpectedException("Did not find name start while parsing object");
			}
			return readString();
		}

		private String readString() throws IOException {
			if (rawChar != '"') {
				throw throwUnexpectedException("Did not find string entry while parsing value");
			}
			StringBuilder builder = new StringBuilder();
			readRawChar();
			char prevChar;
			while (rawChar != '"') {
				prevChar = currentChar;
				readRawChar();
				if (prevChar == '\\' && rawChar == '"')//prev char was an escape sequence, current char is a quote, remove escape from text, add quote
				{
					prevChar = currentChar;
					readRawChar();
				}
				builder.append(prevChar);
			}
			readRawChar();
			return builder.toString();
		}

		private JsonParsingException throwUnexpectedException(String message) {
			return new JsonParsingException(message + "\n" + "At char index: " + charIndex + " char: " + currentChar);
		}

		private static class JsonParsingException extends RuntimeException {
			private JsonParsingException(String message) {
				super(message);
			}
		}

	}

	/*
	 * Abstract Json base class for all json data objects.  Should not be extended or reused outside existing uses (JsonObject, JsonArray, JsonValue)
	 *
	 * @author Shadowmage
	 */
	public abstract static class JsonAbstract {
		protected abstract String getJsonString();
	}

	/*
	 * Denotes a complex Json Object with named fields.  Fields may be retrieved by type or as abstract objects.  Essentially a string-value map of names to JsonAbstract objects.
	 *
	 * @author Shadowmage
	 */
	public static final class JsonObject extends JsonAbstract {

		private HashMap<String, JsonAbstract> fields = new HashMap<>();

		public JsonValue getValue(String name) {
			JsonAbstract a = fields.get(name);
			return a instanceof JsonValue ? (JsonValue) a : null;
		}

		public JsonObject getObject(String name) {
			JsonAbstract a = fields.get(name);
			return a instanceof JsonObject ? (JsonObject) a : null;
		}

		public JsonAbstract getAbstract() {
			return fields.get("val");
		}

		public Set<String> keySet() {
			return fields.keySet();
		}

		public void writeAbstract(String name, JsonAbstract value) {
			fields.put(name, value);
		}

		@Override
		protected String getJsonString() {
			StringBuilder data = new StringBuilder("{");
			Iterator<String> it = fields.keySet().iterator();
			String key;
			while (it.hasNext() && (key = it.next()) != null) {
				data.append("\"").append(key).append("\":").append(fields.get(key).getJsonString());
				if (it.hasNext()) {
					data.append(",");
				}
			}
			data.append("}");
			return data.toString();
		}
	}

	/*
	 * Denotes an array of JsonAbstract objects, essentially an unchecked list.  Ordering should be consistent.  No remove operations are given as this is a data read/write format, not storage.<br>
	 * These objects may be JsonObject, JsonArray, or JsonValue types, no consistency checking is enforced by the class
	 *
	 * @author Shadowmage
	 */
	public static final class JsonArray extends JsonAbstract {

		private List<JsonAbstract> values = new ArrayList<>();

		public void add(JsonAbstract value) {
			values.add(value);
		}

		public int size() {
			return values.size();
		}

		public JsonValue getValue(int index) {
			JsonAbstract a = values.get(index);
			return a instanceof JsonValue ? (JsonValue) a : null;
		}

		public JsonObject getObject(int index) {
			JsonAbstract a = values.get(index);
			return a instanceof JsonObject ? (JsonObject) a : null;
		}

		@Override
		protected String getJsonString() {
			StringBuilder data = new StringBuilder("[");
			Iterator<JsonAbstract> it = values.iterator();
			JsonAbstract value;
			while (it.hasNext() && (value = it.next()) != null) {
				data.append(value.getJsonString());
				if (it.hasNext()) {
					data.append(",");
				}
			}
			data.append("]");
			return data.toString();
		}

	}

	/*
	 * Denotes a single primitive value (string, boolean, byte, short, int, long, float, double)<br>
	 *
	 * @author Shadowmage
	 */
	public static final class JsonValue extends JsonAbstract {
		private String value;

		public JsonValue(String value) {
			this.value = value;
		}

		public String getStringValue() {
			return value;
		}

		public void setStringValue(String value) {
			this.value = value;
		}

		public long getIntegerValue() {
			try {
				return Long.parseLong(value);
			}
			catch (NumberFormatException e) {
				return 0;
			}
		}

		public double getFloatValue() {
			try {
				return Double.parseDouble(value);
			}
			catch (NumberFormatException e) {
				return 0;
			}
		}

		@Override
		public String toString() {
			return value;
		}

		@Override
		protected String getJsonString() {
			String val = value.replace("\"", "\\\"");
			return "\"" + val + "\"";
		}
	}

}
