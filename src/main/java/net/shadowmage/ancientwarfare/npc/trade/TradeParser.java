package net.shadowmage.ancientwarfare.npc.trade;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.util.StringTools;

public class TradeParser
{

public static List<NpcTrade> parseTrades(File file)
  {  
  BufferedReader reader = null;
  try
    {
    reader = new BufferedReader(new FileReader(file));
    } 
  catch (FileNotFoundException e)
    {
    e.printStackTrace();
    return Collections.emptyList();
    }
  List<NpcTrade> parsedTrades = new ArrayList<NpcTrade>();
  List<String> tradeLines = new ArrayList<String>();
  try
    {    
    String line;
    while((line=reader.readLine())!=null)
      {
      if(line.startsWith("#")){continue;}
      else if(line.isEmpty()){continue;}
      else if(line.toLowerCase().startsWith("trade:")){tradeLines.clear();}
      else if(line.toLowerCase().startsWith(":endtrade")){parseTrade(tradeLines, parsedTrades);}
      else{tradeLines.add(line);}      
      }  
    reader.close();
    } 
  catch (IOException e)
    {
    e.printStackTrace();
    }
  return parsedTrades;
  }

private static void parseTrade(List<String> lines, List<NpcTrade> trades)
  {
  String[] npcTypes = null;
  int minLevel = 0;
  int maxLevel = 0;
  
  ItemStack output = null;
  Item item = null;
  int outMeta = 0;
  int outQty = 0;
  
  List<ItemStack> input = new ArrayList<ItemStack>();
  Item[] inItems = new Item[9];
  int[] inMeta = new int[9];
  int[] inQty = new int[9];  
  
  for(String line : lines)
    {
    if(line.toLowerCase().startsWith("outputitem")){item= (Item) Item.itemRegistry.getObject(StringTools.safeParseString("=", line));}
    else if(line.toLowerCase().startsWith("outputmeta")){outMeta=StringTools.safeParseInt("=", line);}
    else if(line.toLowerCase().startsWith("outputquantity")){outQty=StringTools.safeParseInt("=", line);}
    else if(line.toLowerCase().startsWith("npctypes"))
      {
      npcTypes = StringTools.parseStringArray(StringTools.safeParseString("=", line));
      }
    else if(line.toLowerCase().startsWith("npclevels"))
      {
      String[] split = line.split("=");
      minLevel = Integer.parseInt(split[1].split("-")[0]);
      maxLevel = Integer.parseInt(split[1].split("-")[1]);
      }
    else
      {
      for(int i = 0; i <9; i++)
        {
        if(line.toLowerCase().startsWith("inputitem"+i)){inItems[i] = (Item) Item.itemRegistry.getObject(StringTools.safeParseString("=", line));}
        else if(line.toLowerCase().startsWith("inputquantity"+i)){inQty[i]=StringTools.safeParseInt("=", line);}
        else if(line.toLowerCase().startsWith("inputmeta"+i)){inMeta[i]=StringTools.safeParseInt("=", line);}
        }
      }
    }  
  
  if(item!=null){output = new ItemStack(item, outQty,outMeta);}
  for(int i = 0;i<9; i++)
    {
    if(inItems[i]!=null)
      {
      input.add(new ItemStack(inItems[i], inQty[i], inMeta[i]));
      }
    }
  if(output!=null && !input.isEmpty())
    {
    NpcTrade trade = new NpcTrade(output, input, minLevel, maxLevel, npcTypes);
    trades.add(trade);
    }
  }


}
