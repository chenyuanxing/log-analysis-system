package com.cad.web.domain;

public class Panel {


    private String panelid;
    private String title;
    private String currentchart;
    private String currentpara;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCurrentchart() {
        return currentchart;
    }

    public void setCurrentchart(String currentchart) {
        this.currentchart = currentchart;
    }

    public String getCurrentpara() {
        return currentpara;
    }

    public void setCurrentpara(String currentpara) {
        this.currentpara = currentpara;
    }

    public String getPanelid() {
        return panelid;
    }

    public void setPanelid(String panelid) {
        this.panelid = panelid;
    }
}
