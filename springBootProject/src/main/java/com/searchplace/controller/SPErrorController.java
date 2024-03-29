package com.searchplace.controller;

import java.util.Date;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;


/*
 * 	에러 페이지.
 */
@RestController
public class SPErrorController implements ErrorController {

	private static final String ERROR_PATH = "/error";
	
	@Override
	public String getErrorPath() {
		
		return ERROR_PATH;
	}
	
    @RequestMapping("/error")
    public ModelAndView handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        HttpStatus httpStatus = HttpStatus.valueOf(Integer.valueOf(status.toString()));
        
        ModelAndView mv = new ModelAndView();
        mv.addObject("code", status.toString());
        mv.addObject("msg", httpStatus.getReasonPhrase());
        mv.addObject("timestamp", new Date());
        
        
        return mv;
    }

}
