package cn.lk.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class RsaUtilsTest {

    private String publicFile = "H:\\auth_key\\rsa_key.pub";
    private String privateFile = "H:\\auth_key\\rsa_key";

    @Test
    public void generateKey() throws Exception {
        RsaUtils.generateKey(publicFile,privateFile,"coderK",2048);
    }
}