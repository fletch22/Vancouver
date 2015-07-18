package com.fletch22.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fletch22.Fletch22ApplicationContext;

public class TestServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)  
            throws ServletException, IOException {
    	
    	int count = Fletch22ApplicationContext.getApplicationContext().getBeanDefinitionCount();
    	resp.getOutputStream().write(String.valueOf(count).getBytes());
    	
    	
        resp.getOutputStream().write("Hello World Peace".getBytes());
    }
}