package com.cad.entity.domain;

/**
 * name 为唯一属性 与 BeatConfig 中的相同
 */
public class BeatYml {
    private String name;
    private String ymlFile;
    private String modulesYmlFile;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYmlFile() {
        return ymlFile;
    }

    public void setYmlFile(String ymlFile) {
        this.ymlFile = ymlFile;
    }

    public String getModulesYmlFile() {
        return modulesYmlFile;
    }

    public void setModulesYmlFile(String modulesYmlFile) {
        this.modulesYmlFile = modulesYmlFile;
    }
}
