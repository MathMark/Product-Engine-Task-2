package com.productengine.test.task.model;

public class FileObject {
    private int depth;
    private String path;

    public FileObject(int depth, String path) {
        this.depth = depth;
        this.path = path;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return path;
    }
}
