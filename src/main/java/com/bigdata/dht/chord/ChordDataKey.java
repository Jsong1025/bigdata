package com.bigdata.dht.chord;

public class ChordDataKey extends Object {

    public String sha1Hash() {
        String str = super.toString();
        if (str == null) {
            str = "";
        }
        return SHA1Utils.hash(str);
    }
}
