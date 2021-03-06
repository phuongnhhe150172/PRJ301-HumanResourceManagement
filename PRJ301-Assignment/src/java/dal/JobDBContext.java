/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Department;
import model.Job;

/**
 *
 * @author PhuongNH
 */
public class JobDBContext extends DBContext {

    public ArrayList<Job> getAllJobs() {
        ArrayList<Job> jobs = new ArrayList<>();
        try {
            String sql = "SELECT a.[job_id]\n"
                    + "      ,a.[job_title]\n"
                    + "      ,a.[min_salary]\n"
                    + "      ,a.[max_salary]\n"
                    + "      ,b.[department_id]\n"
                    + "      ,b.[department_name]\n"
                    + "  FROM [Jobs] AS a\n"
                    + "		LEFT JOIN\n"
                    + "	   [Departments] AS b ON a.[department_id] = b.[department_id]";
            PreparedStatement stm = connection.prepareStatement(sql);
            ResultSet rs = stm.executeQuery();

            //Loop to add all information in list            
            while (rs.next()) {
                Job j = new Job();
                j.setJob_id(rs.getInt(1));
                j.setJob_title(rs.getString(2));
                j.setMin_salary(rs.getDouble(3));
                j.setMax_salary(rs.getDouble(4));
                Department department = new Department();
                department.setDepartment_id(rs.getInt(5));
                department.setDepartment_name(rs.getString(6));
                j.setDepartment(department);
                jobs.add(j);
            }
        } catch (SQLException ex) {
            Logger.getLogger(JobDBContext.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jobs;
    }

    public ArrayList<Job> getJobsByDepartmentID(int department_id) {
        ArrayList<Job> jobs = new ArrayList<>();
        try {
            String sql = "SELECT [job_id]\n"
                    + "      ,[job_title]\n"
                    + "      ,[min_salary]\n"
                    + "      ,[max_salary]\n"
                    + "      ,[department_id]\n"
                    + "  FROM [Jobs]\n"
                    + "  WHERE [department_id] = ?";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, department_id);
            ResultSet rs = stm.executeQuery();

            //Loop to add all information in list            
            while (rs.next()) {
                Job j = new Job();
                j.setJob_id(rs.getInt(1));
                j.setJob_title(rs.getString(2));
                j.setMin_salary(rs.getDouble(3));
                j.setMax_salary(rs.getDouble(4));
                Department department = new Department();
                department.setDepartment_id(rs.getInt(5));
                j.setDepartment(department);
                jobs.add(j);
            }
        } catch (SQLException ex) {
            Logger.getLogger(JobDBContext.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jobs;
    }

    public Job getJobByJobId(int jid) {
        try {
            String sql = "SELECT [job_id]\n"
                    + "      ,[job_title]\n"
                    + "      ,[min_salary]\n"
                    + "      ,[max_salary]\n"
                    + "      ,[department_id]\n"
                    + "  FROM [Jobs]\n"
                    + "  WHERE [job_id] = ?";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, jid);
            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                Job j = new Job();
                j.setJob_id(rs.getInt(1));
                j.setJob_title(rs.getString(2));
                j.setMin_salary(rs.getDouble(3));
                j.setMax_salary(rs.getDouble(4));
                Department department = new Department();
                department.setDepartment_id(rs.getInt(5));
                j.setDepartment(department);
                return j;
            }
        } catch (SQLException ex) {
            Logger.getLogger(JobDBContext.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void addJob(Job job) {
        try {
            String sql = "INSERT INTO [Jobs]\n"
                    + "           ([job_title]\n"
                    + "           ,[min_salary]\n"
                    + "           ,[max_salary]\n"
                    + "           ,[department_id])\n"
                    + "     VALUES\n"
                    + "           (?\n"
                    + "           ,?\n"
                    + "           ,?\n"
                    + "           ,?)";

            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, job.getJob_title());
            stm.setDouble(2, job.getMin_salary());
            stm.setDouble(3, job.getMax_salary());
            stm.setInt(4, job.getDepartment().getDepartment_id());
            stm.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(JobDBContext.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void editJob(Job job) {
        try {
            String sql = "UPDATE [Jobs]\n"
                    + "   SET [job_title] = ?\n"
                    + "      ,[min_salary] = ?\n"
                    + "      ,[max_salary] = ?\n"
                    + "      ,[department_id] = ?\n"
                    + " WHERE [job_id] = ?";

            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, job.getJob_title());
            stm.setDouble(2, job.getMin_salary());
            stm.setDouble(3, job.getMax_salary());
            stm.setInt(4, job.getDepartment().getDepartment_id());
            stm.setInt(5, job.getJob_id());
            stm.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(JobDBContext.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void deleteJob(int jid) {
        try {
            connection.setAutoCommit(false);

            String sql_update_employees = "UPDATE [Employees]\n"
                    + "  SET [job_id] = ?\n"
                    + "  WHERE [job_id] = ?";
            PreparedStatement stm_update_employees = connection.prepareCall(sql_update_employees);
            stm_update_employees.setNull(1, Types.INTEGER);
            stm_update_employees.setInt(2, jid);
            stm_update_employees.executeUpdate();

            String sql = "DELETE [Jobs]\n"
                    + "   WHERE [job_id] = ?";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, jid);
            stm.executeUpdate();

            connection.commit();
        } catch (SQLException ex) {
            try {
                connection.rollback();
                Logger.getLogger(JobDBContext.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex1) {
                Logger.getLogger(JobDBContext.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(JobDBContext.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public ArrayList<Job> getJobsByPage(String message, String order_by, int page_index, int page_size) {
        ArrayList<Job> jobs = new ArrayList<>();
        try {
            String sql = "with SampleData AS (SELECT a.[job_id]\n"
                    + "				  ,a.[job_title]\n"
                    + "				  ,a.[min_salary]\n"
                    + "				  ,a.[max_salary]\n"
                    + "				  ,b.[department_id]\n"
                    + "				  ,b.[department_name]\n"
                    + "				  ,ROW_NUMBER() OVER (" + order_by + ") AS row_index\n"
                    + "				  FROM [Jobs] AS a\n"
                    + "				  LEFT JOIN\n"
                    + "				  [Departments] AS b ON a.[department_id] = b.[department_id]\n"
                    + "				  WHERE (1 = 1) AND (a.[job_title] LIKE '%'+?+'%'\n"
                    + "				  OR (? BETWEEN a.min_salary AND a.max_salary)\n"
                    + "				  OR b.[department_name] LIKE '%'+?+'%'))\n"
                    + "SELECT * FROM SampleData\n"
                    + "   WHERE row_index >= (? - 1) * ? + 1\n"
                    + "   AND row_index <= ? * ?";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, message);
            if (isDouble(message)) {
                stm.setDouble(2, Double.parseDouble(message));
            } else {
                stm.setDouble(2, -1);
            }
            stm.setString(3, message);
            stm.setInt(4, page_index);
            stm.setInt(5, page_size);
            stm.setInt(6, page_index);
            stm.setInt(7, page_size);
            ResultSet rs = stm.executeQuery();

            //Loop to add all information in list            
            while (rs.next()) {
                Job j = new Job();
                j.setJob_id(rs.getInt(1));
                j.setJob_title(rs.getString(2));
                j.setMin_salary(rs.getDouble(3));
                j.setMax_salary(rs.getDouble(4));
                Department department = new Department();
                department.setDepartment_id(rs.getInt(5));
                department.setDepartment_name(rs.getString(6));
                j.setDepartment(department);
                jobs.add(j);
            }
        } catch (SQLException ex) {
            Logger.getLogger(JobDBContext.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jobs;
    }

    public int countAll() {
        try {
            String sql = "SELECT COUNT(*) FROM [Jobs]";
            PreparedStatement stm = connection.prepareStatement(sql);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException ex) {
            Logger.getLogger(EmployeeDBContext.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    /**
     * Check user message a double number or not
     *
     * @param message
     * @return true if is double else false
     */
    public static boolean isDouble(String message) {
        String tmp;
        double d = 0;

        //loop until user input correct
        try {
            tmp = message.trim();
            if (Double.parseDouble(tmp) == Double.parseDouble(tmp)) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public int countJobsSearchByMessage(String message) {
        try {
            String sql = "SELECT COUNT(*) FROM [Jobs] AS a\n"
                    + "				  LEFT JOIN\n"
                    + "				  [Departments] AS b ON a.[department_id] = b.[department_id]\n"
                    + "				  WHERE (1 = 1) AND (a.[job_title] LIKE '%'+?+'%'\n"
                    + "				  OR (? BETWEEN a.min_salary AND a.max_salary)\n"
                    + "				  OR b.[department_name] LIKE '%'+?+'%')";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, message);
            if (isDouble(message)) {
                stm.setDouble(2, Double.parseDouble(message));
            } else {
                stm.setDouble(2, -1);
            }
            stm.setString(3, message);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException ex) {
            Logger.getLogger(EmployeeDBContext.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }
}
