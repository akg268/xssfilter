package com.example.sanitizeinputdemo.filters;


import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.stream.Stream;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.sanitizeinputdemo.exceptions.XSSException;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Component
@Order(1)
public class XSSFilter implements Filter {

    private final static String ERROR_MSG= "invalid character found in the header";
    private final static String[] PROHIBITED_TEXTS = new String[] {"jndi","scripy","insert","delete","update","select"};

    @Override
    public void doFilter(ServletRequest sReq, ServletResponse sRes, FilterChain filterChain)
            throws IOException, ServletException {
       
                HttpServletRequest httpRequest = (HttpServletRequest) sReq;
                HttpServletResponse httpResponse = (HttpServletResponse) sRes;
                //1. Validate the http request headers. 
                Enumeration<String> headerNames = httpRequest.getHeaderNames();
                while(headerNames.hasMoreElements())
                {
                  String headerName =  headerNames.nextElement();
                  System.out.println("headerName:"+ headerName);
                  if(!headerName.equalsIgnoreCase("Accept") && !headerName.equalsIgnoreCase("Content-Type") && !headerName.equalsIgnoreCase("host"))
                  try{
                  validateHeader(httpRequest.getHeader(headerName));
                  }catch(XSSException ex){
                    httpResponse.sendError(HttpStatus.BAD_REQUEST.value(),ex.getMessage());  
                  }
                }
                //2. Validate the query parameters
                if(httpRequest.getParameterMap() !=null){
                   
                   for(String param: httpRequest.getParameterMap().keySet()) {
                try{
                    validateParameters(httpRequest.getParameterMap().get(param));
                }catch(XSSException ex){
                    httpResponse.sendError(HttpStatus.BAD_REQUEST.value(),ex.getMessage());   
                }
                   }
                }
                //3. check the byte size of the request body. ex: if it is more than 1KB throw error 
                if(httpRequest.getMethod().equalsIgnoreCase("POST")){
                 
                    if(httpRequest.getContentLength() > 1024)  httpResponse.sendError(HttpStatus.BAD_REQUEST.value(),ERROR_MSG);   
                }
                filterChain.doFilter(sReq, sRes);

    }
    //Receives the header values and passes it to the validateForXSS function
    private void validateHeader(String headerValue){
        validateForXSS(headerValue);
    }

    //Gets the query param array of values loops through it and  passes the query param value to the validateForXSS function
    private void validateParameters(String[] paramValues){
        Stream.of(paramValues).forEach(this::validateForXSS);
    }

    /**
     * This method is used for checking for any disallowed texts in the given string
     * things like jndi, script are added
     * Regex to allow only alpha numeric,underscore, hypen, comma and blank space are allowed. 
     **/
    private void validateForXSS(String data)  {
        System.out.println("data:"+ data);
        if(Arrays.binarySearch(PROHIBITED_TEXTS, data) >= 0) throw new XSSException(ERROR_MSG);
        if(!data.matches("^[a-zA-Z0-9\\s_,-]*$")) throw new XSSException(ERROR_MSG);
    }
    
}
