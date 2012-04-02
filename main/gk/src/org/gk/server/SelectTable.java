package org.gk.server;

public interface SelectTable {
	interface PROJECT_USER {
		String TABLENAME = "_PROJECT_USER";

		interface COLUMN {
			String USERID = "USERID";
			String PROJECTID = "PROJECTID";
			String ROLEID = "ROLEID";
		}
	}

	String _PROGRAM = "_PROGRAM";
}
