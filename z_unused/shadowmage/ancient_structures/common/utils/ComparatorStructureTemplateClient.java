/**
   Copyright 2012-2013 John Cummens (aka Shadowmage, Shadowmage4513)
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
package shadowmage.ancient_structures.common.utils;

import java.util.Comparator;

import shadowmage.ancient_structures.common.template.StructureTemplateClient;

public class ComparatorStructureTemplateClient implements Comparator<StructureTemplateClient>
{

String filterText = "";

public void setFilterText(String tex)
  {
  this.filterText = tex;
  }

@Override
public int compare(StructureTemplateClient arg0, StructureTemplateClient arg1)
  {
  if(arg0==null && arg1!=null){return 1;}
  else if(arg0!=null && arg1==null){return -1;}
  else if(arg0==null && arg1==null){return 0;}
  String a = arg0.name.toLowerCase();
  String b = arg1.name.toLowerCase();
  String tex = filterText.toLowerCase();
  if(a.startsWith(tex) && b.startsWith(tex))
    {
    return arg0.name.compareTo(arg1.name);
    }
  else if(a.startsWith(tex))
    {
    return -1;
    }
  else if(b.startsWith(tex))
    {
    return 1;
    }
  return a.compareTo(b);  
  }
}
