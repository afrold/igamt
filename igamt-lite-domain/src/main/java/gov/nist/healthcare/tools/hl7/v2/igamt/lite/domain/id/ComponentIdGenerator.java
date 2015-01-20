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
public class ComponentIdGenerator implements IdentifierGenerator {

	private static Logger log = Logger.getLogger(ComponentIdGenerator.class);

	@Override
	public Serializable generate(SessionImplementor session, Object object)
			throws HibernateException {
		String prefix = "COMP_";
		Connection connection = session.connection();
		try {
			PreparedStatement ps = connection
					.prepareStatement("select nextval ('seq_component') as nextval");
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				String newId = prefix + rs.getInt("nextval");
				log.debug("Generated Component Id: " + newId);
				return newId;
			}
		} catch (SQLException e) {
			log.error(e);
			throw new HibernateException(
					"Unable to generate Component Sequence");
		}
		return null;
	}
}
