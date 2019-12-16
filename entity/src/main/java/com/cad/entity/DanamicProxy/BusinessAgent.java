package com.cad.entity.DanamicProxy;

public class BusinessAgent implements Sell {
    private Vendor mVendor;

    public BusinessAgent(Vendor vendor) {
        this.mVendor = vendor;
    }

    public void sell() {
        System.out.println("before");
        mVendor.sell();
        System.out.println("after");
    }

    public void ad() {
        System.out.println("before");
        mVendor.ad();
        System.out.println("after");
    }
}