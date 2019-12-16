package com.cad.web.service;

import com.cad.entity.domain.BeatConfig;
import com.cad.entity.domain.BeatJson;
import com.cad.entity.domain.BeatYml;
import com.cad.web.dao.RedisDBHelperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class CollectionRedisService {
    @Resource(name = "RedisDBHelper")
    private RedisDBHelperImpl redisDBHelper;

    @Value("${manage.collection.beatConfig.forwardkey}")
    private String beatConfigForward;
    @Value("${manage.collection.beatYml.forwardkey}")
    private String beatYmlForward;
    @Value("${manage.collection.beatJson.forwardkey}")
    private String beatJsonForward;

    /**
     * 此方法只能在config和yml中添加 不存在该name 的 beatConfig , beatYml
     * @param username
     * @param beatConfig
     * @param beatYml
     * @return
     * @throws Exception
     */
    public boolean addBeatConfigAndYml(String username, BeatConfig beatConfig, BeatYml beatYml)throws Exception{

        // 存入 beatconfig
        if(redisDBHelper.hashhaskey(beatConfigForward+username,beatConfig.getName()) || redisDBHelper.hashhaskey(beatYmlForward+username,beatConfig.getName())){
            return false;
        }else {
            redisDBHelper.hashPut(beatConfigForward+username,beatConfig.getName(),beatConfig);
            redisDBHelper.hashPut(beatYmlForward+username,beatConfig.getName(),beatYml);
        }
        return true;
    }

    /**
     * 此方法只能在config和json中添加 不存在该name 的 beatConfig , beatYml
     * @param username
     * @param beatConfig
     * @param beatJson
     * @return
     * @throws Exception
     */
    public boolean addBeatConfigAndJson(String username, BeatConfig beatConfig, BeatJson beatJson)throws Exception{

        // 存入 beatconfig
        if(redisDBHelper.hashhaskey(beatConfigForward+username,beatConfig.getName()) || redisDBHelper.hashhaskey(beatJsonForward+username,beatConfig.getName())){
            return false;
        }else {
            redisDBHelper.hashPut(beatConfigForward+username,beatConfig.getName(),beatConfig);
            redisDBHelper.hashPut(beatJsonForward+username,beatConfig.getName(),beatJson);
        }
        return true;
    }

    /**
     * 修改collection 的tag
     * @param username
     * @param configName
     * @param tags
     * @return
     * @throws Exception
     */
    public boolean updateCollectionTags(String username, String configName, List<String> tags)throws Exception{

        // 存入 beatconfig
        BeatConfig beatConfig =  (BeatConfig)redisDBHelper.hashGet(beatConfigForward+username,configName);
        beatConfig.setTags(tags);
        redisDBHelper.hashPut(beatConfigForward+username,configName,beatConfig);

        return true;
    }
    /**
     * 删除 某beatConfig
     * @param username
     * @param configName
     * @return
     * @throws Exception
     */
    public boolean delBeatConfigAndJson(String username, String configName)throws Exception{

        redisDBHelper.hashRemove(beatConfigForward+username,configName);
        redisDBHelper.hashRemove(beatJsonForward+username,configName);

        return true;
    }

    /**
     * 获取所有 beatConfig
     * @param username
     * @return
     * @throws Exception
     */
    public Map getAllBeatConfig(String username)throws Exception{
        return redisDBHelper.hashFindAll(beatConfigForward+username);
    }
    /**
     * 获取某个收集器的config
     * @param username
     * @return
     * @throws Exception
     */
    public BeatConfig getBeatConfig(String username,String configname)throws Exception{
        return (BeatConfig) redisDBHelper.hashGet(beatConfigForward+username,configname);
    }
    /**
     * 获取某个收集器的json
     * @param username
     * @return
     * @throws Exception
     */
    public BeatJson getBeatJson(String username,String jsonname)throws Exception{
        return (BeatJson) redisDBHelper.hashGet(beatJsonForward+username,jsonname);
    }
    /**
     * 获取某个收集器的yml
     * @param username
     * @return
     * @throws Exception
     */
    public BeatYml getBeatYml(String username,String ymlname)throws Exception{
        return (BeatYml) redisDBHelper.hashGet(beatYmlForward+username,ymlname);
    }
}
