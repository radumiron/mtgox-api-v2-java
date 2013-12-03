package bitcoinGWT.server.dao.entities;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.classic.Session;

public class MainClass {

	public static void main(String[] args) {


		Session session = getSessionFactory().openSession();

		session.beginTransaction();





		session.save(new TradeEntity());
		session.getTransaction().commit();
		session.close();
	}

	/**
	 * 
	 * @return
	 */
	private static SessionFactory getSessionFactory() {
		return new AnnotationConfiguration()
		.configure()
		.buildSessionFactory();
	}

}
