package com.college.lms.module.course.dto;

public class GpaDistributionBucketDto {
    private String range;
    private long count;

    public GpaDistributionBucketDto() {}

    public GpaDistributionBucketDto(String range, long count) {
        this.range = range;
        this.count = count;
    }

    public String getRange() { return range; }
    public void setRange(String range) { this.range = range; }

    public long getCount() { return count; }
    public void setCount(long count) { this.count = count; }
}
