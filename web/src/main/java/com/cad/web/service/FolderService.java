package com.cad.web.service;




import com.cad.web.dao.RedisDBHelperImpl;
import com.cad.web.domain.Folder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
@Service
public class FolderService {
//    public static Map<String,Folder> FolderMap = new HashMap();

    @Resource(name = "RedisDBHelper")
    private RedisDBHelperImpl redisDBHelper;

    @Value("${manage.dashboard.forwardkey}")
    private String forwardkey;

    @Value("${manage.folder.forwardkey}")
    private String folderforwardkey;

    /** 根据用户名查询所有folder
     * @param username
     * @return
     */
    public Set getFolders(String username)throws Exception{
        return redisDBHelper.setFindAll(folderforwardkey+username);
    }
    /** 是否存在该folder
     * @param username
     * @return
     */
    public boolean hasFolder(String foldername,String username)throws Exception{
        return redisDBHelper.setIsMember(folderforwardkey+username,foldername);
    }
    /** 根据名称添加一个空的folder
     * @param foldername
     * @param username
     * @return
     */
    public void addFolder(String foldername,String username)throws Exception{
        if(redisDBHelper.setIsMember(folderforwardkey+username,foldername)){
            throw new Exception(foldername+"  folder Is Member ");
        }
        redisDBHelper.setPush(folderforwardkey+username,foldername);
    }

    /** 根据名称删除folder
     * @param foldername
     * @param username
     * @return
     */
    public void delFolder(String foldername,String username)throws Exception{
        if(!redisDBHelper.setIsMember(folderforwardkey+username,foldername)){
            throw new Exception(foldername+"  Is not Member ");
        }
        //检查该目录下是否存在dashboard
        if(redisDBHelper.haskey(forwardkey+username+":"+foldername)){
            throw new Exception(foldername+" is not empty ; please del the dashboard in this folder first");
        }
        redisDBHelper.setRemove(folderforwardkey+username,foldername);

    }

}
