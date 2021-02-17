package com.example.oauth2impl;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.paytm.pg.merchant.PaytmChecksum;

@Controller
public class MyController {
	@Autowired
	ArticleRepository article_repo;

	@Autowired
	PaytmDetailPojo paytmDetailPojo;

	@Autowired
	Environment env;

	@RequestMapping(value = "/")
	public String home(Model model) {
		System.out.println("home method called...");
		List<Article> articles = article_repo.findAll();
		model.addAttribute("articles", articles);
		return "home";
	}

	@RequestMapping(value = "/donate")
	public String donate(Model model) {
		System.out.println("donate method called...");
		return "donate";
	}

	@RequestMapping(value = "/login_page")
	public String login_page(Model model) {
		System.out.println("login_page method called...");
		return "login_page";
	}

	@RequestMapping(value = "/logout_page")
	public String logout_page(Model model) {
		System.out.println("logout_page method called...");
		return "logout_page";
	}

	@PostMapping(value = "/submitPaymentDetail")
	public ModelAndView submitPaymentDetail(String CUST_ID, String TXN_AMOUNT) throws Exception {
		System.out.println("submitPaymentDetail method called...");
		ModelAndView modelAndView = new ModelAndView("redirect:" + paytmDetailPojo.getPaytmUrl());
		TreeMap<String, String> parameters = new TreeMap<>();
		paytmDetailPojo.getDetails().forEach((k, v) -> parameters.put(k, v));
		parameters.put("MOBILE_NO", env.getProperty("paytm.mobile"));
		parameters.put("EMAIL", env.getProperty("paytm.email"));
		long millis = System.currentTimeMillis();
		Date date = new java.sql.Date(millis);
		Random random = new Random();
		parameters.put("ORDER_ID", "" + date + "-" + (random.nextInt(89) + 10));
		parameters.put("TXN_AMOUNT", TXN_AMOUNT);
		parameters.put("CUST_ID", CUST_ID);
		String checkSum = getCheckSum(parameters);
		parameters.put("CHECKSUMHASH", checkSum);
		modelAndView.addAllObjects(parameters);
		return modelAndView;
	}

	@PostMapping(value = "/paytm_response")
	public String paytm_response(HttpServletRequest request, Model model) {
		System.out.println("paytm_response method called...");
		Map<String, String[]> mapData = request.getParameterMap();
		TreeMap<String, String> parameters = new TreeMap<String, String>();
		String paytmChecksum = "";
		for (Entry<String, String[]> requestParamsEntry : mapData.entrySet()) {
			if ("CHECKSUMHASH".equalsIgnoreCase(requestParamsEntry.getKey())) {
				paytmChecksum = requestParamsEntry.getValue()[0];
			} else {
				parameters.put(requestParamsEntry.getKey(), requestParamsEntry.getValue()[0]);
			}
		}
		String result;

		boolean isValideChecksum = false;
		System.out.println("RESULT : " + parameters.toString());
		try {
			isValideChecksum = validateCheckSum(parameters, paytmChecksum);
			if (isValideChecksum && parameters.containsKey("RESPCODE")) {
				if (parameters.get("RESPCODE").equals("01")) {
					result = "Payment Successful";
				} else {
					result = "Payment Failed";
				}
			} else {
				result = "Checksum mismatched";
			}
		} catch (Exception e) {
			result = e.toString();
		}
		model.addAttribute("result", result);
		parameters.remove("CHECKSUMHASH");
		model.addAttribute("parameters", parameters);
		return "report";
	}

	private boolean validateCheckSum(TreeMap<String, String> parameters, String paytmChecksum) throws Exception {
		return PaytmChecksum.verifySignature(parameters, paytmDetailPojo.getMerchantKey(), paytmChecksum);
	}

	private String getCheckSum(TreeMap<String, String> parameters) throws Exception {
		return PaytmChecksum.generateSignature(parameters, paytmDetailPojo.getMerchantKey());
	}

}
