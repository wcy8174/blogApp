package com.example.chatapp.JsonMy;

public class JsonTypeAddface {
    private int error_code;
    private String error_msg;
    private Long log_id;
    private Long timestamp;
    private  int cached;
    private Result result;

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }

    public Long getLog_id() {
        return log_id;
    }

    public void setLog_id(Long log_id) {
        this.log_id = log_id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public int getCached() {
        return cached;
    }

    public void setCached(int cached) {
        this.cached = cached;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public static class Result{
        private String face_token;
        private Location location;

        public String getFace_token() {
            return face_token;
        }

        public void setFace_token(String face_token) {
            this.face_token = face_token;
        }

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }
    }

    public static  class  Location{
        private double left;
        private double top;
        private double width;
        private double height;
        private int rotation;

        public double getLeft() {
            return left;
        }

        public void setLeft(double left) {
            this.left = left;
        }

        public double getTop() {
            return top;
        }

        public void setTop(double top) {
            this.top = top;
        }

        public double getWidth() {
            return width;
        }

        public void setWidth(double width) {
            this.width = width;
        }

        public double getHeight() {
            return height;
        }

        public void setHeight(double height) {
            this.height = height;
        }

        public int getRotation() {
            return rotation;
        }

        public void setRotation(int rotation) {
            this.rotation = rotation;
        }
    }
}
