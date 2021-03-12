package io.zero88.jooqx;

import io.vertx.codegen.annotations.DataObject;

import lombok.NonNull;

@DataObject(generateConverter = true, publicConverter = false)
public class TestData {

    private int i1;
    private final long i2;
    private String s3;

    public TestData(long i2) {this.i2 = i2;}

    public int getI1() {
        return i1;
    }

    public long getI2() {
        return i2;
    }

    public void setI1(int i1) {
        this.i1 = i1;
    }

    public void setS3(@NonNull String s3) {
        this.s3 = s3;
    }

    public @NonNull String getS3() {
        return s3;
    }

}

