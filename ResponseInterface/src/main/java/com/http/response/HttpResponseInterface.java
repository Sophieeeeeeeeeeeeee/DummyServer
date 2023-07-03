package com.http.response;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.Properties;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


public class HttpResponseInterface {

    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader("config.properties"));

        String responseFilePath = properties.getProperty("response_file_path");
        int port = Integer.parseInt(properties.getProperty("port"));

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new RequestHandler(responseFilePath));
        server.start();

        System.out.println("Sever Starting:" + port);
    }

    static class RequestHandler implements HttpHandler {
        private String responseFilePath;

        public RequestHandler(String responseFilePath) {
            this.responseFilePath = responseFilePath;
        }


        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
                StringBuilder requestBody = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    requestBody.append(line);
                }
                System.out.println("requestBody：" + URLDecoder.decode(requestBody.toString(), "UTF-8"));
                JSONObject requestJson = new JSONObject(URLDecoder.decode(requestBody.toString(), "UTF-8"));
                System.out.println("key1:" + requestJson.getInt("key1"));
                System.out.println("key2:" + requestJson.getString("key2"));
                System.out.println("key3:" + requestJson.getJSONObject("key3").getString("nestedKey1"));
                System.out.println("key3:" + requestJson.getJSONObject("key3").getString("nestedKey2"));

                String fileContent = readFileContent(responseFilePath);
                JSONObject responseJson = new JSONObject(fileContent);

                int statusCode = responseJson.getInt("status");
                String responseData = responseJson.getString("data");

                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(statusCode, responseData.getBytes().length);

                exchange.getResponseBody().write(responseData.getBytes());
                exchange.getResponseBody().close();
            } catch (IOException e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
            }
        }

        private String readFileContent(String filePath) throws IOException {
            StringBuilder content = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            reader.close();
            return content.toString();
        }
    }
}