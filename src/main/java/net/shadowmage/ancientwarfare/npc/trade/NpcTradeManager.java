package net.shadowmage.ancientwarfare.npc.trade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class NpcTradeManager
{
private NpcTradeManager(){}
public static final NpcTradeManager INSTANCE = new NpcTradeManager();

private List<NpcTrade> tradeList = new ArrayList<NpcTrade>();

private HashMap<String, List<NpcTrade>> tradesByNpcType = new HashMap<String, List<NpcTrade>>();

private Random rng = new Random();

public void addNpcTrade(NpcTrade trade)
  {
  tradeList.add(trade);
  for(String type : trade.npcTypes)
    {
    if(!tradesByNpcType.containsKey(type)){tradesByNpcType.put(type, new ArrayList<NpcTrade>());}
    tradesByNpcType.get(type).add(trade);
    }
  }

/**
 * @param npcType should be the full npc type, e.g. 'trader', 'bandit.trader', 'pirate.trader'
 * @param trades the list to which valid trades will be added
 * @param npcLevel the level of the NPC to get trades for
 * @param randomSeed should be the lsb of npc uuid, used to determine which trades to add for an npc
 */
public void getTradesFor(String npcType, List<NpcTrade> trades, int npcLevel, long randomSeed)
  {
  if(tradesByNpcType.containsKey(npcType))
    {
    rng.setSeed(randomSeed);
    List<NpcTrade> allTrades = tradesByNpcType.get(npcType);
    for(NpcTrade trade : allTrades)
      {
      if(npcLevel>=trade.minLevel && npcLevel<=trade.maxLevel)
        {
        if(trades.isEmpty() || rng.nextInt(100)<20)
          {
          trades.add(trade);          
          }
        }
      }
    }
  }

}
