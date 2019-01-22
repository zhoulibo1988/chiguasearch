package com.dyhdyh.magnetw.model;

import java.io.Serializable;

/**
 * author  dengyuhan
 * created 2018/3/7 11:29
 */
public class MagnetInfo implements Serializable{

    private String group;
    private String magnet;
    private String name;
    private String formatSize;
    private long size;
    private String count;
    private String detailUrl;

    private String resolution;//清晰度

    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }

    public String getFormatSize() {
        return formatSize;
    }

    public void setFormatSize(String formatSize) {
        this.formatSize = formatSize;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getMagnet() {
        return magnet;
    }

    public void setMagnet(String magnet) {
        this.magnet = magnet;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
