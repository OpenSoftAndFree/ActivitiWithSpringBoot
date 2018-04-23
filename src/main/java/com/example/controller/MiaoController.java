package com.example.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.entity.User;
import com.example.entity.VacationForm;
import com.example.service.MiaoService;
import com.example.service.UserService;

@Controller
public class MiaoController {
	@Autowired
	private MiaoService miaoService;

	@Autowired
	private UserService userService;

	@GetMapping("/")
	public String login() {
		return "login";
	}

	// 首页
	@GetMapping("/home")
	public String index(ModelMap model, HttpServletRequest request) {
		List<VacationForm> forms = miaoService.formList();
		Cookie[] cookies = request.getCookies();
		String user = "";
		// 从cookie中获取当前用户
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("userInfo")) {
					user = cookie.getValue();
					break;
				}
			}
		}
		List<User> users = userService.findByName(user);
		Integer uType = users.get(0).getType();
		List<HashMap<String, Object>> formsMap = new ArrayList<HashMap<String, Object>>();
		
		String finalUser = user;
		if (uType == 1) {
			forms = forms.stream().filter(form -> finalUser.equals(form.getApplicant())).collect(Collectors.toList());
		} else {
			forms = forms.stream().filter(form -> form.getState().contains("审核")).collect(Collectors.toList());
		}
		HashMap<String, String> buttons = new HashMap<String, String>();
		for(VacationForm form : forms) {
			buttons = miaoService.getButtons(form.getId(), uType);
			if(!form.getApprover().equals(user)) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("id", form.getId());
				map.put("title", form.getTitle());
				map.put("content", form.getContent());
				map.put("applicant", form.getApplicant());
				map.put("state", form.getState());
				map.put("buttons", buttons);
				formsMap.add(map);
			}
		}
		
		// 将forms参数返回
		model.addAttribute("forms", formsMap);
		model.addAttribute("userType", uType);
		return "index";
	}

	// 请假单页面
	@GetMapping("/form")
	public String form() {
		return "form";
	}
}
