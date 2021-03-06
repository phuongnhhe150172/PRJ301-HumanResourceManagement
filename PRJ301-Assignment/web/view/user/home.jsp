<%-- 
    Document   : home
    Created on : Mar 17, 2022, 11:55:45 AM
    Author     : PhuongNH
--%>

<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

    <head>
        <title>HRM - Home</title>
        <link href="${pageContext.request.contextPath}/Bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.13.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    </head>

    <body>

        <div class="wrapper">

            <!-- Left Sidebar -->
            <nav id="sidebar">
                <jsp:include page="../user/menu-user.jsp"></jsp:include>
                </nav>
                <!-- #End Left Sidebar -->

                <!-- Content -->
                <div id="content" style="background-color: #f1f2f7;">
                    <!-- Header -->
                <jsp:include page="../management/header.jsp"></jsp:include>
                    <!--#END Header -->


                    <div class="block-header">
                        <div class="row">
                            <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
                                <ul class="breadcrumb breadcrumb-style" style="background-color: #f1f2f7; margin-bottom: 14px">
                                    <li class="breadcrumb-item bcrumb-1">
                                        <a href="../../index.html">
                                            <i class="fas fa-home"></i>Home</a>
                                    </li>
                                    <li class="breadcrumb-item bcrumb-2">
                                        <a href="#" onClick="return false;">User</a>
                                    </li>
                                    <li class="breadcrumb-item active">View Detail - ${requestScope.employee.e_last_name} ${requestScope.employee.e_first_name}</li>
                            </ul>
                        </div>
                    </div>
                </div>
                <div class="row" style="margin: 0">
                    <div class="col-lg-12 col-md-12 col-sm-12 table-responsive item-display">
                        <div>
                            <h2 style="font-size: 16px">
                                <strong>View Detail </strong>
                            </h2>
                        </div>
                        <div class="tbl-view-detail">
                            <table>
                                <tr>
                                    <td><strong>ID</strong></td>
                                    <td>${requestScope.employee.e_id}</td>
                                </tr>
                                <tr>
                                    <td><strong>Full Name</strong></td>
                                    <td>${requestScope.employee.e_last_name} ${requestScope.employee.e_first_name}</td>
                                </tr>
                                <tr>
                                    <td><strong>Gender</strong></td>
                                    <td>${requestScope.employee.e_gender ? "Male" : "Female"}</td>
                                </tr>
                                <tr>
                                    <td><strong>Date Of Birth</strong></td>
                                    <td>
                                        <fmt:formatDate type="date" value="${requestScope.employee.e_dob}" />
                                    </td>
                                </tr>
                                <tr>
                                    <td><strong>Email</strong></td>
                                    <td>${requestScope.employee.e_email}</td>
                                </tr>
                                <tr>
                                    <td><strong>Salary</strong></td>
                                    <td>${requestScope.employee.e_salary}</td>
                                </tr>
                                <tr>
                                    <td><strong>Personal Email</strong></td>
                                    <td>${requestScope.employee.contact.email}</td>
                                </tr>
                                <tr>
                                    <td><strong>Phone</strong></td>
                                    <td>${requestScope.employee.contact.phone}</td>
                                </tr>
                                <tr>
                                    <td><strong>Job</strong></td>
                                    <td>${requestScope.employee.job.job_title}</td>
                                </tr>
                                <tr>
                                    <td><strong>Department</strong></td>
                                    <td>${requestScope.employee.department.department_name}</td>
                                </tr>
                                <tr>
                                    <td><strong>Joining Date</strong></td>
                                    <td>
                                        <fmt:formatDate type="date" value="${requestScope.employee.e_join_date}" />
                                    </td>
                                </tr>
                                <tr>
                                    <td><strong>Address</strong></td>
                                    <td>${requestScope.employee.contact.street} ${requestScope.employee.contact.ward.ward_name}, ${requestScope.employee.contact.ward.district.district_name}, ${requestScope.employee.contact.ward.district.province.province_name}</td>
                                </tr>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- #End Content -->



        <script src="${pageContext.request.contextPath}/Bootstrap/js/Jquery.js"></script>
        <script src="${pageContext.request.contextPath}/Bootstrap/js/bootstrap.min.js"></script>
        <script>
                                            $(document).ready(function () {
                                                $('#sidebarCollapse').on('click', function () {
                                                    $('#sidebar').toggleClass('active');
                                                });
                                            });
        </script>
    </body>

</html>