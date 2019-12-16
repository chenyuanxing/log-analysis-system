package com.cad.web.controller;

import com.cad.web.GeneralResult;
import com.cad.web.service.PanelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

/**
 *已废弃
 * @author cyx
 * @version 1.0.0
 *
 */

@RestController
@RequestMapping(value = "/panel" )
public class PanelController {

    @Autowired
    PanelService panelService;
    /**
     * 获取pannel数据
     * /panel/realTimePanel?
     */
    @RequestMapping(value = "/realTimePanel",method = RequestMethod.GET)
    public GeneralResult getLogList(@RequestParam("userId") String id, @RequestParam("isRealTime") Boolean isRealTime ,
                                    @RequestParam("Index") String Index, @RequestParam("LastTime") String LastTime,
                                    @RequestParam("interval") Number interval, @RequestParam("Yaxis") String Yaxis,
                                    @RequestParam("Indicator") String Indicator){


        ArrayList data =  panelService.timeDiagramData(Index,LastTime,interval,Yaxis,Indicator);
        GeneralResult result = new GeneralResult();
        result.setResultStatus(true);
        result.setResultData(data);
        return result;
    }
}
