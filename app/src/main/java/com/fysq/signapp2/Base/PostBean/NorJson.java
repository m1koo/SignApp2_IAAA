package com.fysq.signapp2.Base.PostBean;

import java.util.List;

/**
 * Created by zd on 2016/6/3.
 */
public class NorJson {
    private String mac, id, account;
    private List<Params> param_list;

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Params> getParam_list() {
        return param_list;
    }

    public void setParam_list(List<Params> param_list) {
        this.param_list = param_list;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
