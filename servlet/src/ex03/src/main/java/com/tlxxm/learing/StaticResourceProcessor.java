package com.tlxxm.learing;


import com.tlxxm.learing.connector.http.HttpRequest;
import com.tlxxm.learing.connector.http.HttpResponse;

import java.io.IOException;

public class StaticResourceProcessor {

  public void process(HttpRequest request, HttpResponse response) {
    try {
      response.sendStaticResource();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

}
