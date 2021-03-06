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
import model.Employee;

/**
 *
 * @author PhuongNH
 */
public class DepartmentDBContext extends DBContext {

    public ArrayList<Department> getAllDepartments() {
        ArrayList<Department> departments = new ArrayList<>();
        try {
            String sql = "SELECT a.[department_id]\n" //1
                    + "      ,a.[department_name]\n" //2
                    + "      ,a.[department_phone]\n" //3
                    + "      ,a.[department_email]\n" //4
                    + "      ,a.[starting_date]\n" //5
                    + "      ,a.[description]\n" //6
                    + "      ,a.[manager_id]\n" //7
                    + "      ,b.[e_first_name]\n" //8
                    + "      ,b.[e_last_name]\n" //9
                    + "  FROM [Departments] AS a\n"
                    + "			LEFT JOIN \n"
                    + "	   [Employees] AS b ON a.manager_id = b.e_id\n";
            PreparedStatement stm = connection.prepareStatement(sql);
            ResultSet rs = stm.executeQuery();

            //Loop to add all information in list             
            while (rs.next()) {
                Department d = new Department();
                d.setDepartment_id(rs.getInt(1));
                d.setDepartment_name(rs.getString(2));
                d.setDepartment_phone(rs.getString(3));
                d.setDepartment_email(rs.getString(4));
                d.setDepartment_starting_date(rs.getDate(5));
                d.setDescription(rs.getString(6));
                Employee e = new Employee();
                e.setE_id(rs.getInt(7));
                e.setE_first_name(rs.getString(8));
                e.setE_last_name(rs.getString(9));
                d.setManager(e);

                EmployeeDBContext employeeDBContext = new EmployeeDBContext();
                //Get employee by department id
                ArrayList<Employee> employees = employeeDBContext.getAllEmployeesByDepartmentId(d.getDepartment_id());
                d.setEmployees(employees);
                departments.add(d);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DepartmentDBContext.class.getName()).log(Level.SEVERE, null, ex);
        }
        return departments;
    }

