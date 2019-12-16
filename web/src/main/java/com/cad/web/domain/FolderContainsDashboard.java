package com.cad.web.domain;

import java.util.Set;

public class FolderContainsDashboard {
    private String foldername;
    private Set<String> dashboards;

    public String getFoldername() {
        return foldername;
    }

    public void setFoldername(String foldername) {
        this.foldername = foldername;
    }

    public Set<String> getDashboards() {
        return dashboards;
    }

    public void setDashboards(Set<String> dashboards) {
        this.dashboards = dashboards;
    }
}
