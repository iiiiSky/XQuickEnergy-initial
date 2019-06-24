package pansong291.xposed.quickenergy;

import android.os.Environment;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.json.JSONArray;
import org.json.JSONObject;

public class FileUtils
{
 public static final String TAG = FileUtils.class.getCanonicalName();
 private static File directory;
 private static File configFile;
 private static File friendIdMapFile;
 private static final String js_helpFriend = "helpFriend",
 js_dontCollectList = "dontCollectList", js_dontHelpList = "dontHelpList";
 
 public static File getDirectoryPath()
 {
  if(directory == null)
  {
   directory = new File(Environment.getExternalStorageDirectory(), "Android/data/pansong291.xposed.quickenergy");
   if(directory.exists())
   {
    if(directory.isFile())
    {
     directory.delete();
     directory.mkdirs();
    }
   }else
   {
    directory.mkdirs();
   }
  }
  return directory;
 }
 
 public static File getConfigFile()
 {
  if(configFile == null)
  {
   configFile = new File(getDirectoryPath(), "config.json");
   if(configFile.exists() && configFile.isDirectory())
    configFile.delete();
  }
  return configFile;
 }
 
 public static File getFriendIdMapFile()
 {
  if(friendIdMapFile == null)
  {
   friendIdMapFile = new File(getDirectoryPath(), "friendId.list");
   if(friendIdMapFile.exists() && friendIdMapFile.isDirectory())
    friendIdMapFile.delete();
  }
  return friendIdMapFile;
 }
 
 public static Config getSavedConfig()
 {
  Config config = null;
  if(getConfigFile().exists())
   config = json2Config(readFromFile(getConfigFile()));
  else
   config = json2Config(null);
  if(config.defInit)
   saveConfigFile(config);
  return config;
 }
 
 public static boolean saveConfigFile(Config config)
 {
  return write2File(config2Json(config), getConfigFile());
 }
 
 public static Config json2Config(String json)
 {
  Config config = null;
  try
  {
   JSONObject jo = new JSONObject(json);
   config = new Config();
   config.helpFriend = jo.optBoolean(js_helpFriend);
   Log.i(TAG, js_helpFriend + ":" + config.helpFriend);
   
   JSONArray ja1 = jo.optJSONArray(js_dontCollectList);
   config.dontCollectList = new ArrayList<>();
   Log.i(TAG, js_dontCollectList + ":[");
   for(int i = 0; i < ja1.length(); i++)
   {
    config.dontCollectList.add(ja1.optString(i));
    Log.i(TAG, config.dontCollectList.get(i)+",");
   }
   
   JSONArray ja2 = jo.optJSONArray(js_dontHelpList);
   config.dontHelpList = new ArrayList<>();
   Log.i(TAG, js_dontHelpList + ":[");
   for(int i = 0; i < ja2.length(); i++)
   {
    config.dontHelpList.add(ja2.optString(i));
    Log.i(TAG, config.dontHelpList.get(i)+",");
   }
  }catch(Exception e)
  {
   e.printStackTrace();
   config = Config.defInit();
  }
  return config;
 }
 
 public static String config2Json(Config config)
 {
  JSONObject jo = new JSONObject();
  try
  {
   if(config != null)jo.put(js_helpFriend, config.helpFriend);
   else jo.put(js_helpFriend, true);
   
   JSONArray ja1 = new JSONArray();
   if(config != null && config.dontCollectList != null)
   for(String s: config.dontCollectList)
   {
    ja1.put(s);
   }
   jo.put(js_dontCollectList, ja1);
   
   JSONArray ja2 = new JSONArray();
   if(config != null && config.dontHelpList != null)
    for(String s: config.dontHelpList)
    {
     ja2.put(s);
    }
   jo.put(js_dontHelpList, ja2);
  }catch(Exception e)
  {
   e.printStackTrace();
  }
  return jo.toString();
 }
 
 public static Map getSavedFriendIdMap()
 {
  Map<String, String> idMap = new TreeMap<>();
  String str = readFromFile(getFriendIdMapFile());
  if(str != null && str.length() > 0)
  {
   try
   {
    String[] idSet = str.split("\n");
    for(String s: idSet)
    {
     Log.i(TAG, s);
     String[] entry = s.split(":");
     idMap.put(entry[0], entry[1]);
    }
   }catch(Exception e)
   {
    e.printStackTrace();
    idMap.clear();
   }
  }
  return idMap;
 }
 
 public static boolean saveFriendIdMapFile(Map idMap)
 {
  if(idMap == null) return false;
  StringBuilder sb = new StringBuilder();
  Set idSet = idMap.entrySet();
  for(Map.Entry entry: idSet)
  {
   sb.append(entry.getKey());
   sb.append(':');
   sb.append(entry.getValue());
   sb.append('\n');
  }
  return write2File(sb.toString(), getFriendIdMapFile());
 }
 
 public static String readFromFile(File f)
 {
  StringBuilder result = new StringBuilder();
  FileReader fr = null;
  try
  {
   fr = new FileReader(f);
   char[] chs = new char[1024];
   int len = 0;
   while((len = fr.read(chs)) >= 0)
   {
    result .append(chs, 0, len);
   }
  }catch(Exception e)
  {
   e.printStackTrace();
  }
  close(fr);
  return result.toString();
 }
 
 public static boolean write2File(String s, File f)
 {
  boolean success = false;
  FileWriter fw = null;
  try
  {
   fw = new FileWriter(f);
   fw.write(s);
   fw.flush();
   success = true;
  }catch(Exception e)
  {
   e.printStackTrace();
  }
  close(fw);
  return success;
 }
 
 public static void close(Closeable c)
 {
  try
  {
   if(c != null) c.close();
  }catch(Exception e)
  {
   e.printStackTrace();
  }
 }
 
}