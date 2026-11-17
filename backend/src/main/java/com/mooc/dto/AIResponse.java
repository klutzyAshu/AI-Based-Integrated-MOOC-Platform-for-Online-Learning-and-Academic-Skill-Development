package com.mooc.dto;

public class AIResponse {
    private String type;
    private String reply;
    private String dataJson;

    public AIResponse() {
    }

    public AIResponse(String type, String reply, String dataJson) {
        this.type = type;
        this.reply = reply;
        this.dataJson = dataJson;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getDataJson() {
        return dataJson;
    }

    public void setDataJson(String dataJson) {
        this.dataJson = dataJson;
    }

    public static AIResponseBuilder builder() {
        return new AIResponseBuilder();
    }

    public static class AIResponseBuilder {
        private String type;
        private String reply;
        private String dataJson;

        AIResponseBuilder() {
        }

        public AIResponseBuilder type(String type) {
            this.type = type;
            return this;
        }

        public AIResponseBuilder reply(String reply) {
            this.reply = reply;
            return this;
        }

        public AIResponseBuilder dataJson(String dataJson) {
            this.dataJson = dataJson;
            return this;
        }

        public AIResponse build() {
            return new AIResponse(type, reply, dataJson);
        }
    }
}
