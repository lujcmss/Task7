package task7.controller;

import java.sql.Date;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.mybeans.form.FormBeanFactory;

import task7.databeans.CustomerBean;
import task7.databeans.FundBean;
import task7.databeans.FundInfoBean;
import task7.databeans.PositionBean;
import task7.databeans.TransactionBean;
import task7.databeans.TransactionHistoryBean;
import task7.formbeans.SellFundForm;
import task7.formbeans.ViewForm;
import task7.model.CustomerDAO;
import task7.model.FundDAO;
import task7.model.FundPriceHistoryDAO;
import task7.model.Model;
import task7.model.PositionDAO;
import task7.model.TransactionDAO;

public class ViewCustomerInformation extends Action {
	private FormBeanFactory<ViewForm> formBeanFactory = FormBeanFactory.getInstance(ViewForm.class);

	private FundDAO fundDAO;
	private CustomerDAO customerDAO;
	private PositionDAO positionDAO;
	private TransactionDAO transactionDAO;
	private FundPriceHistoryDAO fundPriceHistoryDAO;
	
	public ViewCustomerInformation(Model model) {
		fundDAO = model.getFundDAO();
		customerDAO = model.getCustomerDAO();
		positionDAO = model.getPositionDAO();
		transactionDAO = model.getTransactionDAO();
		fundPriceHistoryDAO = model.getFundPriceHistoryDAO();
	}
	
	public String getName() { return "viewCustomerInformation.do"; }

	public String perform(HttpServletRequest request) {
		// Set up the errors list
	    List<String> errors = new ArrayList<String>();
	    request.setAttribute("errors", errors);
	    HttpSession session = request.getSession();
	    
		try {
			ViewForm form = formBeanFactory.create(request);

	   	    if (!form.isPresent()) {
		        return "viewCustomerInformation.jsp";
			}		
			
	   	    errors.addAll(form.getValidationErrors());
		    if (errors.size() != 0) {
		        return "viewCustomerInformation.jsp";
		    }

			if (form.getButton() == null || form.getCustomerEmail() == null || form.getCustomerEmail().equals("")) {
				session.setAttribute("userInfo", null);
				session.setAttribute("fundInfo", null);
			} else {
				//Information

				CustomerBean customerBean = customerDAO.getCustomerByEmail(form.getCustomerEmail());
				FundBean[] fundBeans = fundDAO.getAllFunds();

				PositionBean[] positionBeans = positionDAO.getByCustomerId(customerBean.getCustomerId());
				
				FundInfoBean[] fundInfoBeans = new FundInfoBean[positionBeans.length];

				for (int i = 0; i < positionBeans.length; i++) {
	
					fundInfoBeans[i] = new FundInfoBean();
					fundInfoBeans[i].setShare(positionBeans[i].getShares() / 1000.0);
					fundInfoBeans[i].setFundId(positionBeans[i].getFundBean().getFundId());
					
					int fundId = positionBeans[i].getFundBean().getFundId();
					for (int j = 0; j < fundBeans.length; j++) {
						if (fundBeans[j].getFundId() == fundId) {
							long nowPrice = fundPriceHistoryDAO.getPriceByFundAndDate(
									fundId, (Date)session.getAttribute("date"));
							fundInfoBeans[i].setFundPrice(nowPrice / 100.0);
	
							fundInfoBeans[i].setName(fundBeans[j].getName());
							fundInfoBeans[i].setSymbol(fundBeans[j].getSymbol());
						}
					}
				}
				session.setAttribute("userInfo", customerBean);
				session.setAttribute("fundInfo", fundInfoBeans);
				
				//History
				TransactionBean[] transactionBeans = transactionDAO.getTransactionsByCustomerId(customerBean.getCustomerId());
				TransactionHistoryBean[] transactionHistoryBeans = new TransactionHistoryBean[transactionBeans.length];
				
				for (int i = 0; i < transactionBeans.length; i++) {
					transactionHistoryBeans[i] = new TransactionHistoryBean();
					
					transactionHistoryBeans[i].setExecuteDate(transactionBeans[i].getExecuteDate());
					transactionHistoryBeans[i].setFundName(transactionBeans[i].getFundBean().getName());
					transactionHistoryBeans[i].setTransactionId(transactionBeans[i].getTransactionId());
					transactionHistoryBeans[i].setTransactionType(transactionBeans[i].getTransactionType());
					
					String type = transactionHistoryBeans[i].getTransactionType();
					if (type.startsWith("D") || type.startsWith("R") || type.equals("Buy (pending)")) {
						transactionHistoryBeans[i].setAmount(transactionBeans[i].getAmount() / 100.0);
						transactionHistoryBeans[i].setShares(-1);
						transactionHistoryBeans[i].setSharePrice(-1);
					} else if (type.equals("Sell (pending)")) {
						transactionHistoryBeans[i].setShares(transactionBeans[i].getAmount() / 1000.0);
						transactionHistoryBeans[i].setAmount(-1);
						transactionHistoryBeans[i].setSharePrice(-1);
					} else if (type.equals("Sell")) {
						long price = fundPriceHistoryDAO.getPriceByFundAndDate(
								transactionBeans[i].getFundBean().getFundId(), transactionBeans[i].getExecuteDate());
						long shares = transactionBeans[i].getAmount();
						transactionHistoryBeans[i].setShares(shares / 1000.0);
						transactionHistoryBeans[i].setSharePrice(price / 100.0);
						transactionHistoryBeans[i].setAmount((shares / 1000.0) * (price / 100.0));
					} else {
						long price = fundPriceHistoryDAO.getPriceByFundAndDate(
								transactionBeans[i].getFundBean().getFundId(), transactionBeans[i].getExecuteDate());
						long amount = transactionBeans[i].getAmount();
						transactionHistoryBeans[i].setAmount(amount / 100.0);
						transactionHistoryBeans[i].setSharePrice(price / 100.0);
						transactionHistoryBeans[i].setShares((amount / 100.0) * (price / 100.0));
					}
				}

				session.setAttribute("historyInfo", transactionHistoryBeans);
			}
			
	        return "viewCustomerInformation.jsp";
	    } catch (Exception e) {
	    	System.out.println(e);
    	    errors.add(e.getMessage());
    	    return "viewCustomerInformation.jsp";
    	}
    }
}

