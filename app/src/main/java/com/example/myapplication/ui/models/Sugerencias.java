package com.example.myapplication.ui.models;

public class Sugerencias {
    private String to_email;
    private String asunto;
    private String body;

    public Sugerencias(String to_email, String asunto, String body) {
        this.to_email = to_email;
        this.asunto = asunto;
        this.body = body;
    }

    public String getTo_email() {
        return to_email;
    }

    public void setTo_email(String to_email) {
        this.to_email = to_email;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
