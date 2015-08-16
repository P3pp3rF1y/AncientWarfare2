/**
 Copyright 2012 John Cummens (aka Shadowmage, Shadowmage4513)
 This software is distributed under the terms of the GNU General Public License.
 Please see COPYING for precise license information.

 This file is part of Ancient Warfare.

 Ancient Warfare is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Ancient Warfare is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Ancient Warfare.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.shadowmage.ancientwarfare.core.util;

import cpw.mods.fml.common.registry.GameData;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

import java.io.*;
import java.util.*;

public class StringTools {

    public static String getCSVfor(Set<String> values) {
        StringBuilder b = new StringBuilder();
        Iterator<String> it = values.iterator();
        while (it.hasNext()) {
            b.append(it.next());
            if (it.hasNext()) {
                b.append(",");
            }
        }
        return b.toString();
    }

    public static Set<String> safeParseStringsToSet(Set<String> toFill, String test, String line, boolean lowerCase) {
        String[] lines = safeParseStringArray("=", line);
        return parseStringsToSet(toFill, lines, lowerCase);
    }

    public static Set<String> parseStringsToSet(Set<String> toFill, String[] data, boolean lowerCase) {
        for (String name : data) {
            toFill.add(lowerCase ? name.toLowerCase(Locale.ENGLISH) : name);
        }
        return toFill;
    }

    public static String getCSVStringForArray(float[] values) {
        String line = "";
        for (int i = 0; i < values.length; i++) {
            if (i >= 1) {
                line = line + ",";
            }
            line = line + values[i];
        }
        return line;
    }

    public static String getCSVStringForArray(byte[] values) {
        String line = "";
        for (int i = 0; i < values.length; i++) {
            if (i >= 1) {
                line = line + ",";
            }
            line = line + values[i];
        }
        return line;
    }

    public static String getCSVStringForArray(int[] values) {
        if (values == null) {
            return "";
        }
        String line = "";
        for (int i = 0; i < values.length; i++) {
            if (i >= 1) {
                line = line + ",";
            }
            line = line + values[i];
        }
        return line;
    }

    public static String getCSVValueFor(String[] values) {
        String line = "";
        for (int i = 0; i < values.length; i++) {
            if (i >= 1) {
                line = line + ",";
            }
            line = line + values[i];
        }
        return line;
    }

    /**
     * splits test at regex, returns parsed int array from csv value of remaining string
     * returns size 1 int array if no valid split is found
     */
    public static int[] safeParseIntArray(String regex, String test) {
        String[] splits = test.split(regex);
        if (splits.length > 1) {
            return parseIntArray(splits[1]);
        }
        return new int[0];
    }

    public static int[] parseIntArray(String csv) {
        String[] splits = csv.split(",");
        int[] array = new int[splits.length];
        for (int i = 0; i < splits.length; i++) {
            array[i] = Integer.parseInt(splits[i].trim());
        }
        return array;
    }

    public static short[] safeParseShortArray(String regex, String test) {
        String[] splits = test.split(regex);
        if (splits.length > 1) {
            return parseShortArray(splits[1]);
        }
        return new short[0];
    }

    public static short[] parseShortArray(String csv) {
        String[] splits = csv.split(",");
        short[] array = new short[splits.length];
        for (int i = 0; i < splits.length; i++) {
            array[i] = Short.parseShort(splits[i].trim());
        }
        return array;
    }

    /**
     * splits test at regex, returns parsed byte array from csv value of remaining string
     * returns size 1 byte array if no valid split is found
     */
    public static byte[] safeParseByteArray(String regex, String test) {
        String[] splits = test.split(regex);
        if (splits.length > 1) {
            return parseByteArray(splits[1].trim());
        }
        return new byte[0];
    }

    public static byte[] parseByteArray(String csv) {
        String[] splits = csv.split(",");
        byte[] array = new byte[splits.length];
        for (int i = 0; i < splits.length; i++) {
            array[i] = Byte.parseByte(splits[i].trim());
        }
        return array;
    }

    public static float[] safeParseFloatArray(String regex, String test) {
        String[] splits = test.split(regex);
        if (splits.length > 1) {
            return parseFloatArray(splits[1].trim());
        }
        return new float[0];
    }

    public static float[] parseFloatArray(String csv) {
        String[] splits = csv.split(",");
        float[] array = new float[splits.length];
        for (int i = 0; i < splits.length; i++) {
            array[i] = Float.parseFloat(splits[i].trim());
        }
        return array;
    }

    public static String[] safeParseStringArray(String regex, String test) {
        String[] splits = test.split(regex);
        if (splits.length > 1) {
            return parseStringArray(splits[1]);
        }
        splits = new String[0];
        return splits;
    }

    public static String[] parseStringArray(String csv) {
        return csv.split(",", -1);
    }

    /**
     * returns beginning of string up to len
     */
    public static String subStringBeginning(String in, int len) {
        return len > in.length() ? in : in.substring(0, len);
    }

    /**
     * returns the value after a split at regex, or false
     */
    public static boolean safeParseBoolean(String regex, String test) {
        String[] split = test.split(regex);
        return split.length > 1 && Boolean.parseBoolean(split[1].trim());
    }

    /**
     * returns the value after a split at regex, or false
     */
    public static boolean safeParseBoolean(String test) {
        return Boolean.parseBoolean(test);
    }

    public static boolean safeParseIntAsBoolean(String regex, String test) {
        String[] split = test.split(regex);
        return split.length > 1 && Integer.parseInt(split[1].trim()) == 1;
    }

    public static float safeParseFloat(String regex, String test) {
        String[] split = test.trim().split(regex);
        if (split.length > 1) {
            return safeParseFloat(split[1]);
        }
        return 0;
    }

    public static float safeParseFloat(String val) {
        try {
            return Float.parseFloat(val.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * returns a value after a split at regex, or an empty string
     */
    public static String safeParseString(String regex, String test) {
        String[] split = test.split(regex);
        if (split.length > 1) {
            return split[1];
        }
        return "";
    }

    /**
     * @param name of the item to search in the registries
     * @return the item instance, or null if none is found
     */
    public static Item safeParseItem(String name) {
        Item item = GameData.getItemRegistry().getObject(name);
        if (item == GameData.getItemRegistry().getDefaultValue()) {
            Block block = GameData.getBlockRegistry().getObject(name);
            if (block == GameData.getBlockRegistry().getDefaultValue())
                return null;
            else
                return Item.getItemFromBlock(block);
        }
        return item;
    }

    /**
     * @param name of the item to search in the registries
     * @param meta to parse for item damage
     * @param qty  to parse for item stack size
     * @return the ItemStack instance, or null if any necessary argument is invalid
     */
    public static ItemStack safeParseStack(String name, String meta, String qty) {

        Item item = safeParseItem(name);
        if (item == null) {
            return null;
        }
        int i = StringTools.safeParseInt(qty);
        if (i <= 0) {
            return null;
        }
        return new ItemStack(item, i, StringTools.safeParseInt(meta));
    }

    public static int safeParseInt(String num) {
        try {
            return Integer.parseInt(num.trim());
        } catch (NumberFormatException e) {

        }
        return 0;
    }

    public static double safeParseDouble(String num) {
        try {
            return Double.parseDouble(num.trim());
        } catch (NumberFormatException e) {

        }
        return 0;
    }

    public static long safeParseLong(String num) {
        try {
            return Long.parseLong(num.trim());
        } catch (NumberFormatException e) {

        }
        return 0;
    }

    public static byte safeParseByte(String num) {
        try {
            return Byte.parseByte(num.trim());
        } catch (NumberFormatException e) {

        }
        return 0;
    }

    public static short safeParseShort(String num) {
        try {
            return Short.parseShort(num.trim());
        } catch (NumberFormatException e) {

        }
        return 0;
    }

    /**
     * returns a value after a split at regex, or zero (0)
     */
    public static int safeParseInt(String regex, String test) {
        String[] split = test.split(regex);
        if (split.length > 1) {
            return Integer.parseInt(split[1].trim());
        }
        return 0;
    }

    /**
     * returns a value after a split at regex, or zero (0)
     */
    public static byte safeParseByte(String regex, String test) {
        String[] split = test.split(regex);
        if (split.length > 1) {
            return Byte.parseByte(split[1].trim());
        }
        return 0;
    }

    /**
     * returns a value after a split at regex, or zero (0)
     */
    public static short safeParseShort(String regex, String test) {
        String[] split = test.split(regex);
        if (split.length > 1) {
            return Short.parseShort(split[1].trim());
        }
        return 0;
    }

    public static boolean isNumber(String test) {
        try {
            Integer.parseInt(test.trim());
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * write a list of lines to a byte[] as UTF-8 encoded chars
     */
    public static byte[] getByteArray(List<String> lines) throws UnsupportedEncodingException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (String line : lines) {
            line = line + "\n";
            baos.write(line.getBytes("UTF-8"));
        }
        return baos.toByteArray();
    }

    /**
     * read a list of lines from a byte[] as UTF-8 encoded chars
     */
    public static List<String> getLines(byte[] bytes) {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        List<String> lines = new ArrayList<String>();
        Scanner scan = new Scanner(bais, "UTF-8");
        while (scan.hasNext()) {
            lines.add(scan.nextLine());
        }
        scan.close();
        return lines;
    }

    /**
     * Return a list of strings for the input fileName -- used to parse configuration data
     * from in-jar resource files.
     *
     * @param path to file, incl. filename + extension, running-dir relative
     */
    public static List<String> getResourceLines(String path) {
        InputStream is = AncientWarfareCore.class.getResourceAsStream(path);
        ArrayList<String> lines = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) {
                    continue;
                }
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public static void writeString(ByteBuf out, String string) {
        byte[] nameBytes = string.getBytes();
        out.writeShort(nameBytes.length);
        out.writeBytes(nameBytes);
    }

    public static String readString(ByteBuf in) {
        short len = in.readShort();
        byte[] nameBytes = new byte[len];
        in.readBytes(nameBytes);
        return new String(nameBytes);
    }

    public static boolean doStringsMatch(String a, String b) {
        if (a == null) {
            return b == null;
        } else
            return b != null && a.equals(b);
    }
}
