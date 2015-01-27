package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.id;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;

/**
 * 
 * @author Harold Affo
 * 
 */
public class TableIdGenerator implements IdentifierGenerator {

	private static Logger log = Logger.getLogger(TableIdGenerator.class);

	@Override
	public Serializable generate(SessionImplementor session, Object object)
			throws HibernateException {
		String prefix = "TABLE_";
		Connection connection = session.connection();
		try {
			PreparedStatement ps = connection
					.prepareStatement("SELECT nextval ('seq_table') as nextval");
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				int id = rs.getInt("nextval");
				String code = prefix + id;
				log.debug("Table Table Id: " + code);
				return code;
			}
		} catch (SQLException e) {
			log.error(e);
			throw new HibernateException("Unable to generate Table Sequence");
		}
		return null;
	}
}
