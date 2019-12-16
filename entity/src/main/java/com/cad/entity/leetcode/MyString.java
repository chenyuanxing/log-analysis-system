package com.cad.entity.leetcode;

public class MyString {
    String myString ;
    @Override
    public int hashCode() {

        return myString.toLowerCase().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
