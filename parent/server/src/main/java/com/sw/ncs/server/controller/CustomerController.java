package com.sw.ncs.server.controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sw.ncs.server.customer.CustomerControl;
import com.sw.ncs.server.customer.RegistrationRequestBean;
import com.sw.ncs.server.db.EntityValidationException;

@Controller
public class CustomerController {
	@RequestMapping(value="/register",method=RequestMethod.GET)
	public String getRegistrationPage( HttpServletRequest request,Model model) throws MalformedURLException{
		model.addAttribute("lang", request.getLocale().getLanguage());
		URL url = new URL(request.getRequestURL().toString());
		model.addAttribute("baseurl", url.getAuthority()+url.getPath().substring(0,url.getPath().lastIndexOf("/")+1));
		return "register";
	}
	
	private String getHomePage(HttpServletRequest request,Model model){
		model.addAttribute("lang", request.getLocale().getLanguage());
		return "home";
	}
	
	@RequestMapping(value="/register",method=RequestMethod.POST)
	public String registerCustomer(@ModelAttribute RegistrationRequestBean registrationBean , HttpSession session,HttpServletRequest request,Model model) throws MalformedURLException{
			
		Map<String,String> propInputMap = new HashMap<String,String>(){
			{put("customerName","company");put("accountFirstName","fName");put("accountLastName","lName");
			put("accountEmail","email");put("accountUsername","user");put("accountPassword","pw");put("customerPath","url");}
		};
		
		try {
			CustomerControl.getInstance().save(registrationBean,session);
			return getHomePage(request,model);
		} catch (EntityValidationException e) {
			
			Map<String,String> errors = new HashMap<String,String>();
			List<String> valid = new ArrayList<String>();
			
			for(Entry<String, String> error : e.getErrorMap().entrySet()){
					errors.put(propInputMap.get(error.getKey()), error.getValue());
			}
			
			if(!registrationBean.getPw().equals(registrationBean.getCnfmPw())){
				errors.put("cnfmPw", "Passwords do not match.");
			}else{
				valid.add("cnfmPw");
			}
			
			valid.addAll(propInputMap.values());
			valid.removeAll(errors.keySet());
			
			model.addAttribute("requestResult", new RequestErrorBean(errors,valid));
		}
		model.addAttribute("registration", registrationBean);
		return getRegistrationPage(request,model);
	}
	
	
	@RequestMapping(value="/" ,method = RequestMethod.GET)
	public String getWebsitePage(){
		return "index";
	}
	
	@RequestMapping(value="/{customerPath}")
	public String getCustomerNo(){
		return "home";
	}
	
	@RequestMapping(value = "/register",method = RequestMethod.POST,params={"path"})
	public @ResponseBody boolean checkPathAvailability(@RequestParam(required=true) String path,HttpServletRequest request){
		return CustomerControl.getInstance().checkAvailability(path);
	}
}
