package com.cad.flinkservice.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import io.thekraken.grok.api.Grok;
import io.thekraken.grok.api.Match;
import io.thekraken.grok.api.exception.GrokException;
import org.apache.flink.api.common.functions.MapFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GrokSplitter implements MapFunction<String,Map<String, Object>> {
    // Get an instance of grok
    Grok grok = new Grok();
    // %{GREEDYDATA:loglevel} %{GREEDYDATA:year}-%{GREEDYDATA:month}-%{GREEDYDATA:day} %{GREEDYDATA:hour}
    String pattern;
    public GrokSplitter(String pattern) throws GrokException {
        this.pattern = pattern;
        // add a pattern to grok
        grok.addPattern("LOGLEVEL", "\\w+");
        grok.addPattern("YEAR", "\\w+");
        grok.addPattern("MONTHNUM", "((?:0?[1-9]|1[0-2]))");
        grok.addPattern("MONTHDAY", "(?:[+-]?(?:[0-9]+))");
        grok.addPattern("HOUR", "(?:[+-]?(?:[0-9]+))");
        grok.addPattern("MINUTE", "(?:[+-]?(?:[0-9]+))");
        grok.addPattern("SECOND", "(?:(?:[0-5][0-9]|60)(?:[:.,][0-9]+)?)");
        grok.addPattern("GREEDYDATA", ".*");

        grok.addPattern("USERNAME", "[a-zA-Z0-9._-]+");
        grok.addPattern("USER", "%{USERNAME}");
        grok.addPattern("EMAILLOCALPART", "[a-zA-Z][a-zA-Z0-9_.+-=:]+");
        grok.addPattern("EMAILADDRESS", "%{EMAILLOCALPART}@%{HOSTNAME}");
        grok.addPattern("INT", "(?:[+-]?(?:[0-9]+))");
        grok.addPattern("BASE10NUM", "(?<![0-9.+-])(?>[+-]?(?:(?:[0-9]+(?:\\.[0-9]+)?)|(?:\\.[0-9]+)))");
        grok.addPattern("NUMBER", "(?:%{BASE10NUM})");
        grok.addPattern("BASE16NUM", "(?<![0-9A-Fa-f])(?:[+-]?(?:0x)?(?:[0-9A-Fa-f]+))");
        grok.addPattern("BASE16FLOAT", "\\b(?<![0-9A-Fa-f.])(?:[+-]?(?:0x)?(?:(?:[0-9A-Fa-f]+(?:\\.[0-9A-Fa-f]*)?)|(?:\\.[0-9A-Fa-f]+)))\\b");
        grok.addPattern("POSINT", "\\b(?:[1-9][0-9]*)\\b");
        grok.addPattern("NONNEGINT", "\\b(?:[0-9]+)\\b");
        grok.addPattern("WORD", "\\b\\w+\\b");
        grok.addPattern("NOTSPACE", "\\S+");
        grok.addPattern("SPACE", "\\s*");
        grok.addPattern("DATA", ".*?");
        grok.addPattern("QUOTEDSTRING", "(?>(?<!\\\\)(?>\"(?>\\\\.|[^\\\\\"]+)+\"|\"\"|(?>'(?>\\\\.|[^\\\\']+)+')|''|(?>`(?>\\\\.|[^\\\\`]+)+`)|``))");
        grok.addPattern("UUID", "[A-Fa-f0-9]{8}-(?:[A-Fa-f0-9]{4}-){3}[A-Fa-f0-9]{12}");
        grok.addPattern("URN", "urn:[0-9A-Za-z][0-9A-Za-z-]{0,31}:(?:%[0-9a-fA-F]{2}|[0-9A-Za-z()+,.:=@;$_!*'/?#-])+");
        grok.addPattern("MAC", "(?:%{CISCOMAC}|%{WINDOWSMAC}|%{COMMONMAC})");
        grok.addPattern("CISCOMAC", "(?:(?:[A-Fa-f0-9]{4}\\.){2}[A-Fa-f0-9]{4})");
        grok.addPattern("WINDOWSMAC", "(?:(?:[A-Fa-f0-9]{2}-){5}[A-Fa-f0-9]{2})");
        grok.addPattern("COMMONMAC", "(?:(?:[A-Fa-f0-9]{2}:){5}[A-Fa-f0-9]{2})");
        grok.addPattern("IPV6", "((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:)))(%.+)?");
        grok.addPattern("IPV4", "(?<![0-9])(?:(?:[0-1]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])[.](?:[0-1]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])[.](?:[0-1]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])[.](?:[0-1]?[0-9]{1,2}|2[0-4][0-9]|25[0-5]))(?![0-9])");
        grok.addPattern("IP", "(?:%{IPV6}|%{IPV4})");
        grok.addPattern("HOSTNAME", "\\b(?:[0-9A-Za-z][0-9A-Za-z-]{0,62})(?:\\.(?:[0-9A-Za-z][0-9A-Za-z-]{0,62}))*(\\.?|\\b)");
        grok.addPattern("IPORHOST", "(?:%{IP}|%{HOSTNAME})");
        grok.addPattern("HOSTPORT", "%{IPORHOST}:%{POSINT}");
        // paths
        grok.addPattern("PATH", "(?:%{UNIXPATH}|%{WINPATH})");
        grok.addPattern("UNIXPATH", "(/([\\w_%!$@:.,+~-]+|\\\\.)*)+");
        grok.addPattern("TTY", "(?:/dev/(pts|tty([pq])?)(\\w+)?/?(?:[0-9]+))");
        grok.addPattern("WINPATH", "(?>[A-Za-z]+:|\\\\)(?:\\\\[^\\\\?*]*)+");
        grok.addPattern("URIPROTO", "[A-Za-z]([A-Za-z0-9+\\-.]+)+");
        grok.addPattern("URIHOST", "%{IPORHOST}(?::%{POSINT:port})?");
        grok.addPattern("URIPATH", "(?:/[A-Za-z0-9$.+!*'(){},~:;=@#%&_\\-]*)+");
        grok.addPattern("URIPATHPARAM", "%{URIPATH}(?:%{URIPARAM})?");

        grok.addPattern("MONTH", "\\b(?:[Jj]an(?:uary|uar)?|[Ff]eb(?:ruary|ruar)?|[Mm](?:a|ä)?r(?:ch|z)?|[Aa]pr(?:il)?|[Mm]a(?:y|i)?|[Jj]un(?:e|i)?|[Jj]ul(?:y)?|[Aa]ug(?:ust)?|[Ss]ep(?:tember)?|[Oo](?:c|k)?t(?:ober)?|[Nn]ov(?:ember)?|[Dd]e(?:c|z)(?:ember)?)\\b");


        grok.compile(pattern);
    }

    /**
     * 如果pattern为空 ，将不会转换.
     * @param sentenceString
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> map(String sentenceString) throws Exception {
        Gson mGson  = new GsonBuilder()
                .setLenient()  // 设置GSON的非严格模式setLenient()
                .create();
        Map<String,Object> linkedTreeMap = new HashMap<>();
        linkedTreeMap = mGson.fromJson(String.valueOf(sentenceString),linkedTreeMap.getClass());
        String sentence = "";
        if (linkedTreeMap.get("message")!=null){
            sentence = (String)linkedTreeMap.get("message");
        }


        Map<String, Object> map = new HashMap<>();
        if (pattern.isEmpty() || "NULL".equals(pattern)){
            try {
                map = new com.google.gson.Gson().fromJson(String.valueOf(sentence),map.getClass());
            }catch (Exception e){
                e.printStackTrace();
                map.put("raw",sentence);
            }
            linkedTreeMap.remove("message");
            linkedTreeMap.put("message",map);
            return linkedTreeMap;
        }
        Match gm = grok.match(sentence);

        gm.captures();
//        String result = gm.toJson();
//        System.out.println("after grok: "+result);
        map =  gm.toMap();
        linkedTreeMap.remove("message");
        linkedTreeMap.put("message",map);
        return linkedTreeMap;
    }
}