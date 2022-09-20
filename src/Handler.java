import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Handler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Map<String,String> requestParamValues=null;
        requestParamValues = getQueryParameters(httpExchange.getRequestURI().getQuery());

        if("GET".equals(httpExchange.getRequestMethod())) {
            handleGetResponse(httpExchange,requestParamValues);
        }else if("POST".equals(httpExchange.getRequestMethod())) {
            handlePostResponse(httpExchange,requestParamValues);
        }
    }

    private void handleGetResponse(HttpExchange httpExchange, Map requestParamValues)  throws  IOException {
        ArrayList<Tax> results = new ArrayList<>();
        String jsonResponse = "";

        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con=DriverManager.getConnection(Environment.databaseUrl,Environment.databaseUser,Environment.databasePassword);
            Statement stmt=con.createStatement();
            String municipalityValue = (String) requestParamValues.get("municipality");
            String dateValue = (String) requestParamValues.get("date");
            if (municipalityValue == null || dateValue == null) {
                jsonResponse = "{\"message\":\"Required parameters not provided.\"}";
                httpExchange.sendResponseHeaders(400, jsonResponse.length());
            } else {
                ResultSet rs = stmt.executeQuery("select * from taxes where municipality = '" + municipalityValue + "' and startDate <= '" + dateValue + "' and endDate >= '" + dateValue + "'");
                while (rs.next()) {
                    results.add(new Tax(rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getFloat(6)));
                }
                Collections.sort(results, new ResultComparator());
                jsonResponse = "{ \"rate\": " + results.get(0).rate + " }";
                httpExchange.sendResponseHeaders(200, jsonResponse.length());
            }
            con.close();
        } catch(Exception e){
            System.out.println(e);
            jsonResponse = "{\"message\":\"Service unavailable.\"}";
            httpExchange.sendResponseHeaders(503, jsonResponse.length());
        }

        OutputStream outputStream = httpExchange.getResponseBody();
        outputStream.write(jsonResponse.getBytes());
        outputStream.flush();
        outputStream.close();
    }

    private void handlePostResponse(HttpExchange httpExchange, Map requestParamValues)  throws  IOException {
        String jsonResponse = "";

        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con=DriverManager.getConnection(Environment.databaseUrl,Environment.databaseUser,Environment.databasePassword);
            Statement stmt=con.createStatement();
            String municipalityValue = (String) requestParamValues.get("municipality");
            String startDateValue = (String) requestParamValues.get("startDate");
            String endDateValue = (String) requestParamValues.get("endDate");
            String periodValue = (String) requestParamValues.get("period");
            String rateValue = (String) requestParamValues.get("rate");
            if (municipalityValue == null || startDateValue == null || endDateValue == null || periodValue == null || rateValue == null) {
                jsonResponse = "{\"message\":\"Required parameters not provided.\"}";
                httpExchange.sendResponseHeaders(400, jsonResponse.length());
            } else {
                int rs=stmt.executeUpdate("insert into taxes (municipality, startDate, endDate, period, rate) values ('"+municipalityValue+"','"+startDateValue+"','"+endDateValue+"','"+periodValue+"',"+rateValue+")");
                if(rs > 0) {
                    jsonResponse = "{\"message\":\"Row successfully inserted.\"}";
                    httpExchange.sendResponseHeaders(200, jsonResponse.length());
                } else {
                    jsonResponse = "{\"message\":\"Row wasn't inserted.\"}";
                    httpExchange.sendResponseHeaders(500, jsonResponse.length());
                }
            }
            con.close();
        } catch(Exception e){
            System.out.println(e);
            jsonResponse = "{\"message\":\"Service unavailable.\"}";
            httpExchange.sendResponseHeaders(503, jsonResponse.length());
        }

        OutputStream outputStream = httpExchange.getResponseBody();
        outputStream.write(jsonResponse.getBytes());
        outputStream.flush();
        outputStream.close();
    }
    public static Map<String, String> getQueryParameters(String queryString) throws UnsupportedEncodingException {
        Map<String, String> queryParameters = new HashMap<>();

        if (queryString != null && !queryString.isEmpty()) {
            queryString = URLDecoder.decode(queryString, StandardCharsets.UTF_8.toString());
            String[] parameters = queryString.split("&");
            for (String parameter : parameters) {
                String[] keyValuePair = parameter.split("=");
                String value = keyValuePair.length == 1 ? "" : keyValuePair[1];
                queryParameters.put(keyValuePair[0], value);
            }
        }

        return queryParameters;
    }
}
