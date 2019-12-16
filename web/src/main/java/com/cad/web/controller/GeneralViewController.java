package com.cad.web.controller;

import com.cad.web.GeneralResult;
import com.cad.web.service.GeneralViewService;
import org.apache.flink.shaded.zookeeper.org.apache.zookeeper.KeeperException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * 对应概览界面的能力封装.
 */
@RestController
@RequestMapping(value = "/general" )
public class GeneralViewController {
    @Autowired
    private GeneralViewService generalViewService;

    @RequestMapping(value = "/getAgentView",method = RequestMethod.GET)
    public GeneralResult getAgentView(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id) {
        GeneralResult result = new GeneralResult();
        result.setResultData(generalViewService.getAgentView());
        result.setResultStatus(true);
        return result;
    }

    @RequestMapping(value = "/getBeatView",method = RequestMethod.GET)
    public GeneralResult getBeatView(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id) {
        GeneralResult result = new GeneralResult();
        result.setResultData(generalViewService.getBeatView(id));
        result.setResultStatus(true);
        return result;
    }

    @RequestMapping(value = "/getESView",method = RequestMethod.GET)
    public GeneralResult getESView(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id) {
        GeneralResult result = new GeneralResult();
        result.setResultData(generalViewService.getESView());
        result.setResultStatus(true);
        return result;
    }

    @RequestMapping(value = "/getKafkaView",method = RequestMethod.GET)
    public GeneralResult getKafkaView(@RequestParam(value = "userId",required = false,defaultValue = "smj") String id) {
        GeneralResult result = new GeneralResult();
        result.setResultStatus(false);
        try {
            result.setResultData(generalViewService.getKafkaView());
            result.setResultStatus(true);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }
}

