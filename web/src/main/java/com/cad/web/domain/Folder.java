package com.cad.web.domain;

import java.util.ArrayList;

public class Folder {
    private String foldername;

    public Folder(String foldername){
        this.foldername = foldername;
    }
    public String getFoldername() {
        return foldername;
    }

    public void setFoldername(String foldername) {
        this.foldername = foldername;
    }



    @Override
    public String toString() {
        return "Folder{" +
                "foldername='" + foldername + '\'' +
                '}';
    }
}
