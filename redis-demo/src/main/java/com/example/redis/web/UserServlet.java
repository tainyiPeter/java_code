package com.example.redis.web;

import com.example.redis.model.User;
import com.example.redis.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class UserServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() throws ServletException {
        // 从 Spring 容器获取 UserService
        ApplicationContext context = WebApplicationContextUtils
                .getWebApplicationContext(getServletContext());
        userService = (UserService) context.getBean("userService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        String action = req.getParameter("action");
        String idStr = req.getParameter("id");
        String username = req.getParameter("username");

        out.println("<html><head><title>Redis User Demo</title></head><body>");
        out.println("<h1>Redis 用户管理</h1>");

        try {
            if ("get".equals(action) && idStr != null) {
                Long id = Long.parseLong(idStr);
                User user = userService.getUserInfo(id);

                if (user != null) {
                    out.println("<h3>用户信息:</h3>");
                    out.println("<p>ID: " + user.getId() + "</p>");
                    out.println("<p>用户名: " + user.getUsername() + "</p>");
                    out.println("<p>邮箱: " + user.getEmail() + "</p>");
                    out.println("<p>年龄: " + user.getAge() + "</p>");
                } else {
                    out.println("<p>用户不存在</p>");
                }

            } else if ("check".equals(action) && username != null) {
                boolean exists = userService.isUsernameExists(username);
                out.println("<p>用户名 '" + username + "' 是否存在: " + exists + "</p>");

            } else {
                out.println("<h3>可用操作:</h3>");
                out.println("<ul>");
                out.println("<li><a href='?action=get&id=1'>查看用户ID=1</a></li>");
                out.println("<li><a href='?action=check&username=zhangsan'>检查用户名zhangsan</a></li>");
                out.println("</ul>");
            }
        } catch (Exception e) {
            out.println("<p style='color:red'>错误: " + e.getMessage() + "</p>");
        }

        out.println("</body></html>");
    }
}