    public void addDepartment(Department department) {

        try {
            connection.setAutoCommit(false);

            // Insert The Department
            String sql = "INSERT INTO [Departments]\n"
                    + "           ([department_name]\n"
                    + "           ,[department_phone]\n"
                    + "           ,[department_email]\n"
                    + "           ,[starting_date]\n"
                    + "           ,[description]\n"
                    + "           ,[manager_id])\n"
                    + "     VALUES\n"
                    + "           (?\n"
                    + "           ,?\n"
                    + "           ,?\n"
                    + "           ,?\n"
                    + "           ,?\n"
                    + "           ,?)";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, department.getDepartment_name());
            stm.setString(2, department.getDepartment_phone());
            stm.setString(3, department.getDepartment_email());
            java.sql.Date currentDate = new java.sql.Date(new java.util.Date().getTime());
            stm.setDate(4, currentDate);
            stm.setString(5, department.getDescription());
            if (department.getManager().getE_id() >= 0) {
                stm.setInt(6, department.getManager().getE_id());
            } else {
                stm.setNull(6, Types.INTEGER);
            }
            stm.executeUpdate();

            if (department.getManager().getE_id() >= 0) {
                int e_id = department.getManager().getE_id();
                // Get department_id of the department has been inserted 
                String sql_get_did = "Select @@Identity as did";
                PreparedStatement stm_get_did = connection.prepareStatement(sql_get_did);
                ResultSet rs = stm_get_did.executeQuery();
                if (rs.next()) {
                    // set location_id for the location of the employee
                    department.setDepartment_id(rs.getInt("did"));
                }

                // Update manager_id in Employees table
                String sql_update_manager_id = "UPDATE [Employees]\n"
                        + "   SET [manager_id] = ?\n"
                        + " WHERE [department_id] = ?\n"
                        + "	AND [e_id] <> ?";
                PreparedStatement stm_update_manager_id = connection.prepareStatement(sql_update_manager_id);
                if (department.getManager().getE_id() >= 0) {
                    stm_update_manager_id.setInt(1, e_id);
                } else {
                    stm.setNull(1, Types.INTEGER);
                }
                stm_update_manager_id.setInt(2, department.getDepartment_id());
                stm_update_manager_id.setInt(3, e_id);
                stm_update_manager_id.executeUpdate();

                // Update role in account of manager
                String sql_update_role = "UPDATE [Accounts]\n"
                        + "   SET [role_id] = ?\n"
                        + " WHERE e_id = ? AND (role_id <> 1 AND role_id <> 2)";
                PreparedStatement stm_update_role = connection.prepareStatement(sql_update_role);
                stm_update_role.setInt(1, 3);
                stm_update_role.setInt(2, e_id);
                stm_update_role.executeUpdate();
            }
            connection.commit();
        } catch (SQLException ex) {
            try {
                connection.rollback();
                Logger.getLogger(DepartmentDBContext.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex1) {
                Logger.getLogger(DepartmentDBContext.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(DepartmentDBContext.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public Department getDepartmentById(int did) {
        Department d = new Department();
        try {
            String sql = "SELECT a.[department_id]\n" //1
                    + "      ,a.[department_name]\n" //2
                    + "      ,a.[department_phone]\n" //3
                    + "      ,a.[department_email]\n" //4
                    + "      ,a.[starting_date]\n" //5
                    + "      ,a.[description]\n" //6
                    + "      ,a.[manager_id]\n" //7
                    + "      ,b.[e_first_name]\n" //8
                    + "      ,b.[e_last_name]\n" //9
                    + "  FROM [Departments] AS a\n"
                    + "			LEFT JOIN \n"
                    + "	   [Employees] AS b ON a.manager_id = b.e_id\n"
                    + "  WHERE a.[department_id] = ?";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, did);
            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                d.setDepartment_id(rs.getInt(1));
                d.setDepartment_name(rs.getString(2));
                d.setDepartment_phone(rs.getString(3));
                d.setDepartment_email(rs.getString(4));
                d.setDepartment_starting_date(rs.getDate(5));
                d.setDescription(rs.getString(6));
                Employee e = new Employee();
                e.setE_id(rs.getInt(7));
                e.setE_first_name(rs.getString(8));
                e.setE_last_name(rs.getString(9));

                EmployeeDBContext employeeDBContext = new EmployeeDBContext();
                //Get employee by department id
                ArrayList<Employee> employees = employeeDBContext.getAllEmployeesByDepartmentId(d.getDepartment_id());
                d.setEmployees(employees);
                d.setManager(e);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DepartmentDBContext.class.getName()).log(Level.SEVERE, null, ex);
        }
        return d;
    }

    public void updateDepartmentById(Department department) {
        try {
            connection.setAutoCommit(false);

            //Get old manager
            String sql_get_old_manager = "SELECT [manager_id]\n"
                    + "FROM [Departments] WHERE [department_id] = ?";
            PreparedStatement stm_get_old_manager = connection.prepareStatement(sql_get_old_manager);
            stm_get_old_manager.setInt(1, department.getDepartment_id());
            ResultSet rs__get_old_manager = stm_get_old_manager.executeQuery();
            Department d = new Department();
            Employee e = new Employee();
            if (rs__get_old_manager.next()) {
                e.setE_id(rs__get_old_manager.getInt(1));
                d.setManager(e);
            }

            // Insert The Department
            String sql = "UPDATE [Departments]\n"
                    + "   SET [department_name] = ?\n"
                    + "      ,[manager_id] = ?\n"
                    + "      ,[department_phone] = ?\n"
                    + "      ,[department_email] = ?\n"
                    + "      ,[description] = ?\n"
                    + " WHERE [department_id] = ?";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, department.getDepartment_name());
            if (department.getManager().getE_id() >= 0) {
                stm.setInt(2, department.getManager().getE_id());
            } else {
                stm.setNull(2, Types.INTEGER);
                if (d.getManager().getE_id() > 0) {
                    // Update role in account of manager
                    String sql_update_role = "UPDATE [Accounts]\n"
                            + "   SET [role_id] = ?\n"
                            + " WHERE (e_id = ? AND role_id <> 1 AND role_id <> 2)";
                    PreparedStatement stm_update_role = connection.prepareStatement(sql_update_role);
                    stm_update_role.setNull(1, Types.INTEGER);
                    stm_update_role.setInt(2, d.getManager().getE_id());
                    stm_update_role.executeUpdate();
                }
            }
            stm.setString(3, department.getDepartment_phone());
            stm.setString(4, department.getDepartment_email());
            stm.setString(5, department.getDescription());
            stm.setInt(6, department.getDepartment_id());
            stm.executeUpdate();

            if (department.getManager().getE_id() > 0) {
                int e_id = department.getManager().getE_id();
                // Update manager_id in Employees table
                String sql_update_manager_id = "UPDATE [Employees]\n"
                        + "   SET [manager_id] = ?\n"
                        + " WHERE [department_id] = ?\n"
                        + "	AND [e_id] <> ?";
                PreparedStatement stm_update_manager_id = connection.prepareStatement(sql_update_manager_id);
                if (department.getManager().getE_id() >= 0) {
                    stm_update_manager_id.setInt(1, e_id);
                } else {
                    stm_update_manager_id.setNull(1, Types.INTEGER);
                }
                stm_update_manager_id.setInt(2, department.getDepartment_id());
                stm_update_manager_id.setInt(3, e_id);
                stm_update_manager_id.executeUpdate();

                //Update_manager_current
                String sql_update_manager_current = "UPDATE [Employees]\n"
                        + "   SET [manager_id] = ?\n"
                        + " WHERE [e_id] = ?\n";
                PreparedStatement stm_update_manager_current = connection.prepareStatement(sql_update_manager_current);
                stm_update_manager_current.setNull(1, Types.INTEGER);
                stm_update_manager_current.setInt(2, e_id);
                stm_update_manager_current.executeUpdate();

                // Update role in account of manager
                String sql_update_role = "UPDATE [Accounts]\n"
                        + "   SET [role_id] = ?\n"
                        + " WHERE (e_id = ?) AND role_id <> 1 AND role_id <> 2";
                PreparedStatement stm_update_role = connection.prepareStatement(sql_update_role);
                stm_update_role.setInt(1, 3);
                stm_update_role.setInt(2, e_id);
                stm_update_role.executeUpdate();
            }
            connection.commit();
        } catch (SQLException ex) {
            Logger.getLogger(DepartmentDBContext.class.getName()).log(Level.SEVERE, null, ex);
            try {
                connection.rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(DepartmentDBContext.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(DepartmentDBContext.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void deleteDepartmentById(int did) {
        try {
            connection.setAutoCommit(false);

            String sql_update_employees = "UPDATE [Employees]\n"
                    + "   SET [manager_id] = ?\n"
                    + "      ,[department_id] = ?\n"
                    + "      ,[job_id] = ?\n"
                    + " WHERE [department_id] = ?";
            PreparedStatement stm_update_employees = connection.prepareStatement(sql_update_employees);
            stm_update_employees.setNull(1, Types.INTEGER);
            stm_update_employees.setNull(2, Types.INTEGER);
            stm_update_employees.setNull(3, Types.INTEGER);
            stm_update_employees.setInt(4, did);
            stm_update_employees.executeUpdate();

            String sql_delete_jobs = "DELETE [Jobs]\n"
                    + " WHERE [department_id] = ?";
            PreparedStatement stm_delete_jobs = connection.prepareStatement(sql_delete_jobs);
            stm_delete_jobs.setInt(1, did);
            stm_delete_jobs.executeUpdate();

            String sql_delete_job_by_department_id = "DELETE FROM [Jobs]\n"
                    + "      WHERE [department_id] = ?";
            PreparedStatement stm_delete_job_by_department_id = connection.prepareStatement(sql_delete_job_by_department_id);
            stm_delete_job_by_department_id.setInt(1, did);
            stm_delete_job_by_department_id.executeUpdate();

            //getmanager 
            Employee e = new Employee();
            String sql_get_manager = "SELECT [manager_id] FROM Departments WHERE department_id = ?";
            PreparedStatement stm_get_manager = connection.prepareStatement(sql_get_manager);
            stm_get_manager.setInt(1, did);
            ResultSet rs_get_manager = stm_get_manager.executeQuery();
            if (rs_get_manager.next()) {
                e.setE_id(rs_get_manager.getInt(1));
            }

            String sql = "DELETE FROM [Departments]\n"
                    + "      WHERE [department_id] = ?";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, did);
            stm.executeUpdate();

            if (e.getE_id() > 0) {
                // Update role in account of manager
                String sql_update_role = "UPDATE [Accounts]\n"
                        + "   SET [role_id] = ?\n"
                        + " WHERE e_id = ? AND role_id = 3";
                PreparedStatement stm_update_role = connection.prepareStatement(sql_update_role);
                stm_update_role.setNull(1, Types.INTEGER);
                stm_update_role.setInt(2, e.getE_id());
                stm_update_role.executeUpdate();
            }

            connection.commit();
        } catch (SQLException ex) {
            try {
                Logger.getLogger(DepartmentDBContext.class.getName()).log(Level.SEVERE, null, ex);
                connection.rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(DepartmentDBContext.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(DepartmentDBContext.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public ArrayList<Department> getDepartmentsByPageIndex(String order_by, int page_index, int page_size) {
        ArrayList<Department> departments = new ArrayList<>();
        try {
            String sql = "With SampleData AS (SELECT a.[department_id]\n"
                    + "                             ,a.[department_name]\n"
                    + "                             ,a.[department_phone]\n"
                    + "                             ,a.[department_email]\n"
                    + "                             ,a.[starting_date]\n"
                    + "                             ,a.[description]\n"
                    + "                             ,a.[manager_id]\n"
                    + "                             ,b.[e_first_name]\n"
                    + "                             ,b.[e_last_name]\n"
                    + "                             ,ROW_NUMBER() OVER (" + order_by + ") AS row_index\n"
                    + "				FROM [Departments] AS a\n"
                    + "					LEFT JOIN\n"
                    + "				[Employees] AS b ON a.manager_id = b.e_id)\n"
                    + "SELECT * FROM SampleData\n"
                    + "WHERE row_index >= (? - 1) * ? + 1\n"
                    + "AND row_index <= ? * ?";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, page_index);
            stm.setInt(2, page_size);
            stm.setInt(3, page_index);
            stm.setInt(4, page_size);
            ResultSet rs = stm.executeQuery();

            //Loop to add all information in list             
            while (rs.next()) {
                Department d = new Department();
                d.setDepartment_id(rs.getInt(1));
                d.setDepartment_name(rs.getString(2));
                d.setDepartment_phone(rs.getString(3));
                d.setDepartment_email(rs.getString(4));
                d.setDepartment_starting_date(rs.getDate(5));
                d.setDescription(rs.getString(6));
                Employee e = new Employee();
                e.setE_id(rs.getInt(7));
                e.setE_first_name(rs.getString(8));
                e.setE_last_name(rs.getString(9));
                d.setManager(e);

                EmployeeDBContext employeeDBContext = new EmployeeDBContext();
                //Get employee by department id
                ArrayList<Employee> employees = employeeDBContext.getAllEmployeesByDepartmentId(d.getDepartment_id());
                d.setEmployees(employees);
                departments.add(d);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DepartmentDBContext.class.getName()).log(Level.SEVERE, null, ex);
        }
        return departments;
    }

    public int countAll() {
        try {
            String sql = "SELECT COUNT(*) FROM [Departments]";
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
}
