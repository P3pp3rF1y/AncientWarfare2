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
package shadowmage.ancient_framework.common.lang;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import shadowmage.ancient_framework.AWFramework;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class LanguageLoader
{

private static LanguageLoader INSTANCE = new LanguageLoader();
private LanguageLoader(){}
public static LanguageLoader instance(){return INSTANCE;}

ArrayList<String> defaultLanguages = new ArrayList<String>();
Properties defaultLanguage;



public void loadLanguageFiles()
  {  
  this.addLanguage("af_ZA");
  this.addLanguage("ar_SA");
  this.addLanguage("bg_BG");
  this.addLanguage("ca_ES");
  this.addLanguage("cs_CZ");
  this.addLanguage("cy_GB");
  this.addLanguage("da_DK");
  this.addLanguage("el_GR");
  this.addLanguage("en_AU");
  this.addLanguage("en_CA");
  this.addLanguage("en_GB");
  this.addLanguage("en_PT");
  this.addLanguage("eo_YU");
  this.addLanguage("es_AR");
  this.addLanguage("es_ES");
  this.addLanguage("es_MX");
  this.addLanguage("es_UY");
  this.addLanguage("es_VE");
  this.addLanguage("et_EE");
  this.addLanguage("eu_ES");
  this.addLanguage("fi_FI");
  this.addLanguage("fr_CA");
  this.addLanguage("fr_FR");
  this.addLanguage("ga_IE");
  this.addLanguage("gl_ES");
  this.addLanguage("he_IL");
  this.addLanguage("hi_IN");
  this.addLanguage("hr_HR");
  this.addLanguage("hu_HU");
  this.addLanguage("id_ID");
  this.addLanguage("is_IS");
  this.addLanguage("it_IT");
  this.addLanguage("ja_JP");
  this.addLanguage("ka_GE");
  this.addLanguage("ko_KR");
  this.addLanguage("kw_GB");
  this.addLanguage("ky_KG");
  this.addLanguage("lt_LT");
  this.addLanguage("lv_LV");
  this.addLanguage("mi_NZ");
  this.addLanguage("ms_MY");
  this.addLanguage("mt_MT");
  this.addLanguage("nb_NO");
  this.addLanguage("nl_NL");
  this.addLanguage("nn_NO");
  this.addLanguage("no_NO");
  this.addLanguage("pl_PL");
  this.addLanguage("pt_BR");
  this.addLanguage("pt_PT");
  this.addLanguage("qya_AA");
  this.addLanguage("ro_RO");
  this.addLanguage("ru_RU");
  this.addLanguage("sk_SK");
  this.addLanguage("sl_SL");
  this.addLanguage("sr_SP");
  this.addLanguage("sv_SE");
  this.addLanguage("th_TH");
  this.addLanguage("tlh_AA");
  this.addLanguage("tr_TR");
  this.addLanguage("uk_UA");
  this.addLanguage("vi_VN");
  this.addLanguage("zh_CN");
  this.addLanguage("zh_TW");  
  
  this.loadEnglishFile();
  this.loadOtherLocalizations();
  }

protected void addLanguage(String s)
  {
  this.defaultLanguages.add(s);
  }

/**
 * will NPE/crash if no english translation found (intended behavior)
 */
protected void loadEnglishFile()
  {
  Properties languageFile = new Properties();
  try
    {
    InputStream is = AWFramework.instance.getClass().getResourceAsStream("/assets/ancientwarfare/lang/en_US.lang");	
    if(is==null)
      {
      AWFramework.instance.logError("error loading english language file...could not locate file.../lang/ancientwarfare/en_US.lang");
      return;
      }
    languageFile.load(is);    
    if(!languageFile.isEmpty())
      {
      LanguageRegistry.instance().addStringLocalization(languageFile, "en_US");
      }
    defaultLanguage = languageFile;
    is.close();
    } 
  catch (IOException e)
    {
    AWFramework.instance.logError("error loading english language file...");
    e.printStackTrace();
    }
  }

protected void loadOtherLocalizations()
  {
  for(String langName : this.defaultLanguages)
    {
    Properties languageFile = new Properties();
    try
      {
      InputStream is = this.getClass().getResourceAsStream("assets/ancientwarfare/lang/"+langName+".lang");          
      if(is!=null)
        {
        languageFile.load(is);
        LanguageRegistry.instance().addStringLocalization(languageFile, langName);
        }
      else
        {
        LanguageRegistry.instance().addStringLocalization(defaultLanguage, langName);
        }
      if(is!=null)
        {
        is.close();    	  
        }
      } 
    catch (IOException e)
      {
      e.printStackTrace();
      }
    }
  }

}

