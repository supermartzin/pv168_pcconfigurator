/**
 * @author David Kaya
 * @version 0.1
 */

package pcconfigurator;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class SqlScriptRunner {

    public static final String DEFAULT_SCRIPT_DELIMETER = ";";
    public static final Logger LOGGER = Logger.getLogger(SqlScriptRunner.class.getName());

    private final boolean autoCommit, logErrors;
    private final DataSource dataSource;
    private final Connection connection;

    /**
     * 
     * @param connection : Connection to database.
     * @param autoCommit : True - it will commit automatically, false - you have to commit manualy.
     */
    public SqlScriptRunner(final DataSource dataSource, final boolean autoCommit) {
        this(dataSource, autoCommit, false);
    }
    
    /**
     * 
     * @param connection : Connection to database.
     * @param autoCommit : True - it will commit automatically, false - you have to commit manualy.
     * @param logErrors : True - it will log errors, false - it will not log errors.
     */
    public SqlScriptRunner(final DataSource dataSource, final boolean autoCommit, final boolean logErrors) {
        if (dataSource == null) {
            throw new RuntimeException("DataSource is required");
        }
        this.dataSource = dataSource;
        this.autoCommit = autoCommit;       
        this.logErrors = logErrors;
    }
    
    /**
     * 
     * @param reader - file with your script
     * @throws SQLException - throws SQLException on error
     */
    public void runScript(final Reader reader) throws SQLException {
        final boolean originalAutoCommit = this.connection.getAutoCommit();
        try {
            if (originalAutoCommit != this.autoCommit) {
                this.connection.setAutoCommit(autoCommit);
            }
            this.runScript(this.connection, reader);
        } finally {
            this.connection.setAutoCommit(originalAutoCommit);
        }
    }
    
    private void runScript(final Connection connection, final Reader reader) {

        for (String script : formatString(reader)) {
            PreparedStatement statement = null;
            
            try {
                statement = connection.prepareStatement(script);                
                statement.execute();
                
                //If auto commit is enabled, then commit
                if (autoCommit) {
                    connection.commit();
                }

            } catch (SQLException ex) {
                if (logErrors) {
                    Logger.getLogger(SqlScriptRunner.class.getName()).log(Level.SEVERE, null, ex);
                } else {
                    ex.fillInStackTrace();
                }
            } finally {                
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (SQLException ex) {
                        if (logErrors) {
                            LOGGER.log(Level.SEVERE, null, ex);
                        } else {
                            ex.fillInStackTrace();
                        }
                    }
                }
            }
        }

    }
    
    /**
     * Parses file into commands delimeted by ';'
     * @param reader 
     * @return string[] - commands from file
     */
    private String[] formatString(final Reader reader) {
        String result = "";
        String line;
        final LineNumberReader lineReader = new LineNumberReader(reader);

        try {
            while ((line = lineReader.readLine()) != null) {
                if (line.startsWith("--") || line.startsWith("//") || line.startsWith("#")) {
                    //DO NOTHING - It is a commented line
                } else {
                    result += line;
                }
            }
        } catch (IOException ex) {
            if (logErrors) {
                LOGGER.log(Level.SEVERE, null, ex);
            } else {
                ex.fillInStackTrace();

            }
        }

        if (result == null) {
            throw new RuntimeException("Error while parsing or no scripts in file!");
        } else {
            return result.replaceAll("(?<!"+DEFAULT_SCRIPT_DELIMETER+")(\\r?\\n)+", "").split(DEFAULT_SCRIPT_DELIMETER);
        }
    }
    /**
     * Drops all tables - NOT FUNCTIONAL YET
     */
    public void dropAllTables(){       
        DatabaseMetaData md;
        ResultSet rs;
        PreparedStatement st;
        try {
            md = connection.getMetaData();
            rs = md.getTables(null, null, "%", null);
            
            while(rs.next()){
                st = connection.prepareStatement("DROP TABLE ?");
                st.setString(1, rs.getString(3));
                st.execute();
            }
            
            if(autoCommit)
                connection.commit();
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Deletes all records but does not reset primary key counter
     */
    public void deleteAllRecors(){
        deleteAllRecords(false);
    }
    
    /**
     * Deletes all records
     * @param resetPrimaryKey : true - resets primary key counter, false - does not reset primary key counter
     */
    public void deleteAllRecords(final boolean resetPrimaryKey){
        DatabaseMetaData md;
        ResultSet rs;
        ResultSet primaryKeySet;
        PreparedStatement st;
        
        try {
            md = connection.getMetaData();
            rs = md.getTables(null, null, "%", null);
           
            while(rs.next()){
                st = connection.prepareStatement("DELETE * FROM ?");
                st.setString(1, rs.getString(3));
                st.execute();
                if(resetPrimaryKey){
                    PreparedStatement alter = connection.prepareStatement("ALTER SEQUENCE ? RESTART WITH 1");
                    primaryKeySet = md.getPrimaryKeys(null, null, rs.getString(3));
                    while(primaryKeySet.next()){
                        alter.setString(1, rs.getString(3)+"_"+primaryKeySet.getString("COLUMN_NAME")+"_seq");
                        st.execute();
                    }
                }
            }
            
            if(autoCommit)
                connection.commit();
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
}
