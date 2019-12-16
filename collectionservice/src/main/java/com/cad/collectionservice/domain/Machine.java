package com.cad.collectionservice.domain;

public class Machine {
    private String arch = "amd64";
    private String hostname ="";
    private int boot_time =1541746307;
    private String kernel ="linux";
    private String kernel_version ="4.4.0-117-generic";
    private String platform ="ubuntu";

    public String getArch() {
        return arch;
    }

    public void setArch(String arch) {
        this.arch = arch;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getBoot_time() {
        return boot_time;
    }

    public void setBoot_time(int boot_time) {
        this.boot_time = boot_time;
    }

    public String getKernel() {
        return kernel;
    }

    public void setKernel(String kernel) {
        this.kernel = kernel;
    }

    public String getKernel_version() {
        return kernel_version;
    }

    public void setKernel_version(String kernel_version) {
        this.kernel_version = kernel_version;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}
