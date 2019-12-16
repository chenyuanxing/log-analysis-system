package com.cad.flinkservice.wikiedits;





import io.thekraken.grok.api.Grok;
import io.thekraken.grok.api.Match;
import io.thekraken.grok.api.exception.GrokException;

import javax.swing.text.html.parser.Entity;
import java.io.IOException;
import java.util.Map;

// grok解析 先用 .* 若时间充裕，可改为精确匹配 <耗时更少 此数据量情况下耗时大约为 1/14 >
public class GrokTest {

        public static void main(String[] args) throws GrokException, IOException {

            String log = "DEBUG 2019-06-07 11:24:57,374 (com.test.logging.exceptionmanager.ExceptionTreeModel:findNodeByIdRecursively:651) - Could not find node with Id: 1914  DEBUG 2019-06-07 11:24:57,374 (com.test.logging.exceptionmanager.ExceptionTreeModel:findNodeByIdRecursively:651) - Could not find node with Id: 1914";

            // Get an instance of grok
            Grok grok = new Grok();

            // add a pattern to grok
            grok.addPattern("LOGLEVEL", "\\w+");
            grok.addPattern("YEAR", "\\w+");
            grok.addPattern("MONTHNUM", "((?:0?[1-9]|1[0-2]))");
            grok.addPattern("MONTHDAY", "(?:[+-]?(?:[0-9]+))");
            grok.addPattern("HOUR", "(?:[+-]?(?:[0-9]+))");
            grok.addPattern("MINUTE", "(?:[+-]?(?:[0-9]+))");
            grok.addPattern("SECOND", "(?:(?:[0-5][0-9]|60)(?:[:.,][0-9]+)?)");
            grok.addPattern("GREEDYDATA", ".*");

            for (String key:grok.getPatterns().keySet()){
                System.out.println(key+" "+grok.getPatterns().get(key));
            }

            for (int t = 0;t<1;t++){



                int times = (int) Math.pow(10,t);
                grok.compile("%{GREEDYDATA:loglevel} %{GREEDYDATA:year}-%{GREEDYDATA:month}-%{GREEDYDATA:day} %{GREEDYDATA:hour}:%{GREEDYDATA:minute}:%{GREEDYDATA:second} (%{GREEDYDATA:data}) - %{GREEDYDATA:message} %{GREEDYDATA:Erreur}  %{GREEDYDATA:loglevel} %{GREEDYDATA:year}-%{GREEDYDATA:month}-%{GREEDYDATA:day} %{GREEDYDATA:hour}:%{GREEDYDATA:minute}:%{GREEDYDATA:second} (%{GREEDYDATA:data}) - %{GREEDYDATA:message} %{GREEDYDATA:Erreur}");

                long before = System.currentTimeMillis();
            for (int i = 0;i<times;i++){
                Match gm = grok.match(log);
                gm.captures();
                System.out.println(gm.toJson());
            }
            System.out.println("use : "+"indistinct "+times+"  "+ (System.currentTimeMillis()-before));

            grok.compile("%{LOGLEVEL:loglevel} %{YEAR:year}-%{MONTHNUM:month}-%{MONTHDAY:day} %{HOUR:hour}:%{MINUTE:minute}:%{SECOND:second} (%{GREEDYDATA:data}) - %{GREEDYDATA:message}  %{LOGLEVEL:loglevel} %{YEAR:year}-%{MONTHNUM:month}-%{MONTHDAY:day} %{HOUR:hour}:%{MINUTE:minute}:%{SECOND:second} (%{GREEDYDATA:data}) - %{GREEDYDATA:message}");

            before = System.currentTimeMillis();
            for (int i = 0;i<times;i++){
                Match gm = grok.match(log);
                gm.captures();
            }
            System.out.println("use : "+"accurate  "+times+"  "+  (System.currentTimeMillis()-before));

            }

            Match gm = grok.match(log);
            gm.captures();
            System.out.println(gm.toJson());

        }
}