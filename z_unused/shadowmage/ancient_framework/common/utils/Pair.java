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
package shadowmage.ancient_framework.common.utils;

/**
 * generic 2-tuple...becuase...java doesn't freaking have one...
 * @author Shadowmage
 *
 */
public class Pair<T1, T2>
{
private T1 a;
private T2 b;

public Pair(T1 a, T2 b)
  {
  this.a = a;
  this.b = b;  
  }

public T1 key()
  {
  return a;
  }

public T2 value()
  {
  return b;
  }

@Override
public String toString()
  {
  return String.valueOf(a.toString() + "," + b.toString());
  }

//TODO -- all used for hashing..if I ever intend to use it as a key...
//hashcode
//equals
}
