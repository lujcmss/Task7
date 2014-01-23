package task7.controller;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.mybeans.form.FormBeanFactory;

import task7.databeans.CustomerBean;
import task7.databeans.EmployeeBean;
import task7.formbeans.CreateCustomerForm;
import task7.formbeans.CreateEmployeeForm;
import task7.model.EmployeeDAO;
import task7.model.Model;

public class CreateEmployeeAccount extends Action {
	private FormBeanFactory<CreateEmployeeForm> formBeanFactory = FormBeanFactory.getInstance(CreateEmployeeForm.class);
	
	private EmployeeDAO employeeDAO;
	
	public CreateEmployeeAccount(Model model) {
	}

	public String getName() { return "createEmployeeAccount.do"; }

	public String perform(HttpServletRequest request) {
        // Set up the errors list
        List<String> errors = new ArrayList<String>();
        request.setAttribute("errors", errors);
        HttpSession session = request.getSession(true);
		try {
			CreateEmployeeForm form = formBeanFactory.create(request);
			
			if (!form.isPresent()) {
	            return "createEmployeeAccount.jsp";
	        }
			
			errors.addAll(form.getValidationErrors());
			
		    if (employeeDAO.hasEmployee(form.getEmail()) == true) {
		    	errors.add("Email already been used.");
		    }
		    
		    if (errors.size() != 0) {
		        return "createEmployeeAccount.jsp";
		    }
		    
		    EmployeeBean employeeBean = new EmployeeBean();
		    employeeBean.setEmail(form.getEmail());
		    employeeBean.setFirstName(form.getFirstName());
		    employeeBean.setLastName(form.getLastName());
		    employeeBean.setPassword(md5(form.getPsw()));
		    employeeDAO.insert(employeeBean);
		    
			// check for errors
			return "home.jsp";
        } catch (Exception e) {
        	errors.add(e.getMessage());
        	return "error.jsp";
        }
    }
	
	private String md5(String org) {
	    MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
		    md.update(org.getBytes(), 0, org.length());
		    String re = new BigInteger(1, md.digest()).toString(16);
		    return re;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	    return null;
	}
}
