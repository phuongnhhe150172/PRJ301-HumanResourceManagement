/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.login_security;

import common.Generator;
import common.Mailler;
import dal.AccountDBContext;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Account;

/**
 *
 * @author PhuongNH
 */
public class LoginController extends HttpServlet {

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("title", "System Management");
        request.setAttribute("isValidRole", true);
        request.setAttribute("isLogin", true);
        request.getRequestDispatcher("../view/authentication/login.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String raw_user = request.getParameter("username");
        String raw_pass = request.getParameter("password");

        AccountDBContext accountDBContext = new AccountDBContext();
        Account account = accountDBContext.getAccount(raw_user, raw_pass);
        if (account != null) {
            String OTP = Generator.generateRandomOTP();
            account.setOtp(OTP);
            account.setPortal(true);
            request.getSession().setAttribute("account", account);
            String remember = request.getParameter("remember");
            if (remember != null) {
                Cookie c_user = new Cookie("username", account.getUsername());
                Cookie c_pass = new Cookie("password", account.getPassword());
                c_user.setMaxAge(24 * 3600 * 7);
                c_pass.setMaxAge(24 * 3600 * 7);
                response.addCookie(c_user);
                response.addCookie(c_pass);
            }
            //check role 
            if (accountDBContext.isMasterData(account)) {
                Mailler.sendOTP(account.getEmployee().getContact().getEmail(), OTP);
                response.sendRedirect("verify");
            } else {
                request.getSession().setAttribute("account", null);
                request.setAttribute("isLogin", true);
                request.setAttribute("isValidRole", false);
                request.getRequestDispatcher("../view/authentication/login.jsp").forward(request, response);
            }
        } else {
            request.getSession().setAttribute("account", null);
            request.setAttribute("isLogin", false);
            request.setAttribute("isValidRole", true);
            request.getRequestDispatcher("../view/authentication/login.jsp").forward(request, response);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
