package task8.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.Query;

import task8.databeans.CommentBean;
import task8.databeans.CommentGraphBean;
import task8.databeans.UserBean;

public class CommentHistoryDAO {
	public void insert(CommentBean commentBean) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			session.beginTransaction();
			session.save(commentBean);
			session.getTransaction().commit();
		} catch (Exception e) {
			if (session.getTransaction() != null) {
				session.getTransaction().rollback();
			}
		} finally {
			session.close();
		}
	}

	public void update(CommentBean commentBean) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			session.beginTransaction();
			session.merge(commentBean);
			session.getTransaction().commit();
		} catch (Exception e) {
			if (session.getTransaction() != null) {
				session.getTransaction().rollback();
			}
		} finally {
			session.close();
		}
	}

	public CommentBean[] getCommentsByUser(UserBean userBean) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Query query = session
				.createQuery("from CommentBean where userBean = :userBean");
		query.setParameter("userBean", userBean);
		List<?> list = (List<?>) query.list();
		CommentBean[] commentBeans = list.toArray(new CommentBean[list.size()]);
		session.close();

		return commentBeans;
	}

	public List<CommentGraphBean> getTopComments(int top) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Query query = session.createQuery("from CommentBean");
		List<?> list = (List<?>) query.list();
		CommentBean[] commentBeans = list.toArray(new CommentBean[list.size()]);
		session.close();

		Map<String, Integer> count = new HashMap<String, Integer>();
		count.clear();

		for (int i = 0; i < commentBeans.length; i++) {
			String username = commentBeans[i].getUserBean().getScreen_name();
			if (count.containsKey(username)) {
				count.put(username, count.get(username) + 1);
			} else {
				count.put(username, 1);
			}
		}

		List<Integer> mapValues = new ArrayList<Integer>(count.values());
		Collections.sort(mapValues);
		
		List<CommentGraphBean> commentGraphBeans = new ArrayList<CommentGraphBean>();

		int tot = 0;
		for (int i = mapValues.size() - 1; i >= 0; i--) {
			if (tot >= top) {
				for (String key : count.keySet()) {
					if (count.get(key) == mapValues.get(i)) {
						if (commentGraphBeans.size() != tot) {
							commentGraphBeans.get(tot).incCount(count.get(key));
						} else {
							CommentGraphBean tmp = new CommentGraphBean();
							tmp.setCount(count.get(key));
							tmp.setUsername("Others");
							commentGraphBeans.add(tmp);
						}
					}
				}
			} else {
				for (String key : count.keySet()) {
					if (count.get(key) == mapValues.get(i)) {
						CommentGraphBean tmp = new CommentGraphBean();
						tmp.setCount(count.get(key));
						tmp.setUsername(key);
						commentGraphBeans.add(tmp);
						tot++;
					}
				}
			}
		}

		return commentGraphBeans;
	}
}