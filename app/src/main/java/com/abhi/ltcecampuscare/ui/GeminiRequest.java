package com.abhi.ltcecampuscare.ui;
import java.util.List;

public class GeminiRequest {
    public List<Content> contents;

    public static class Content {
        public String role;
        public List<Part> parts;

        public Content(String role, List<Part> parts) {
            this.role = role;
            this.parts = parts;
        }
    }

    public static class Part {
        public String text;

        public Part(String text) {
            this.text = text;
        }
    }
}
