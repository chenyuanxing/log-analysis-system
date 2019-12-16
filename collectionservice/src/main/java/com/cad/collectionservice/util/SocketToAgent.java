package com.cad.collectionservice.util;

import com.cad.entity.domain.AgentInfo;
import com.cad.entity.domain.Operate;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Service
public class SocketToAgent {
    public Operate executeOperate(AgentInfo agentInfo, Operate operate) throws Exception{
        Socket socket;
        BufferedReader in;
        PrintWriter out;
        JSONObject jsonObj = JSONObject.fromObject(operate);
        System.out.println(jsonObj);
        System.out.println(agentInfo.getAddress()+ agentInfo.getPort());
        socket = new Socket(agentInfo.getAddress(), agentInfo.getPort());
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        out.println(jsonObj);
        out.flush();

//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            System.out.println(e);
//        }
        String line = in.readLine();
        System.out.println("Object read from golang side:");
        JSONObject backjsonObj = JSONObject.fromObject(line);
        Operate op = (Operate) JSONObject.toBean(backjsonObj,Operate.class);
        System.out.println(" operate is:" +op.toString());

        socket.close();
        return op;
    }
}
